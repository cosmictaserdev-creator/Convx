package com.convx.music.di

import com.convx.music.core.updater.ApkVerifier
import com.convx.music.core.updater.GitHubReleaseSource
import com.convx.music.core.updater.UpdateInstaller
import com.convx.music.core.updater.UpdateRepository
import com.convx.music.core.updater.UpdateStore
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val updaterDataModule = module {
    factoryOf(::ApkVerifier)
    singleOf(::GitHubReleaseSource)
    singleOf(::UpdateStore)
    singleOf(::UpdateInstaller)
    singleOf(::UpdateRepository)
}
