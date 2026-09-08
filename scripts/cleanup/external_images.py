#!/usr/bin/env python3
"""Pull every EXTERNAL image a post references into that post's own bundle.

Run by hand, like cleanup/images.py, and for the same reason: it talks to ~100
third-party hosts and needs Pillow.

  python3 scripts/cleanup/external_images.py --dry-run
  python3 scripts/cleanup/external_images.py --path content/posts/2024/01/05/...
  python3 scripts/cleanup/external_images.py --path content/posts --limit 20

WHY. 393 posts referenced 1710 images on 115 hosts they do not control, and 129
of those across 46 posts are ALREADY DEAD -- a broken glyph mid-article on the
live site today. Hotlinking also spends a stranger's bandwidth on every page
view, leaks each reader's IP to whoever owns the host, and puts 455 MB of
pictures outside anything this repo can measure or shrink.

The durable half of the fix is in shared/HtmlToMarkdown.localizeImages, which
now localizes any host, so a re-scrape produces local files. This is the one-off
sweep for what is already in content/, and the two agree ON THE FILENAME by
construction (see local_name): a file this script wrote is found by the
scraper's own extension-blind stem lookup and never downloaded again.

FOUR THINGS THIS DOES ON PURPOSE.

1. IT SHRINKS BEFORE THE FILE EVER ENTERS THE REPO. The originals average
   330 KB and reach 15.5 MB; 455 MB of them would put the built site past
   GitHub Pages' 1 GB artifact limit, whose warning arrives on a run that is
   otherwise GREEN. Downloading the original into content/ and shrinking it
   afterwards would also leave the full-size blob in git history for ever, so
   the download lands in a temp directory, is re-encoded there with
   cleanup/images.py's own measured rules, and only the result is moved in.

2. IT VERIFIES THE BYTES ARE AN IMAGE. A dead third-party URL rarely 404s
   cleanly: it serves a login wall, a "not found" page or a placeholder, all of
   them HTML with a 200. Writing that as foo.png would turn a visibly broken
   image into a file that is broken for ever and looks localized.

3. IT FINISHES EACH BUNDLE BEFORE STARTING THE NEXT. images.py learned this the
   expensive way -- it converted every GIF and rewrote references at the END, a
   commit landed mid-run, and four images broke on the live site.

4. A FAILURE LEAVES THE REFERENCE ALONE AND IS REPORTED. A 403 is a bot wall,
   not proof of deletion (Medium and Oracle answer a script with 403/410 and a
   browser with 200), so those stay hotlinked and get named at the end rather
   than being rewritten to a file that is not there.
"""

import argparse
import hashlib
import re
import shutil
import sys
import tempfile
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
try:
    import images as imagelib          # cleanup/images.py -- the shrink rules
    from PIL import Image
except ImportError as e:
    sys.exit(f"needs Pillow and cleanup/images.py next to this script: {e}")

# Identify ourselves rather than pose as a browser -- the rule fetch/JugEvents.java
# follows. A browser UA is still needed for the hosts behind a WAF that 403s a
# bare Python one, so it is the FALLBACK and not the first thing tried.
UA = "foojay-website-image-localizer/1.0 (+https://github.com/foojayio/website)"
UA_BROWSER = ("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
              "(KHTML, like Gecko) Chrome/124.0 Safari/537.36")

# Every shape an image reference takes in content/, all four live today.
REF_PATTERNS = [
    re.compile(r'!\[[^\]]*\]\(\s*(https?://[^)\s]+)'),          # ![alt](url)
    re.compile(r'\{\{<\s*img[^>]*?src="(https?://[^"]+)"'),      # {{< img src="url" >}}
    re.compile(r'<img[^>]+src="(https?://[^"]+)"', re.I),        # raw <img> in a preserved block
    re.compile(r'^image:\s*"?(https?://[^"\s]+)', re.M),         # the frontmatter hero
]
URL_EXT = re.compile(r'\.(jpe?g|png|gif|webp|svg|avif|bmp)$', re.I)
EXT_FOR_TYPE = {
    "image/jpeg": ".jpg", "image/jpg": ".jpg", "image/png": ".png",
    "image/gif": ".gif", "image/webp": ".webp", "image/avif": ".avif",
    "image/svg+xml": ".svg", "image/bmp": ".bmp",
}
# A live SERVICE rather than a picture: a GitHub Actions badge reports whether a
# build passes RIGHT NOW, so a frozen copy asserts a stale CI result for ever.
SKIP = re.compile(r'(?i)^https?://(?:[^/]+\.)?github\.com/[^?#]*/badge\.svg(?:[?#]|$)')
# WordPress replaced an emoji CHARACTER with an <img>, and the alt is that
# character -- so the original is recoverable exactly. Restored as text instead
# of downloading 23 emoji SVGs into eight bundles. Same pass as
# HtmlToMarkdown.restoreWordPressEmoji, so a re-scrape agrees.
EMOJI_IMG = re.compile(r'!\[([^\]]+)\]\(https?://s\.w\.org/images/core/emoji/[^)]+\)')


