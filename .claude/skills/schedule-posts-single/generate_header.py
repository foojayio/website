#!/usr/bin/env python3
"""Give a post without a header image one (1600x900 JPEG, header.jpg).

Usage: generate_header.py --path content/posts/Y/M/D/SLUG [--no-reuse] [--force]

Reuses the best raster already in the bundle when one is wide enough and close
to 16:9, otherwise renders a plain branded card from the title and first
category. Adds `image: "header.jpg"` to the frontmatter when it has none.
Deterministic: the same slug gives the same card.
"""
import argparse, hashlib, random, re, sys
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageOps

W, H = 1600, 900
NAVY, BLUE, ACCENT = (55, 71, 79), (7, 111, 160), (80, 194, 247)
FONTS = ["/System/Library/Fonts/Supplemental/Arial Bold.ttf",
         "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"]
RASTERS = {".jpg", ".jpeg", ".png"}
MIN_WIDTH, ASPECT = 1000, (1.4, 2.2)  # a crop to 16:9 outside this loses too much


def font(size):
    for f in FONTS:
        if Path(f).exists():
            return ImageFont.truetype(f, size)
    return ImageFont.load_default()


def field(fm, key):
    m = re.search(rf'^{key}:\s*"?(.*?)"?\s*$', fm, re.M)
    return m.group(1) if m else ""


def first_category(fm):
    m = re.search(r'^categories:\s*(?:\[\s*"([^"]+)"|\n\s*-\s*"?([^"\n]+))', fm, re.M)
    return (m.group(1) or m.group(2)).strip() if m else ""


def reusable(bundle, body):
    """Largest qualifying raster the body references, or None."""
    best = None
    for p in bundle.iterdir():
        if p.suffix.lower() not in RASTERS or p.name == "header.jpg" or p.name not in body:
            continue
        with Image.open(p) as im:
            w, h = im.size
        if w >= MIN_WIDTH and ASPECT[0] <= w / h <= ASPECT[1] and (best is None or w * h > best[0]):
            best = (w * h, p)
    return best[1] if best else None


def wrap(draw, text, fnt, width):
    lines, line = [], ""
    for word in text.split():
        trial = f"{line} {word}".strip()
        if draw.textlength(trial, font=fnt) <= width:
            line = trial
        else:
            lines.append(line)
            line = word
    return lines + [line]


def card(title, category, seed):
    rnd = random.Random(int(hashlib.sha1(seed.encode()).hexdigest(), 16))
    img = Image.new("RGB", (W, H))
    px = ImageDraw.Draw(img)
    for y in range(H):
        t = y / H
        px.line([(0, y), (W, y)], fill=tuple(int(NAVY[i] + (BLUE[i] - NAVY[i]) * t) for i in range(3)))
    over = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    od = ImageDraw.Draw(over)
    for _ in range(5):
        r = rnd.randint(120, 320)
        x, y = rnd.randint(W // 2, W), rnd.randint(0, H)
        od.ellipse([x - r, y - r, x + r, y + r], fill=(*ACCENT, rnd.randint(18, 45)))
    img = Image.alpha_composite(img.convert("RGBA"), over).convert("RGB")
    d = ImageDraw.Draw(img)

    d.text((100, 90), "foojay.io", font=font(54), fill=ACCENT)
    if category:
        f = font(40)
        w = d.textlength(category, font=f)
        d.rounded_rectangle([100, 190, 100 + w + 60, 262], radius=36, outline=ACCENT, width=3)
        d.text((130, 200), category, font=f, fill="white")

    size = 96
    while True:
        f = font(size)
        lines = wrap(d, title, f, W - 260)
        if len(lines) * size * 1.2 <= 460 or size <= 56:
            break
        size -= 4
    y = 330
    for line in lines:
        d.text((100, y), line, font=f, fill="white")
        y += int(size * 1.2)
    return img


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--path", required=True)
    ap.add_argument("--no-reuse", action="store_true", help="always render the card")
    ap.add_argument("--force", action="store_true", help="overwrite an existing header.jpg")
    a = ap.parse_args()
    bundle = Path(a.path)
    index = next((bundle / n for n in ("index.md", "index.adoc") if (bundle / n).exists()), None)
    if not index:
        sys.exit(f"no index.md or index.adoc in {bundle}")
    text = index.read_text()
    m = re.match(r"---\n(.*?)\n---\n", text, re.S)
    if not m:
        sys.exit("no frontmatter")
    fm, body = m.group(1), text[m.end():]
    out = bundle / "header.jpg"
    if out.exists() and not a.force:
        sys.exit(f"{out} exists; use --force")

    src = None if a.no_reuse else reusable(bundle, body)
    if src:
        with Image.open(src) as im:
            img = ImageOps.fit(im.convert("RGB"), (W, H), Image.LANCZOS)
        how = f"reused {src.name}"
    else:
        img = card(field(fm, "title"), first_category(fm), bundle.name)
        how = "rendered card"
    img.save(out, "JPEG", quality=85, optimize=True)

    if not re.search(r"^image:\s*\S", fm, re.M):
        index.write_text(f'---\n{fm}\nimage: "header.jpg"\n---\n{body}')
    print(f"{how} -> {out} ({out.stat().st_size // 1024} KB)")


if __name__ == "__main__":
    main()
