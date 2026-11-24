package com.convx.music.viewmodels

/** Resolves only generated background filenames from the Android backup format. */
internal object BackupArchiveEntryPolicy {
    private const val BACKGROUND_DIRECTORY = "home_background_files/"
    private const val BACKGROUND_FILE_PREFIX = "home_background_"

    fun backgroundFileName(entryName: String): String? {
        if (!entryName.startsWith(BACKGROUND_DIRECTORY)) return null

        val fileName = entryName.removePrefix(BACKGROUND_DIRECTORY)
        if (fileName.isBlank() || fileName != fileName.substringAfterLast('/')) return null
        if ('\\' in fileName || fileName == "." || fileName == "..") return null
        if (!fileName.startsWith(BACKGROUND_FILE_PREFIX)) return null

        return fileName
    }
}
