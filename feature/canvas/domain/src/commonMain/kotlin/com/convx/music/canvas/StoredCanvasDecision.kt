package com.convx.music.canvas

import kotlinx.serialization.Serializable

/** Provider metadata needed to locate an already-cached canvas after a restart. */
@Serializable
data class StoredCanvasDecision(
    val source: String,
    val artwork: CanvasArtwork,
)

/** Byte counts recorded only after a segmented download finishes successfully. */
@Serializable
data class CachedCanvasSegments(val manifestKey: String, val entries: Map<String, Long>)
