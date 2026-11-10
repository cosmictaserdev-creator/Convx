package com.convx.music.di

import com.convx.music.viewmodels.YouTubeBrowseViewModel
import com.convx.music.viewmodels.BrowseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val browsePresentationModule = module {
    viewModelOf(::YouTubeBrowseViewModel)
    viewModelOf(::BrowseViewModel)
}
