package com.convx.music.di

import com.convx.music.viewmodels.JamViewModel
import com.convx.music.viewmodels.ListenTogetherViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val integrationPresentationModule = module {
    viewModelOf(::JamViewModel)
    viewModelOf(::ListenTogetherViewModel)
}
