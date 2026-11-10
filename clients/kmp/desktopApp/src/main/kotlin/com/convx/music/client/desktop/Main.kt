package com.convx.music.client.desktop

import dev.nucleusframework.macoscompose.window.MacosDecoratedWindow
import dev.nucleusframework.window.WindowsBackdrop
import dev.nucleusframework.window.WindowsBackdropStyle

import androidx.room.Room
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowDecoration
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.convx.music.account.SecureSessionStore
import com.convx.music.account.presentation.musicAccountPresentationModule
import com.convx.music.catalog.presentation.catalogPresentationModule
import com.convx.music.library.libraryCoreModule
import com.convx.music.library.data.libraryDataModule
import com.convx.music.library.data.MusicLibraryDatabase
import com.convx.music.library.data.buildMusicLibraryDatabase
import com.convx.music.playback.AudioStreamResolver
import com.convx.music.modules.ModuleEnvironment
import com.convx.music.modules.ModuleAudioStreamResolver
import com.convx.music.modules.DesktopModuleStore
import java.nio.file.Path
import com.convx.music.playback.data.PlaybackSettingsDatabase
import com.convx.music.playback.data.buildPlaybackSettingsDatabase
import com.convx.music.playback.data.playbackDataModule
import com.convx.music.playback.presentation.playbackPresentationModule
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import com.convx.music.playback.presentation.LyricsStateHolder
import com.convx.music.lyrics.data.desktopLyricsSources
import com.convx.music.playback.PlaybackSettingsRepository
import com.convx.music.playback.PlaybackCheckpointRepository
import com.convx.music.playback.PlaybackTrackAnalysisRepository
import com.music.innertube.innertubeDataModule
import kotlinx.coroutines.runBlocking
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import java.io.File
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.channels.OverlappingFileLockException
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE
import java.awt.Frame
import java.awt.EventQueue
import java.awt.MouseInfo
import java.awt.Window as AwtWindow
import kotlin.math.roundToInt

private val desktopHostModule = module {
    single { DesktopFileAssociations() }
    single { DesktopMediaLibrary(File(desktopLibraryDatabasePath()).parentFile.resolve("media-index.json"),
        File(desktopLibraryDatabasePath()).parentFile.resolve("media-artwork"), get()) }
    single { DesktopDownloadManager(get(), get(), File(System.getProperty("user.home"), "Music/Convx/Downloads")) }
    factory { DesktopLocalLibraryStateHolder(get(), get(), get()) }
    factory {
        val catalog = get<com.convx.music.catalog.MusicCatalogRepository>()
        val innertube = get<com.music.innertube.InnertubeRepository>()
        DesktopTrackLikeStateHolder(
            loadLiked = { catalog.library(com.convx.music.catalog.CatalogLibraryCollection.LIKED_TRACKS, it) },
            setRemoteLiked = { id, liked -> innertube.likeVideo(id, liked).getOrThrow().status.value in 200..299 },
        )
    }
    factory { DesktopDetailArtworkStateHolder(get(), get()) }
    single { DesktopPlayerAssets(File(desktopLibraryDatabasePath()).parentFile.resolve("player-assets")) }
    single { DesktopDiyStateHolder(get(), get()) }
    single<SecureSessionStore> { DesktopSecureSessionStore() }
    single { LyricsStateHolder(listOf(DesktopLocalLyricsSource(get())) + desktopLyricsSources(get())) }
    single<DesktopMusicController> {
        DesktopPlaybackController(
            streamResolver = DesktopLocalAudioStreamResolver(get<AudioStreamResolver>(), get(), get()),
            playbackSettingsRepository = get<PlaybackSettingsRepository>(),
            playbackCheckpointRepository = get<PlaybackCheckpointRepository>(),
            trackAnalysisRepository = get<PlaybackTrackAnalysisRepository>(),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) {
    if (args.contains(SECURE_STORAGE_PROBE_ARGUMENT)) {
        runBlocking { verifyDesktopSecureSessionStoreRoundTrip() }
        println("Desktop secure session storage round trip passed")
        return
    }

    val fileRequests = DesktopFileOpenRequests(File(desktopLibraryDatabasePath()).resolveSibling("file-open-requests"))
    fileRequests.submit(args.toList())
    if (!DesktopInstanceGuard.acquire()) {
        println("Convx is already running for this user.")
        return
    }

    val networkEnvironment = DesktopNetworkEnvironment(Path.of(desktopLibraryDatabasePath()).parent)
    coil3.SingletonImageLoader.setSafe { context ->
        coil3.ImageLoader.Builder(context)
            .components {
                add(DesktopFileArtworkMapper())
                add(com.convx.music.designsystem.ArtworkSizeInterceptor())
                add(com.convx.music.utils.NetworkArtworkFetcher.Factory { networkEnvironment.repository })
                add(coil3.svg.SvgDecoder.Factory())
            }
            .build()
    }
    com.convx.music.canvas.AppleMusicCatalog.cacheDir = File(desktopLibraryDatabasePath()).parentFile.resolve("artwork-catalog")

    val libraryDatabase = buildMusicLibraryDatabase(
        Room.databaseBuilder<MusicLibraryDatabase>(desktopLibraryDatabasePath()),
    )
    val playbackSettingsDatabase = buildPlaybackSettingsDatabase(
        Room.databaseBuilder<PlaybackSettingsDatabase>(desktopPlaybackSettingsDatabasePath()),
    )
    val moduleEnvironment = ModuleEnvironment(
        DesktopModuleStore(Path.of(desktopLibraryDatabasePath()).resolveSibling("modules.json")),
    )
    dev.nucleusframework.application.nucleusApplication(args, backend = dev.nucleusframework.application.NucleusBackend.Tao, enableSingleInstance = false) {
        val musicWindowState = androidx.compose.ui.window.rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center), size = DpSize(1440.dp, 900.dp))
        MacosDecoratedWindow(
            onCloseRequest = ::exitApplication,
            title = "Convx",
            state = musicWindowState,
            minimumSize = DpSize(900.dp, 640.dp),
            // The pill in the search toolbar replaces the platform caption
            // buttons, so the native caption band collapses to zero width.
            titleBarHeight = 0.dp,
        ) {
            if (remember { System.getProperty("os.name").startsWith("Windows", ignoreCase = true) }) {
                WindowsBackdrop(WindowsBackdropStyle.Mica)
            }
            KoinApplication(application = {
                modules(
                    libraryDataModule(libraryDatabase),
                    playbackDataModule(playbackSettingsDatabase),
                    playbackPresentationModule(),
                    innertubeDataModule(DesktopWebPoTokenProvider()) {
                        networkEnvironment.resolver(ModuleAudioStreamResolver(moduleEnvironment.repository, it))
                    },
                    module { single { moduleEnvironment.repository } },
                    module { single<com.convx.storage.domain.NetworkLibraryRepository> { networkEnvironment.repository } },
                    libraryCoreModule,
                    musicAccountPresentationModule,
                    catalogPresentationModule,
                    desktopHostModule,
                )
            }) {
                val mediaLibrary: DesktopMediaLibrary = org.koin.compose.koinInject()
                val musicController: DesktopMusicController = org.koin.compose.koinInject()
                LaunchedEffect(mediaLibrary, musicController) {
                    while (true) {
                        val pending = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { fileRequests.takePending() }
                        for (files in pending) {
                            try {
                                val tracks = mediaLibrary.importFiles(files)
                                if (tracks.isNotEmpty()) {
                                    musicWindowState.isMinimized = false
                                    musicController.playQueue(tracks.map { it.request() }, 0)
                                }
                            } catch (cancelled: kotlinx.coroutines.CancellationException) { throw cancelled }
                            catch (_: Exception) { mediaLibrary.report("Couldn't open that audio file. Check that it is available and readable.") }
                        }
                        kotlinx.coroutines.delay(350)
                    }
                }
                ConvxDesktopShell(
                    micaBackdropActive = remember { System.getProperty("os.name").startsWith("Windows", ignoreCase = true) },
                    windowsHostWindow = java.awt.Window.getWindows().firstOrNull { it.isDisplayable && it.isShowing },
                    onCloseWindow = ::exitApplication,
                    onMinimizeWindow = { musicWindowState.isMinimized = true },
                    onZoomWindow = { musicWindowState.placement = if (musicWindowState.placement == androidx.compose.ui.window.WindowPlacement.Maximized)
                        androidx.compose.ui.window.WindowPlacement.Floating else androidx.compose.ui.window.WindowPlacement.Maximized },
                )
                DisposableEffect(libraryDatabase, playbackSettingsDatabase, moduleEnvironment, networkEnvironment) {
                    onDispose {
                        networkEnvironment.close()
                        moduleEnvironment.close()
                        libraryDatabase.close()
                        playbackSettingsDatabase.close()
                        JavaFxRuntime.close()
                    }
                }
            }
        }
    }
}

