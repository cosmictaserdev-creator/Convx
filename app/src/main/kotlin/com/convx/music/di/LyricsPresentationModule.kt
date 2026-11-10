package com.convx.music.di

import com.convx.music.viewmodels.LyricsAppearanceViewModel
import com.convx.music.viewmodels.LyricsMenuViewModel
import com.convx.music.viewmodels.LyricsProviderViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val lyricsPresentationModule = module {
    viewModelOf(::LyricsAppearanceViewModel)
    viewModelOf(::LyricsMenuViewModel)
    viewModelOf(::LyricsProviderViewModel)
}