def short_hash(url):
    return hashlib.sha256(url.encode("utf-8")).hexdigest()[:8]


def local_name(url, content_type):
    """The filename to store `url` under -- BYTE-IDENTICAL to what
    HtmlToMarkdown.externalStem + its extension choice produce, which is what
    keeps a re-scrape from downloading the same image a second time.

    The URL hash is not decoration. Third-party basenames collide constantly:
    58 bundles in content/ reference an external URL whose basename is already
    the name of a file sitting in that bundle (`lambda.gif`, `layers.png`,
    `banner-1.png`). Without the hash the reference would silently be repointed
    at a picture from a different source, which nothing downstream would report.
    """
    path = urllib.parse.urlparse(url).path
    name = path[path.rfind("/") + 1:]
    ext = None
    m = URL_EXT.search(name)
    if m:
        ext = m.group(0).lower()
        name = name[: -len(ext)]
    if ext == ".jpeg":
        ext = ".jpeg"          # keep what the URL said; images.py treats both
    name = re.sub(r"[^A-Za-z0-9._-]", "-", name)
    name = re.sub(r"-{2,}", "-", name).strip("-.")
    if len(name) > 40:
        name = name[:40].rstrip("-.")
    if not name or not any(c.isalpha() for c in name):
        host = urllib.parse.urlparse(url).netloc.lower()
        host = host[4:] if host.startswith("www.") else host
        name = re.sub(r"[^a-z0-9]+", "-", host)
    if ext is None:
        base = (content_type or "").split(";")[0].strip().lower()
        ext = EXT_FOR_TYPE.get(base)
        if ext is None:
            return None
    return f"{name}-{short_hash(url)}{ext}"


def fetch(url, timeout):
    """(bytes, content_type, None) on success, (None, None, reason) otherwise.

    Tries our own UA and then a browser one, because a 403 says something about
    the CLIENT and not about the file -- several of these hosts sit behind a WAF
    that rejects a bare Python agent. Only the status codes that mean "not you"
    are retried; a 404 is answered once.
    """
    last = None
    for ua in (UA, UA_BROWSER):
        req = urllib.request.Request(url, headers={"User-Agent": ua, "Accept": "image/*,*/*"})
        try:
            with urllib.request.urlopen(req, timeout=timeout) as r:
                return r.read(), r.headers.get("Content-Type", ""), None
        except urllib.error.HTTPError as e:
            last = f"HTTP {e.code}"
            if e.code not in (401, 403, 405, 406, 429):
                break
        except Exception as e:                     # timeout, DNS, TLS, reset
            last = type(e).__name__
            break
    return None, None, last


def existing_for(folder, name):
    """A file already in this bundle for the same URL, whatever extension it now
    carries -- images.py may have re-encoded .png to .jpg. Extension-blind on the
    hashed stem, which is exactly HtmlToMarkdown.findByStem."""
    stem = name[: name.rfind(".")]
    for p in sorted(folder.glob(stem + ".*")):
        return p.name
    return None


def shrink(tmp_path, args):
    """images.py's own rules, applied in the temp directory so an oversized
    original never enters the repo. Returns the path that survived.

    THE GIF BRANCH IS WHERE THE WEIGHT IS. Of the 166 MB the first 20 posts
    pulled in, 120 MB was 39 screen-recording GIFs -- one of them 15.5 MB -- and
    a GIF is the one format neither of the raster passes touches. Converting it
    here rather than after the move is what keeps the original out of git
    history, and it is the expensive step: minutes per large GIF, which is why a
    batch of these is a background job and not something to wait on.

    convert_gif() rewrites references and unlinks the source itself; in a temp
    directory both are no-ops, and the caller repoints the URL at whatever
    filename comes back."""
    ext = tmp_path.suffix.lower()
    if ext == ".gif" and tmp_path.stat().st_size > args.gif_min:
        if imagelib.convert_gif(tmp_path, args.budget, False) is not None:
            webp = tmp_path.with_suffix(".webp")
            if webp.is_file():
                return webp
    if ext == ".png" and tmp_path.stat().st_size > args.png_min:
        r = imagelib.png_to_jpeg(tmp_path, args.png_jpeg_quality, args.cap, False)
        if r is not None:
            return tmp_path.with_suffix(".jpg")
    if tmp_path.suffix.lower() in imagelib.RASTER:
        imagelib.shrink_raster(tmp_path, args.cap, args.jpeg_quality, False)
    return tmp_path


