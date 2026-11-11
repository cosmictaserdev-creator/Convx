package com.convx.music.canvas

enum class CanvasProvider { SPOTIFY, APPLE_MUSIC, TIDAL, CONVX }

data class CanvasProviderPreferences(
    val order: List<CanvasProvider>,
    val disabled: Set<CanvasProvider>,
    val mode: CanvasSelectionMode,
    val priorityWaitMillis: Long,
) {
    val enabled: List<CanvasProvider> get() = order.filterNot(disabled::contains)

    companion object {
        /** Unknown and duplicate persisted IDs are ignored; new providers are appended. */
        fun fromStored(
            order: String,
            disabled: String,
            firstAvailable: Boolean,
            waitSeconds: Int,
        ): CanvasProviderPreferences {
            fun parse(value: String) = value.split(',').mapNotNull { name ->
                CanvasProvider.entries.firstOrNull { it.name == name.trim() }
            }.distinct()
            val saved = parse(order)
            return CanvasProviderPreferences(
                order = saved + CanvasProvider.entries.filterNot(saved::contains),
                disabled = parse(disabled).toSet(),
                mode = if (firstAvailable) CanvasSelectionMode.FIRST_AVAILABLE else CanvasSelectionMode.PRIORITY,
                priorityWaitMillis = waitSeconds.coerceIn(0, 10) * 1_000L,
            )
        }
    }
}
