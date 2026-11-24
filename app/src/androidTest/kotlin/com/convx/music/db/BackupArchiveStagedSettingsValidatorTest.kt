package com.convx.music.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.convx.music.viewmodels.BackupArchiveStagedSettingsValidator
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BackupArchiveStagedSettingsValidatorTest {
    @Test
    fun rejectsMalformedPreferencesBeforeRestore() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.cacheDir, "restore-settings-validation").apply {
            deleteRecursively()
            mkdirs()
        }
        val stagedSettings = File(directory, "settings.preferences_pb").apply {
            writeBytes(byteArrayOf(0x80.toByte()))
        }

        try {
            try {
                BackupArchiveStagedSettingsValidator.validate(stagedSettings)
                fail("Malformed settings must be rejected before replacing app data")
            } catch (expected: Exception) {
                // PreferencesSerializer rejects the truncated protobuf.
            }
        } finally {
            directory.deleteRecursively()
        }
    }
}
