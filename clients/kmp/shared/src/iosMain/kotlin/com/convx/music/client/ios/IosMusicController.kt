package com.convx.music.client.ios

import androidx.room.Room
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.ui.window.Dialog
import com.convx.music.account.MusicAccountState
import com.convx.music.account.SecureSessionStore
import com.convx.music.library.libraryCoreModule
import com.convx.music.library.data.libraryDataModule
import com.convx.music.library.data.MusicLibraryDatabase
import com.convx.music.library.data.buildMusicLibraryDatabase
import com.convx.music.account.presentation.MusicAccountPresentationState
import com.convx.music.account.presentation.MusicAccountStateHolder
import com.convx.music.account.presentation.musicAccountPresentationModule
import com.convx.music.catalog.presentation.ConvxMusicClientShell
import com.convx.music.catalog.presentation.MusicBrowserScreen
import com.convx.music.catalog.presentation.catalogPresentationModule
import com.convx.music.playback.AudioStreamResolver
import com.convx.music.modules.IosModuleStore
import com.convx.music.modules.ModuleEnvironment
import com.convx.music.modules.ModuleAudioStreamResolver
import com.convx.music.modules.ModulesScreen
import com.convx.music.playback.PlaybackController
import com.convx.music.playback.PlaybackSettingsRepository
import com.convx.music.playback.PlaybackCheckpointRepository
import com.convx.music.playback.PlaybackTrackAnalysisRepository
import com.convx.music.playback.data.PlaybackSettingsDatabase
import com.convx.music.playback.data.buildPlaybackSettingsDatabase
import com.convx.music.playback.data.playbackDataModule
import com.convx.music.playback.presentation.playbackPresentationModule
import com.convx.music.playback.presentation.PlaybackSleepTimerSheet
import com.music.innertube.innertubeDataModule
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory

