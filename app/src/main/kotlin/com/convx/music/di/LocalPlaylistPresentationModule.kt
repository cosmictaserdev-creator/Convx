package com.convx.music.di

import com.convx.music.viewmodels.LocalPlaylistViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val localPlaylistPresentationModule = module {
    viewModelOf(::LocalPlaylistViewModel)
}
