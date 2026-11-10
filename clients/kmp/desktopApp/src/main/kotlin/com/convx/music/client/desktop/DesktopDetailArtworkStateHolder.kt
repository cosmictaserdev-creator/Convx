package com.convx.music.client.desktop

import com.convx.music.canvas.AppleArtist
import com.convx.music.canvas.AppleArtwork
import com.convx.music.canvas.AppleMusicCatalog
import com.convx.music.canvas.ArtistSignature
import com.convx.music.catalog.*
import com.music.innertube.InnertubeRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal data class DesktopDetailArtwork(
    val artist: AppleArtist? = null,
    val album: AppleArtwork? = null,
    val signatureUrl: String? = null,
    val following: Boolean = false,
    val updatingFollow: Boolean = false,
    val message: String? = null,
    val backgroundRgb: Int? = null,
)

internal class DesktopDetailArtworkStateHolder(
    private val innertube: InnertubeRepository,
    private val catalog: MusicCatalogRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutableState = MutableStateFlow(DesktopDetailArtwork())
    val state = mutableState.asStateFlow()
    private var loading: Job? = null
    private var followRequest: Job? = null
    private var entryId: CatalogId? = null

    fun load(entry: CatalogEntry?, signedIn: Boolean) {
        loading?.cancel()
        followRequest?.cancel()
        entryId = entry?.id
        mutableState.value = DesktopDetailArtwork()
        val name = when (entry) {
            is CatalogEntry.Artist -> entry.title
            is CatalogEntry.Album -> entry.artists.firstOrNull()?.name
            else -> null
        }?.takeIf { it.isNotBlank() } ?: return
        loading = scope.launch {
            supervisorScope {
                if (entry is CatalogEntry.Artist) launch {
                    val fallback = optionalLookup { DesktopArtworkColors.estimate(entry.artworkUrls.firstOrNull()?.let {
                        com.convx.music.designsystem.artworkUrlCandidates(it, 256).first()
                    }) }
                    if (mutableState.value.artist?.hero?.bgColor == null) mutableState.value = mutableState.value.copy(backgroundRgb = fallback)
                }
                if (entry is CatalogEntry.Album) launch {
                    val album = optionalLookup { AppleMusicCatalog.album(entry.title, name, priority = true) }
                    mutableState.value = mutableState.value.copy(album = album)
                }
                if (entry is CatalogEntry.Artist && signedIn) launch {
                    val followed = withContext(Dispatchers.Default) { catalog.library(CatalogLibraryCollection.ARTISTS) }
                    if (followed is CatalogResult.Success) mutableState.value = mutableState.value.copy(
                        following = followed.value.entries.any { it.id == entry.id })
                }
                val artist = optionalLookup { AppleMusicCatalog.artist(name) }
                mutableState.value = mutableState.value.copy(artist = artist, backgroundRgb = artist?.hero?.bgColor ?: mutableState.value.backgroundRgb)
                val hero = artist?.hero
                if (entry is CatalogEntry.Artist && hero != null && hero.bgColor == null) {
                    val color = optionalLookup { DesktopArtworkColors.estimate(hero.sized(256)) }
                    if (color != null) mutableState.value = mutableState.value.copy(backgroundRgb = color)
                }
                if (artist?.logo == null) {
                    val signature = optionalLookup { ArtistSignature.findUrl(name) }
                    mutableState.value = mutableState.value.copy(signatureUrl = signature)
                }
            }
        }
    }

    fun setFollowing(following: Boolean) {
        val id = entryId ?: return
        if (mutableState.value.updatingFollow) return
        mutableState.value = mutableState.value.copy(updatingFollow = true, message = null)
        followRequest = scope.launch {
            val success = try {
                withContext(Dispatchers.Default) {
                    val channelId = innertube.getChannelId(id.value)
                    innertube.subscribeChannel(channelId, following).getOrNull()?.status?.value in 200..299
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { false }
            mutableState.value = mutableState.value.copy(updatingFollow = false,
                following = if (success) following else mutableState.value.following,
                message = if (success) null else "Couldn't update this artist. Try again.")
        }
    }

    fun close() = scope.cancel()

    private suspend fun <T> optionalLookup(block: suspend () -> T?): T? = try {
        withContext(Dispatchers.IO) { block() }
    } catch (cancelled: CancellationException) { throw cancelled }
    catch (_: Exception) { null }
}
