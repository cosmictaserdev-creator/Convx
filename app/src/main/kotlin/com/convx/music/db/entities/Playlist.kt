/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.db.entities

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.convx.music.library.PlaylistCardData
import java.time.ZoneId

@Immutable
data class Playlist(
    @Embedded
    val playlist: PlaylistEntity,
    override val songCount: Int,
    @Relation(
        entity = SongEntity::class,
        entityColumn = "id",
        parentColumn = "id",
        projection = ["thumbnailUrl"],
        associateBy =
        Junction(
            value = PlaylistSongMapPreview::class,
            parentColumn = "playlistId",
            entityColumn = "songId",
        ),
    )
    val songThumbnails: List<String?>,
) : LocalItem(), PlaylistCardData {
    override val id: String
        get() = playlist.id
    override val title: String
        get() = playlist.name
    override val name: String
        get() = playlist.name
    override val remoteSongCount: Int?
        get() = playlist.remoteSongCount
    override val browseId: String?
        get() = playlist.browseId
    override val sourceThumbnailUrl: String?
        get() = playlist.thumbnailUrl
    override val isEditable: Boolean
        get() = playlist.isEditable
    override val isBookmarked: Boolean
        get() = playlist.bookmarkedAt != null
    override val isLocal: Boolean
        get() = playlist.isLocal
    override val pinnedAtEpochMillis: Long?
        get() = playlist.pinnedAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    override val artworkUrls: List<String>
        get() = thumbnails
    override val thumbnailUrl: String?
        get() = null
    
    val thumbnails: List<String>
        get() {
            return if (playlist.thumbnailUrl != null)
                listOf(playlist.thumbnailUrl)
            else songThumbnails.filterNotNull()
        }
}
