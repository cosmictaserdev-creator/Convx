package com.convx.music.catalog.presentation

import org.koin.dsl.module

val catalogPresentationModule = module {
    factory { CatalogStateHolder(get()) }
    factory { CatalogSidebarStateHolder(get(), get()) }
    factory { CatalogSearchStateHolder(get(), get()) }
    factory { CatalogLibraryActionStateHolder(get()) }
    factory { CatalogPlaylistActionStateHolder(get(), get(), get()) }
}
