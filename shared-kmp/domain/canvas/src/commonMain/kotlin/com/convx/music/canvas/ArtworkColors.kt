/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.canvas

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Apple Music artwork colour rules; colours are packed 0xRRGGBB ints. Apple's server-side
 * `bgColor` fed through [pageBackground] is the 99% path; [estimateArtworkBackground] is
 * the fallback for non-Apple artwork.
 */
object ArtworkColors {
    const val WHITE = 0xFFFFFF

    // Fitted on the 27 reference screenshots: worst channel error 2/255.
    private const val DARKEN_PER_LUMINANCE = 165.9
    private const val DARKEN_THRESHOLD = 0.1

    fun red(c: Int) = (c shr 16) and 0xFF
    fun green(c: Int) = (c shr 8) and 0xFF
    fun blue(c: Int) = c and 0xFF
    fun rgb(r: Int, g: Int, b: Int) =
        (r.coerceIn(0, 255) shl 16) or (g.coerceIn(0, 255) shl 8) or b.coerceIn(0, 255)


    private fun linear(v: Int): Double {
        val s = v / 255.0
        return if (s <= 0.04045) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
    }

    /** WCAG relative luminance. */
    fun luminance(c: Int) = 0.2126 * linear(red(c)) + 0.7152 * linear(green(c)) + 0.0722 * linear(blue(c))

    /** Flat page background below the hero: Apple's bgColor darkened by luminance. */
    fun pageBackground(artworkBg: Int): Int {
        val y = luminance(artworkBg)
        if (y <= DARKEN_THRESHOLD) return artworkBg and 0xFFFFFF
        val d = DARKEN_PER_LUMINANCE * y
        return rgb(
            (red(artworkBg) - d).roundToInt(),
            (green(artworkBg) - d).roundToInt(),
            (blue(artworkBg) - d).roundToInt(),
        )
    }

    /** Linear blend; Apple's textColor3/4 = mix(bgColor, textColor1/2, 0.8). */
    fun mix(from: Int, to: Int, t: Double) = rgb(
        (red(from) + (red(to) - red(from)) * t).roundToInt(),
        (green(from) + (green(to) - green(from)) * t).roundToInt(),
        (blue(from) + (blue(to) - blue(from)) * t).roundToInt(),
    )

    /** Play button fill — the artist's keyColor, white when the artist has none. */
    fun playFill(keyColor: Int?) = keyColor ?: WHITE

    /** Play glyph — page background 30% toward black, as measured on the play button. */
    fun playGlyph(pageBg: Int) = mix(pageBg, 0x000000, 0.3)

    /** White overlay alpha of translucent page controls; the featured card rises with luminance. */
    const val CONTROL_FILL_ALPHA = 0.08f
    fun cardFillAlpha(pageBg: Int): Float = (0.090 + 0.25 * luminance(pageBg)).toFloat().coerceIn(0.08f, 0.14f)

    fun accuracy(x: Int, y: Int): Double =
        1.0 - maxOf(kotlin.math.abs(red(x) - red(y)), kotlin.math.abs(green(x) - green(y)), kotlin.math.abs(blue(x) - blue(y))) / 255.0

    fun downscale(pixels: IntArray, width: Int, height: Int, size: Int): IntArray {
        val cells = size * size
        val n = IntArray(cells)
        val sr = IntArray(cells)
        val sg = IntArray(cells)
        val sb = IntArray(cells)
        for (y in 0 until height) {
            val rowCell = y * size / height * size
            val row = y * width
            for (x in 0 until width) {
                val t = rowCell + x * size / width
                val c = pixels[row + x]
                n[t]++; sr[t] += red(c); sg[t] += green(c); sb[t] += blue(c)
            }
        }
        return IntArray(cells) { i ->
            val k = max(1, n[i])
            rgb((sr[i] + k / 2) / k, (sg[i] + k / 2) / k, (sb[i] + k / 2) / k)
        }
    }

