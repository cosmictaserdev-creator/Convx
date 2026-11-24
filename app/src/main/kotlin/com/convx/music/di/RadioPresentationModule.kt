package com.convx.music.di

import com.convx.music.viewmodels.RadioStationViewModel
import com.convx.music.viewmodels.RadioViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val radioPresentationModule = module {
    viewModelOf(::RadioStationViewModel)
    viewModelOf(::RadioViewModel)
}
