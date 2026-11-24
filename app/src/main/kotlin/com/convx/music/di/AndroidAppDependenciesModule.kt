package com.convx.music.di

import android.content.Context
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.convx.music.constants.MaxSongCacheSizeKey
import com.convx.music.db.DatabaseDao
import com.convx.music.db.InternalDatabase
import com.convx.music.db.MusicDatabase
import com.convx.music.eq.EqualizerService
import com.convx.music.eq.data.EQProfileRepository
import com.convx.music.history.HistoryLocalDataSource
import com.convx.music.history.DataStoreListenHistorySettingsRepository
import com.convx.music.history.ListenHistoryRepository
import com.convx.music.history.ListenHistorySettingsRepository
import com.convx.music.history.RoomHistoryLocalDataSource
import com.convx.music.library.LocalPlaylistSongDataSource
import com.convx.music.library.MusicLibraryRemoteDataSource
import com.convx.music.library.MusicPlaylistRemoteDataSource
import com.music.innertube.InnertubeRepository
import com.music.innertube.YouTube
import com.music.innertube.library.InnertubeMusicLibraryDataSource
import com.music.innertube.library.InnertubeMusicPlaylistDataSource
import com.convx.music.library.DataStorePlaylistDisplayPreferencesRepository
import com.convx.music.library.DataStorePendingPlaylistDeletionRepository
import com.convx.music.library.PendingPlaylistDeletionRepository
import com.convx.music.library.PlaylistDisplayPreferencesRepository
import com.convx.music.library.PlaylistRepository
import com.convx.music.library.RoomPlaylistRepository
import com.convx.music.search.RoomSearchHistoryRepository
import com.convx.music.search.SearchHistoryRepository
import com.convx.music.search.DataStoreSearchHistorySettingsRepository
import com.convx.music.search.SearchHistorySettingsRepository
import com.convx.music.jam.JamManager
import com.convx.music.listentogether.ListenTogetherClient
import com.convx.music.listentogether.ListenTogetherManager
import com.convx.music.listentogether.ListenTogetherPlayerConnectionBridge
import com.convx.music.lyrics.LyricsHelper
import com.convx.music.lyrics.LyricsProviderRepository
import com.convx.music.playback.DownloadUtil
import com.convx.music.playback.MediaLibrarySessionCallback
import com.convx.music.playback.PlaybackServiceConnector
import com.convx.music.utils.NetworkConnectivityObserver
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.widget.ConvxWidgetManager
import com.convx.storage.data.EncryptedNetworkServerStore
import com.convx.storage.data.NetworkMusicRepository
import com.convx.storage.domain.NetworkServerStore
import com.convx.music.ui.screens.wrapped.WrappedAudioService
import com.convx.music.ui.screens.wrapped.WrappedManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val androidAppDependenciesModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single<MusicDatabase> { InternalDatabase.newInstance(androidContext()) }
    single<InternalDatabase> { get<MusicDatabase>().delegate }
    single<DatabaseDao> { get<InternalDatabase>().dao }
    // Must be the signed-in global client; a new instance has no session.
    single<InnertubeRepository> { YouTube }
    single<MusicLibraryRemoteDataSource> { InnertubeMusicLibraryDataSource(get()) }
    single<MusicPlaylistRemoteDataSource> { InnertubeMusicPlaylistDataSource(get()) }
    single<DatabaseProvider> { StandaloneDatabaseProvider(androidContext()) }
    single<SimpleCache>(named("playerCache")) {
        val context: Context = androidContext()
        val cacheSize = context.dataStore[MaxSongCacheSizeKey] ?: 1024
        SimpleCache(
            context.filesDir.resolve("exoplayer"),
            if (cacheSize == -1) NoOpCacheEvictor() else LeastRecentlyUsedCacheEvictor(cacheSize * 1024 * 1024L),
            get(),
        )
    }
    single<SimpleCache>(named("downloadCache")) {
        val context: Context = androidContext()
        SimpleCache(context.filesDir.resolve("download"), NoOpCacheEvictor(), get())
    }
    single<NetworkServerStore> { EncryptedNetworkServerStore(androidContext()) }
    single { NetworkMusicRepository(get()) }
    single { NetworkConnectivityObserver(androidContext()) }
    single { RoomHistoryLocalDataSource(get()) }
    single<HistoryLocalDataSource> { get<RoomHistoryLocalDataSource>() }
    single<ListenHistoryRepository> { get<RoomHistoryLocalDataSource>() }
    single<ListenHistorySettingsRepository> {
        DataStoreListenHistorySettingsRepository(androidContext())
    }
    single<RoomPlaylistRepository> { RoomPlaylistRepository(get()) }
    single<PlaylistRepository> { get<RoomPlaylistRepository>() }
    single<LocalPlaylistSongDataSource> { get<RoomPlaylistRepository>() }
    single<PlaylistDisplayPreferencesRepository> {
        DataStorePlaylistDisplayPreferencesRepository(androidContext())
    }
    single<PendingPlaylistDeletionRepository> {
        DataStorePendingPlaylistDeletionRepository(androidContext())
    }
    single<SearchHistoryRepository> { RoomSearchHistoryRepository(get()) }
    single<SearchHistorySettingsRepository> {
        DataStoreSearchHistorySettingsRepository(androidContext())
    }
    single { ListenTogetherPlayerConnectionBridge() }
    single { ListenTogetherClient(androidContext()) }
    single { ListenTogetherManager(get(), androidContext(), get()) }
    single { JamManager(androidContext(), get(), ValueProvider { getKoin().get() }) }
    single { LyricsProviderRepository(androidContext()) }
    single { LyricsHelper(androidContext(), get(), get()) }
    single { SyncUtils(androidContext(), get(), get(), get()) }
    single { EqualizerService() }
    single { EQProfileRepository(androidContext()) }
    single { WrappedManager(get(), androidContext()) }
    single { WrappedAudioService(androidContext()) }
    single {
        DownloadUtil(
            context = androidContext(),
            database = get(),
            databaseProvider = get(),
            downloadCache = get(named("downloadCache")),
            playerCache = get(named("playerCache")),
            lyricsHelper = get(),
        )
    }
    single { MediaLibrarySessionCallback(androidContext(), get(), get()) }
    single { ConvxWidgetManager(androidContext(), get()) }
    factory { PlaybackServiceConnector(ValueProvider { getKoin().get() }, get()) }
}
