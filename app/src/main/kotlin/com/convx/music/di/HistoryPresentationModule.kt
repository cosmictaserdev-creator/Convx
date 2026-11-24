package com.convx.music.di

import com.convx.music.viewmodels.HistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val historyPresentationModule = module {
    viewModelOf(::HistoryViewModel)
}
