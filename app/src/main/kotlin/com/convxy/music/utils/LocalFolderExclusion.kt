/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

/** Delimiter for LocalExcludedFoldersKey — a filesystem path can never contain a
 *  newline, unlike '/' or ','. */
private const val DELIMITER = "\n"

fun encodeExcludedFolders(paths: Set<String>): String = paths.joinToString(DELIMITER)

fun decodeExcludedFolders(raw: String): Set<String> =
    if (raw.isEmpty()) emptySet() else raw.split(DELIMITER).toSet()
