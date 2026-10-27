package com.convx.music.client.desktop

import coil3.map.Mapper
import coil3.request.Options
import java.io.File
import java.net.URI

/** Decode file URIs before Coil converts them to native Windows paths. */
internal class DesktopFileArtworkMapper : Mapper<String, File> {
    override fun map(data: String, options: Options): File? = desktopArtworkFile(data)
}

internal fun desktopArtworkFile(value: String): File? {
    if (!value.startsWith("file:", ignoreCase = true)) return null
    return try { File(URI(value)) } catch (_: IllegalArgumentException) { null }
}