private val iosHostModule = module {
    single<SecureSessionStore> { IosSecureSessionStore() }
    single<PlaybackController> {
        IosPlaybackController(
            streamResolver = get<AudioStreamResolver>(),
            playbackSettingsRepository = get<PlaybackSettingsRepository>(),
            playbackCheckpointRepository = get<PlaybackCheckpointRepository>(),
            trackAnalysisRepository = get<PlaybackTrackAnalysisRepository>(),
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun iosDatabasePath(fileName: String): String {
    val directory = "${NSHomeDirectory()}/Library/Application Support/Convx"
    val fileManager = NSFileManager.defaultManager
    check(
        fileManager.fileExistsAtPath(directory) ||
            fileManager.createDirectoryAtPath(
                path = directory,
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            ),
    ) { "Unable to create the Convx application support directory." }
    return "$directory/$fileName"
}

private fun iosLibraryDatabasePath() = iosDatabasePath("client-library.db")

private fun iosPlaybackSettingsDatabasePath() = iosDatabasePath("client-playback-settings.db")

fun iosMusicController() = iosMusicController(runKeychainProbe = false)

fun iosMusicControllerWithKeychainProbe() = iosMusicController(runKeychainProbe = true)

private fun iosMusicController(runKeychainProbe: Boolean) = ComposeUIViewController {
    val moduleEnvironment = remember { ModuleEnvironment(IosModuleStore()) }
    val webPoTokenProvider = remember { IosWebPoTokenProvider() }
    val libraryDatabase = remember {
        buildMusicLibraryDatabase(Room.databaseBuilder<MusicLibraryDatabase>(iosLibraryDatabasePath()))
    }
    val playbackSettingsDatabase = remember {
        buildPlaybackSettingsDatabase(
            Room.databaseBuilder<PlaybackSettingsDatabase>(iosPlaybackSettingsDatabasePath()),
        )
    }
    DisposableEffect(libraryDatabase, playbackSettingsDatabase) {
        onDispose {
            libraryDatabase.close()
            playbackSettingsDatabase.close()
        }
    }
    KoinApplication(application = {
        modules(
            libraryDataModule(libraryDatabase),
            playbackDataModule(playbackSettingsDatabase),
            playbackPresentationModule(),
            innertubeDataModule(webPoTokenProvider) {
                ModuleAudioStreamResolver(moduleEnvironment.repository, it)
            },
            libraryCoreModule,
            musicAccountPresentationModule,
            catalogPresentationModule,
            iosHostModule,
            com.convx.music.client.watch.watchCompanionModule(),
        )
    }) {
        val playbackController = koinInject<PlaybackController>()
        val watch = koinInject<com.convx.music.client.watch.WatchCompanion>()
        DisposableEffect(watch) {
            val connection = IosWatchCompanion(watch)
            onDispose(connection::close)
        }
        val playback by playbackController.playbackSnapshot.collectAsState()
        val sleepTimer = remember(playbackController) { requireNotNull(playbackController.sleepTimer) }
        val sleepTimerState by sleepTimer.state.collectAsState()
        val accountHolder = koinInject<MusicAccountStateHolder>()
        val account by accountHolder.state.collectAsState()
        var showSignIn by remember { mutableStateOf(false) }
        var showModules by remember { mutableStateOf(false) }
        var showSleepTimer by remember { mutableStateOf(false) }
        var keychainProbeMessage by remember { mutableStateOf<String?>(null) }
        LaunchedEffect(account.account, account.isWorking, account.message) {
            if (
                account.account is MusicAccountState.SignedIn ||
                account.account is MusicAccountState.ChoosingChannel ||
                (!account.isWorking && account.message != null)
            ) {
                showSignIn = false
            }
        }
        LaunchedEffect(runKeychainProbe) {
            if (runKeychainProbe) {
                keychainProbeMessage = verifyIosSecureSessionStoreRoundTrip()
            }
        }
        DisposableEffect(playbackController, accountHolder, webPoTokenProvider) {
            onDispose {
                moduleEnvironment.close()
                accountHolder.close()
                playbackController.release()
                webPoTokenProvider.close()
            }
        }

        val accountSubtitle = when (val accountState = account.account) {
            MusicAccountState.SignedOut -> "Guest"
            is MusicAccountState.ChoosingChannel -> "Choose a YouTube channel"
            is MusicAccountState.SignedIn -> accountState.account.name
        }
        val accountActionLabel = when (account.account) {
            MusicAccountState.SignedOut -> when {
                account.isRestoring -> "Checking…"
                account.isWorking -> "Signing in…"
                else -> "Sign in"
            }
            is MusicAccountState.ChoosingChannel -> "Cancel"
            is MusicAccountState.SignedIn -> "Sign out"
        }
        ConvxMusicClientShell(
            onModulesAction = { showModules = !showModules },
            accountSubtitle = accountSubtitle,
            accountActionLabel = accountActionLabel,
            accountActionEnabled = !account.isRestoring && !account.isWorking,
            statusMessages = listOfNotNull(keychainProbeMessage, account.message),
            onAccountAction = {
                when (account.account) {
                    MusicAccountState.SignedOut -> showSignIn = true
                    is MusicAccountState.ChoosingChannel -> accountHolder.cancelSignIn()
                    is MusicAccountState.SignedIn -> {
                        accountHolder.signOut()
                        clearIosWebCookies()
                    }
                }
            },
        ) { contentModifier ->
            if (showModules) {
                ModulesScreen(
                    repository = moduleEnvironment.repository,
                    onPlay = { playbackController.playTrack(it); showModules = false },
                    modifier = contentModifier,
                    onBack = { showModules = false },
                )
            } else {
            MusicBrowserScreen(
                playbackController = playbackController,
                crossfadeAvailable = true,
                equalizerAvailable = true,
                modifier = contentModifier,
                isSignedIn = account.account is MusicAccountState.SignedIn,
                isSleepTimerActive = sleepTimerState.isActive,
                onSleepTimerRequested = { showSleepTimer = true },
                onSignInRequested = { showSignIn = true },
            )
            }
        }

        if (showSleepTimer) {
            PlaybackSleepTimerSheet(
                timer = sleepTimer,
                playback = playback,
                darkTheme = true,
                onDismissRequest = { showSleepTimer = false },
            )
        }

        if (showSignIn) {
            IosSignInDialog(
                accountIsWorking = account.isWorking,
                onComplete = { cookie, visitorData, dataSyncId ->
                    accountHolder.completeWebSignIn(cookie, visitorData, dataSyncId)
                },
                onDismiss = {
                    accountHolder.cancelSignIn()
                    clearIosWebCookies()
                    showSignIn = false
                },
            )
        }

        val channels = (account.account as? MusicAccountState.ChoosingChannel)?.channels.orEmpty()
        if (channels.isNotEmpty()) {
            Dialog(onDismissRequest = accountHolder::cancelSignIn) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    BasicText("Choose a YouTube channel")
                    channels.forEach { channel ->
                        BasicText(
                            channel.name,
                            Modifier.fillMaxWidth().clickable { accountHolder.selectChannel(channel.dataSyncId) }
                                .padding(vertical = 8.dp),
                        )
                    }
                }
            }
        }
    }
}
