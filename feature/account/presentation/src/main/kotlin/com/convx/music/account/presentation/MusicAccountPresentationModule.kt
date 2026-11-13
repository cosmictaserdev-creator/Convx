package com.convx.music.account.presentation

import org.koin.dsl.module

val musicAccountPresentationModule = module {
    factory { MusicAccountStateHolder(get()) }
}
