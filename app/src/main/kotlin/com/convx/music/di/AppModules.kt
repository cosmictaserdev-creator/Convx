package com.convx.music.di

import com.convx.music.library.libraryCoreModule
import com.convx.music.search.searchHistoryModule

/** Every Koin module the Android app starts with; [KoinGraphTest] verifies the same list. */
val appModules = listOf(
    chartsPresentationModule,
    catalogPresentationModule,
    backupRestorePresentationModule,
    accountPresentationModule,
    homePresentationModule,
    androidAppDependenciesModule,
    equalizerPresentationModule,
    networkStoragePresentationModule,
    playlistDetailPresentationModule,
    integrationPresentationModule,
    artistItemsPresentationModule,
    browsePresentationModule,
    lyricsPresentationModule,
    historyPresentationModule,
    libraryCoreModule,
    searchHistoryModule,
    localLibraryPresentationModule,
    librarySectionsPresentationModule,
    playlistPresentationModule,
    localPlaylistPresentationModule,
    searchPresentationModule,
    statsPresentationModule,
    radioPresentationModule,
    updaterDataModule,
    updaterPresentationModule,
)
