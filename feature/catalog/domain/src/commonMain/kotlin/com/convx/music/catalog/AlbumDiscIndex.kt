package com.convx.music.catalog

/**
 * Keeps a song's disc inside the album position it already stores, so no schema change is needed:
 * the position is ordered disc first, then track, and the disc is read back from it.
 * An album with one disc (every streamed album) decodes as disc 1.
 */
object AlbumDiscIndex {
    const val DISC_STRIDE = 10_000

    fun encode(disc: Int?, position: Int): Int {
        require(position in 0 until DISC_STRIDE) { "Position out of range: $position" }
        return ((disc ?: 1).coerceAtLeast(1) - 1) * DISC_STRIDE + position
    }

    fun discOf(index: Int): Int = index.coerceAtLeast(0) / DISC_STRIDE + 1
}
