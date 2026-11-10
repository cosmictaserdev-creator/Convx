package com.convx.music.di

import com.convx.music.viewmodels.NewReleaseViewModel
import com.convx.music.viewmodels.HomeViewModel
import com.convx.music.history.ListenHistoryRepository
import com.convx.music.history.ListenHistorySettingsRepository
import com.convx.music.utils.SyncUtils
import com.convx.music.ui.screens.wrapped.WrappedAudioService
import com.convx.music.ui.screens.wrapped.WrappedManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homePresentationModule = module {
    viewModelOf(::NewReleaseViewModel)
    viewModel {
        val koin = getKoin()
        HomeViewModel(
            context = get(),
            database = get(),
            listenHistoryRepository = ValueProvider { koin.get<ListenHistoryRepository>() },
            listenHistorySettingsRepository = ValueProvider { koin.get<ListenHistorySettingsRepository>() },
            syncUtils = ValueProvider { koin.get<SyncUtils>() },
            wrappedManager = get<WrappedManager>(),
            wrappedAudioService = get<WrappedAudioService>(),
        )
    }
}
