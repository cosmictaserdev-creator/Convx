/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase as AndroidSQLiteDatabase
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.convx.music.MainActivity
import com.convx.music.R
import com.convx.music.db.InternalDatabase
import com.convx.music.db.INTERNAL_DATABASE_VERSION
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.Song
import com.convx.music.db.entities.SongEntity
import com.convx.music.extensions.div
import com.convx.music.extensions.zipInputStream
import com.convx.music.extensions.zipOutputStream
import com.convx.music.playback.MusicService
import com.convx.music.playback.MusicService.Companion.PERSISTENT_AUTOMIX_FILE
import com.convx.music.playback.MusicService.Companion.PERSISTENT_PLAYBACK_SNAPSHOT_FILE
import com.convx.music.playback.MusicService.Companion.PERSISTENT_QUEUE_FILE
import com.convx.music.playback.MusicService.Companion.PERSISTENT_PLAYER_STATE_FILE
import com.convx.music.utils.reportException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import kotlin.system.exitProcess

data class CsvImportState(
    val previewRows: List<List<String>> = emptyList(),
    val artistColumnIndex: Int = 0,
    val titleColumnIndex: Int = 1,
    val urlColumnIndex: Int = -1,
    val hasHeader: Boolean = true,
)

data class ConvertedSongLog(
    val title: String,
    val artists: String,
)

