///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//JAVA 17+

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Post-build check on the site Hugo actually produced, run by
 * .github/workflows/build-deploy.yml between the build and the deploy -- so a
 * broken build stops before it replaces the live site rather than after.
 *
 * Two checks, both DERIVED, so there is no list of URLs to keep in step with
 * the content:
 *
 *   1. Every source page produced a built page. content/ is the expectation:
 *      every post bundle must yield a /today/<slug>/index.html, every author
 *      bundle a profile, every content/pages/*.md the `url:` it claims. This is
 *      the check that catches a whole SECTION going missing -- the failure mode
 *      of the branch-bundle conversion, where the templates rendered fine and
 *      simply matched nothing.
 *
 *   2. Every internal link resolves to a file that exists. Covers href, src,
 *      srcset, poster and the meta-refresh in every alias page, so it also
 *      verifies each legacy URL still lands somewhere real. This subsumes a
 *      "the nav works" check: the menus render on all 4200 pages, so a dead
 *      menu entry is a dead link 4200 times over.
 *
 * THE TWO KINDS OF DEAD LINK ARE NOT THE SAME PROBLEM, and only one of them may
 * stop a deploy. A link the TEMPLATES emit -- nav, pagination, stylesheet,
 * thumbnail, alias target -- is broken for every reader on every page and is a
 * bug in this repo, so it fails the run. A link an author typed inside their
 * own article is a fact about 2000 imported WordPress posts: there are already
 * dozens (`_wp_link_placeholder`, `/wp-admin/post.php`, tag URLs that never
 * existed here), none of them introduced by the build, and blocking every
 * future deploy on a 2021 typo would mean the gate gets switched off within the
 * week. Those are REPORTED, with their count, the way
 * fetch/DiscoverJugCalendars.java reports its near-misses. The boundary is
 * `.prose`, which is exactly where `.Content` -- author-written markdown -- is
 * rendered, and nowhere else.
 *
 * Deliberately NOT a crawler: it reads the files on disk rather than making
 * HTTP requests, so it needs no server, runs in seconds, and cannot be flaky.
 * External links are not checked at all -- a third-party host being down is not
 * a reason to block a deploy of our own site.
 *
 * Usage: jbang scripts/validate/BuiltSite.java [--public <dir>] [--max <n>] [--strict]
 *   --strict  also fail on the author-written dead links, for a cleanup pass.
 * Exits non-zero if anything blocking is broken.
 */
public class BuiltSite {

    /** URL schemes that never point at a file of ours. */
    static final Set<String> SKIP_SCHEMES = Set.of(
            "mailto", "tel", "javascript", "data", "blob", "ftp", "irc", "sms", "about", "file");

    /** The attributes worth following. `poster` is a still image, `srcset` a list. */
    static final String LINK_QUERY = "a[href], link[href], area[href], img[src], script[src], iframe[src],"
            + " source[src], video[src], audio[src], embed[src], track[src], input[src], video[poster],"
            + " img[srcset], source[srcset], meta[http-equiv=refresh]";

    static final Pattern REFRESH_URL = Pattern.compile("url\\s*=\\s*['\"]?([^'\"]+)", Pattern.CASE_INSENSITIVE);

    /** A link found on a page: where it points, and whether an author wrote it. */
    record Link(String url, boolean authored) {}

    /** A resolved link target: a path under public/, plus why it might be wrong. */
    record Target(String path, boolean escapesBasePath) {}

    public static void main(String[] args) throws IOException {
        Path publicDir = Path.of(arg(args, "--public", "public"));
        int max = Integer.parseInt(arg(args, "--max", "8"));
        boolean strict = List.of(args).contains("--strict");

        if (!Files.isDirectory(publicDir)) {
            System.err.println("No built site at " + publicDir.toAbsolutePath() + " -- run `hugo` first.");
            System.exit(2);
        }

        // Every file in the build, as a path relative to public/ with forward
        // slashes. Held in a set rather than hitting the filesystem per link:
        // 4200 pages carry half a million links between them, and a Set lookup
        // is also CASE-SENSITIVE on every platform -- which matters, because
        // macOS is not and GitHub Pages is, so a case-wrong link that resolves
        // on a laptop would otherwise only 404 once deployed.
        Set<String> files = new HashSet<>();
        try (Stream<Path> walk = Files.walk(publicDir)) {
            walk.filter(Files::isRegularFile).forEach(p -> files.add(rel(publicDir, p)));
        }

        String basePath = deriveBasePath(publicDir);
        System.out.println("Built site: " + files.size() + " files under " + publicDir
                + ", served at base path " + basePath);

        List<String> blocking = new ArrayList<>(checkExpectedPages(files, basePath));
        List<String> authored = new ArrayList<>();
        checkLinks(publicDir, files, basePath, max, blocking, authored);

        if (!authored.isEmpty()) {
            System.out.println("\n--- " + authored.size() + " dead link(s) inside article text ---");
            System.out.println("Written by an author, not produced by the build. Reported, not blocking"
                    + (strict ? " -- but --strict was passed, so they fail this run." : "; fix them in content/."));
            authored.forEach(System.out::println);
            if (strict) blocking.addAll(authored);
        }

        if (blocking.isEmpty()) {
            System.out.println("\nOK -- every source page was built and every link the site itself emits resolves.");
            return;
        }
        System.out.println("\n--- " + blocking.size() + " blocking problem(s) ---");
        blocking.forEach(System.out::println);
        System.out.println("\nNot deploying.");
        System.exit(1);
    }

