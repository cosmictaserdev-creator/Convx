/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.filterExplicit
import com.music.innertube.models.filterVideoSongs
import com.music.innertube.models.filterYoutubeShorts
import com.music.innertube.pages.ArtistPage
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.HideYoutubeShortsKey
import com.convx.music.db.MusicDatabase
import com.convx.music.extensions.filterExplicit
import com.convx.music.extensions.filterExplicitAlbums
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.convx.music.utils.PageCaches
import java.io.File
import com.convx.music.utils.ArtistWordmarkAssets
import com.convx.music.extensions.filterVideoSongs as filterVideoSongsLocal

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistViewModel(
    private val context: Context,
    database: MusicDatabase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val artistId = requireNotNull(savedStateHandle.get<String>("artistId")) { "Missing artistId" }
    val thumbArg = savedStateHandle.get<String>("thumb")?.takeIf { it.isNotEmpty() }
    var artistPage by mutableStateOf<ArtistPage?>(null)
    var artistSignatureUrl by mutableStateOf<String?>(null)
        private set
    var artistBundledLogo by mutableStateOf<File?>(null)
        private set
    private var signatureLookupName: String? = null

    val libraryArtist = database.artist(artistId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    val librarySongs = context.dataStore.data
        .map { (it[HideExplicitKey] ?: false) to ((it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false)) }
        .distinctUntilChanged()
        .flatMapLatest { (hideExplicit, hideVideoSongs) ->
            database.artistSongsPreview(artistId).map { it.filterExplicit(hideExplicit).filterVideoSongsLocal(hideVideoSongs) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val libraryAlbums = context.dataStore.data
        .map { it[HideExplicitKey] ?: false }
        .distinctUntilChanged()
        .flatMapLatest { hideExplicit ->
            database.artistAlbumsPreview(artistId).map { it.filterExplicitAlbums(hideExplicit) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            database.artist(artistId).first()?.artist?.name?.takeIf { it.isNotBlank() }?.let { name ->
                com.convx.music.utils.AppleArt.prefetch(context, com.convx.music.utils.AppleArtQuery.Artist(name))
            }
        }
        viewModelScope.launch {
            context.dataStore.data
                .map {
                    Triple(
                        it[HideExplicitKey] ?: false,
                        (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false),
                        it[HideYoutubeShortsKey] ?: false
                    )
                }
                .distinctUntilChanged()
                .collect {
                    fetchArtistsFromYTM()
                }
        }
    }

    fun fetchArtistsFromYTM() {
        viewModelScope.launch {
            val page = PageCaches.artistPages.get(artistId) {
                YouTube.artist(artistId).onFailure { reportException(it) }.getOrNull()
            } ?: return@launch
            com.convx.music.utils.AppleArt.prefetch(context, com.convx.music.utils.AppleArtQuery.Artist(page.artist.title))
            artistPage = page.filtered()
        }
    }

    fun fetchArtistSignature(name: String) {
        val normalized = name.trim()
        if (normalized.isBlank() || signatureLookupName == normalized) return
        signatureLookupName = normalized
        val saved = ArtistWordmarkAssets.saved(context, normalized)
        artistBundledLogo = saved.appleLogo
        artistSignatureUrl = saved.signatureUrl
        if (saved.appleLogo != null && saved.signature != null) return
        viewModelScope.launch {
            val assets = ArtistWordmarkAssets.bundle(context, normalized)
            if (signatureLookupName == normalized) {
                artistBundledLogo = assets.appleLogo
                artistSignatureUrl = assets.signatureUrl
            }
        }
    }

    private suspend fun ArtistPage.filtered(): ArtistPage {
        val hideExplicit = context.dataStore.get(HideExplicitKey, false)
        val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
        val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
        return copy(
            sections = sections
                .map { it.copy(items = it.items.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs).filterYoutubeShorts(hideYoutubeShorts)) }
                .filter { it.items.isNotEmpty() },
        )
    }
}