class BackupRestoreViewModel(
    val database: MusicDatabase,
) : ViewModel() {
    fun backup(context: Context, uri: Uri) {
        runCatching {
            context.applicationContext.contentResolver.openOutputStream(uri)?.use {
                it.buffered().zipOutputStream().use { outputStream ->
                    (context.filesDir / "datastore" / SETTINGS_FILENAME).inputStream().buffered()
                        .use { inputStream ->
                            outputStream.putNextEntry(ZipEntry(SETTINGS_FILENAME))
                            inputStream.copyTo(outputStream)
                        }
                    runBlocking(Dispatchers.IO) {
                        database.checkpoint()
                    }
                    FileInputStream(database.openHelper.writableDatabase.path).use { inputStream ->
                        outputStream.putNextEntry(ZipEntry(InternalDatabase.DB_NAME))
                        inputStream.copyTo(outputStream)
                    }
                    // Background media is stored as files; DataStore saves only its path.
                    context.filesDir.listFiles { file -> file.name.startsWith(HOME_BACKGROUND_FILE_PREFIX) }
                        ?.forEach { bgFile ->
                            bgFile.inputStream().use { inputStream ->
                                outputStream.putNextEntry(ZipEntry("$HOME_BACKGROUND_ZIP_DIR/${bgFile.name}"))
                                inputStream.copyTo(outputStream)
                            }
                        }
                }
            }
        }.onSuccess {
            Toast.makeText(context, R.string.backup_create_success, Toast.LENGTH_SHORT).show()
        }.onFailure {
            reportException(it)
            Toast.makeText(context, R.string.backup_create_failed, Toast.LENGTH_SHORT).show()
        }
    }

    fun restore(context: Context, uri: Uri) {
        var databaseClosed = false
        var restoreCommitted = false
        runCatching {
            Timber.tag("RESTORE").i("Starting restore from URI: $uri")
            val appContext = context.applicationContext
            val databaseFile = File(database.openHelper.writableDatabase.path)
            val stagedFiles = linkedMapOf<File, StagedFileReplacement>()
            try {
                val raw = appContext.contentResolver.openInputStream(uri)
                    ?: error("Could not open backup archive")
                raw.use { archive ->
                    archive.zipInputStream().use { inputStream ->
                        var entry = inputStream.nextEntry
                        while (entry != null) {
                            Timber.tag("RESTORE").i("Found zip entry: ${entry.name}")
                            stageArchiveEntry(
                                entryName = entry.name,
                                inputStream = inputStream,
                                context = appContext,
                                databaseFile = databaseFile,
                                stagedFiles = stagedFiles,
                            )
                            inputStream.closeEntry()
                            entry = inputStream.nextEntry
                        }
                    }
                }

                check(stagedFiles.isNotEmpty()) { "Backup archive contains no supported files" }
                val settingsFile = appContext.filesDir.resolve("datastore").resolve(SETTINGS_FILENAME)
                stagedFiles[settingsFile.canonicalFile]?.let {
                    BackupArchiveStagedSettingsValidator.validate(it.stagedFile)
                }
                stagedFiles.values.firstOrNull { it.destinationFile.canonicalFile == databaseFile.canonicalFile }
                    ?.let { validateStagedDatabase(appContext, it.stagedFile) }

                if (stagedFiles.values.any { it.destinationFile.canonicalFile == databaseFile.canonicalFile }) {
                    appContext.stopService(Intent(appContext, MusicService::class.java))
                    runBlocking(Dispatchers.IO) { database.checkpoint() }
                    com.convx.music.utils.CrashHandler.databaseCloseExpected = true
                    databaseClosed = true
                    database.close()
                    deleteDatabaseSidecars(databaseFile, required = true)
                }

                AtomicFileReplacement.commit(stagedFiles.values.toList())
                restoreCommitted = true
            } finally {
                stagedFiles.values.forEach { replacement ->
                    replacement.stagedFile.delete()
                    if (replacement.destinationFile.canonicalFile == databaseFile.canonicalFile) {
                        deleteDatabaseSidecars(replacement.stagedFile)
                    }
                }
            }

            appContext.stopService(Intent(appContext, MusicService::class.java))
            clearPersistedPlaybackState(appContext)
            restartApplication(appContext)
            exitProcess(0)
        }.onFailure {
            reportException(it)
            Timber.tag("RESTORE").e(it, "Restore failed")
            Toast.makeText(context, R.string.restore_failed, Toast.LENGTH_SHORT).show()
            if (databaseClosed || restoreCommitted) {
                restartApplication(context.applicationContext)
                exitProcess(0)
            }
        }
    }

    private fun stageArchiveEntry(
        entryName: String,
        inputStream: InputStream,
        context: Context,
        databaseFile: File,
        stagedFiles: MutableMap<File, StagedFileReplacement>,
    ) {
        val destinationFile = when (entryName) {
            SETTINGS_FILENAME -> context.filesDir.resolve("datastore").resolve(SETTINGS_FILENAME)
            InternalDatabase.DB_NAME -> databaseFile
            else -> {
                if (!entryName.startsWith("$HOME_BACKGROUND_ZIP_DIR/")) {
                    Timber.tag("RESTORE").i("Skipping unexpected entry: $entryName")
                    return
                }
                val fileName = BackupArchiveEntryPolicy.backgroundFileName(entryName)
                if (fileName == null) {
                    Timber.tag("RESTORE").w("Skipping invalid home background entry: $entryName")
                    return
                }
                context.filesDir.resolve(fileName)
            }
        }

        val canonicalDestination = destinationFile.canonicalFile
        val replacement = stagedFiles[canonicalDestination] ?: run {
            val parent = canonicalDestination.parentFile ?: error("Restore destination has no parent")
            check(parent.isDirectory || parent.mkdirs()) { "Could not create restore directory: $parent" }
            val stagedFile = Files.createTempFile(
                parent.toPath(),
                "convx-restore-${canonicalDestination.name}-",
                ".staged",
            ).toFile()
            StagedFileReplacement(stagedFile, canonicalDestination).also {
                stagedFiles[canonicalDestination] = it
            }
        }

        Timber.tag("RESTORE").i("Staging archive entry: $entryName")
        replacement.stagedFile.outputStream().buffered().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    private fun validateStagedDatabase(context: Context, stagedFile: File) {
        val source = AndroidSQLiteDatabase.openDatabase(
            stagedFile.absolutePath,
            null,
            AndroidSQLiteDatabase.OPEN_READONLY,
        )
        val sourceVersion = try {
            source.version
        } finally {
            source.close()
        }
        require(sourceVersion in 1..INTERNAL_DATABASE_VERSION) {
            "Unsupported backup database version: $sourceVersion"
        }

        val stagedDatabase = InternalDatabase.newInstance(
            context = context,
            databaseName = stagedFile.name,
            allowDestructiveMigrationOnDowngrade = false,
        )
        try {
            val migratedVersion = stagedDatabase.openHelper.writableDatabase.version
            check(migratedVersion == INTERNAL_DATABASE_VERSION) {
                "Backup database did not migrate to version $INTERNAL_DATABASE_VERSION"
            }
            runBlocking(Dispatchers.IO) { stagedDatabase.checkpoint() }
        } finally {
            stagedDatabase.close()
        }
    }

    private fun deleteDatabaseSidecars(databaseFile: File, required: Boolean = false) {
        listOf("-wal", "-shm", "-journal").forEach { suffix ->
            val sidecar = File(databaseFile.absolutePath + suffix)
            if (sidecar.exists()) {
                val deleted = sidecar.delete()
                check(deleted || !sidecar.exists() || !required) {
                    "Could not remove database sidecar: $sidecar"
                }
            }
        }
    }

    private fun clearPersistedPlaybackState(context: Context) {
        context.filesDir.resolve(PERSISTENT_QUEUE_FILE).delete()
        context.filesDir.resolve(PERSISTENT_AUTOMIX_FILE).delete()
        context.filesDir.resolve(PERSISTENT_PLAYER_STATE_FILE).delete()
        context.filesDir.resolve(PERSISTENT_PLAYBACK_SNAPSHOT_FILE).delete()
    }

    private fun restartApplication(context: Context) {
        context.startActivity(
            Intent(context, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK,
            ),
        )
    }

    fun previewCsvFile(context: Context, uri: Uri): CsvImportState {
        val previewState = runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                val lines = reader.lineSequence().take(6).toList()
                CsvImportState(
                    previewRows = lines.map(::parseCsvLine),
                    hasHeader = lines.firstOrNull()?.contains(",") == true,
                )
            }
        }.onFailure { error ->
            reportException(error)
            Toast.makeText(context, "Failed to preview CSV file", Toast.LENGTH_SHORT).show()
        }.getOrNull()

        return previewState ?: CsvImportState()
    }

    fun importPlaylistFromCsv(
        context: Context,
        uri: Uri,
        columnMapping: CsvImportState,
        onProgress: (Int) -> Unit = {},
        onLogUpdate: (List<ConvertedSongLog>) -> Unit = {},
    ): ArrayList<Song> {
        val songs = arrayListOf<Song>()
        val recentLogs = mutableListOf<ConvertedSongLog>()

        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val lines = stream.bufferedReader().readLines()
                val startIndex = if (columnMapping.hasHeader) 1 else 0
                val totalLines = lines.size - startIndex

                lines.drop(startIndex).forEachIndexed { index, line ->
                    val song = parseCsvSong(parseCsvLine(line), columnMapping)
                    if (song != null) {
                        songs.add(song)
                        val logEntry = ConvertedSongLog(
                            title = song.song.title,
                            artists = song.artists.joinToString(", ") { it.name },
                        )
                        recentLogs.add(0, logEntry)
                        if (recentLogs.size > 3) recentLogs.removeAt(recentLogs.lastIndex)
                        onLogUpdate(recentLogs.toList())
                    }

                    val progress = ((index + 1) * 100) / totalLines
                    onProgress(progress.coerceIn(0, 99))
                }
            }
        }.onFailure {
            reportException(it)
            Timber.tag("CSV_IMPORT").e(it, "CSV import failed")
            Toast.makeText(
                context,
                "Failed to import CSV file",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (songs.isEmpty()) {
            Toast.makeText(
                context,
                "No songs found. Invalid file, or perhaps no song matches were found.",
                Toast.LENGTH_SHORT
            ).show()
        }
        return songs
    }

    private fun parseCsvSong(parts: List<String>, mapping: CsvImportState): Song? {
        if (parts.isEmpty()) return null
        if (mapping.artistColumnIndex >= parts.size || mapping.titleColumnIndex >= parts.size) return null

        val title = parts[mapping.titleColumnIndex].trim()
        val artistNames = parts[mapping.artistColumnIndex].trim()
        if (title.isEmpty() || artistNames.isEmpty()) return null

        val artists = artistNames.split(";", ",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { ArtistEntity(id = "", name = it) }

        return Song(
            song = SongEntity(id = "", title = title),
            artists = artists,
        )
    }

    fun importPlaylistFromCsv(context: Context, uri: Uri): ArrayList<Song> {
        return importPlaylistFromCsv(context, uri, CsvImportState())
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString())
        return result.map { it.trim().trim('"') }
    }

    fun loadM3UOnline(
        context: Context,
        uri: Uri,
    ): ArrayList<Song> {
        val songs = ArrayList<Song>()

        runCatching {
            context.applicationContext.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                if (reader.readLine()?.startsWith("#EXTM3U") == true) {
                    reader.forEachLine { line ->
                        parseM3uEntry(line)?.let(songs::add)
                    }
                }
            }
        }.onFailure { reportException(it) }

        if (songs.isEmpty()) {
            Toast.makeText(
                context,
                "No songs found. Invalid file, or perhaps no song matches were found.",
                Toast.LENGTH_SHORT
            ).show()
        }
        return songs
    }

    private fun parseM3uEntry(line: String): Song? {
        if (!line.startsWith("#EXTINF:")) return null

        val metadata = line.substringAfter("#EXTINF:").substringAfter(',')
        val artists = metadata.substringBefore(" - ").split(';')
        val title = metadata.substringAfter(" - ")
        return Song(
            song = SongEntity(id = "", title = title),
            artists = artists.map { ArtistEntity("", it) },
        )
    }

    companion object {
        const val SETTINGS_FILENAME = "settings.preferences_pb"

        // Keep this prefix in sync with HomeBackgroundSettings.copyBackgroundMedia().
        private const val HOME_BACKGROUND_FILE_PREFIX = "home_background_"
        private const val HOME_BACKGROUND_ZIP_DIR = "home_background_files"
    }
}
