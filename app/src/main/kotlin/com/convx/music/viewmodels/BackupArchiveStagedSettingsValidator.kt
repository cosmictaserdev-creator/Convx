package com.convx.music.viewmodels

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File

internal object BackupArchiveStagedSettingsValidator {
    fun validate(file: File) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val stagedDataStore = PreferenceDataStoreFactory.create(
                scope = scope,
                produceFile = { file },
            )
            runBlocking(Dispatchers.IO) { stagedDataStore.data.first() }
        } finally {
            scope.cancel()
        }
    }
}
