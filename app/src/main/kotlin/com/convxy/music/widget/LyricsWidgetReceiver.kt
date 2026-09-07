/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.widget

import android.content.Context

/**
 * Lyrics widget: current lyric line with its neighbours faded, plus transport.
 *
 * Same update/click plumbing as [MusicWidgetReceiver]; only the rendering differs.
 */
class LyricsWidgetReceiver : MusicWidgetReceiver() {

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        WidgetConfig.delete(context, appWidgetIds)
    }
}
