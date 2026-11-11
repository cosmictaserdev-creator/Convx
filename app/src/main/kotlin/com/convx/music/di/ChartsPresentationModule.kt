package com.convx.music.di

import com.convx.music.viewmodels.ChartsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chartsPresentationModule = module {
    viewModelOf(::ChartsViewModel)
}
