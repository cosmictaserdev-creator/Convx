package com.convx.music.di

import com.convx.music.viewmodels.AppleMusicImportViewModel
import com.convx.music.viewmodels.AutoPlaylistViewModel
import com.convx.music.viewmodels.PlaylistsViewModel
import com.convx.music.viewmodels.SpotifyImportViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val playlistPresentationModule = module {
    viewModelOf(::PlaylistsViewModel)
    viewModelOf(::AutoPlaylistViewModel)
    viewModelOf(::SpotifyImportViewModel)
    viewModelOf(::AppleMusicImportViewModel)
}