def index_files(root):
    if root.is_file() and root.name in imagelib.INDEX_NAMES:
        return [root]
    return sorted(p for p in root.rglob("*") if p.name in imagelib.INDEX_NAMES)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--path", action="append", default=None,
                    help="content dir, post bundle or index.md (repeatable)")
    ap.add_argument("--limit", type=int, default=0, help="max bundles to touch")
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--timeout", type=int, default=30)
    ap.add_argument("--cap", type=int, default=1600, help="max long edge in px")
    ap.add_argument("--jpeg-quality", type=int, default=82)
    ap.add_argument("--png-min", type=int, default=300_000)
    ap.add_argument("--png-jpeg-quality", type=int, default=85)
    ap.add_argument("--gif-min", type=int, default=200_000,
                    help="convert animated GIFs above this to WebP")
    ap.add_argument("--budget", type=int, default=3_000_000,
                    help="per-file target for the GIF ladder; matches images.py")
    args = ap.parse_args()

    roots = [Path(p) for p in (args.path or ["content"])]
    for r in roots:
        if not r.exists():
            sys.exit(f"no such path: {r}")

    indexes = []
    for r in roots:
        indexes.extend(index_files(r))

    localized = dead = skipped = emoji_restored = 0
    downloaded = stored = 0
    touched_bundles = 0
    failures = []

    for md in indexes:
        text = md.read_text(encoding="utf-8")
        urls = []
        for pat in REF_PATTERNS:
            for m in pat.finditer(text):
                if m.group(1) not in urls:
                    urls.append(m.group(1))
        emoji_here = EMOJI_IMG.findall(text)
        if not urls and not emoji_here:
            continue
        if args.limit and touched_bundles >= args.limit:
            break
        touched_bundles += 1
        folder = md.parent
        print(f"\n{md}")

        # WordPress emoji images -> the character itself. Before the downloads,
        # so those URLs are gone from the list rather than fetched.
        if emoji_here:
            new = EMOJI_IMG.sub(lambda m: m.group(1), text)
            if new != text:
                emoji_restored += len(emoji_here)
                print(f"  restored {len(emoji_here)} WordPress emoji image(s) to the character")
                text = new
                urls = [u for u in urls if "s.w.org/images/core/emoji/" not in u]

        for url in urls:
            if SKIP.search(url):
                skipped += 1
                print(f"  live badge, left hotlinked: {url}")
                continue

            # Already localized on an earlier run? Cheap, and it makes the whole
            # script safe to re-run: no request, no rewrite of an unchanged file.
            probe = local_name(url, None)
            if probe:
                have = existing_for(folder, probe)
                if have:
                    text = text.replace(url, have)
                    print(f"  have  {have}")
                    continue

            body, ctype, why = fetch(url, args.timeout)
            if body is None:
                dead += 1
                failures.append((md, url, why))
                print(f"  DEAD  {why:<12} {url}")
                continue

            name = local_name(url, ctype)
            if name is None:
                skipped += 1
                failures.append((md, url, f"unknown type {ctype!r}"))
                print(f"  SKIP  unknown image type {ctype!r}: {url}")
                continue

            with tempfile.TemporaryDirectory() as td:
                tmp = Path(td) / name
                tmp.write_bytes(body)
                try:
                    with Image.open(tmp) as im:
                        im.verify()
                except Exception:
                    if tmp.suffix.lower() != ".svg":
                        skipped += 1
                        failures.append((md, url, "not a decodable image"))
                        print(f"  SKIP  not an image ({len(body)} bytes): {url}")
                        continue
                before = tmp.stat().st_size
                final = tmp if args.dry_run else shrink(tmp, args)
                after = final.stat().st_size
                downloaded += before
                stored += after
                if args.dry_run:
                    print(f"  would localize {url} -> {final.name} "
                          f"({before/1024:.0f} KB)")
                else:
                    shutil.move(str(final), folder / final.name)
                    print(f"  {before/1024:>7.0f} KB -> {after/1024:>7.0f} KB  {final.name}")
                text = text.replace(url, final.name)
                localized += 1

        if not args.dry_run and text != md.read_text(encoding="utf-8"):
            md.write_text(text, encoding="utf-8")

    print(f"\n{touched_bundles} bundle(s) touched")
    print(f"  localized      {localized}")
    print(f"  emoji restored {emoji_restored}")
    print(f"  left alone     {skipped} (live badges / not an image)")
    print(f"  unreachable    {dead}")
    if downloaded:
        print(f"  downloaded {downloaded/1e6:.1f} MB -> stored {stored/1e6:.1f} MB"
              f"  ({100 - stored*100/downloaded:.0f}% smaller)")
    if failures:
        print("\nleft hotlinked, needs a human (a 403 is a bot wall, not proof the file is gone):")
        for md, url, why in failures:
            print(f"  {why:<22} {url}\n      in {md}")


if __name__ == "__main__":
    main()
