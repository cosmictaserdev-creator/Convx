/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.theme

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.convxy.music.constants.CustomFontArtistOnlyKey
import com.convxy.music.constants.CustomFontEnabledKey
import com.convxy.music.constants.CustomFontPathKey
import com.convxy.music.utils.rememberPreference
import java.io.File

/** Where installed custom fonts live, one file per install (old one is deleted on replace). */
internal fun customFontDir(context: Context) = File(context.filesDir, "custom_fonts").apply { mkdirs() }

/**
 * Copies a user-picked font file into app storage so it survives without a
 * persistable URI permission, and returns its absolute path, or null on
 * failure (wrong extension, unreadable, etc). Only .ttf/.otf are accepted —
 * font MIME types are unreliable enough across devices/pickers that
 * extension sniffing after the copy is the sturdier check.
 */
fun copyCustomFont(context: Context, source: Uri): String? = runCatching {
    val displayName = context.contentResolver.query(source, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
    } ?: source.lastPathSegment.orEmpty()

    val extension = displayName.substringAfterLast('.', "").lowercase()
    if (extension != "ttf" && extension != "otf") return null

    val dir = customFontDir(context)
    dir.listFiles()?.forEach { it.delete() }
    val dest = File(dir, "custom_font.$extension")
    context.contentResolver.openInputStream(source)?.use { input ->
        dest.outputStream().use { output -> input.copyTo(output) }
    } ?: return null
    dest.absolutePath
}.getOrNull()

/** Loads the installed custom font file, or null if unset/missing/unreadable. */
@Composable
private fun rememberInstalledCustomFont(): FontFamily? {
    val (enabled) = rememberPreference(CustomFontEnabledKey, defaultValue = false)
    val (path) = rememberPreference(CustomFontPathKey, defaultValue = "")
    return remember(enabled, path) {
        if (!enabled || path.isEmpty()) return@remember null
        val file = File(path)
        if (!file.exists()) return@remember null
        runCatching { FontFamily(Font(file)) }.getOrNull()
    }
}

/**
 * The user's installed custom font, applied to [AppTypography] app-wide —
 * null (falls through to the default Material typeface) whenever the font is
 * off/missing, OR when the user scoped it to artist names only via
 * [CustomFontArtistOnlyKey], in which case [rememberCustomArtistFontFamily]
 * is what artist-name call sites use instead.
 */
@Composable
fun rememberCustomFontFamily(): FontFamily? {
    val font = rememberInstalledCustomFont()
    val (artistOnly) = rememberPreference(CustomFontArtistOnlyKey, defaultValue = false)
    return if (artistOnly) null else font
}

/**
 * The user's installed custom font for artist-name text specifically —
 * applies regardless of the artist-only scope setting (it's always a subset
 * of "everywhere"). Mirrors [rememberBrandFontFamily]'s call sites.
 */
@Composable
fun rememberCustomArtistFontFamily(): FontFamily? = rememberInstalledCustomFont()
