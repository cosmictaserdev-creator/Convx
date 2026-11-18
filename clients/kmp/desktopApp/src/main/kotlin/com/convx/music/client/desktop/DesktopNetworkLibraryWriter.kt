package com.convx.music.client.desktop

import com.convx.storage.domain.ImportedAlbum
import com.convx.storage.domain.ImportedSong
import com.convx.storage.domain.NetworkLibraryWriter
import com.convx.storage.domain.NetworkTags
import com.convx.storage.domain.SongArtworkState
import java.io.File
import java.net.URI

internal class DesktopNetworkLibraryWriter(private val library: DesktopMediaLibrary) : NetworkLibraryWriter {
    override suspend fun artworkState(songId: String): SongArtworkState {
        library.awaitReady()
        val track = library.state.value.index.tracks.firstOrNull { it.id == songId } ?: return SongArtworkState.NEW
        return if (track.artworkPath?.let { File(it).isFile } == true) SongArtworkState.PRESENT else SongArtworkState.MISSING
    }

    override suspend fun write(songs: List<ImportedSong>, albums: List<ImportedAlbum>) {
        val tracks = songs.map { song ->
            val source = song.track
            DesktopLibraryTrack(
                id = source.id, filePath = source.id, title = source.title,
                artists = listOfNotNull(source.artist?.takeIf(String::isNotBlank)), album = source.album,
                durationMs = source.durationSeconds.takeIf { it >= 0 }?.toLong()?.times(1000),
                artworkPath = localArtworkPath(song.artworkUri), bytes = source.size,
                albumArtist = source.albumArtist, discNumber = source.discNumber, trackNumber = source.trackNumber,
            )
        }
        library.cacheTracks(tracks)
        library.updateIndex { index ->
            val existing = index.tracks.mapTo(mutableSetOf()) { it.id }
            index.copy(tracks = index.tracks + tracks.filter { existing.add(it.id) })
        }
    }

    override suspend fun updateArtwork(songId: String, artworkUri: String) {
        val path = localArtworkPath(artworkUri) ?: return
        library.updateIndex { index ->
            index.copy(tracks = index.tracks.map { if (it.id == songId) it.copy(artworkPath = path) else it })
        }
        val track = library.state.value.index.tracks.firstOrNull { it.id == songId } ?: return
        library.cacheTracks(listOf(track))
    }

    override suspend fun updateMetadata(songId: String, tags: NetworkTags) {
        library.updateIndex { index ->
            index.copy(tracks = index.tracks.map { track ->
                if (track.id != songId) track else track.copy(
                    title = tags.title?.takeIf(String::isNotBlank) ?: track.title,
                    artists = tags.artist?.takeIf(String::isNotBlank)?.let(::listOf) ?: track.artists,
                    album = tags.album?.takeIf(String::isNotBlank) ?: track.album,
                    albumArtist = tags.albumArtist?.takeIf(String::isNotBlank) ?: track.albumArtist,
                    trackNumber = tags.trackNumber ?: track.trackNumber,
                    discNumber = tags.discNumber ?: track.discNumber,
                    durationMs = tags.durationSeconds?.takeIf { it > 0 }?.toLong()?.times(1000) ?: track.durationMs,
                )
            })
        }
        library.state.value.index.tracks.firstOrNull { it.id == songId }?.let { library.cacheTracks(listOf(it)) }
    }

    private fun localArtworkPath(uri: String?): String? = try {
        uri?.let { URI(it) }?.takeIf { it.scheme == "file" }?.let { File(it).path }
    } catch (_: IllegalArgumentException) { null }
}
