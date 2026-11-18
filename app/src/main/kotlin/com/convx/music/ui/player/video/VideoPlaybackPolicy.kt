package com.convx.music.ui.player.video

import kotlin.math.abs

internal object PlayerVideoBuffers {
    const val SETTLE_MS = 700
    const val MIN_BUFFER_MS = 12_000
    const val MAX_BUFFER_MS = 40_000
    const val START_BUFFER_MS = 750
    const val RESUME_BUFFER_MS = 1_500
}

private const val SYNC_DEAD_ZONE_MS = 80L
private const val SYNC_SEEK_THRESHOLD_MS = 1_200L
private const val MAX_SPEED_ADJUSTMENT = 0.03f

/** Positive drift means video is ahead of audio. */
internal fun synchronizedVideoSpeed(audioSpeed: Float, driftMs: Long): Float {
    val drift = driftMs.coerceIn(-SYNC_SEEK_THRESHOLD_MS, SYNC_SEEK_THRESHOLD_MS)
    val correction = when {
        drift > SYNC_DEAD_ZONE_MS -> drift - SYNC_DEAD_ZONE_MS
        drift < -SYNC_DEAD_ZONE_MS -> drift + SYNC_DEAD_ZONE_MS
        else -> return audioSpeed
    }
    val adjustment = correction.toFloat() / (SYNC_SEEK_THRESHOLD_MS - SYNC_DEAD_ZONE_MS)
    return audioSpeed * (1f - adjustment * MAX_SPEED_ADJUSTMENT)
}

internal fun shouldSeekVideo(driftMs: Long): Boolean =
    driftMs < -SYNC_SEEK_THRESHOLD_MS || driftMs > SYNC_SEEK_THRESHOLD_MS

private const val LETTERBOX_MAX_ZOOM = 1.35f
private const val LETTERBOX_SAMPLES_PER_ROW = 16

/**
 * Estimates uniform zoom from a tightly packed ARGB sample of the uncropped frame.
 * Sample at multiple frames before applying zoom if the content can change framing.
 */
internal fun letterboxCropZoom(pixels: IntArray, width: Int, height: Int): Float {
    if (width < 8 || height < 16 || height > pixels.size / width) return 1f

    val sampleCount = minOf(width, LETTERBOX_SAMPLES_PER_ROW)
    val maxBarRows = height / 5
    val topRows = countBlackRows(pixels, width, sampleCount, 0, 1, maxBarRows)
    val bottomRows = countBlackRows(pixels, width, sampleCount, height - 1, -1, maxBarRows)
    val barRows = minOf(topRows, bottomRows)
    if (barRows < 2 || abs(topRows - bottomRows) > 1) return 1f

    if (!hasPictureContrast(pixels, width, sampleCount, topRows) ||
        !hasPictureContrast(pixels, width, sampleCount, height - 1 - bottomRows) ||
        !hasPictureContrast(pixels, width, sampleCount, height / 2)
    ) return 1f

    return (height.toFloat() / (height - 2 * barRows)).coerceAtMost(LETTERBOX_MAX_ZOOM)
}

private fun countBlackRows(
    pixels: IntArray,
    width: Int,
    sampleCount: Int,
    firstRow: Int,
    step: Int,
    limit: Int,
): Int {
    for (offset in 0 until limit) {
        val row = firstRow + offset * step
        val blackSamples = countRowSamples(pixels, width, sampleCount, row) { pixel ->
            pixel ushr 24 == 255 && (pixel ushr 16 and 255) <= 24 &&
                (pixel ushr 8 and 255) <= 24 && (pixel and 255) <= 24
        }
        if (blackSamples * 10 < sampleCount * 9) return offset
    }
    return limit
}

private fun hasPictureContrast(pixels: IntArray, width: Int, sampleCount: Int, row: Int): Boolean =
    countRowSamples(pixels, width, sampleCount, row) { pixel ->
        pixel ushr 24 == 255 && (
            (pixel ushr 16 and 255) >= 48 || (pixel ushr 8 and 255) >= 48 || (pixel and 255) >= 48
        )
    } * 2 >= sampleCount

private inline fun countRowSamples(
    pixels: IntArray,
    width: Int,
    sampleCount: Int,
    row: Int,
    matches: (Int) -> Boolean,
): Int {
    val start = row * width
    var count = 0
    for (sample in 0 until sampleCount) {
        val column = (sample.toLong() * width / sampleCount).toInt()
        if (matches(pixels[start + column])) count++
    }
    return count
}
