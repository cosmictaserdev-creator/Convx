package com.convx.music.di

import com.convx.music.viewmodels.BackupRestoreViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val backupRestorePresentationModule = module {
    viewModelOf(::BackupRestoreViewModel)
}
