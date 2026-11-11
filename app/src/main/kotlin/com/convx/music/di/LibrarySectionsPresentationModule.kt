package com.convx.music.di

import com.convx.music.viewmodels.LibraryAlbumsViewModel
import com.convx.music.viewmodels.LibraryArtistsViewModel
import com.convx.music.viewmodels.LibraryMixViewModel
import com.convx.music.viewmodels.LibraryPlaylistsViewModel
import com.convx.music.viewmodels.LibrarySongsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val librarySectionsPresentationModule = module {
    viewModelOf(::LibrarySongsViewModel)
    viewModelOf(::LibraryArtistsViewModel)
    viewModelOf(::LibraryAlbumsViewModel)
    viewModelOf(::LibraryPlaylistsViewModel)
    viewModelOf(::LibraryMixViewModel)
}
