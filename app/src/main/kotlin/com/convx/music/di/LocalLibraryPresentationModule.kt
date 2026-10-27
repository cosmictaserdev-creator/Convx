package com.convx.music.di

import com.convx.music.ui.screens.library.LocalMusicViewModel
import com.convx.music.ui.screens.library.apple.AppleLibraryViewModel
import com.convx.music.ui.screens.library.apple.AppleLocalSongsViewModel
import com.convx.music.viewmodels.LocalHomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val localLibraryPresentationModule = module {
    viewModelOf(::LocalMusicViewModel)
    viewModelOf(::LocalHomeViewModel)
    viewModelOf(::AppleLibraryViewModel)
    viewModelOf(::AppleLocalSongsViewModel)
}
