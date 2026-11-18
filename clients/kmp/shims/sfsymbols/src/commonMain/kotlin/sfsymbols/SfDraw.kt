package com.composables.sfsymbols

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import kotlin.math.max
import kotlin.math.min

/** Number of reveal steps a stroke is drawn in. */
internal const val DRAW_LEVELS = 48

private const val GRID = 112
private const val SQRT2 = 1.4142135f

/**
 * Reveal regions for drawing a stroked layer on/off: `levels[k]` is the part of the layer reached by progress
 * `(k + 1) / DRAW_LEVELS`, measured as distance travelled *through the shape* from the stroke's start rail tie,
 * so the reveal follows corners and curves instead of sweeping straight across.
 */
internal class SfDrawField(val levels: Array<Path>)

private class MinHeap(cap: Int) {
    private val k = FloatArray(cap)
    private val v = IntArray(cap)
    var n = 0
        private set

    fun push(key: Float, value: Int) {
        var i = n++
        while (i > 0) {
            val p = (i - 1) / 2
            if (k[p] <= key) break
            k[i] = k[p]; v[i] = v[p]; i = p
        }
        k[i] = key; v[i] = value
    }

    fun pop(): Int {
        val top = v[0]
        n--
        if (n > 0) {
            val key = k[n]; val value = v[n]
            var i = 0
            while (true) {
                var c = 2 * i + 1
                if (c >= n) break
                if (c + 1 < n && k[c + 1] < k[c]) c++
                if (k[c] >= key) break
                k[i] = k[c]; v[i] = v[c]; i = c
            }
            k[i] = key; v[i] = value
        }
        return top
    }
}

/**
 * Builds the reveal field for one layer.
 *
 * @param layerPath the layer's outline in glyph space
 * @param symbolBounds bounds the rail ties are normalised against (glyph space)
 * @param ties rail tie midpoints as (u, v) fractions of [symbolBounds] (y from top); the first one is the start
 */
internal fun buildDrawField(layerPath: Path, symbolBounds: Rect, ties: FloatArray): SfDrawField? {
    val lb = layerPath.getBounds()
    if (lb.width <= 0f && lb.height <= 0f) return null
    val pad = max(lb.width, lb.height) * 0.02f + 1f
    val x0 = lb.left - pad
    val y0 = lb.top - pad
    val cell = (max(lb.width, lb.height) + 2f * pad) / GRID

    // rasterise the layer
    val bmp = ImageBitmap(GRID, GRID)
    val canvas = Canvas(bmp)
    canvas.scale(1f / cell, 1f / cell)
    canvas.translate(-x0, -y0)
    canvas.drawPath(layerPath, Paint().apply { color = Color.White; isAntiAlias = true })
    val px = IntArray(GRID * GRID)
    bmp.readPixels(px)
    val mask = BooleanArray(GRID * GRID) { (px[it] ushr 24) >= 110 }
    if (mask.none { it }) return null

    val dist = FloatArray(GRID * GRID) { Float.MAX_VALUE }
    val comp = IntArray(GRID * GRID) { -1 }
    val compMax = ArrayList<Float>()
    val heap = MinHeap(GRID * GRID * 8)

    fun nearestUnvisited(u: Float, v: Float): Int {
        val gx = symbolBounds.left + u * symbolBounds.width
        val gy = symbolBounds.top + v * symbolBounds.height
        val cx = ((gx - x0) / cell).toInt().coerceIn(0, GRID - 1)
        val cy = ((gy - y0) / cell).toInt().coerceIn(0, GRID - 1)
        var best = -1
        var bestD = Int.MAX_VALUE
        val r = 10
        for (yy in max(0, cy - r)..min(GRID - 1, cy + r)) for (xx in max(0, cx - r)..min(GRID - 1, cx + r)) {
            val i = yy * GRID + xx
            if (!mask[i] || dist[i] != Float.MAX_VALUE) continue
            val d = (xx - cx) * (xx - cx) + (yy - cy) * (yy - cy)
            if (d < bestD) { bestD = d; best = i }
        }
        return best
    }

    fun flood(seed: Int, id: Int) {
        var dmax = 0f
        dist[seed] = 0f; comp[seed] = id
        heap.push(0f, seed)
        while (heap.n > 0) {
            val i = heap.pop()
            val d = dist[i]
            val x = i % GRID
            val y = i / GRID
            for (dy in -1..1) for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nx = x + dx
                val ny = y + dy
                if (nx < 0 || ny < 0 || nx >= GRID || ny >= GRID) continue
                val j = ny * GRID + nx
                if (!mask[j]) continue
                if (dx != 0 && dy != 0 && (!mask[y * GRID + nx] || !mask[ny * GRID + x])) continue   // no corner cutting
                val nd = d + if (dx != 0 && dy != 0) SQRT2 else 1f
                if (nd < dist[j]) {
                    dist[j] = nd; comp[j] = id
                    if (nd > dmax) dmax = nd
                    heap.push(nd, j)
                }
            }
        }
        compMax += dmax
    }

    // the first tie starts the first stroke; strokes the flood can't reach start at their lowest-numbered tie
    var id = 0
    var tie = 0
    val nTies = ties.size / 2
    while (true) {
        var seed = -1
        while (seed < 0 && tie < nTies) { seed = nearestUnvisited(ties[tie * 2], ties[tie * 2 + 1]); tie++ }
        if (seed < 0) seed = mask.indices.firstOrNull { mask[it] && dist[it] == Float.MAX_VALUE } ?: break
        flood(seed, id++)
    }

    // bucket pixels by reveal level and build cumulative clip paths
    val level = IntArray(GRID * GRID) { 0 }
    for (i in mask.indices) if (mask[i]) {
        val m = compMax[comp[i]]
        val t = if (m > 0f) dist[i] / m else 0f
        level[i] = (t * DRAW_LEVELS).toInt().coerceIn(0, DRAW_LEVELS - 1)   // 0..47 -> revealed once progress passes (level+1)/48
    }
    val levels = Array(DRAW_LEVELS) { Path() }
    val half = cell / 2f
    for (lv in 0 until DRAW_LEVELS) {
        val p = levels[lv]
        if (lv > 0) p.addPath(levels[lv - 1])
        for (y in 0 until GRID) {
            var x = 0
            while (x < GRID) {
                if (mask[y * GRID + x] && level[y * GRID + x] == lv) {
                    var e = x
                    while (e + 1 < GRID && mask[y * GRID + e + 1] && level[y * GRID + e + 1] == lv) e++
                    p.addRect(Rect(x0 + x * cell - half, y0 + y * cell - half, x0 + (e + 1) * cell + half, y0 + (y + 1) * cell + half))
                    x = e + 1
                } else x++
            }
        }
    }
    return SfDrawField(levels)
}

