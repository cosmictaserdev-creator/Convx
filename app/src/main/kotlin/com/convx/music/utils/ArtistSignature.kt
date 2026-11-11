package com.convx.music.utils

/** Android entry point for the shared artist wordmark lookup. */
object ArtistSignature {
    suspend fun findUrl(artistName: String): String? = com.convx.music.canvas.ArtistSignature.findUrl(artistName)
}
