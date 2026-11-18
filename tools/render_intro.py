"""Renders the cold-start intro to app/src/main/res/raw/convx_intro.mp4.

Needs: pip install skia-python imageio-ffmpeg
The reveal hole is not baked in: ConvxIntro.kt clips the app over the last frames.
Keep BEATS and TOTAL_MS in step with ConvxIntro.kt.
"""
import math
import re
import subprocess
import sys
from pathlib import Path

import imageio_ffmpeg
import skia

ROOT = Path(__file__).resolve().parent.parent
LOGO = ROOT / "app/src/main/res/drawable/convx_logo.xml"
OUT = ROOT / "app/src/main/res/raw/convx_intro.mp4"

W, H, FPS = 1080, 2340, 60
DENSITY = W / 411.4
TOTAL_MS, ZOOM_START, ZOOM_END = 1700.0, 1000.0, 1480.0
RIPPLE_MS, PULSE_MS = 600.0, 220.0
BEATS = (600.0, 850.0)
VIEWPORT = 24.0


def phase(t, a, b):
    return min(max((t - a) / (b - a), 0.0), 1.0)


def out_cubic(x):
    return 1 - (1 - x) ** 3


def in_cubic(x):
    return x ** 3


def out_back(x):
    c1 = 1.70158
    c3 = c1 + 1
    return 1 + c3 * (x - 1) ** 3 + c1 * (x - 1) ** 2


def pulse(dt):
    return math.sin(math.pi * dt / PULSE_MS) if 0 <= dt <= PULSE_MS else 0.0


def logo_path():
    data = re.search(r'android:pathData="([^"]+)"', LOGO.read_text()).group(1)
    path = skia.Path()
    # The monogram is flattened to M/L/Z only.
    for cmd, args in re.findall(r"([MLZ])([^MLZ]*)", data):
        if cmd == "Z":
            path.close()
            continue
        x, y = map(float, args.split(","))
        (path.moveTo if cmd == "M" else path.lineTo)(x, y)
    path.setFillType(skia.PathFillType.kEvenOdd)
    return path


def paint(color, alpha=1.0, stroke=None):
    p = skia.Paint(AntiAlias=True, Color=color)
    p.setAlphaf(alpha)
    if stroke:
        p.setStyle(skia.Paint.kStroke_Style)
        p.setStrokeWidth(stroke)
    return p


def draw(canvas, logo, t):
    cx, cy = W / 2, H / 2
    r0 = 52 * DENSITY
    cover = math.hypot(W, H) / 2 * 1.05
    canvas.clear(skia.ColorBLACK)
    for beat in BEATS:
        x = (t - beat) / RIPPLE_MS
        if 0 <= x <= 1:
            canvas.drawCircle(
                cx, cy, r0 * (1 + 2.4 * out_cubic(x)),
                paint(skia.ColorWHITE, 0.45 * (1 - x), 2 * DENSITY * (1 - 0.6 * x)),
            )
    beat = sum(pulse(t - b) for b in BEATS)
    zoom = in_cubic(phase(t, ZOOM_START, ZOOM_END))
    disc_in = out_back(phase(t, 0, 450))
    disc_r = r0 * disc_in * (1 + 0.09 * beat) + (cover - r0) * zoom
    canvas.drawCircle(cx, cy, max(disc_r, 0), paint(skia.ColorWHITE))
    glyph_in = phase(t, 100, 550)
    glyph = r0 * 2 * 0.64 * out_back(glyph_in) * (1 + 0.09 * beat) * (1 + 16 * zoom ** 1.6)
    spin = -140 * (1 - out_cubic(glyph_in)) + 35 * zoom
    if glyph > 0:
        canvas.save()
        canvas.rotate(spin, cx, cy)
        canvas.translate(cx - glyph / 2, cy - glyph / 2)
        canvas.scale(glyph / VIEWPORT, glyph / VIEWPORT)
        canvas.drawPath(logo, paint(skia.ColorBLACK))
        canvas.restore()


def main():
    logo = logo_path()
    surface = skia.Surface(W, H)
    OUT.parent.mkdir(parents=True, exist_ok=True)
    ffmpeg = subprocess.Popen(
        [
            imageio_ffmpeg.get_ffmpeg_exe(), "-y", "-loglevel", "error",
            "-f", "rawvideo", "-pix_fmt", "rgba", "-s", f"{W}x{H}", "-r", str(FPS), "-i", "-",
            "-an", "-c:v", "libx264", "-preset", "veryslow", "-crf", "14", "-g", "15",
            "-pix_fmt", "yuv420p", "-profile:v", "high", "-level", "5.1",
            "-colorspace", "bt709", "-color_primaries", "bt709", "-color_trc", "bt709",
            "-movflags", "+faststart", str(OUT),
        ],
        stdin=subprocess.PIPE,
    )
    frames = round(TOTAL_MS / 1000 * FPS) + 1
    with surface as canvas:
        for i in range(frames):
            draw(canvas, logo, min(i * 1000 / FPS, TOTAL_MS))
            ffmpeg.stdin.write(surface.makeImageSnapshot().tobytes())
    ffmpeg.stdin.close()
    if ffmpeg.wait() != 0:
        sys.exit("ffmpeg failed")
    print(f"{OUT} {OUT.stat().st_size / 1024:.0f} KB, {frames} frames")


if __name__ == "__main__":
    main()
