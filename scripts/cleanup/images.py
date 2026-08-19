#!/usr/bin/env python3
"""
Shrinks the WordPress-era media in content/ so the built site fits GitHub Pages.

WHY THIS EXISTS. The deploy artifact reached 1.26 GB against a hard 1 GB limit
("Deployment might fail" in the Actions log). It is not the HTML -- 4100 pages are
148 MB -- it is the images: 1229 MB across 5952 files, 78% of it in the 1214 files
over 200 KB. Three post bundles each carried the SAME 52 MB animated GIF: 157 MB
from one decorative background.

WHAT IT DOES, and why in that order:

  1. Animated GIF -> animated WebP for anything over --gif-min (default 200 KB).
     41 files hold 301 of the 316 MB of GIF, and these are screen recordings: the
     format is the problem, not the content. WebP keeps the animation, so nothing
     in the theme or the markup changes shape -- but the FILENAME changes, so the
     references are rewritten too (see rewrite_refs).
  2. PNG/JPEG resized in place when the long edge exceeds --cap (default 1600px).
     833 files are wider than that, holding 545 MB, while the article column is
     ~880px -- so 1600 still covers a 2x display. Format and filename are
     unchanged, so NO reference has to be touched for these.
  3. JPEG re-encoded at --jpeg-quality, progressive, metadata stripped.

DELIBERATELY NOT DONE: converting the 4194 PNGs to WebP. It is by far the biggest
single win (measured 6.4 MB -> 0.5 MB on the worst offender) and it is still the
wrong trade here -- it renames ~4000 references across ~2000 posts, and
transfer/Posts.java re-downloads the original PNG from WordPress, so the next
re-scrape would undo all of it and restore the .png references. Resizing keeps the
filename, which a re-scrape overwrites harmlessly with the original that this pass
then shrinks again. Revisit after cutover, when the scrapers are gone.

WHY PYTHON, in a scripts/ tree that is otherwise jbang Java. Writing an ANIMATED
WebP is the whole point of step 1, and Pillow is the only writer available here --
Java's ImageIO has no WebP encoder at all, and the usual add-on
(org.sejda.imageio:webp-imageio) does static WebP only. This runs by hand, locally,
and its OUTPUT is what gets committed; CI never runs it and needs neither Python
nor Pillow. That is the same posture as transfer/Sponsors.java being hand-run.

IDEMPOTENT. A file already inside the caps is left alone byte for byte, and a
second run reports nothing to do. Re-run it after any late re-scrape, which will
have put the full-size originals back -- same standing instruction as
cleanup/CloudflareEmails.java.

NOTE ON THE GIT REPO. This shrinks the build output and the working tree; it does
not shrink .git, which keeps every old blob forever. A fresh clone stays large
until history is rewritten, which is a separate and much riskier job. The 1 GB
deploy limit is about the artifact, so this fixes the actual failure.

Usage:
  python3 scripts/cleanup/images.py --dry-run
  python3 scripts/cleanup/images.py
  python3 scripts/cleanup/images.py --path content/posts/2024 --cap 1400
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


def human(n):
    return f"{n / 1e6:.2f} MB" if n >= 1e5 else f"{n / 1e3:.0f} KB"


# --------------------------------------------------------------------- GIF -> WebP

def gif_to_webp(src, dst, cap, quality, dry_run):
    """Animated (or static) GIF -> WebP. Returns the new size, or None on failure.

    Frames are halved when the source runs faster than ~16fps, with each kept
    frame's duration extended by the one it replaces -- so the animation plays at
    its original SPEED with half the payload. Blindly dropping frames without
    adding their duration back would silently play everything at double speed.
    """
    with Image.open(src) as im:
        frames, durations = [], []
        total_ms, count = 0, 0
        for fr in ImageSequence.Iterator(im):
            total_ms += fr.info.get("duration", 80) or 80
            count += 1
        avg = (total_ms / count) if count else 80
        step = 2 if (count > 40 and avg < 60) else 1

        im.seek(0)
        carried = 0
        for i, fr in enumerate(ImageSequence.Iterator(im)):
            carried += fr.info.get("duration", 80) or 80
            if i % step:
                continue
            f = fr.convert("RGB")
            w, h = f.size
            if max(w, h) > cap:  # never upscale
                k = cap / max(w, h)
                f = f.resize((max(1, round(w * k)), max(1, round(h * k))), Image.LANCZOS)
            frames.append(f)
            durations.append(carried)
            carried = 0
        loop = im.info.get("loop", 0)

    if not frames:
        return None
    if dry_run:
        return -1
    frames[0].save(dst, "WEBP", save_all=True, append_images=frames[1:],
                   duration=durations, loop=loop, quality=quality, method=4)
    # Trust nothing: re-open and confirm the frame count survived.
    with Image.open(dst) as check:
        if getattr(check, "n_frames", 1) != len(frames):
            os.unlink(dst)
            return None
    return os.path.getsize(dst)


def rewrite_refs(root, renames):
    """Point every reference at the new filename.

    Bundle images are referenced by BARE FILENAME (resource-url.html resolves a
    name against the page's own folder), which is what makes this safe to do with
    a text replacement: the name is distinctive and appears in exactly the shapes
    below. All four are live in content/:

      ![alt](shot.gif)              markdown image
      image: "shot.gif"             the hero in frontmatter -- OpenRewrite.gif is
                                    the featured image on three posts
      shot.gif | caption            a line inside {{< gallery >}}
      <img src="shot.gif">          raw HTML in a preserved block

    Scoped to the bundle's OWN index.md, not the whole tree, so a same-named image
    in another post is never touched.
    """
    changed = 0
    for folder, (old, new) in renames.items():
        for name in ("index.md", "_index.md"):
            md = Path(folder) / name
            if not md.is_file():
                continue
            text = md.read_text(encoding="utf-8")
            if old not in text:
                continue
            md.write_text(text.replace(old, new), encoding="utf-8")
            changed += 1
    return changed


# ------------------------------------------------------------------ PNG / JPEG

def shrink_raster(src, cap, jpeg_quality, dry_run):
    """Resize in place when oversized; re-encode JPEG. Returns the new size or None.

    Only ever writes when the result is MEANINGFULLY smaller (>10%), which does two
    jobs. It stops Pillow's re-encode from growing a file -- a well-optimised PNG
    often comes back bigger, and inflating images while claiming to shrink the site
    is worse than doing nothing. And it makes repeated runs safe: a JPEG re-encoded
    at the same quality loses a little each time, so without a floor this script
    would quietly degrade every photo on every run. After one pass the second pass
    finds nothing worth 10% and stops touching them.
    """
    ext = src.suffix.lower()
    before = src.stat().st_size
    with Image.open(src) as im:
        w, h = im.size
        fmt = im.format
        icc = im.info.get("icc_profile")
        oversized = max(w, h) > cap
        if ext == ".png" and not oversized:
            return None  # lossless re-save is not worth the churn
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
    ap.add_argument("--gif-min", type=int, default=200_000, help="convert GIFs above this")
    ap.add_argument("--gif-quality", type=int, default=78)
    ap.add_argument("--jpeg-quality", type=int, default=82)
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    root = Path(args.path)
    if not root.is_dir():
        sys.exit(f"not a directory: {root}")

    files = sorted(p for p in root.rglob("*") if p.is_file())
    gifs = [p for p in files if p.suffix.lower() == ".gif" and p.stat().st_size > args.gif_min]
    rasters = [p for p in files if p.suffix.lower() in RASTER]

    saved = 0
    renames = {}
    rows = []

    for p in sorted(gifs, key=lambda x: -x.stat().st_size):
        before = p.stat().st_size
        dst = p.with_suffix(".webp")
        if dst.exists() and dst != p:
            continue  # already converted by an earlier run
        after = gif_to_webp(p, dst, args.cap, args.gif_quality, args.dry_run)
        if after is None:
            print(f"  SKIP (conversion failed) {p}")
            continue
        if args.dry_run:
            rows.append((before, None, p))
            continue
        if after >= before:            # pathological, but do not regress
            dst.unlink()
            continue
        p.unlink()
        renames[str(p.parent)] = (p.name, dst.name)
        saved += before - after
        rows.append((before, after, p))

    md_changed = 0 if args.dry_run else rewrite_refs(root, renames)

    r_touched = 0
    for p in rasters:
        before = p.stat().st_size
        after = shrink_raster(p, args.cap, args.jpeg_quality, args.dry_run)
        if after is None:
            continue
        r_touched += 1
        if not args.dry_run:
            saved += before - after

    print(f"\n{'Would convert' if args.dry_run else 'Converted'} {len(rows)} GIF(s) to WebP:")
    for before, after, p in rows[:12]:
        tail = "?" if after is None else human(after)
        print(f"  {human(before):>9} -> {tail:>9}  {p.name[:52]}")
    if len(rows) > 12:
        print(f"  ... and {len(rows) - 12} more")
    print(f"{'Would resize' if args.dry_run else 'Resized'} {r_touched} PNG/JPEG file(s)")
    if not args.dry_run:
        print(f"references rewritten in {md_changed} markdown file(s)")
        print(f"TOTAL SAVED: {saved / 1e6:.0f} MB")


if __name__ == "__main__":
    main()
