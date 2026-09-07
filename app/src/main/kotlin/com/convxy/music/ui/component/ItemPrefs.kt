/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.convxy.music.constants.CropAlbumArtKey
import com.convxy.music.constants.GridCardHeightOverrideKey
import com.convxy.music.constants.GridItemSize
import com.convxy.music.constants.GridItemsSizeKey
import com.convxy.music.constants.SwipeToRemoveSongKey
import com.convxy.music.constants.SwipeToSongKey
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference

/**
 * The handful of preferences that every list row and grid tile reads.
 *
 * These used to be read with [rememberPreference] inside the item composables
 * themselves. That is once per item, and `remember` does not help in a lazy list
 * because an item scrolling into view IS a first composition — so each arriving
 * row paid a `runBlocking` DataStore read on the main thread for its seed value,
 * plus a fresh Flow collector for updates. On a dense list that is the scroll
 * budget spent on settings that change maybe twice a year.
 *
 * Read once at the app root instead and passed down. [staticCompositionLocalOf]
 * on purpose: reads cost nothing, and a change invalidating the subtree is the
 * right trade for values that change this rarely.
 */
@Immutable
data class ItemPrefs(
    val cropAlbumArt: Boolean = false,
    val swipeToSong: Boolean = false,
    val swipeToRemoveSong: Boolean = false,
    val gridCardHeightOverrideDp: Int = 0,
    val gridItemSize: GridItemSize = GridItemSize.BIG,
)

val LocalItemPrefs = staticCompositionLocalOf { ItemPrefs() }

/** Reads the item preferences from DataStore. Call once, at the app root. */
@Composable
fun rememberItemPrefs(): ItemPrefs {
    val (cropAlbumArt) = rememberPreference(CropAlbumArtKey, false)
    val (swipeToSong) = rememberPreference(SwipeToSongKey, false)
    val (swipeToRemoveSong) = rememberPreference(SwipeToRemoveSongKey, false)
    val (gridCardHeightOverride) = rememberPreference(GridCardHeightOverrideKey, 0)
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)
    return remember(
        cropAlbumArt, swipeToSong, swipeToRemoveSong, gridCardHeightOverride, gridItemSize,
    ) {
        ItemPrefs(
            cropAlbumArt = cropAlbumArt,
            swipeToSong = swipeToSong,
            swipeToRemoveSong = swipeToRemoveSong,
            gridCardHeightOverrideDp = gridCardHeightOverride,
            gridItemSize = gridItemSize,
        )
    }
}

/**
 * Every song's download state, collected once instead of once per row.
 *
 * The download badge used to call `downloadUtil.getDownload(id)`, which builds a
 * fresh `downloads.map { it[id] }` cold Flow, and collect it per row. In a lazy
 * list that is a coroutine per visible row, torn down and started again on every
 * recycle, for a value that changes only while something is actually downloading.
 *
 * [androidx.compose.runtime.compositionLocalOf] rather than static: rows must
 * recompose when a download completes, which is the whole point of the badge.
 */
val LocalDownloads = androidx.compose.runtime.compositionLocalOf<Map<String, androidx.media3.exoplayer.offline.Download>> { emptyMap() }
