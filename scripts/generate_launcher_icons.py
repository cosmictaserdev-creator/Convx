#!/usr/bin/env python3
"""Build the Convxy launcher set from app_icons/Convxy.png.

Cleans JPEG-style noise in the near-black field, then writes:

* full-bleed black-square masters (Play Store, docs, legacy mipmap)
* adaptive-icon foreground / monochrome with the C inside the 72dp safe zone
* tinted foregrounds for the in-app colour picker
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageOps

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "app_icons" / "Convxy.png"
RES = ROOT / "app" / "src" / "main" / "res"
DOCS = ROOT / "docs"

# Adaptive-icon canvas is 108dp; launchers only reveal the inner ~72dp.
# 0.62 leaves a little extra so the C's open side is never clipped.
SAFE_FILL = 0.62

DENSITIES = {
    "mdpi": 1,
    "hdpi": 1.5,
    "xhdpi": 2,
    "xxhdpi": 3,
    "xxxhdpi": 4,
}

# Colour-picker tints. Each is composited over the shared #1C1C1E tile.
VARIANT_COLORS = {
    "white": (245, 245, 247, 255),
    "azure": (90, 200, 250, 255),
    "sky": (100, 210, 255, 255),
    "teal": (48, 176, 199, 255),
    "slate": (142, 142, 147, 255),
    "silver": (199, 199, 204, 255),
    "periwinkle": (155, 140, 255, 255),
    "violet": (191, 90, 242, 255),
    "midnight": (94, 92, 230, 255),
    "mauve": (218, 143, 255, 255),
    "rose": (255, 55, 95, 255),
    "copper": (212, 165, 116, 255),
    "rust": (255, 107, 74, 255),
}


def clean(src: Path) -> Image.Image:
    img = Image.open(src).convert("RGBA")
    px = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            luma = 0.2126 * r + 0.7152 * g + 0.0722 * b
            chroma = max(r, g, b) - min(r, g, b)
            if luma < 26 or (luma < 46 and chroma < 42):
                px[x, y] = (0, 0, 0, 255)
    return img


def punch_black(img: Image.Image) -> Image.Image:
    """Turn the near-black field into transparency so layers composite cleanly."""
    out = img.copy()
    px = out.load()
    w, h = out.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            luma = 0.2126 * r + 0.7152 * g + 0.0722 * b
            if luma < 18:
                px[x, y] = (0, 0, 0, 0)
    return out


def content_bbox(img: Image.Image) -> tuple[int, int, int, int]:
    alpha = img.split()[-1]
    bbox = alpha.getbbox()
    if bbox is None:
        return (0, 0) + img.size
    return bbox


def fit_on_canvas(
    mark: Image.Image,
    canvas_size: int,
    fill: float,
    background: tuple[int, int, int, int],
) -> Image.Image:
    cropped = mark.crop(content_bbox(mark))
    cw, ch = cropped.size
    target = max(1, int(canvas_size * fill))
    scale = target / max(cw, ch)
    nw, nh = max(1, int(cw * scale)), max(1, int(ch * scale))
    scaled = cropped.resize((nw, nh), Image.Resampling.LANCZOS)
    canvas = Image.new("RGBA", (canvas_size, canvas_size), background)
    canvas.paste(scaled, ((canvas_size - nw) // 2, (canvas_size - nh) // 2), scaled)
    return canvas


def resize(img: Image.Image, size: int) -> Image.Image:
    return img.resize((size, size), Image.Resampling.LANCZOS)


def monochrome(img: Image.Image) -> Image.Image:
    """White silhouette of the C on a transparent field (themed icons)."""
    gray = ImageOps.grayscale(img)
    alpha = img.split()[-1]
    # Anything that isn't the transparent field becomes white.
    mask = Image.new("L", img.size, 0)
    gp, ap, mp = gray.load(), alpha.load(), mask.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            if ap[x, y] > 18 and gp[x, y] > 18:
                mp[x, y] = 255
    out = Image.new("RGBA", img.size, (0, 0, 0, 0))
    white = Image.new("RGBA", img.size, (255, 255, 255, 255))
    out.paste(white, mask=mask)
    return out


def tint(mono: Image.Image, color: tuple[int, int, int, int]) -> Image.Image:
    out = Image.new("RGBA", mono.size, (0, 0, 0, 0))
    layer = Image.new("RGBA", mono.size, color)
    out.paste(layer, mask=mono.split()[-1])
    return out


def solid(color: tuple[int, int, int, int], size: int) -> Image.Image:
    return Image.new("RGBA", (size, size), color)


def save(img: Image.Image, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path, "PNG", optimize=True)
    print(f"  wrote {path.relative_to(ROOT)} ({img.size[0]}x{img.size[1]})")


def main() -> None:
    if not SRC.exists():
        raise SystemExit(f"missing source icon: {SRC}")
    print(f"source: {SRC}")
    cleaned = clean(SRC)
    punched = punch_black(cleaned)

    # Masters: the full colourful mark on true black (store / docs / splash).
    bleed = fit_on_canvas(punched, 1024, 0.86, (0, 0, 0, 255))
    save(bleed, ROOT / "app_icons" / "Convxy-1024.png")
    save(resize(bleed, 512), ROOT / "app" / "src" / "main" / "ic_launcher-playstore.png")
    save(resize(bleed, 512), RES / "drawable" / "icon.png")
    save(resize(bleed, 256), DOCS / "icon.png")

    # Adaptive layers at 432px = 108dp xxxhdpi. Colourful C, transparent field.
    fg_master = fit_on_canvas(punched, 432, SAFE_FILL, (0, 0, 0, 0))
    mono_master = monochrome(fg_master)
    save(fg_master, RES / "drawable" / "ic_launcher_foreground.png")
    save(mono_master, RES / "drawable" / "ic_launcher_monochrome.png")

    for name, scale in DENSITIES.items():
        folder = RES / f"mipmap-{name}"
        launcher = int(48 * scale)
        fg = int(108 * scale)
        save(resize(bleed, launcher), folder / "ic_launcher.png")
        save(resize(bleed, launcher), folder / "ic_launcher_round.png")
        save(resize(fg_master, fg), folder / "ic_launcher_foreground.png")
        save(solid((0, 0, 0, 255), fg), folder / "ic_launcher_background.png")
        save(resize(mono_master, fg), folder / "ic_launcher_monochrome.png")

    # Colour-picker foregrounds live at xxxhdpi; Android scales the rest.
    xxx = RES / "mipmap-xxxhdpi"
    for variant, color in VARIANT_COLORS.items():
        path = xxx / f"ic_launcher_{variant}_fg.webp"
        if path.exists():
            path.unlink()
        png = xxx / f"ic_launcher_{variant}_fg.png"
        save(tint(mono_master, color), png)


if __name__ == "__main__":
    main()
