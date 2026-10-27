package com.convx.music.di

import com.convx.music.viewmodels.UpdateViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val updaterPresentationModule = module {
    viewModelOf(::UpdateViewModel)
}
