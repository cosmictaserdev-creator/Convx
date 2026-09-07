/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.viewmodels

import androidx.lifecycle.ViewModel
import com.convxy.music.comments.CommentsCache
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * One job: drop the cached comment answers when the user changes which sources are asked, and in what
 * order.
 *
 * A cached entry records which source produced it, and the repository serves it without consulting the
 * priority list at all — which is correct while the list is stable and wrong the moment it is not. Left
 * alone, a user who promoted SoundCloud above Audius would keep being served Audius's comments for
 * every track they had already opened, for up to twelve hours, and would reasonably conclude the
 * setting does nothing.
 *
 * Clearing is the cheap fix and the cost is bounded: a handful of refetches for tracks the user has
 * recently played, which is exactly what they asked for by changing the setting. The alternative —
 * stamping every entry with the ordering that produced it and comparing on read — costs a serialised
 * list on disk per entry to avoid a refetch nobody complained about.
 *
 * Note what is NOT cleared here. Changing SoundCloud *credentials* goes through
 * [SoundCloudSettingsViewModel], which invalidates the minted token instead: a cached comment written
 * under an old client_id is still a true statement about the track, so it should survive.
 */
@HiltViewModel
class CommentSourceSettingsViewModel
@Inject
constructor(
    private val cache: CommentsCache,
) : ViewModel() {

    /** Call after writing [com.convxy.music.constants.CommentSourceOrderKey]. */
    fun onPriorityChanged() {
        cache.clear()
    }
}
