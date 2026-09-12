/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens.settings

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import com.nexamusic.app.ui.component.GlassSwitchCompat as Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexamusic.app.R
import com.nexamusic.app.constants.HomeBackgroundAnimateKey
import com.nexamusic.app.constants.HomeBackgroundBlurKey
import com.nexamusic.app.constants.HomeBackgroundDimKey
import com.nexamusic.app.constants.HomeBackgroundEnabledKey
import com.nexamusic.app.constants.HomeBackgroundIsVideoKey
import com.nexamusic.app.constants.HomeBackgroundPathKey
import com.nexamusic.app.constants.HomeBackgroundQualityKey
import com.nexamusic.app.constants.LibraryBackgroundMode
import com.nexamusic.app.constants.LibraryBackgroundModeKey
import com.nexamusic.app.ui.component.DefaultDialog
import com.nexamusic.app.ui.component.HomeVideoBackground
import com.nexamusic.app.ui.component.rememberHomeBackgroundTargetSize
import com.nexamusic.app.ui.component.Material3SettingsGroup
import com.nexamusic.app.ui.component.Material3SettingsItem
import com.nexamusic.app.utils.rememberEnumPreference
import com.nexamusic.app.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** Caps on a picked background file, so a stray 4K/multi-gigabyte video or GIF
 *  doesn't get copied into app storage and tank Home's scroll perf. Video gets
 *  a higher ceiling than image/GIF since it's expected to be the bigger file. */
private const val MaxHomeBackgroundImageBytes = 20L * 1024 * 1024
private const val MaxHomeBackgroundVideoBytes = 80L * 1024 * 1024

/** Reads the picked Uri's size via the content resolver without opening the
 *  whole stream. -1 if the provider doesn't report a size (allowed through —
 *  can't enforce a cap it can't measure). */
private fun mediaSizeBytes(context: android.content.Context, uri: Uri): Long {
    return context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.SIZE), null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
        if (idx >= 0 && cursor.moveToFirst()) cursor.getLong(idx) else -1L
    } ?: -1L
}


/** Copies a picked image/video into app storage so the background survives
 *  without a persistable URI permission. Unique filename cache-busts Coil.
 *  Returns the absolute path, or null on failure.
 *
 *  Images are re-encoded down to [maxWidth]x[maxHeight] first. A modern phone
 *  camera or a downloaded wallpaper is routinely 20-50 MP, and keeping the raw
 *  bytes whole means every screen that shows the background pays a full-size
 *  decode and downsample, with the file sitting in app storage at its original
 *  size forever. [maxWidth]/[maxHeight] are the caller's target — see
 *  [rememberHomeBackgroundTargetSize] for how that's derived from the device's
 *  own screen resolution and the user's quality preference.
 *  Videos are copied as-is: transcoding one is not a per-pick cost worth paying.
 *
 *  Decoding through Coil rather than BitmapFactory deliberately: it already
 *  applies EXIF orientation, so a portrait photo taken sideways does not come out
 *  rotated the way a hand-rolled decode would.
 */
private suspend fun copyBackgroundMedia(
    context: android.content.Context,
    source: Uri,
    isVideo: Boolean,
    isGif: Boolean = false,
    maxWidth: Int,
    maxHeight: Int,
): String? = runCatching {
    val ext = if (isVideo) "mp4" else if (isGif) "gif" else "jpg"
    val dest = File(context.filesDir, "home_background_${System.currentTimeMillis()}.$ext")

    if (isVideo || isGif) {
        // Raw copy, no bake: decoding a GIF down to one frame and re-encoding it as a JPEG
        // (the still-image path below) throws the animation away permanently. Coil already
        // has a GIF decoder registered (App.kt), so the file plays as-is once HomeImageBackground
        // points AsyncImage at it — animation is only lost again if blur bakes it to a static
        // file, which is an intentional perf tradeoff there, not this copy step's problem.
        context.contentResolver.openInputStream(source)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        return@runCatching dest.absolutePath
    }

    val request = ImageRequest.Builder(context)
        .data(source)
        .size(maxWidth, maxHeight)
        .allowHardware(false) // compress() needs readable pixels
        .build()
    val bitmap = (context.imageLoader.execute(request) as? SuccessResult)?.image?.toBitmap()

    if (bitmap == null) {
        // Decode failed (exotic format, corrupt file). The raw copy still renders
        // through Coil's own decoders, so fall back rather than losing the pick.
        context.contentResolver.openInputStream(source)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        return@runCatching dest.absolutePath
    }

    dest.outputStream().use { output ->
        // Was 90 — fine at the old flat 1080x1920 cap, but now that the stored
        // size can go up to full screen resolution, the same ratio leaves more
        // visible blocking in smooth/gradient regions at the larger pixel
        // count. Bump quality to match.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, output)
    }
    dest.absolutePath
}.getOrNull()

