/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings

import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.animateFloatAsState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.only
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.rememberScrollState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.verticalScroll
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.AlertDialog
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.LinearProgressIndicator
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
// Routed to the glass dispatcher: every Slider( in this file is a settings value
// slider, and this is the single place that decides between the liquid glass rail
// and Material's. Call sites are unchanged.
import com.convxy.music.ui.component.GlassSlider as Slider
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TextButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBarScrollBehavior
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.LaunchedEffect
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.getValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableLongStateOf
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateOf
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.remember
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.rememberCoroutineScope
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.setValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.graphics.StrokeCap
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.platform.LocalContext
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.stringResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavController
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.playback.ExoDownloadService
import com.convxy.music.ui.utils.appTopBarWindowInsets
import coil3.SingletonImageLoader
import com.convxy.music.ui.utils.appTopBarWindowInsets
import coil3.annotation.DelicateCoilApi
import com.convxy.music.ui.utils.appTopBarWindowInsets
import coil3.annotation.ExperimentalCoilApi
import com.convxy.music.ui.utils.appTopBarWindowInsets
import coil3.imageLoader
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.LocalDatabase
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.R
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.MaxImageCacheSizeKey
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.MaxSongCacheSizeKey
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.extensions.tryOrNull
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.ActionPromptDialog
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.Material3SettingsGroup
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.Material3SettingsItem
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.formatFileSize
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.utils.rememberPreference
import com.convxy.music.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.Dispatchers
import com.convxy.music.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.delay
import com.convxy.music.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.flow.first
import com.convxy.music.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.isActive
import com.convxy.music.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.launch
import com.convxy.music.ui.utils.appTopBarWindowInsets
import okio.ByteString.Companion.encodeUtf8
import com.convxy.music.ui.utils.appTopBarWindowInsets
import kotlin.math.roundToInt

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class, DelicateCoilApi::class)
@Composable
fun StorageSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current
    val imageDiskCache = context.imageLoader.diskCache ?: return
    val playerCache = LocalPlayerConnection.current?.service?.playerCache ?: return
    val downloadCache = LocalPlayerConnection.current?.service?.downloadCache ?: return

    val coroutineScope = rememberCoroutineScope()
    val songCacheString = stringResource(R.string.song_cache).lowercase()
    val imageCacheString = stringResource(R.string.image_cache).lowercase()
    val (maxImageCacheSize, onMaxImageCacheSizeChange) = rememberPreference(
        key = MaxImageCacheSizeKey,
        defaultValue = 512
    )
    val (maxSongCacheSize, onMaxSongCacheSizeChange) = rememberPreference(
        key = MaxSongCacheSizeKey,
        defaultValue = 1024
    )

    var clearDownloads by remember { mutableStateOf(false) }
    var clearCacheDialog by remember { mutableStateOf(false) }
    var clearImageCacheDialog by remember { mutableStateOf(false) }

    // State for the confirmation dialog
    var showCacheWarningDialog by remember { mutableStateOf(false) }
    var cacheType by remember { mutableStateOf("") }
    var cacheUsage by remember { androidx.compose.runtime.mutableLongStateOf(0L) }
    var onConfirmAction by remember { mutableStateOf<() -> Unit>({}) }


    var imageCacheSize by remember {
        androidx.compose.runtime.mutableLongStateOf(imageDiskCache.size)
    }
    var playerCacheSize by remember {
        androidx.compose.runtime.mutableLongStateOf(tryOrNull { playerCache.cacheSpace } ?: 0)
    }
    var downloadCacheSize by remember {
        mutableLongStateOf(tryOrNull { downloadCache.cacheSpace } ?: 0)
    }
    val imageCacheProgress by animateFloatAsState(
        targetValue = (imageCacheSize.toFloat() / (maxImageCacheSize * 1024 * 1024L)).coerceIn(
            0f,
            1f
        ),
        label = "imageCacheProgress",
    )
    val playerCacheProgress by animateFloatAsState(
        targetValue = (playerCacheSize.toFloat() / (maxSongCacheSize * 1024 * 1024L)).coerceIn(
            0f,
            1f
        ),
        label = "playerCacheProgress",
    )

    LaunchedEffect(maxImageCacheSize) {
        SingletonImageLoader.reset()
        if (maxImageCacheSize == 0) {
            coroutineScope.launch(Dispatchers.IO) {
                imageDiskCache.clear()
            }
        }
    }
    LaunchedEffect(maxSongCacheSize) {
        if (maxSongCacheSize == 0) {
            coroutineScope.launch(Dispatchers.IO) {
                playerCache.keys.forEach { key ->
                    playerCache.removeResource(key)
                }
            }
        }
    }

    LaunchedEffect(imageDiskCache) {
        while (isActive) {
            delay(500)
            imageCacheSize = imageDiskCache.size
        }
    }
    LaunchedEffect(playerCache) {
        while (isActive) {
            delay(500)
            playerCacheSize = tryOrNull { playerCache.cacheSpace } ?: 0
        }
    }
    LaunchedEffect(downloadCache) {
        while (isActive) {
            delay(500)
            downloadCacheSize = tryOrNull { downloadCache.cacheSpace } ?: 0
        }
    }

    if (clearDownloads) {
        ActionPromptDialog(
            title = stringResource(R.string.clear_all_downloads),
            onDismiss = { clearDownloads = false },
            onConfirm = {
                // Route through DownloadService/DownloadManager, not the cache directly:
                // stomping downloadCache bypasses DownloadManager's own index, which keeps
                // reporting those songs as downloaded (and can re-trigger them) since it
                // never saw the removal.
                DownloadService.sendRemoveAllDownloads(
                    context,
                    ExoDownloadService::class.java,
                    false,
                )
                clearDownloads = false
            },
            onCancel = { clearDownloads = false },
            content = {
                Text(text = stringResource(R.string.clear_downloads_dialog))
            }
        )
    }
    if (clearCacheDialog) {
        ActionPromptDialog(
            title = stringResource(R.string.clear_song_cache),
            onDismiss = { clearCacheDialog = false },
            onConfirm = {
                coroutineScope.launch(Dispatchers.IO) {
                    playerCache.keys.forEach { key ->
                        playerCache.removeResource(key)
                    }
                }
                clearCacheDialog = false
            },
            onCancel = { clearCacheDialog = false },
            content = {
                Text(text = stringResource(R.string.clear_song_cache_dialog))
            }
        )
    }
    if (clearImageCacheDialog) {
        ActionPromptDialog(
            title = stringResource(R.string.clear_image_cache),
            onDismiss = { clearImageCacheDialog = false },
            onConfirm = {
                coroutineScope.launch(Dispatchers.IO) {
                    val urlsToPreserve = mutableSetOf<String>()
                    val downloadedSongs = try {
                        database.downloadedSongsByNameAsc().first()
                    } catch (e: Exception) {
                        emptyList()
                    }
                    downloadedSongs.forEach { song ->
                        song.song.thumbnailUrl?.let { urlsToPreserve.add(it.encodeUtf8().sha256().hex()) }
                        song.album?.thumbnailUrl?.let { urlsToPreserve.add(it.encodeUtf8().sha256().hex()) }
                    }
                    val directory = imageDiskCache.directory.toFile()
                    if (directory.exists() && directory.isDirectory) {
                        directory.listFiles()?.forEach { file ->
                            if (file.isFile && !file.name.startsWith("journal")) {
                                val isPreserved = urlsToPreserve.any { hash -> file.name.startsWith(hash) }
                                if (!isPreserved) {
                                    file.delete()
                                }
                            }
                        }
                    }
                }
                clearImageCacheDialog = false
            },
            onCancel = { clearImageCacheDialog = false },
            content = {
                Text(text = stringResource(R.string.clear_image_cache_dialog))
            }
        )
    }

    // Confirmation Dialog
    if (showCacheWarningDialog) {
        AlertDialog(
            onDismissRequest = { showCacheWarningDialog = false },
            title = { Text(stringResource(R.string.cache_size_warning_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.cache_size_warning_message,
                        formatFileSize(cacheUsage),
                        cacheType
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmAction()
                        showCacheWarningDialog = false
                    }
                ) {
                    Text(
                        stringResource(R.string.cache_size_warning_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showCacheWarningDialog = false }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        )
    }

    Column(
        Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Top
                )
            )
        )
        Material3SettingsGroup(
            title = stringResource(R.string.storage),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.storage),
                    title = { Text(stringResource(R.string.downloaded_songs)) },
                    description = {
                        Text(text = formatFileSize(downloadCacheSize))
                    }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.clear_all),
                    title = { Text(stringResource(R.string.clear_all_downloads)) },
                    onClick = {
                        clearDownloads = true
                    }
                )
            )
        )

        Material3SettingsGroup(
            title = stringResource(R.string.song_cache),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.cached),
                    title = { Text(stringResource(R.string.max_song_cache_size)) },
                    description = {
                        val songCacheValues =
                            remember { listOf(0, 128, 256, 512, 1024, 2048, 4096, 8192, -1) }
                        Column {
                            Text(
                                text = when (maxSongCacheSize) {
                                    0 -> stringResource(R.string.disable)
                                    -1 -> stringResource(R.string.unlimited)
                                    else -> formatFileSize(maxSongCacheSize * 1024 * 1024L)
                                }
                            )
                            Slider(
                                value = songCacheValues.indexOf(maxSongCacheSize).toFloat(),
                                onValueChange = {
                                    val newValue = songCacheValues[it.roundToInt()]
                                    val newLimitInBytes = if (newValue == -1) {
                                        Long.MAX_VALUE
                                    } else {
                                        newValue * 1024 * 1024L
                                    }

                                    if (newLimitInBytes < playerCacheSize) {
                                        cacheUsage = playerCacheSize
                                        cacheType = songCacheString
                                        onConfirmAction = { onMaxSongCacheSizeChange(newValue) }
                                        showCacheWarningDialog = true
                                    } else {
                                        onMaxSongCacheSizeChange(newValue)
                                    }
                                },
                                steps = songCacheValues.size - 2,
                                valueRange = 0f..(songCacheValues.size - 1).toFloat()
                            )
                            LinearProgressIndicator(
                                progress = { playerCacheProgress },
                                modifier = Modifier.fillMaxWidth(),
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.padding(2.dp))
                            Text(
                                text = if (maxSongCacheSize == -1) {
                                    formatFileSize(playerCacheSize)
                                } else {
                                    "${formatFileSize(playerCacheSize)} / ${
                                        formatFileSize(
                                            maxSongCacheSize * 1024 * 1024L
                                        )
                                    }"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.clear_all),
                    title = { Text(stringResource(R.string.clear_song_cache)) },
                    onClick = {
                        clearCacheDialog = true
                    }
                )
            )
        )

        Material3SettingsGroup(
            title = stringResource(R.string.image_cache),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.manage_search),
                    title = { Text(stringResource(R.string.max_image_cache_size)) },
                    description = {
                        val imageCacheValues =
                            remember { listOf(0, 128, 256, 512, 1024, 2048, 4096, 8192) }
                        Column {
                            Text(
                                text = when (maxImageCacheSize) {
                                    0 -> stringResource(R.string.disable)
                                    else -> formatFileSize(maxImageCacheSize * 1024 * 1024L)
                                }
                            )
                            Slider(
                                value = imageCacheValues.indexOf(maxImageCacheSize).toFloat(),
                                onValueChange = {
                                    val newValue = imageCacheValues[it.roundToInt()]
                                    val newLimitInBytes = newValue * 1024 * 1024L

                                    if (newLimitInBytes < imageCacheSize) {
                                        cacheUsage = imageCacheSize
                                        cacheType = imageCacheString
                                        onConfirmAction = { onMaxImageCacheSizeChange(newValue) }
                                        showCacheWarningDialog = true
                                    } else {
                                        onMaxImageCacheSizeChange(newValue)
                                    }
                                },
                                steps = imageCacheValues.size - 2,
                                valueRange = 0f..(imageCacheValues.size - 1).toFloat()
                            )
                            LinearProgressIndicator(
                                progress = { imageCacheProgress },
                                modifier = Modifier.fillMaxWidth(),
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.padding(2.dp))
                            Text(
                                text = "${formatFileSize(imageCacheSize)} / ${
                                    formatFileSize(
                                        maxImageCacheSize * 1024 * 1024L
                                    )
                                }",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.clear_all),
                    title = { Text(stringResource(R.string.clear_image_cache)) },
                    onClick = {
                        clearImageCacheDialog = true
                    }
                )
            )
        )
        Spacer(Modifier.padding(bottom = 30.dp))
    }

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.storage)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        }
    )
}
