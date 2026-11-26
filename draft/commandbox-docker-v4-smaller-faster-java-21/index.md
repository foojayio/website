---
title: "CommandBox Docker Images v4: Smaller Images, Faster Startups, Java 21"
date: "2026-11-24"
description: "A preview of CommandBox Docker Images v4, with Java 21 across all bases, FHS-aligned paths, non-root runtime conventions, a BoxLang-backed CLI, smaller images, and faster container startup."
authors:
  - "luis-majano"
  - "jon-clausen"
image: "cover.jpg"
categories:
  - "Java"
  - "Docker"
  - "DevOps"
  - "Containers"
  - "Developer Tools"
canonical: "https://www.ortussolutions.com/blog/coming-soon-version-4-docker-commandbox-images-with-enhanced-performance"
related_posts: []
---

CommandBox Docker images were the first CFML docker images and have nearly 2 million pulls, since their introduction. 

It's been a source of pride at Ortus that these images have been the most popular and reliable way to run CFML in containers and we are so very pleased to announce the next evolution. Coming soon, the v4 images, which run entirely on the BoxLang CLI, represent a significant overhaul of the image architecture.  

Taking advantage of the major version change, these images also introduce breaking changes which serve the purpose of:

- Reducing container startup overhead for CLI operations.
- Align the filesystem layout with the Linux FHS.
- Simplify runtime account management and remove the need for elevated privileges.
- Standardize on Java 21 across all base images while using the native CommandBox functionality to run legacy server versions on their respective JVMs.

The key issues we wanted to address and resolved in image version v4 were:

- **Heavyweight CLI operations.** In v3, running any `box` command in a container meant spinning up a full Lucee server under the hood — even for tasks that had nothing to do with serving requests. CommandBox version 7 ( and the v4 images ) are built entirely on the BoxLang CLI, so utility commands, scripts, and one-off tasks run as lightweight BoxLang processes with no server startup overhead.
- **Non-standard paths.** Putting the application root at `/app` and binaries under `/usr/local` made sense as a quick convention, but it diverges from the Linux Filesystem Hierarchy Standard and creates friction when composing images or running in environments with strict path policies. It also created friction between Coldbox modern templates by having directories such as `/app/app` which sometimes resulted in path expansion issues.
- **Runtime account management.** v3 created OS users and re-owned files at container startup via `USER`/`USER_ID` environment variables. That approach requires elevated privileges during startup, is fragile on shared volumes, and duplicates something Docker already handles cleanly with the `USER` directive.
- **Inconsistent Java versions.** Different base images shipped different JDKs. Standardizing on Java 21 (Eclipse Temurin) across every base removes a class of subtle compatibility bugs and aligns with where the ecosystem is heading, while still allowing legacy engines to run on their required JVMs via the native CommandBox functionality.

CommandBox v4 Docker images address all of these issues. Runtime files now adhere to the Linux FHS hierarchy, remove the startup-time account management entirely, ship Java 21 on every base image, and replace the Lucee-backed `box` CLI with the BoxLang CLI — so the `box` binary and `boxlang` are now the same tool.

Because this is a major release, **it is important for existing users** to note that the upcoming release will contain breaking changes. Here are the breaking changes in the forthcoming release.  If your CI builds currently use the `:latest` tag or no tag at all (implicitly using `:latest`), you should pin to a specific version to avoid unexpected breakages, until you have a chance to update your configurations and workflows accordingly.

- Application root moves from `/app` to `/srv/app`; homes and binaries move to `/opt`, adhering to the Linux FHS.
- `USER` / `USER_ID` environment variables have been removed, in favor of using the Dockerfile `USER` directive (or `docker run --user` / Compose `user`).
- Mounted volumes which relied on root user privileges may need to be adjusted, as the default runtime identity is no longer root. As such, the runtime may not have R/W access to those volumes.
- The default runtime identity is `commandbox:runwar` (UID/GID `1000:1000`).
- Generated and finalized startup files now live in `/opt/commandbox/run` (`STARTUP_DIR`).
- All base images run Java 21; engines that need older Java must pin it in `server.json`.
- Because `box` is now the Boxlang CLI, any existing commands using `box boxlang cli …` should be updated to use `boxlang …`.

