/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.content.Context
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

/**
 * The player the media session exposes. Car hosts only load local artwork, so the current
 * item's artwork is re-served through [CarArtworkProvider]. Only the session sees this;
 * the app's own UI keeps reading the original artwork from the underlying player.
 */
@androidx.annotation.OptIn(UnstableApi::class)
class CarArtworkPlayer(
    private val context: Context,
    player: Player,
) : ForwardingPlayer(player) {
    private var sourceMetadata: MediaMetadata? = null
    private var carMetadata: MediaMetadata = MediaMetadata.EMPTY
    private var sourceItem: MediaItem? = null
    private var carItem: MediaItem? = null

    override fun getMediaMetadata(): MediaMetadata {
        val metadata = super.getMediaMetadata()
        if (metadata !== sourceMetadata) {
            sourceMetadata = metadata
            carMetadata = metadata.withCarArtwork()
        }
        return carMetadata
    }

    override fun getCurrentMediaItem(): MediaItem? {
        val item = super.getCurrentMediaItem()
        if (item !== sourceItem) {
            sourceItem = item
            carItem = item?.let { it.buildUpon().setMediaMetadata(it.mediaMetadata.withCarArtwork()).build() }
        }
        return carItem
    }

    private fun MediaMetadata.withCarArtwork(): MediaMetadata {
        val carArtworkUri = CarArtworkProvider.artworkUri(context, artworkUri)
        if (carArtworkUri == null || carArtworkUri == artworkUri) return this
        return buildUpon().setArtworkUri(carArtworkUri).build()
    }
}
