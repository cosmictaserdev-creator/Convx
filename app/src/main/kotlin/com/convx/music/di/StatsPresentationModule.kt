package com.convx.music.di

import com.convx.music.viewmodels.StatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val statsPresentationModule = module {
    viewModelOf(::StatsViewModel)
}
