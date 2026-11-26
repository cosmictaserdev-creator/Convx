package com.convx.music.canvas

/** A manual choice overrides automatic source order, but never a disabled provider. */
fun StoredCanvasDecision.usableManualSelection(preferences: CanvasProviderPreferences): StoredCanvasDecision? =
    takeIf { selected ->
        preferences.enabled.any { it.name == selected.source } &&
            !selected.artwork.preferredAnimationUrl.isNullOrBlank()
    }

/** Shared validation for session-only and persisted per-song choices. */
fun manualCanvasSelection(provider: CanvasProvider, artwork: CanvasArtwork): StoredCanvasDecision {
    require(!artwork.preferredAnimationUrl.isNullOrBlank()) { "A canvas selection needs a playable URL" }
    return StoredCanvasDecision(provider.name, artwork)
}
