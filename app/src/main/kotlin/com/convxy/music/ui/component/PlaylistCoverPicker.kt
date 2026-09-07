/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.music.innertube.YouTube
import com.convxy.music.LocalDatabase
import com.convxy.music.R
import com.convxy.music.constants.DarkModeKey
import com.convxy.music.ui.screens.settings.DarkMode
import com.convxy.music.db.entities.Playlist
import com.convxy.music.ui.screens.playlist.uriToByteArray
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.reportException
import com.yalantis.ucrop.UCrop
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Sets up the pick → crop → save flow for a playlist cover and returns a lambda that
 * launches it. Saves to the DB thumbnail for local playlists; uploads to YouTube Music
 * for synced ones. Shared by the playlist header and the playlist menu.
 *
 * @param onError surfaces a message (e.g. HTTP failure) to the caller.
 */
@Composable
fun rememberPlaylistCoverPicker(
    playlist: Playlist,
    onError: (String) -> Unit = {},
    onCoverSaved: (String) -> Unit = {},
): () -> Unit {
    val context = LocalContext.current
    val database = LocalDatabase.current
    val (darkMode) = rememberEnumPreference(DarkModeKey, DarkMode.AUTO)
    val darkTheme = darkMode == DarkMode.ON || (darkMode == DarkMode.AUTO && isSystemInDarkTheme())
    val cropColor = MaterialTheme.colorScheme

    val result = remember { mutableStateOf<Uri?>(null) }
    var pendingCropDestUri by remember { mutableStateOf<Uri?>(null) }

    val cropLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val output = res.data?.let { UCrop.getOutput(it) } ?: pendingCropDestUri
            if (output != null) result.value = output
        }
    }

    val pickLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { sourceUri ->
            val destFile = File(context.cacheDir, "playlist_cover_crop_${System.currentTimeMillis()}.jpg")
            val destUri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", destFile)
            pendingCropDestUri = destUri
            val options = UCrop.Options().apply {
                setCompressionFormat(Bitmap.CompressFormat.JPEG)
                setCompressionQuality(90)
                setHideBottomControls(true)
                setToolbarTitle(context.getString(R.string.edit_playlist_cover))
                setStatusBarLight(!darkTheme)
                setToolbarColor(cropColor.surface.toArgb())
                setToolbarWidgetColor(cropColor.inverseSurface.toArgb())
                setRootViewBackgroundColor(cropColor.surface.toArgb())
                setLogoColor(cropColor.surface.toArgb())
            }
            val intent = UCrop.of(sourceUri, destUri)
                .withAspectRatio(1f, 1f)
                .withOptions(options)
                .getIntent(context)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            cropLauncher.launch(intent)
        }
    }

    LaunchedEffect(result.value) {
        val uri = result.value ?: return@LaunchedEffect
        withContext(Dispatchers.IO) {
            // Persist the crop out of the volatile cache dir and store it as a local DB
            // override so EVERY card (local or online) updates immediately, regardless of
            // whether the playlist can be synced to YouTube.
            val localUri = persistPlaylistCover(context, uri, playlist.playlist.id) ?: uri
            database.query { update(playlist.playlist.copy(thumbnailUrl = localUri.toString())) }
            
            withContext(Dispatchers.Main) {
                onCoverSaved(localUri.toString())
            }

            // Owned/synced playlists: also push the cover to YouTube (best-effort).
            val browseId = playlist.playlist.browseId
            if (browseId != null && playlist.playlist.isEditable) {
                val bytes = uriToByteArray(context, uri)
                if (bytes != null) {
                    YouTube.uploadCustomThumbnailLink(browseId, bytes)
                        .onSuccess { newUrl ->
                            database.query { update(playlist.playlist.copy(thumbnailUrl = newUrl)) }
                        }
                        .onFailure {
                            if (it is ClientRequestException) {
                                onError("${it.response.status.value} ${it.response.status.description}")
                            }
                            reportException(it)
                        }
                }
            }
        }
        result.value = null
    }

    return {
        pickLauncher.launch(
            PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}

/**
 * Copies the cropped cover out of the volatile cache dir into filesDir so it survives cache
 * eviction, using a fresh timestamped filename (which also busts Coil's uri-keyed cache) and
 * deleting any prior cover for this playlist. Returns the persisted file uri, or null on failure.
 */
private fun persistPlaylistCover(context: Context, src: Uri, playlistId: String): Uri? = runCatching {
    val safeId = playlistId.replace(Regex("[^A-Za-z0-9_-]"), "_")
    val dir = File(context.filesDir, "playlist_covers").apply { mkdirs() }
    dir.listFiles { f -> f.name.startsWith("${safeId}_") }?.forEach { it.delete() }
    val dest = File(dir, "${safeId}_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(src)?.use { input ->
        dest.outputStream().use { out -> input.copyTo(out) }
    } ?: return@runCatching null
    dest.toUri()
}.getOrNull()
