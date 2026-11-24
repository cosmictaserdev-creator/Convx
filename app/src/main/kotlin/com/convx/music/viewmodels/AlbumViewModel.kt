/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.convx.music.db.MusicDatabase
import com.convx.music.utils.reportException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import com.convx.music.utils.Wikipedia
import com.convx.music.utils.AppleMusicAboutAlbum
import com.convx.music.utils.ArtistWordmarkAssets
import java.io.File

class AlbumViewModel(
    private val context: Context,
    private val database: MusicDatabase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val albumId = requireNotNull(savedStateHandle.get<String>("albumId")) { "Missing albumId" }
    val thumbArg = savedStateHandle.get<String>("thumb")?.takeIf { it.isNotEmpty() }
    val playlistId = MutableStateFlow("")
    val loadFailed = MutableStateFlow(false)
    val albumWithSongs =
        database
            .albumWithSongs(albumId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    var otherVersions = MutableStateFlow<List<AlbumItem>>(emptyList())
    var releasesForYou = MutableStateFlow<List<AlbumItem>>(emptyList())
    var description = MutableStateFlow<String?>(null)
    var descriptionRuns = MutableStateFlow<List<com.music.innertube.models.Run>?>(null)
    val artistSignatureUrl = MutableStateFlow<String?>(null)
    val artistBundledLogo = MutableStateFlow<File?>(null)
    private var signatureLookupName: String? = null

    init {
        refresh()
        viewModelScope.launch {
            albumWithSongs.filterNotNull().first()
                .artists.firstOrNull()?.name
                ?.let(::fetchArtistSignature)
        }
    }

    private fun fetchArtistSignature(name: String) {
        val normalized = name.trim()
        if (normalized.isBlank() || signatureLookupName == normalized) return
        signatureLookupName = normalized
        val saved = ArtistWordmarkAssets.saved(context, normalized)
        artistBundledLogo.value = saved.appleLogo
        artistSignatureUrl.value = saved.signatureUrl
        if (saved.appleLogo != null && saved.signature != null) return
        viewModelScope.launch {
            val assets = ArtistWordmarkAssets.bundle(context, normalized)
            if (signatureLookupName == normalized) {
                artistBundledLogo.value = assets.appleLogo
                artistSignatureUrl.value = assets.signatureUrl
            }
        }
    }

    /** Re-fetches the album page. Bound to the pull-to-refresh gesture. */
    fun refresh() {
        loadFailed.value = false
        viewModelScope.launch {
            val album = database.album(albumId).first()
            if (album?.description != null) {
                description.value = album.description
            }
            val albumResult = try {
                withTimeoutOrNull(25_000) { YouTube.album(albumId) }
                    ?: Result.failure(java.io.IOException("Album request timed out"))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
            albumResult
                .onSuccess {
                    playlistId.value = it.album.playlistId
                    otherVersions.value = it.otherVersions
                    releasesForYou.value = it.releasesForYou
                    if (it.description != null) {
                        description.value = it.description
                    }
                    descriptionRuns.value = it.descriptionRuns
                    database.transaction {
                        if (album == null) {
                            insert(it)
                        } else {
                            update(album.album, it, album.artists)
                        }
                    }

                    val albumArtists = it.album.artists
                    if (albumArtists?.size == 1) {
                        albumArtists.firstOrNull()?.id?.let { artistId ->
                            viewModelScope.launch(Dispatchers.IO) {
                                val artistEntity = database.getArtistById(artistId)
                                if (artistEntity?.thumbnailUrl == null) {
                                    YouTube.artist(artistId).onSuccess { artistPage ->
                                        database.query {
                                            getArtistById(artistId)?.let { currentArtist ->
                                                update(currentArtist, artistPage)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if (description.value == null && descriptionRuns.value == null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val artistName = album?.artists?.firstOrNull()?.name 
                                ?: database.albumWithSongs(albumId).first()?.artists?.firstOrNull()?.name
                            val wikiDescription = Wikipedia.fetchAlbumInfo(it.album.title, artistName)
                            if (wikiDescription != null) {
                                description.value = wikiDescription
                                val currentAlbum = database.album(albumId).first()
                                if (currentAlbum != null) {
                                    database.query {
                                        update(currentAlbum.album.copy(description = wikiDescription))
                                    }
                                }
                            } else {
                                val appleDescription = AppleMusicAboutAlbum.fetchAlbumDescription(it.album.title, artistName)
                                if (appleDescription != null) {
                                    description.value = appleDescription
                                    val currentAlbum = database.album(albumId).first()
                                    if (currentAlbum != null) {
                                        database.query {
                                            update(currentAlbum.album.copy(description = appleDescription))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.onFailure {
                    loadFailed.value = true
                    reportException(it)
                    if (it.message?.contains("NOT_FOUND") == true) {
                        val albumToDelete = album?.album
                        if (albumToDelete != null) {
                            database.query {
                                delete(albumToDelete)
                            }
                        }
                    }
                }
        }
    }
}
