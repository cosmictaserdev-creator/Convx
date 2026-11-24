package com.convx.music.di

import com.convx.music.viewmodels.AccountViewModel
import com.convx.music.viewmodels.AccountSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val accountPresentationModule = module {
    viewModelOf(::AccountViewModel)
    viewModelOf(::AccountSettingsViewModel)
}