## 1. New filesystem layout

Runtime paths now follow the FHS: application data lives in `/srv`, and image-managed binaries, libraries, and state live under `/opt`.

| Purpose | v3 (3.x) | v4 |
| --- | --- | --- |
| Application root (`APP_DIR`) | `/app` | `/srv/app` |
| CommandBox home (`COMMANDBOX_HOME`) | `/usr/local/lib/CommandBox` | `/opt/commandbox` |
| BoxLang home (`BOXLANG_HOME`) | `/usr/local/boxlang` | `/opt/boxlang` |
| Binaries (`BIN_DIR`) | `/usr/local/bin` | `/opt/bin` |
| Build scripts (`BUILD_DIR`) | `/usr/local/lib/build` | `/opt/build` |
| Server home (default) | `/usr/local/lib/serverHome` (most builds); `/usr/lib/serverHome` on Alpine | `/opt/lib/serverHome` || Generated startup (`STARTUP_DIR`) | `/usr/local/bin` | `/opt/commandbox/run` |

There are no compatibility aliases for the old paths. Update your volume mounts and any derived-image `COPY`/`WORKDIR` paths before upgrading.  Examples of the differences in simple `docker run` commands are shown below:

```bash
# v3
docker run -p 8080:8080 -v "/path/to/app:/app" ortussolutions/commandbox

# v4
docker run -p 8080:8080 -v "/path/to/app:/srv/app" ortussolutions/commandbox:boxlang-snapshot 
```

## 2. Runtime user updates: say goodbye to the `$USER` environment variables, and adopt the Docker `USER` convention

Images now default to using `commandbox:runwar` (UID/GID `1000:1000`) as the runtime user. This helps to mitigate security scan issues with the previous image version, which ran as root, and ensures the runtime user has only the necessary permissions to run the container process. 

The `USER` and `USER_ID` environment variables no longer select a runtime identity. v3 created accounts and re-owned files at startup; v4 never does. Instead, pick the identity the Docker way:

```dockerfile
# Dockerfile
USER commandbox:runwar
```

If you wish to use the root user, in order to maintain backward compatibility, you will need to do the following:

### Dockerfile:

```dockerfile
# Dockerfile
USER root
```

### docker run:

```bash
docker run --user root:root -p 8080:8080 ortussolutions/commandbox
```

### docker compose: root user

```yaml
# docker compose
services:
  app:
    image: ortussolutions/commandbox
    user: "root:root"
```

Writable directories should be group-owned by GID `1000` and startup uses `umask 0002`, so a custom UID only needs access to the shared group. 

### Important notes for volume mounts:

If you have any volumes which need to be writable by the container, ensure that the UID/GID combination you use has the necessary permissions.  

**Bind mounts are the main gotcha.** A bind mount replaces the image's permissions with the host directory's. Before starting v4:

```bash
# Option A: make the host directory writable by GID 1000
chgrp -R 1000 /path/to/app && chmod -R g+rwX /path/to/app

# Option B: run as your host UID but keep the shared group
docker run --user "$(id -u):1000" -v "/path/to/app:/srv/app" ortussolutions/commandbox
```

Newly-created volumes inherit the image's permissions; existing volumes, created by previous image versions, do not, so update ownership/group access on existing volumes before upgrading.

## 3. Startup files move to `STARTUP_DIR`

Generated and finalized startup scripts now live in `/opt/commandbox/run` (`STARTUP_DIR`) instead of the binaries directory. A finalized build produces two files:

- `startup-final.sh` — the authoritative startup script
- `startup-final.env` — records the baked server home and is sourced by the startup script

This represents a change from v3 where finalized startup scripts included all of the environment variables as JVM arguments.

