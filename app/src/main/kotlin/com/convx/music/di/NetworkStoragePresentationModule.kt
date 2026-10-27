package com.convx.music.di

import com.convx.music.viewmodels.NetworkBrowserViewModel
import com.convx.music.viewmodels.NetworkStorageViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val networkStoragePresentationModule = module {
    viewModelOf(::NetworkStorageViewModel)
    viewModelOf(::NetworkBrowserViewModel)
}
