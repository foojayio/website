#!/usr/bin/env python3
"""
Shrinks the WordPress-era media in content/ so the built site fits GitHub Pages.

WHY THIS EXISTS. The deploy artifact reached 1.26 GB against a hard 1 GB limit
("Deployment might fail" -- a warning that appears on a run which otherwise goes
GREEN, so the site sails past the limit invisibly). It is not the HTML: 4100 pages
are 148 MB. It is the images -- 1229 MB across 5952 files, 78% of it in the 1214
files over 200 KB. Three post bundles each carried the SAME 52 MB animated GIF,
and all three used it as their `image:` hero.

WHAT IT DOES

  1. Animated GIF -> animated WebP, for GIFs over --gif-min. 41 files held 301 of
     the 316 MB of GIF. These are screen recordings: the container is the problem,
     not the content. WebP animates too, so the markup keeps its shape -- but the
     FILENAME changes, so references are rewritten (see convert_gif).
  2. An animated `image:` hero gets a STILL poster, and `image:` is repointed at
     it. A hero is the card thumbnail, the og:image and the JSON-LD image, none of
     which animate: a link preview shows frame one, a grid of animating cards is
     unreadable, and the whole file downloads to draw a thumbnail.
     validate/Frontmatter.checkHeroImageStill enforces this at PR time.
  3. PNG/JPEG resized when the long edge exceeds --cap. 833 files were wider,
     holding 545 MB, while the article column is ~880px -- so 1600 still covers a
     2x display. Format and filename are unchanged, so nothing references these
     by a name that moves.

TARGETS THE BUDGET, NOT A FIXED QUALITY. A single quality setting is not good
enough: converting the 52 MB OpenRewrite.gif at q78 still left 10.2 MB, over the
3 MB per-file budget the validator enforces. So each GIF is retried with
progressively harder settings until it fits, and the first result that does is
kept (see LADDER).

DELIBERATELY NOT DONE: converting the 4194 PNGs to WebP. It is the biggest single
win (measured 6.4 MB -> 0.5 MB on the worst offender) and still the wrong trade
here -- it renames ~4000 references across ~2000 posts, and transfer/Posts.java
re-downloads the original PNG from WordPress, so the next re-scrape would undo all
of it and restore the .png references. Resizing keeps the filename, which a
re-scrape overwrites harmlessly with an original this pass then shrinks again.
Revisit after cutover, when the scrapers are gone.

TWO SAFETY RULES, both learned the hard way on the first run:

  * EVERY BUNDLE IS FINISHED BEFORE THE NEXT ONE STARTS. The first version
    converted every GIF and rewrote the references at the END. A commit landed
    while it was running and captured the state in between -- converted files,
    references still pointing at the deleted .gif -- and put 4 broken images on
    the live site. Nothing is deleted now until its references are rewritten, so
    the tree is consistent at every instant and killing the run is safe.
  * IT NEVER WRITES OVER, OR DELETES, A FILE IT DID NOT CREATE. One bundle holds
    both projectexplorer.gif and a hand-made projectexplorer.webp; the first
    version destroyed the latter. A destination that already exists is now a SKIP
    with a report, and the only file ever unlinked is the source that was just
    converted and verified.

WHY PYTHON, in a scripts/ tree that is otherwise jbang Java. Writing an ANIMATED
WebP is the point of step 1, and Pillow is the only writer available: Java's
ImageIO has no WebP encoder, and the usual add-on (org.sejda.imageio:webp-imageio)
does static WebP only. This runs by hand, locally, and its OUTPUT is committed --
CI needs neither Python nor Pillow, the same posture as transfer/Sponsors.java.

UNLIKE ITS cleanup/ SIBLINGS, THIS ONE OUTLIVES CUTOVER. The others repair a
WordPress conversion artefact and are then dead. Contributors keep adding images
forever, so this stays useful -- and the validator's message names it.

IDEMPOTENT. A file already inside the caps is untouched, and a second run reports
nothing to do. Re-run after any late re-scrape, which restores the originals --
same standing instruction as cleanup/CloudflareEmails.java.

NOTE ON THE GIT REPO. This shrinks the build output and the working tree, not
.git, which keeps every old blob. A fresh clone stays large until history is
rewritten -- a separate and much riskier job. The 1 GB limit is on the artifact,
so this fixes the actual failure.

Usage:
  python3 scripts/cleanup/images.py --dry-run
  python3 scripts/cleanup/images.py
  python3 scripts/cleanup/images.py --path content/posts/2024
"""

