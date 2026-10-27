package com.convx.music.di

import com.convx.music.viewmodels.AlbumViewModel
import com.convx.music.viewmodels.AiDjSetViewModel
import com.convx.music.viewmodels.ArtistAlbumsViewModel
import com.convx.music.viewmodels.ArtistViewModel
import com.convx.music.viewmodels.ArtistSongsViewModel
import com.convx.music.viewmodels.SongInsightViewModel
import com.convx.music.viewmodels.TopPlaylistViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val catalogPresentationModule = module {
    viewModelOf(::AlbumViewModel)
    viewModelOf(::ArtistViewModel)
    viewModelOf(::ArtistSongsViewModel)
    viewModelOf(::ArtistAlbumsViewModel)
    viewModelOf(::TopPlaylistViewModel)
    viewModelOf(::AiDjSetViewModel)
    viewModelOf(::SongInsightViewModel)
}
