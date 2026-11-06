/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music

import com.convx.music.utils.openSearchResults

import ai.onnxruntime.BuildConfig
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.convx.music.constants.ListenTogetherUsernameKey
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.listentogether.ListenTogetherManager
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import com.music.innertube.YouTube
import com.music.innertube.models.WatchEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import dagger.Lazy

/** Routes a share/open-with/notification intent to the right screen or playback action. */
internal class AppIntentRouter(
    private val activity: MainActivity,
    private val currentPlayerConnection: () -> PlayerConnection?,
    private val listenTogetherManagerProvider: Lazy<ListenTogetherManager>,
    private val partyManagerProvider: Lazy<com.convx.music.jam.JamManager>,
) {

    private val allowedIdPattern = Regex("^[A-Za-z0-9_\\-]+$")

    // Keep shared IDs from injecting extra route segments.
    private fun cleanId(raw: String?, min: Int = 4, max: Int = 64): String? {
        if (raw.isNullOrBlank()) return null

        val value = raw.trim()
        if (value.length !in min..max || !allowedIdPattern.matches(value)) return null
        return value
    }

    fun handleDeepLinkIntent(intent: Intent, navController: NavHostController) {
        if (intent.getBooleanExtra("open_update", false)) {
            intent.removeExtra("open_update")
            navController.navigate("update") { launchSingleTop = true }
            return
        }
        if (handleDebugIntent(intent, navController)) return

        val uri = intent.data ?: intent.extras?.getString(Intent.EXTRA_TEXT)?.toUri() ?: return
        intent.data = null
        intent.removeExtra(Intent.EXTRA_TEXT)
        routeSharedLink(uri, navController)
    }

    private fun routeSharedLink(uri: Uri, navController: NavHostController) {
        if (openPartyInvite(uri, navController)) return
        if (joinListenRoom(uri)) return

        when (uri.pathSegments.firstOrNull()) {
            "playlist" -> openPlaylist(uri, navController)
            "browse" -> cleanId(uri.lastPathSegment)?.let { browseId ->
                navController.navigate("album/$browseId") { launchSingleTop = true }
            }
            "channel", "c" -> cleanId(uri.lastPathSegment)?.let { artistId ->
                navController.navigate("artist/$artistId") { launchSingleTop = true }
            }
            "search" -> openSearch(uri, navController)
            else -> openYouTubeLink(uri)
        }
    }

    /** `convx://party/CODE?server=...` opens the Party screen, which joins the code on arrival. */
    private fun openPartyInvite(uri: Uri, navController: NavHostController): Boolean {
        if (!uri.scheme.equals("convx", ignoreCase = true) || !uri.host.equals("party", ignoreCase = true)) return false
        val code = cleanId(uri.pathSegments.firstOrNull(), min = 4, max = 16) ?: return true
        partyManagerProvider.get().offerInvite(code, uri.getQueryParameter("server"))
        navController.navigate("listen_together/party") { launchSingleTop = true }
        return true
    }

    private fun joinListenRoom(uri: Uri): Boolean {
        val rawCode = uri.getQueryParameter("code")
            ?: uri.getQueryParameter("room")
            ?: uri.pathSegments.getOrNull(1)
        val listenCode = cleanId(
            rawCode,
            min = 4,
            max = 16,
        )
        val isListenLink = uri.pathSegments.firstOrNull() == "listen" ||
            uri.host?.equals("listen", ignoreCase = true) == true
        if (listenCode == null || !isListenLink) return false

        // One listening session at a time: leaving any party first keeps two syncs off one player.
        partyManagerProvider.get().leave()
        val username = activity.dataStore.get(ListenTogetherUsernameKey, "").ifBlank { "Guest" }
        val manager = listenTogetherManagerProvider.get()
        manager.joinRoom(listenCode, username)
        return true
    }

    private fun openPlaylist(uri: Uri, navController: NavHostController) {
        val playlistId = cleanId(uri.getQueryParameter("list"), max = 128) ?: return
        if (!playlistId.startsWith("OLAK5uy_")) {
            navController.navigate("online_playlist/$playlistId") { launchSingleTop = true }
            return
        }

        activity.lifecycle.coroutineScope.launch(Dispatchers.IO) {
            YouTube.albumSongs(playlistId).onSuccess { songs ->
                cleanId(songs.firstOrNull()?.album?.id)?.let { browseId ->
                    withContext(Dispatchers.Main) {
                        navController.navigate("album/$browseId") { launchSingleTop = true }
                    }
                }
            }.onFailure { reportException(it) }
        }
    }

    private fun openSearch(uri: Uri, navController: NavHostController) {
        val query = uri.getQueryParameter("q")?.takeIf { it.isNotBlank() }?.take(200) ?: return
        navController.openSearchResults(query) { launchSingleTop = true }
    }

    private fun openYouTubeLink(uri: Uri) {
        val path = uri.pathSegments.firstOrNull()
        val videoId = cleanId(
            when {
                path == "watch" -> uri.getQueryParameter("v")
                uri.host == "youtu.be" -> uri.pathSegments.firstOrNull()
                else -> null
            },
            min = 6,
            max = 32,
        )
        val playlistId = cleanId(uri.getQueryParameter("list"), max = 128)

        if (videoId != null || playlistId != null) {
            playFromYouTubeLink(videoId, playlistId)
        }
    }

    private fun handleDebugIntent(intent: Intent, navController: NavHostController): Boolean {
        // Debug intents let emulator tests open real destinations directly.
        if (!BuildConfig.DEBUG || !intent.hasExtra("debug_screen_route")) return false

        val route = intent.getStringExtra("debug_screen_route")
        val libraryFilter = intent.getStringExtra("debug_library_filter")
        intent.removeExtra("debug_screen_route")
        intent.removeExtra("debug_library_filter")

        if (!route.isNullOrBlank()) {
            activity.lifecycleScope.launch {
                if (libraryFilter != null) {
                    activity.dataStore.edit {
                        it[com.convx.music.constants.ChipSortTypeKey] = libraryFilter
                    }
                }
                runCatching {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }.onFailure { Timber.tag("ScreenPass").e(it, "Cannot open %s", route) }
            }
        }
        return true
    }

    private fun playFromYouTubeLink(
        videoId: String?,
        playlistId: String?,
    ) {
        activity.lifecycle.coroutineScope.launch(Dispatchers.IO) {
            YouTube.queue(videoId?.let(::listOf), playlistId)
                .onSuccess { queue ->
                    val firstItem = queue.firstOrNull()
                    withContext(Dispatchers.Main) {
                        currentPlayerConnection()?.playQueue(
                            YouTubeQueue(
                                endpoint = WatchEndpoint(
                                    videoId = firstItem?.id,
                                    playlistId = playlistId,
                                ),
                                preloadItem = firstItem?.toMediaMetadata(),
                            ),
                        )
                    }
                }
                .onFailure(::reportException)
        }
    }
}
