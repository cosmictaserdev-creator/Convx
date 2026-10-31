/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.session.MediaConstants
import com.convx.music.R
import com.convx.music.db.entities.Song
import com.music.innertube.models.SongItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Builds the browsable and playable [MediaItem]s shown by car and other media browsers. */
@androidx.annotation.OptIn(UnstableApi::class)
class MediaBrowseItemFactory
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val downloadUtil: DownloadUtil,
) {
    fun folder(
        id: String,
        title: String,
        subtitle: String? = null,
        iconUri: Uri? = null,
        mediaType: Int = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
        itemStyle: Int = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM,
        childBrowsableStyle: Int = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM,
        childPlayableStyle: Int = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM,
        groupTitle: String? = null,
    ): MediaItem = MediaItem.Builder()
        .setMediaId(id)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setArtist(subtitle)
                .setArtworkUri(CarArtworkProvider.artworkUri(context, iconUri))
                .setIsPlayable(false)
                .setIsBrowsable(true)
                .setMediaType(mediaType)
                .setExtras(
                    Bundle().apply {
                        putInt(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM, itemStyle)
                        putInt(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_BROWSABLE, childBrowsableStyle)
                        putInt(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_PLAYABLE, childPlayableStyle)
                        putGroupTitle(groupTitle)
                    },
                )
                .build(),
        ).build()

    fun song(song: Song, parentId: String, groupTitle: String? = null): MediaItem = playable(
        id = "$parentId/${song.id}",
        title = song.song.title,
        subtitle = song.artists.joinToString { it.name },
        artworkUri = song.song.thumbnailUrl?.toUri(),
        isExplicit = song.song.explicit,
        downloadStatus = downloadStatus(song.id, isLocal = song.song.isLocal),
        groupTitle = groupTitle,
    )

    fun song(song: SongItem, parentId: String, groupTitle: String? = null): MediaItem = playable(
        id = "$parentId/${song.id}",
        title = song.title,
        subtitle = song.artists.joinToString { it.name },
        artworkUri = song.thumbnail.toUri(),
        isExplicit = song.explicit,
        downloadStatus = downloadStatus(song.id, isLocal = false),
        groupTitle = groupTitle,
    )

    fun shuffle(parentId: String): MediaItem = playable(
        id = "$parentId/${MusicService.SHUFFLE_ACTION}",
        title = context.getString(R.string.shuffle),
        subtitle = null,
        artworkUri = drawableUri(R.drawable.shuffle),
        isExplicit = false,
        downloadStatus = null,
        groupTitle = null,
    )

    fun drawableUri(@DrawableRes id: Int): Uri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(id))
        .appendPath(context.resources.getResourceTypeName(id))
        .appendPath(context.resources.getResourceEntryName(id))
        .build()

    private fun playable(
        id: String,
        title: String,
        subtitle: String?,
        artworkUri: Uri?,
        isExplicit: Boolean,
        downloadStatus: Long?,
        groupTitle: String?,
    ): MediaItem = MediaItem.Builder()
        .setMediaId(id)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setArtist(subtitle)
                .setArtworkUri(CarArtworkProvider.artworkUri(context, artworkUri))
                .setIsPlayable(true)
                .setIsBrowsable(false)
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .setExtras(
                    Bundle().apply {
                        putInt(
                            MediaConstants.EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM,
                            MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM,
                        )
                        if (isExplicit) {
                            putLong(MediaConstants.EXTRAS_KEY_IS_EXPLICIT, MediaConstants.EXTRAS_VALUE_ATTRIBUTE_PRESENT)
                        }
                        downloadStatus?.let { putLong(MediaConstants.EXTRAS_KEY_DOWNLOAD_STATUS, it) }
                        putGroupTitle(groupTitle)
                    },
                )
                .build(),
        ).build()

    private fun downloadStatus(songId: String, isLocal: Boolean): Long {
        if (isLocal) return MediaConstants.EXTRAS_VALUE_STATUS_DOWNLOADED
        return when (downloadUtil.downloads.value[songId]?.state) {
            Download.STATE_COMPLETED -> MediaConstants.EXTRAS_VALUE_STATUS_DOWNLOADED
            Download.STATE_QUEUED, Download.STATE_DOWNLOADING, Download.STATE_RESTARTING ->
                MediaConstants.EXTRAS_VALUE_STATUS_DOWNLOADING
            else -> MediaConstants.EXTRAS_VALUE_STATUS_NOT_DOWNLOADED
        }
    }

    private fun Bundle.putGroupTitle(groupTitle: String?) {
        groupTitle?.let { putString(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE, it) }
    }
}
