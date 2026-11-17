package com.music.innertube.utils

import com.music.innertube.pages.LibraryPage
import com.music.innertube.pages.PlaylistPage

/** Preserves the original Android source API while common callers use distinct names. */
@JvmName("completedPlaylist")
suspend fun Result<PlaylistPage>.completed(): Result<PlaylistPage> = completedPlaylist()

@JvmName("completedLibrary")
suspend fun Result<LibraryPage>.completed(): Result<LibraryPage> = completedLibrary()