    /**
     * On-device estimate of Apple's server-side bgColor: edge-weighted 32-level bucket
     * vote over the top quarter, black bucket skipped. Only a fallback for non-Apple artwork.
     */
    fun estimateArtworkBackground(pixels: IntArray, width: Int, height: Int): Int {
        val levels = 32
        val shift = 3
        val buckets = levels * levels * levels
        val mass = DoubleArray(buckets)
        val sr = DoubleArray(buckets)
        val sg = DoubleArray(buckets)
        val sb = DoubleArray(buckets)
        var total = 0.0
        var tr = 0.0
        var tg = 0.0
        var tb = 0.0
        val rows = max(1, height / 4)
        val hw = width / 2.0
        val hh = height / 2.0
        for (y in 0 until rows) {
            val ny = (y - hh) / hh
            val row = y * width
            for (x in 0 until width) {
                val c = pixels[row + x]
                val r = red(c)
                val g = green(c)
                val b = blue(c)
                val nx = (x - hw) / hw
                val w = 1.0 + nx * nx + ny * ny
                total += w; tr += w * r; tg += w * g; tb += w * b
                val k = ((r shr shift) * levels + (g shr shift)) * levels + (b shr shift)
                if (k == 0) continue
                mass[k] += w; sr[k] += w * r; sg[k] += w * g; sb[k] += w * b
            }
        }
        var best = -1
        for (k in 1 until buckets) if (best < 0 || mass[k] > mass[best]) best = k
        if (best < 0 || mass[best] <= 0.0) {
            val m = max(1.0, total)
            return rgb((tr / m).roundToInt(), (tg / m).roundToInt(), (tb / m).roundToInt())
        }
        val m = mass[best]
        return rgb((sr[best] / m).roundToInt(), (sg[best] / m).roundToInt(), (sb[best] / m).roundToInt())
    }

    /** Single-pass fast path: bit-identical to [estimateArtworkBackground] with a 64×64 grid, no intermediate. */
    fun estimateDirect(pixels: IntArray, width: Int, height: Int, grid: Int = 64): Int {
        val qRows = max(1, grid / 4)
        val cells = qRows * grid
        val n = IntArray(cells)
        val cr = IntArray(cells)
        val cg = IntArray(cells)
        val cb = IntArray(cells)
        val srcRows = ((qRows * height + grid - 1) / grid).coerceAtMost(height)
        for (y in 0 until srcRows) {
            val base = y * grid / height * grid
            if (base >= cells) continue
            val row = y * width
            for (x in 0 until width) {
                val t = base + x * grid / width
                val c = pixels[row + x]
                n[t]++; cr[t] += red(c); cg[t] += green(c); cb[t] += blue(c)
            }
        }
        val levels = 32
        val shift = 3
        val buckets = levels * levels * levels
        val mass = DoubleArray(buckets)
        val sr = DoubleArray(buckets)
        val sg = DoubleArray(buckets)
        val sb = DoubleArray(buckets)
        var total = 0.0
        var tr = 0.0
        var tg = 0.0
        var tb = 0.0
        val half = grid / 2.0
        for (t in 0 until cells) {
            val k2 = max(1, n[t])
            val r = (cr[t] + k2 / 2) / k2
            val g = (cg[t] + k2 / 2) / k2
            val b = (cb[t] + k2 / 2) / k2
            val nx = (t % grid - half) / half
            val ny = (t / grid - half) / half
            val w = 1.0 + nx * nx + ny * ny
            total += w; tr += w * r; tg += w * g; tb += w * b
            val k = ((r shr shift) * levels + (g shr shift)) * levels + (b shr shift)
            if (k == 0) continue
            mass[k] += w; sr[k] += w * r; sg[k] += w * g; sb[k] += w * b
        }
        var best = -1
        for (k in 1 until buckets) if (best < 0 || mass[k] > mass[best]) best = k
        if (best < 0 || mass[best] <= 0.0) {
            val m = max(1.0, total)
            return rgb((tr / m).roundToInt(), (tg / m).roundToInt(), (tb / m).roundToInt())
        }
        val m = mass[best]
        return rgb((sr[best] / m).roundToInt(), (sg[best] / m).roundToInt(), (sb[best] / m).roundToInt())
    }

}
