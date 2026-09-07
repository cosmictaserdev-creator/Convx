/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.soundcloud

import android.content.Context
import androidx.compose.runtime.Immutable
import com.convxy.music.BuildConfig
import com.convxy.music.constants.SoundCloudAccessTokenKey
import com.convxy.music.constants.SoundCloudClientIdKey
import com.convxy.music.constants.SoundCloudClientSecretKey
import com.convxy.music.constants.TimestampCommentsEnabledKey
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.get
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Where the SoundCloud source gets its credentials, and whether it is switched on at all.
 *
 * Resolution order, first non-blank wins:
 *  1. an access token the user pasted in Settings → Integrations → SoundCloud (skips the token
 *     exchange entirely — useful for a token obtained by any legitimate means);
 *  2. a client_id + client_secret entered in the same place;
 *  3. `SOUNDCLOUD_CLIENT_ID` / `SOUNDCLOUD_CLIENT_SECRET` from local.properties or the environment,
 *     baked into BuildConfig exactly like `LASTFM_API_KEY`.
 *
 * All three are optional. Nothing here is a build requirement and nothing here is a paid service:
 * SoundCloud's developer API is free, it just needs a registered application, and a build without
 * one simply reports [Config.isUsable] false so the feature degrades to an honest "unavailable"
 * instead of shipping a hardcoded key that would be revoked the moment it was published.
 */
@Singleton
class SoundCloudCredentials @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    @Immutable
    data class Config(
        val accessToken: String?,
        val clientId: String,
        val clientSecret: String,
    ) {
        /** True when there is something to authenticate with. Never true by accident. */
        val isUsable: Boolean
            get() = !accessToken.isNullOrBlank() || (clientId.isNotBlank() && clientSecret.isNotBlank())

        /** Only a client_id/client_secret pair can mint tokens; a pasted token cannot be refreshed. */
        val canExchangeTokens: Boolean
            get() = clientId.isNotBlank() && clientSecret.isNotBlank()
    }

    /** The user-facing master switch. Off means the source declines without reading credentials. */
    val isEnabled: Boolean
        get() = context.dataStore.get(TimestampCommentsEnabledKey, true)

    /** Synchronous: reads the DataStore snapshot mirror, no suspension and no disk hit. */
    fun resolve(): Config {
        val store = context.dataStore
        val token = store[SoundCloudAccessTokenKey]?.trim().takeUnless { it.isNullOrEmpty() }
        val clientId = store[SoundCloudClientIdKey]?.trim()?.takeUnless { it.isNullOrEmpty() }
            ?: BuildConfig.SOUNDCLOUD_CLIENT_ID.trim().takeUnless { it.isEmpty() }
            ?: ""
        val clientSecret = store[SoundCloudClientSecretKey]?.trim()?.takeUnless { it.isNullOrEmpty() }
            ?: BuildConfig.SOUNDCLOUD_CLIENT_SECRET.trim().takeUnless { it.isEmpty() }
            ?: ""
        return Config(
            accessToken = token,
            clientId = clientId,
            clientSecret = clientSecret,
        )
    }
}
