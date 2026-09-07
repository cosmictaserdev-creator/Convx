/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

import android.content.Context
import com.convxy.music.constants.CommentSourceOrderKey
import com.convxy.music.constants.TimestampCommentsEnabledKey
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.get
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Which comment sources are switched on, and in what order they get asked.
 *
 * Both live in one DataStore string — the enabled sources, highest priority first — so membership and
 * ranking cannot drift apart. A source that is in the list is on; a source that is not, is off.
 *
 * Read synchronously off the DataStore snapshot mirror (the same accessor `SoundCloudCredentials`
 * uses) rather than collected as a Flow: [CommentsRepository] calls it once per track change, on a
 * background dispatcher, and a snapshot read there is a map lookup. Making it a Flow would mean the
 * repository had to hold collected state whose only job is to answer a question that is asked once.
 */
@Singleton
class CommentSourcePreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * The master switch for the whole feature, shared with the player's comment button and the
     * seek-bar markers. Off means no source is asked and nothing is fetched.
     */
    val isFeatureEnabled: Boolean
        get() = context.dataStore.get(TimestampCommentsEnabledKey, true)

    /**
     * Sources to try, highest priority first. Empty when the feature is off — which is what makes the
     * switch mean something at the network layer too, not just in the UI: with it off, no provider can
     * spend a request.
     */
    fun orderedSources(): List<CommentSource> {
        if (!isFeatureEnabled) return emptyList()
        return CommentSource.parseEnabled(context.dataStore[CommentSourceOrderKey])
    }

    /** Whether one source is switched on, ignoring the master switch. For the settings rows. */
    fun isSourceEnabled(source: CommentSource): Boolean =
        source in CommentSource.parseEnabled(context.dataStore[CommentSourceOrderKey])
}
