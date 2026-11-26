/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.widget

import android.content.Context
import com.tencent.mmkv.MMKV

/** How a widget instance paints its card behind the content. */
enum class WidgetBackground {
    /** Dominant colour of the current artwork (default, Apple Music behaviour). */
    ALBUM_TINT,
    DARK,
    LIGHT,
    /** A still image the user picked in the widget's config screen. */
    IMAGE,

    /** Samsung One UI's native wallpaper blur behind a translucent tint. Only offered where it works. */
    BLUR;

    companion object {
        /** Frosted glass where the launcher supports it, otherwise the artwork colour. */
        fun defaultForDevice(): WidgetBackground = if (OneUiBlur.isSupported) BLUR else ALBUM_TINT
    }
}

/**
 * Per-instance widget settings.
 *
 * Deliberately NOT in DataStore: these are keyed by appWidgetId rather than being
 * app-wide, and they have to be readable synchronously from AppWidgetProvider /
 * the widget manager, which is not a coroutine context. MMKV gives the same
 * synchronous access with mmap-backed writes (migrated from SharedPreferences).
 */
data class WidgetConfig(
    val background: WidgetBackground = WidgetBackground.defaultForDevice(),
    /** content:// uri of the user's chosen background, when [background] is IMAGE. */
    val imageUri: String? = null,
    val showArtwork: Boolean = true,
    val showPrevNext: Boolean = true,
    val showLike: Boolean = false,
    /**
     * Playlist this widget launches/plays instead of the current queue.
     * null = act on whatever is currently playing.
     */
    val playlistId: String? = null,
) {
    companion object {
        private const val FILE = "widget_config"

        private fun keyOf(widgetId: Int, name: String) = "w${widgetId}_$name"

        private fun prefs(context: Context) = MMKV.mmkvWithID(FILE)

        fun load(context: Context, widgetId: Int): WidgetConfig {
            val p = prefs(context)
            val default = WidgetConfig()
            val backgroundName = p.decodeString(keyOf(widgetId, "bg"))
            return WidgetConfig(
                background = backgroundName
                    ?.let { name -> WidgetBackground.entries.firstOrNull { it.name == name } }
                    ?: default.background,
                imageUri = p.decodeString(keyOf(widgetId, "image")),
                showArtwork = p.decodeBool(keyOf(widgetId, "art"), default.showArtwork),
                showPrevNext = p.decodeBool(keyOf(widgetId, "prevnext"), default.showPrevNext),
                showLike = p.decodeBool(keyOf(widgetId, "like"), default.showLike),
                playlistId = p.decodeString(keyOf(widgetId, "playlist")),
            )
        }

        fun save(context: Context, widgetId: Int, config: WidgetConfig) {
            val p = prefs(context)
            p.encode(keyOf(widgetId, "bg"), config.background.name)
            p.encode(keyOf(widgetId, "image"), config.imageUri)
            p.encode(keyOf(widgetId, "art"), config.showArtwork)
            p.encode(keyOf(widgetId, "prevnext"), config.showPrevNext)
            p.encode(keyOf(widgetId, "like"), config.showLike)
            p.encode(keyOf(widgetId, "playlist"), config.playlistId)
        }

        /** Called from onDeleted so removed widgets don't leak their settings forever. */
        fun delete(context: Context, widgetIds: IntArray) {
            val p = prefs(context)
            widgetIds.forEach { id ->
                listOf("bg", "image", "art", "prevnext", "like", "playlist")
                    .forEach { p.removeValueForKey(keyOf(id, it)) }
            }
        }
    }
}
