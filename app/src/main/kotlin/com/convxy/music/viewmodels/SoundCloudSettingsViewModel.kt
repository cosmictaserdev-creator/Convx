/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.viewmodels

import androidx.lifecycle.ViewModel
import com.convxy.music.comments.soundcloud.SoundCloudApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * One job: tell the SoundCloud client that the credentials underneath it have changed.
 *
 * [SoundCloudApi] is an `@Singleton` that keeps the access token it minted in memory, and it cannot
 * observe DataStore. Without this, a user who pastes a token — or replaces a client secret — would
 * keep being served the token minted from the *old* credentials until it expired, which is up to an
 * hour of "I changed it and nothing happened". Invalidating here makes the next request re-authenticate
 * against whatever is in preferences now.
 *
 * Nothing else is cleared on purpose. The comment cache is keyed per track and its contents are a
 * property of the track, not of which registered application fetched them, so an entry written under
 * the old credentials is still true under the new ones; and `CommentsRepository` re-checks
 * `isConfigured()` *before* reading the cache, so removing credentials entirely stops cached comments
 * being served without any extra work here.
 *
 * Obtained with `hiltViewModel()` rather than an `@EntryPoint` so the settings screen stays on the
 * UI → ViewModel → provider path the rest of the app uses.
 */
@HiltViewModel
class SoundCloudSettingsViewModel
@Inject
constructor(
    private val soundCloudApi: SoundCloudApi,
) : ViewModel() {

    /** Call after writing any of the three credential preferences. */
    fun onCredentialsChanged() {
        soundCloudApi.invalidateToken()
    }
}