If you build finalized or multi-stage images, rebuild them against v4 and copy **both** files when you assemble the final stage. In a standalone final stage, source `startup-final.env` before running `startup-final.sh` so the baked server-home metadata is loaded correctly. Single-stage users don't need to change anything.

## 4. Java 21 is now the only OS JVM/JRE

All base CommandBox images now ship with the base image Java version of 21. The Alpine, Debian, and RHEL/UBI bases — and their engine images — run Java 21 out of the box.  Alternate JVMs needed to run specific CFML engines can be specified in `server.json`.

If you use an engine that needs an older Java, pin it in `server.json`:

```json
{
  "app": {
    "cfengine": "adobe@2023.0.26+331018"
  },
  "jvm": {
    "javaVersion": "17"
  }
}
```

That JRE is fetched at runtime, which adds weight to the image and lengthens cold starts — so warming up your servers in your Docker build process is recommended.

## 5. `box boxlang cli` is now just `boxlang`

CommandBox v7 is built on BoxLang, and in v4 the `box` binary and the BoxLang CLI are the same tool. Anything that used to be `box boxlang cli …` should move to `boxlang …`:

```bash
# before
box boxlang cli repl

# after
boxlang repl
```

The `boxlang` binary is on `PATH` in v4 images.

## Migration checklist

- [ ] Update mounts and `COPY`/`WORKDIR` paths: `/app` → `/srv/app`, homes/binaries → `/opt`.
- [ ] Remove `USER`/`USER_ID` env vars; add a Dockerfile `USER`, `docker run --user`, or Compose `user`.
- [ ] Prepare bind mounts and reused volumes for UID/GID `1000:1000`.
- [ ] Rebuild finalized/multi-stage images; copy `startup-final.sh` **and** `startup-final.env`.
- [ ] Confirm engines run on Java 21, or pin a JRE in `server.json`.
- [ ] Replace `box boxlang cli …` with `boxlang …`.

## Performance and system improvements

Another area we are highlighting in v4 is improved performance and resource usage. The benchmarks below compare the directly-comparable pre-seeded BoxLang images — `ortussolutions/commandbox:boxlang` (v3) and `ortussolutions/commandbox:boxlang-snapshot` (v4).

**Test Environment:** `linux/arm64` (Apple Silicon), Docker Desktop on macOS (Docker 29), via the companion `benchmark.sh` script. Each startup number is the best of 3 runs.

| Metric | v3 `boxlang` (3.x) | v4 `boxlang-snapshot` | Δ |
| --- | --- | --- | --- |
| Compressed image size | 542.2 MiB | 259.4 MiB | **−52%** |
| Uncompressed image size | 865.9 MiB | 606.0 MiB | **−30%** |
| Seeded startup → HTTP 200 | 8042 ms | 4584 ms | **−43%** |
| Time to first byte | 5.2 ms | 4.0 ms | — |
| Idle memory (RSS) | 218.3 MB | 205.7 MB | −6% |
| Image layers | 18 | 18 | — |

As you can see from the table above, v4 offers significant improvements in image size and startup performance compared to v3, while maintaining similar resource usage and image layer count.

## Summary

v4 brings the CommandBox Docker images in line with standard Linux and Docker conventions: FHS-compliant paths, native `USER`-based identity, Java 21 across all bases, and a BoxLang-backed CLI that no longer requires a running server for utility work. The result is smaller images, faster startups, and a simpler security model. The migration checklist covers the required changes; for most deployments it reduces to updating volume paths and dropping the `USER`/`USER_ID` env vars.

These images are now available to test by appending `-snapshot` to your existing version tags and will be released as stable, once CommandBox v7 emerges from release candidate status and is published as stable.

## Important Links

- Docker Hub: [ortussolutions/commandbox](https://hub.docker.com/r/ortussolutions/commandbox)
- Source: [Ortus-Solutions/docker-commandbox](https://github.com/ortus-solutions/docker-commandbox)
- Issues/feedback: [GitHub issues](https://github.com/ortus-solutions/docker-commandbox/issues)
