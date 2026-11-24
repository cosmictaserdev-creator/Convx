package com.convx.music.di

import com.convx.music.viewmodels.ArtistItemsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val artistItemsPresentationModule = module {
    viewModelOf(::ArtistItemsViewModel)
}