import argparse
import os
import re
import sys
from pathlib import Path

try:
    from PIL import Image, ImageSequence
except ImportError:
    sys.exit("needs Pillow:  python3 -m pip install --user Pillow")

Image.MAX_IMAGE_PIXELS = None

RASTER = {".png", ".jpg", ".jpeg"}
INDEX_NAMES = ("index.md", "_index.md")

# (long-edge cap, WebP quality). Tried in order until the result fits the budget;
# the last rung is deliberately aggressive, because a screencast that still will
# not fit is better small than over the limit.
LADDER = [(1600, 78), (1600, 70), (1400, 62), (1200, 55), (1000, 48)]

# Pillow will not write an arbitrarily long animation -- projectexplorer.gif's 805
# frames come back short and the verification rejects them -- and decoding 800
# frames five times over is minutes per file. So the frame STEP is derived once,
# from the frame count, rather than being another rung to try: 805 frames at
# step 4 is 202, which encodes correctly and quickly. Each kept frame absorbs the
# duration of the ones it replaces, so playback speed is unchanged; it is choppier,
# which for a screen recording is the right thing to trade for fitting the budget.
MAX_FRAMES = 250


def human(n):
    return f"{n / 1e6:.2f} MB" if n >= 1e5 else f"{n / 1e3:.0f} KB"


def index_files(folder):
    return [Path(folder) / n for n in INDEX_NAMES if (Path(folder) / n).is_file()]


# --------------------------------------------------------------- GIF -> WebP

