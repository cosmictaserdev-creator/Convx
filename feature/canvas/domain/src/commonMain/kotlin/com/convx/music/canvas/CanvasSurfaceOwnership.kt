package com.convx.music.canvas

/** Platform surfaces are opaque values; ownership rules are shared across all player hosts. */
class CanvasSurfaceOwnership<T> {
    private data class Entry<T>(val token: Any, val surface: T, val priority: Int)
    private val entries = mutableListOf<Entry<T>>()

    val current: T? get() = entries.asReversed().maxByOrNull { it.priority }?.surface

    fun attach(token: Any, surface: T, priority: Int = 0): T? {
        entries.removeAll { it.token === token }
        entries += Entry(token, surface, priority)
        return current
    }

    fun detach(token: Any): T? {
        entries.removeAll { it.token === token }
        return current
    }

    fun clear() { entries.clear() }
}