/**
 * Background-image controls (preview + enable/pick/blur/dim/animate/remove) —
 * embedded inside [ThemeScreen] rather than its own settings screen, since it's
 * just another facet of the app's theme. See PLAN notes: moved out of
 * AppearanceSettings' standalone "settings/appearance/homebackground" route.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBackgroundControls() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val (enabled, onEnabledChange) = rememberPreference(HomeBackgroundEnabledKey, defaultValue = false)
    val (path, onPathChange) = rememberPreference(HomeBackgroundPathKey, defaultValue = "")
    val (blur, onBlurChange) = rememberPreference(HomeBackgroundBlurKey, defaultValue = 20f)
    val (dim, onDimChange) = rememberPreference(HomeBackgroundDimKey, defaultValue = 0.4f)
    val (animate, onAnimateChange) = rememberPreference(HomeBackgroundAnimateKey, defaultValue = false)
    val (isVideo, onIsVideoChange) = rememberPreference(HomeBackgroundIsVideoKey, defaultValue = false)
    val (quality, onQualityChange) = rememberPreference(HomeBackgroundQualityKey, defaultValue = 1f)
    val (targetWidth, targetHeight) = rememberHomeBackgroundTargetSize(quality)

    var showBlurDialog by rememberSaveable { mutableStateOf(false) }
    var showDimDialog by rememberSaveable { mutableStateOf(false) }
    var showQualityDialog by rememberSaveable { mutableStateOf(false) }
    var pendingVideoUri by remember { mutableStateOf<Uri?>(null) }

    fun applyPickedMedia(uri: Uri, video: Boolean, gif: Boolean = false) {
        val previous = path
        scope.launch {
            val newPath = withContext(Dispatchers.IO) {
                copyBackgroundMedia(context, uri, video, gif, targetWidth, targetHeight)
            }
            if (newPath != null) {
                onPathChange(newPath)
                onIsVideoChange(video)
                if (!enabled) onEnabledChange(true)
                if (previous.isNotEmpty()) withContext(Dispatchers.IO) { File(previous).delete() }
            }
        }
    }

    val pickLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val mimeType = context.contentResolver.getType(uri)
        val video = mimeType?.startsWith("video/") == true
        val gif = mimeType == "image/gif"
        val cap = if (video) MaxHomeBackgroundVideoBytes else MaxHomeBackgroundImageBytes
        val size = mediaSizeBytes(context, uri)
        if (size > cap) {
            val capMb = cap / (1024 * 1024)
            android.widget.Toast.makeText(
                context,
                context.getString(R.string.home_background_too_large, "${capMb}MB"),
                android.widget.Toast.LENGTH_LONG
            ).show()
            return@rememberLauncherForActivityResult
        }
        if (video) {
            // Video decode/blur runs every frame, unlike a static image — warn
            // before committing since this can visibly cost battery/frame time
            // on lower-end devices.
            pendingVideoUri = uri
        } else {
            applyPickedMedia(uri, video = false, gif = gif)
        }
    }

    if (pendingVideoUri != null) {
        DefaultDialog(
            onDismiss = { pendingVideoUri = null },
            buttons = {
                TextButton(onClick = { pendingVideoUri = null }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = {
                    applyPickedMedia(pendingVideoUri!!, video = true)
                    pendingVideoUri = null
                }) { Text(stringResource(R.string.home_background_video_confirm)) }
            }
        ) {
            Text(
                text = stringResource(R.string.home_background_video_warning),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
    }

    Column(
        Modifier.padding(horizontal = 16.dp),
    ) {
        // Live preview: image with the same blur + dim the home screen applies.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        ) {
            if (path.isNotEmpty() && isVideo) {
                HomeVideoBackground(path = path, blur = blur, dim = dim)
            } else if (path.isNotEmpty()) {
                // Mirrors HomeImageBackground's own steady state: a real live
                // RenderEffect blur, same as what the home screen actually shows.
                val previewRequest = remember(path) {
                    ImageRequest.Builder(context)
                        .data(File(path))
                        .build()
                }
                AsyncImage(
                    model = previewRequest,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (blur > 0f) Modifier.blur(blur.dp) else Modifier),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = dim)),
                )
            } else {
                Text(
                    text = stringResource(R.string.home_background_image_none),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        Material3SettingsGroup(
            title = stringResource(R.string.home_background),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.home_background_enable)) },
                    trailingContent = {
                        Switch(
                            checked = enabled,
                            onCheckedChange = onEnabledChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enabled) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnabledChange(!enabled) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.image),
                    title = { Text(stringResource(R.string.home_background_image)) },
                    description = {
                        Text(
                            when {
                                path.isEmpty() -> stringResource(R.string.home_background_image_none)
                                isVideo -> stringResource(R.string.home_background_video_set)
                                else -> stringResource(R.string.home_background_image_set)
                            }
                        )
                    },
                    onClick = {
                        pickLauncher.launch(
                            PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.sliders),
                    title = { Text(stringResource(R.string.home_background_blur)) },
                    onClick = { showBlurDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.home_background_dim)) },
                    onClick = { showDimDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.sliders),
                    title = { Text(stringResource(R.string.home_background_quality)) },
                    description = { Text(stringResource(R.string.home_background_quality_desc)) },
                    onClick = { showQualityDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.home_background_animate)) },
                    description = { Text(stringResource(R.string.home_background_animate_desc)) },
                    trailingContent = {
                        Switch(
                            checked = animate,
                            onCheckedChange = onAnimateChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (animate) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onAnimateChange(!animate) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.delete),
                    title = { Text(stringResource(R.string.home_background_remove)) },
                    onClick = {
                        val current = path
                        if (current.isNotEmpty()) scope.launch(Dispatchers.IO) { File(current).delete() }
                        onPathChange("")
                        onIsVideoChange(false)
                        onEnabledChange(false)
                    }
                ),
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // What the Library screen falls back to when the custom image above is
        // off/unset — plain, a flat theme-color wash, or a blurred thumbnail
        // pulled from the library's own content.
        val (libraryBackgroundMode, onLibraryBackgroundModeChange) = rememberEnumPreference(
            LibraryBackgroundModeKey,
            LibraryBackgroundMode.THUMBNAIL_BLUR,
        )
        Material3SettingsGroup(
            title = stringResource(R.string.library_background),
            items = LibraryBackgroundMode.entries.map { mode ->
                Material3SettingsItem(
                    icon = painterResource(
                        when (mode) {
                            LibraryBackgroundMode.PLAIN -> R.drawable.close
                            LibraryBackgroundMode.THEME -> R.drawable.palette
                            LibraryBackgroundMode.THUMBNAIL_BLUR -> R.drawable.image
                        }
                    ),
                    title = {
                        Text(
                            stringResource(
                                when (mode) {
                                    LibraryBackgroundMode.PLAIN -> R.string.library_background_plain
                                    LibraryBackgroundMode.THEME -> R.string.library_background_theme
                                    LibraryBackgroundMode.THUMBNAIL_BLUR -> R.string.library_background_thumbnail_blur
                                }
                            )
                        )
                    },
                    trailingContent = {
                        if (mode == libraryBackgroundMode) {
                            Icon(painter = painterResource(R.drawable.check), contentDescription = null)
                        }
                    },
                    onClick = { onLibraryBackgroundModeChange(mode) }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showBlurDialog) {
        var tempValue by remember { mutableFloatStateOf(blur) }
        DefaultDialog(
            onDismiss = { tempValue = blur; showBlurDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 20f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = blur; showBlurDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onBlurChange(tempValue); showBlurDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.home_background_blur), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.0f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..50f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showDimDialog) {
        var tempValue by remember { mutableFloatStateOf(dim) }
        DefaultDialog(
            onDismiss = { tempValue = dim; showDimDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.4f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = dim; showDimDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onDimChange(tempValue); showDimDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.home_background_dim), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.0f%%".format(tempValue * 100), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..1f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showQualityDialog) {
        var tempValue by remember { mutableFloatStateOf(quality) }
        DefaultDialog(
            onDismiss = { tempValue = quality; showQualityDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 1f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = quality; showQualityDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onQualityChange(tempValue); showQualityDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.home_background_quality), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.0f%%".format(tempValue * 100), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0.3f..1f, modifier = Modifier.fillMaxWidth())
                Text(
                    text = stringResource(R.string.home_background_quality_reapply_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