    // ---------------------------------------------------------------- check 1

    /** A content section, its PLURAL label, and the permalink hugo.toml gives it. */
    record Section(String label, Path dir, String urlPrefix, boolean bundles) {}

    /**
     * content/ is the expectation and public/ is the answer. Each rule below is
     * the permalink from hugo.toml, so a section that stops rendering -- or a
     * post whose bundle folder is not a usable slug -- shows up as a missing
     * file rather than as a page nobody notices is gone.
     */
    static List<String> checkExpectedPages(Set<String> files, String basePath) throws IOException {
        List<String> problems = new ArrayList<>();
        List<Section> sections = List.of(
                // `bundles` = the section's items are page bundles, so only
                // index.md/_index.md is a PAGE. Every other .md beside it is a
                // resource -- 98 podcast episodes ship a transcript.md, and
                // Hugo renders none of them as a page of their own.
                new Section("posts", Path.of("content/posts"), "today/", true),
                new Section("authors", Path.of("content/authors"), "today/author/", true),
                new Section("sponsors", Path.of("content/sponsors"), "sponsor/", true),
                new Section("pedia entries", Path.of("content/pedia"), "pedia/", false));

        for (Section s : sections) {
            if (!Files.isDirectory(s.dir())) continue;
            int found = 0, missing = 0;
            try (Stream<Path> walk = Files.walk(s.dir())) {
                for (Path src : walk.filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(".md"))
                        .filter(p -> !p.equals(s.dir().resolve("_index.md")))   // the section landing page
                        .filter(p -> s.bundles() == isBundleIndex(p))
                        .sorted().toList()) {
                    String slug = slugOf(src);
                    if (slug == null) continue;
                    if (files.contains(s.urlPrefix() + slug + "/index.html")) {
                        found++;
                    } else if (++missing <= 10) {
                        problems.add("MISSING PAGE  " + src + " -> " + basePath + s.urlPrefix() + slug + "/");
                    }
                }
            }
            if (missing > 10) problems.add("MISSING PAGE  ... and " + (missing - 10) + " more " + s.label());
            report(s.label(), found, missing);
        }