def decode_frames(src):
    """Decode a GIF once into (frames, durations, loop), thinned to MAX_FRAMES.

    Decoding is the expensive half for a long animation, so it happens ONCE and the
    result is re-encoded at each quality rung, rather than re-reading the file five
    times.
    """
    with Image.open(src) as im:
        total = getattr(im, "n_frames", 1)
        fast = []
        for fr in ImageSequence.Iterator(im):
            fast.append(fr.info.get("duration", 80) or 80)
        avg = (sum(fast) / len(fast)) if fast else 80
        step = 2 if (total > 40 and avg < 60) else 1
        step = max(step, -(-total // MAX_FRAMES))  # ceil division

        im.seek(0)
        frames, durations, carried = [], [], 0
        for i, fr in enumerate(ImageSequence.Iterator(im)):
            carried += fr.info.get("duration", 80) or 80
            if i % step:
                continue
            frames.append(fr.convert("RGB"))
            durations.append(carried)
            carried = 0
        return frames, durations, im.info.get("loop", 0)


def encode_webp(frames, durations, loop, dst, cap, quality):
    """Encode already-decoded frames to dst. Returns the frame count written."""
    out = []
    for f in frames:
        w, h = f.size
        if max(w, h) > cap:  # never upscale
            k = cap / max(w, h)
            f = f.resize((max(1, round(w * k)), max(1, round(h * k))), Image.LANCZOS)
        out.append(f)
    out[0].save(dst, "WEBP", save_all=True, append_images=out[1:],
                duration=durations, loop=loop, quality=quality, method=4)
    return len(out)


def rewrite_in_bundle(folder, old, new):
    """Repoint every reference to `old` at `new`, in this bundle's index only.

    Bundle images are referenced by BARE FILENAME (resource-url.html resolves a
    name against the page's own folder), which is what makes a text replacement
    safe -- the name is distinctive, and it appears in exactly these shapes, all
    of which are live in content/:

        ![alt](shot.gif)          markdown image
        image: "shot.gif"         the hero in frontmatter
        shot.gif | caption        a line inside {{< gallery >}}
        <img src="shot.gif">      raw HTML in a preserved block

    Scoped to this bundle, so a same-named image in another post is never touched.
    """
    n = 0
    for md in index_files(folder):
        text = md.read_text(encoding="utf-8")
        if old not in text:
            continue
        md.write_text(text.replace(old, new), encoding="utf-8")
        n += 1
    return n


def convert_gif(gif, budget, dry_run):
    """One GIF -> WebP, references rewritten, original removed. Returns (before, after)."""
    before = gif.stat().st_size
    dst = gif.with_suffix(".webp")
    if dst.exists():
        # Never clobber a file we did not create -- one bundle ships both
        # projectexplorer.gif and a hand-made projectexplorer.webp.
        print(f"  SKIP {gif} -- {dst.name} already exists")
        return None
    if dry_run:
        return (before, None)

    frames, durations, loop = decode_frames(gif)
    if not frames:
        print(f"  SKIP {gif.name} -- no frames decoded")
        return None

    # Encode to a TEMP path and rename only once the result is verified. Writing
    # straight to dst meant an interrupted run left a 0-byte .webp behind -- which
    # the "destination exists" guard above then treated as a real file, so the GIF
    # could never be converted again and the junk got committed. A partial temp file
    # is cleaned up in the finally below and blocks nothing.
    tmp = dst.with_name(dst.name + ".tmp")
    best = None
    try:
        for cap, q in LADDER:
            written = encode_webp(frames, durations, loop, tmp, cap, q)
            size = tmp.stat().st_size
            # Verify the animation survived -- but NOT by frame equality. libwebp
            # merges duplicate consecutive frames as an optimisation (221 in, 198
            # out on the TestBox recording), which is correct and desirable. An
            # equality check rejected 23 of 65 GIFs, leaving the biggest offenders
            # in place; reading durations back is no help either, since Pillow
            # reports 0 ms for a WebP it just wrote. So the test is: still animated,
            # and not truncated to a fraction of its length.
            with Image.open(tmp) as check:
                got = getattr(check, "n_frames", 1)
            if written > 1 and (got < 2 or got < written * 0.5):
                print(f"  SKIP {gif.name} -- animation truncated ({written} -> {got} frames)")
                return None
            best = size
            if size <= budget:
                break
        if best is not None and best < before:
            tmp.replace(dst)
    finally:
        if tmp.exists():
            tmp.unlink()

    if best is None or best >= before:  # pathological; leave the GIF alone
        return None

    # Order matters: references first, THEN delete. A run killed between the two
    # would otherwise leave the tree pointing at a file that no longer exists.
    rewrite_in_bundle(gif.parent, gif.name, dst.name)
    gif.unlink()
    return (before, best)


# ------------------------------------------------------------ animated hero

def poster_for(img, cap, dry_run):
    """A still poster next to an animated image. Returns the new Path, or None.

    PNG when the frame has 256 colours or fewer -- a GIF frame always does, so the
    poster is LOSSLESS and crisp for UI screenshots -- otherwise JPEG, which is
    what an og:image wants for a photo or a rendered graphic.
    """
    with Image.open(img) as im:
        im.seek(0)
        frame = im.convert("RGB")
    w, h = frame.size
    if max(w, h) > cap:
        k = cap / max(w, h)
        frame = frame.resize((max(1, round(w * k)), max(1, round(h * k))), Image.LANCZOS)
    palette = frame.getcolors(maxcolors=256) is not None
    dst = img.with_name(img.stem + "-poster" + (".png" if palette else ".jpg"))
    if dst.exists():
        print(f"  SKIP poster for {img.name} -- {dst.name} already exists")
        return None
    if dry_run:
        return dst
    if palette:
        frame.convert("P", palette=Image.ADAPTIVE, colors=256).save(dst, "PNG", optimize=True)
    else:
        frame.save(dst, "JPEG", quality=88, optimize=True, progressive=True)
    return dst


def is_animated(path):
    try:
        with Image.open(path) as im:
            return getattr(im, "n_frames", 1) > 1
    except Exception:
        return False


def fix_animated_heroes(root, cap, dry_run):
    """Repoint an animated `image:` at a still poster. Returns (fixed, orphaned)."""
    fixed, orphaned = [], []
    for md in sorted(list(root.rglob("index.md")) + list(root.rglob("_index.md"))):
        text = md.read_text(encoding="utf-8", errors="replace")
        end = text.find("\n---", 3)
        if end < 0:
            continue
        fm, body = text[:end], text[end:]
        m = re.search(r'^image:\s*"?([^"\n]+)"?\s*$', fm, re.M)
        if not m:
            continue
        hero = m.group(1).strip()
        if not hero or "://" in hero or hero.startswith("/"):
            continue
        img = md.parent / hero
        if not img.is_file() or not is_animated(img):
            continue
        dst = poster_for(img, cap, dry_run)
        if dst is None:
            continue
        if not dry_run:
            md.write_text(text.replace(f'image: "{hero}"', f'image: "{dst.name}"'), encoding="utf-8")
        fixed.append((md, hero, dst.name))
        # The animation itself is NOT moved into the body -- editing someone's
        # article text is not this script's call. Where the body does not already
        # show it, that is reported so a human can decide.
        #
        # Remote URLs are stripped first: java-on-visual-studio-code-may-2023 links
        # the SAME image on Microsoft's blog, so the bare substring "projectexplorer.gif"
        # appears in the body while no LOCAL reference to it exists -- which read as
        # "still shown" and hid the orphan.
        if hero not in re.sub(r"https?://\S+", "", body):
            orphaned.append((md, hero))
    return fixed, orphaned


# ------------------------------------------------------------------ PNG / JPEG

def shrink_raster(src, cap, jpeg_quality, dry_run):
    """Resize in place when oversized; re-encode JPEG. Returns the new size or None.

    Writes only when the result is MEANINGFULLY smaller (>10%), which does two
    jobs. It stops a re-encode from GROWING a file -- a well-optimised PNG often
    comes back bigger, and inflating images while claiming to shrink the site is
    worse than doing nothing. And it makes repeated runs safe: a JPEG re-encoded at
    the same quality loses a little each time, so without a floor this would
    quietly degrade every photo on every run.
    """
    ext = src.suffix.lower()
    before = src.stat().st_size
    with Image.open(src) as im:
        w, h = im.size
        icc = im.info.get("icc_profile")
        oversized = max(w, h) > cap
        if ext == ".png" and not oversized:
            return None
        out = im.copy()
        if oversized:
            k = cap / max(w, h)
            out = out.resize((max(1, round(w * k)), max(1, round(h * k))), Image.LANCZOS)

    if dry_run:
        return -1

    tmp = src.with_suffix(src.suffix + ".tmp")
    try:
        if ext == ".png":
            out.save(tmp, "PNG", optimize=True)
        else:
            if out.mode in ("RGBA", "P", "LA"):
                out = out.convert("RGB")
            out.save(tmp, "JPEG", quality=jpeg_quality, optimize=True,
                     progressive=True, **({"icc_profile": icc} if icc else {}))
        after = tmp.stat().st_size
        if after >= before * 0.9:
            tmp.unlink()
            return None
        tmp.replace(src)
        return after
    finally:
        if tmp.exists():
            tmp.unlink()


# ------------------------------------------------------------------------ main

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--path", default="content")
    ap.add_argument("--cap", type=int, default=1600, help="max long edge in px")
    ap.add_argument("--gif-min", type=int, default=200_000)
    ap.add_argument("--budget", type=int, default=3_000_000,
                    help="per-file target; matches Frontmatter.MAX_IMAGE_BYTES")
    ap.add_argument("--jpeg-quality", type=int, default=82)
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    root = Path(args.path)
    if not root.is_dir():
        sys.exit(f"not a directory: {root}")

    saved = 0

    gifs = sorted((p for p in root.rglob("*")
                   if p.suffix.lower() == ".gif" and p.stat().st_size > args.gif_min),
                  key=lambda p: -p.stat().st_size)
    print(f"GIF -> WebP ({len(gifs)} candidates)")
    converted = 0
    for gif in gifs:
        r = convert_gif(gif, args.budget, args.dry_run)
        if r is None:
            continue
        before, after = r
        converted += 1
        if after is not None:
            saved += before - after
            print(f"  {human(before):>9} -> {human(after):>9}  {gif.name[:50]}")
        else:
            print(f"  {human(before):>9} -> {'?':>9}  {gif.name[:50]}")

    print(f"\nanimated hero -> still poster")
    fixed, orphaned = fix_animated_heroes(root, args.cap, args.dry_run)
    for md, hero, poster in fixed:
        print(f"  {hero} -> {poster}   {md.parent.name[:46]}")

    print(f"\nresizing PNG/JPEG over {args.cap}px")
    touched = 0
    for p in root.rglob("*"):
        if p.suffix.lower() not in RASTER:
            continue
        before = p.stat().st_size
        after = shrink_raster(p, args.cap, args.jpeg_quality, args.dry_run)
        if after is None:
            continue
        touched += 1
        if not args.dry_run:
            saved += before - after

    print(f"\n{'would convert' if args.dry_run else 'converted'} {converted} GIF(s)"
          f", {'would repoint' if args.dry_run else 'repointed'} {len(fixed)} hero(es)"
          f", {'would resize' if args.dry_run else 'resized'} {touched} raster(s)")
    if not args.dry_run:
        print(f"TOTAL SAVED: {saved / 1e6:.0f} MB")
    if orphaned:
        print(f"\nNeeds a human -- {len(orphaned)} post(s) whose animation was ONLY the hero,"
              f"\nso it now appears nowhere. Add it to the body if it was carrying the point:")
        for md, hero in orphaned:
            print(f"  {hero}  in {md}")


if __name__ == "__main__":
    main()