internal fun desktopLibraryDatabasePath(): String {
    val userHome = System.getProperty("user.home")
    val operatingSystem = System.getProperty("os.name").lowercase()
    val configuredDataDirectory = System.getProperty("convx.desktop.data.dir")?.takeIf(String::isNotBlank)
        ?: System.getenv("CONVX_DESKTOP_DATA_DIR")?.takeIf(String::isNotBlank)
    val directory = when {
        configuredDataDirectory != null -> File(configuredDataDirectory)
        operatingSystem.contains("win") -> File(
            System.getenv("APPDATA")?.takeIf(String::isNotBlank)
                ?: File(userHome, "AppData/Roaming").absolutePath,
            "Convx",
        )
        operatingSystem.contains("mac") -> File(userHome, "Library/Application Support/Convx")
        else -> {
            val xdgDataHome = System.getenv("XDG_DATA_HOME")?.takeIf { it.isNotBlank() && File(it).isAbsolute }
            File(xdgDataHome ?: File(userHome, ".local/share").absolutePath, "convx")
        }
    }
    check(directory.isDirectory || directory.mkdirs()) {
        "Unable to create the Convx application data directory."
    }
    return File(directory, "client-library.db").absolutePath
}

private fun desktopPlaybackSettingsDatabasePath(): String =
    File(desktopLibraryDatabasePath()).resolveSibling("client-playback-settings.db").absolutePath

/** Prevents repeated Gradle or installer launches from creating competing playback sessions. */
private object DesktopInstanceGuard {
    private var channel: FileChannel? = null
    private var lock: FileLock? = null

    @Synchronized
    fun acquire(): Boolean {
        if (lock?.isValid == true) return true

        val lockFile = File(desktopLibraryDatabasePath()).resolveSibling("client-instance.lock").toPath()
        val openedChannel = FileChannel.open(lockFile, CREATE, WRITE)
        val acquiredLock = try {
            openedChannel.tryLock()
        } catch (_: OverlappingFileLockException) {
            null
        }
        if (acquiredLock == null) {
            openedChannel.close()
            return false
        }

        channel = openedChannel
        lock = acquiredLock
        Runtime.getRuntime().addShutdownHook(Thread({ release() }, "convx-instance-lock-release"))
        return true
    }

    @Synchronized
    private fun release() {
        runCatching { lock?.release() }
        lock = null
        runCatching { channel?.close() }
        channel = null
    }
}

private const val SECURE_STORAGE_PROBE_ARGUMENT = "--convx-secure-storage-probe"