        // Pages carry an explicit `url:` -- all of them do, because they are
        // keeping their legacy WordPress path -- so that is what to expect,
        // rather than a path derived from the filename.
        Path pagesDir = Path.of("content/pages");
        if (Files.isDirectory(pagesDir)) {
            int found = 0, missing = 0;
            try (Stream<Path> walk = Files.walk(pagesDir)) {
                for (Path src : walk.filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(".md"))
                        .filter(p -> !p.getFileName().toString().equals("_index.md"))
                        .sorted().toList()) {
                    String url = frontmatterValue(src, "url");
                    if (url == null) {
                        missing++;
                        problems.add("NO url:       " + src + " -- a page must claim the path it is served at");
                    } else if (files.contains(trimSlashes(url) + "/index.html")) {
                        found++;
                    } else {
                        missing++;
                        problems.add("MISSING PAGE  " + src + " -> " + basePath + trimSlashes(url) + "/");
                    }
                }
            }
            report("pages", found, missing);
        }
        return problems;
    }

    static boolean isBundleIndex(Path p) {
        String n = p.getFileName().toString();
        return n.equals("index.md") || n.equals("_index.md");
    }

    static void report(String label, int found, int missing) {
        System.out.printf("  %-14s %5d built%s%n", label + ":", found, missing == 0 ? "" : "   " + missing + " MISSING");
    }

    /** The URL slug of a content file: `slug:` frontmatter, else the bundle folder name. */
    static String slugOf(Path src) throws IOException {
        String explicit = frontmatterValue(src, "slug");
        if (explicit != null) return explicit;
        if (isBundleIndex(src)) return src.getParent().getFileName().toString();
        String name = src.getFileName().toString();
        return name.substring(0, name.length() - 3);
    }

    // ---------------------------------------------------------------- check 2

    static void checkLinks(Path publicDir, Set<String> files, String basePath, int max,
                           List<String> blocking, List<String> authoredOut) throws IOException {
        List<Path> html;
        try (Stream<Path> walk = Files.walk(publicDir)) {
            html = walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".html"))
                    .toList();
        }

        // target -> the pages linking to it, kept separately for the two kinds
        // so the reporting can treat them differently. Concurrent because the
        // HTML parse is the expensive part and there are thousands of files.
        Map<String, Set<String>> deadTemplate = new ConcurrentHashMap<>();
        Map<String, Set<String>> deadAuthored = new ConcurrentHashMap<>();
        LongAdder counted = new LongAdder();

        html.parallelStream().forEach(page -> {
            String from = rel(publicDir, page);
            Document doc;
            try {
                doc = Jsoup.parse(page.toFile(), "UTF-8");
            } catch (IOException e) {
                deadTemplate.computeIfAbsent("<unreadable: " + e.getMessage() + ">",
                        k -> ConcurrentHashMap.newKeySet()).add(from);
                return;
            }
            for (Link link : linksIn(doc)) {
                Target t = resolve(link.url(), page, publicDir, basePath);
                if (t == null) continue;                       // external, or not a link at all
                counted.increment();
                if (t.escapesBasePath()) {
                    // Resolves on a laptop and 404s once deployed under the
                    // /website/ subpath -- the failure mode that is invisible
                    // locally, so it is always worth naming as its own kind.
                    (link.authored() ? deadAuthored : deadTemplate)
                            .computeIfAbsent("/" + t.path() + "   (escapes the base path " + basePath + ")",
                                    k -> ConcurrentHashMap.newKeySet()).add(from);
                } else if (!exists(t.path(), files)) {
                    (link.authored() ? deadAuthored : deadTemplate)
                            .computeIfAbsent(basePath + t.path(), k -> ConcurrentHashMap.newKeySet()).add(from);
                }
            }
        });

        System.out.printf("  %-14s %5d pages scanned, %,d internal links, %d dead (%d of them author-written)%n",
                "links:", html.size(), counted.sum(),
                deadTemplate.size() + deadAuthored.size(), deadAuthored.size());

        blocking.addAll(describe(deadTemplate, "DEAD LINK   ", max));
        authoredOut.addAll(describe(deadAuthored, "dead link   ", max));
    }

    /** Worst first: a target linked from many pages is a template bug, one page is a typo. */
    static List<String> describe(Map<String, Set<String>> dead, String prefix, int max) {
        List<String> out = new ArrayList<>();
        dead.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Set<String>>>comparingInt(e -> -e.getValue().size())
                        .thenComparing(Map.Entry::getKey))
                .forEach(e -> {
                    List<String> from = new ArrayList<>(e.getValue());
                    Collections.sort(from);
                    String where = from.size() <= max
                            ? String.join(", ", from)
                            : String.join(", ", from.subList(0, max)) + ", and " + (from.size() - max) + " more";
                    out.add(prefix + "  " + e.getKey() + "\n                from " + where);
                });
        return out;
    }

    /**
     * Every URL-bearing attribute value on the page, srcset expanded, each
     * flagged with whether it sits inside `.prose` -- i.e. inside the markdown
     * an author wrote, rather than in the surrounding template.
     */
    static List<Link> linksIn(Document doc) {
        List<Link> out = new ArrayList<>();
        for (Element el : doc.select(LINK_QUERY)) {
            boolean authored = el.closest(".prose") != null;
            if (el.hasAttr("srcset")) {
                for (String candidate : el.attr("srcset").split(",")) {
                    String url = candidate.trim().split("\\s+")[0];
                    if (!url.isBlank()) out.add(new Link(url, authored));
                }
            }
            if (el.normalName().equals("meta")) {
                Matcher m = REFRESH_URL.matcher(el.attr("content"));
                if (m.find()) out.add(new Link(m.group(1).trim(), false));
                continue;
            }
            for (String attr : List.of("href", "src", "poster")) {
                if (el.hasAttr(attr)) out.add(new Link(el.attr(attr), authored));
            }
        }
        return out;
    }

    /**
     * A raw attribute value -> the file under public/ it must resolve to, or
     * null when it is not ours to check.
     */
    static Target resolve(String raw, Path page, Path publicDir, String basePath) {
        String url = raw.trim();
        if (url.isEmpty() || url.startsWith("#")) return null;
        if (url.startsWith("//")) return null;                  // protocol-relative: another host

        int colon = url.indexOf(':');
        int slash = url.indexOf('/');
        if (colon > 0 && (slash < 0 || colon < slash)) {
            String scheme = url.substring(0, colon).toLowerCase(Locale.ROOT);
            if (!scheme.equals("http") && !scheme.equals("https")) return null;
            // Absolute, and only interesting when it points back at us. The site
            // emits absolute URLs by contract in canonical, og:url and the feeds,
            // and those go through the same resolution as everything else.
            int sep = url.indexOf("//");
            String after = sep < 0 ? "" : url.substring(sep + 2);
            int firstSlash = after.indexOf('/');
            String path = firstSlash < 0 ? "/" : after.substring(firstSlash);
            if (!path.startsWith(basePath)) return null;        // another host, or the production URL
            url = path;
        }

        url = stripAfter(stripAfter(url, '#'), '?');
        if (url.isEmpty()) return null;
        url = percentDecode(url);

        if (url.startsWith("/")) {
            if (!url.startsWith(basePath)) return new Target(trimSlashes(url), true);
            return new Target(url.substring(basePath.length()), false);
        }
        // Relative to the page's own directory. public/ mirrors the URL tree
        // below the base path, so resolving on disk is resolving the URL.
        Path resolved = page.getParent().resolve(url).normalize();
        if (!resolved.startsWith(publicDir)) return new Target(resolved.toString(), true);
        return new Target(rel(publicDir, resolved), false);
    }

    /** Hugo serves pretty URLs, so a directory means its index.html. */
    static boolean exists(String target, Set<String> files) {
        String clean = trimSlashes(target);
        if (clean.isEmpty()) return files.contains("index.html");
        return files.contains(clean)
                || files.contains(clean + "/index.html")
                || files.contains(clean + ".html");
    }

    // ----------------------------------------------------------------- helpers

    /**
     * The path the site is served at, read from the home page's own canonical --
     * so this works for a /website/ trial build and a / production build with
     * nothing to configure and nothing to remember to change at cutover.
     */
    static String deriveBasePath(Path publicDir) throws IOException {
        Path home = publicDir.resolve("index.html");
        if (Files.isRegularFile(home)) {
            Element link = Jsoup.parse(home.toFile(), "UTF-8").selectFirst("link[rel=canonical]");
            if (link != null) {
                String href = link.attr("href");
                int i = href.indexOf("//");
                if (i >= 0) {
                    String after = href.substring(i + 2);
                    int s = after.indexOf('/');
                    String path = s < 0 ? "/" : after.substring(s);
                    return path.endsWith("/") ? path : path + "/";
                }
            }
        }
        return "/";
    }

    static String rel(Path publicDir, Path p) {
        return publicDir.relativize(p).toString().replace('\\', '/');
    }

    static String trimSlashes(String s) {
        int a = 0, b = s.length();
        while (a < b && s.charAt(a) == '/') a++;
        while (b > a && s.charAt(b - 1) == '/') b--;
        return s.substring(a, b);
    }

    static String stripAfter(String s, char c) {
        int i = s.indexOf(c);
        return i < 0 ? s : s.substring(0, i);
    }

    /**
     * %XX only. URLDecoder is wrong here: it turns '+' into a space, and a plus
     * in a path is a literal plus.
     */
    static String percentDecode(String s) {
        if (s.indexOf('%') < 0) return s;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '%' && i + 2 < s.length()) {
                try {
                    out.write(Integer.parseInt(s.substring(i + 1, i + 3), 16));
                    i += 2;
                    continue;
                } catch (NumberFormatException ignored) {
                    // A stray % that isn't an escape: keep it as written.
                }
            }
            byte[] bytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
            out.write(bytes, 0, bytes.length);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    /** Read one key out of a content file's frontmatter. */
    static String frontmatterValue(Path file, String key) throws IOException {
        List<String> lines;
        try (Stream<String> s = Files.lines(file, StandardCharsets.UTF_8)) {
            lines = s.limit(120).toList();
        } catch (Exception e) {
            return null;
        }
        if (lines.isEmpty() || !lines.get(0).startsWith("---")) return null;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("---")) break;
            if (!line.startsWith(key + ":")) continue;
            String v = line.substring(key.length() + 1).trim();
            if (v.length() >= 2 && ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'")))) {
                v = v.substring(1, v.length() - 1);
            }
            return v.isBlank() ? null : v;
        }
        return null;
    }

    static String arg(String[] args, String name, String fallback) {
        for (int i = 0; i < args.length - 1; i++) if (args[i].equals(name)) return args[i + 1];
        return fallback;
    }
}
