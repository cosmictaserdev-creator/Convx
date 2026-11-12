package com.convx.music.utils

object ShareUtils {
    const val DOMAIN = "convx.aryavl.com"
    const val BASE_URL = "https://$DOMAIN"

    fun songUrl(songId: String): String = "$BASE_URL/watch/?v=$songId"

    fun playlistUrl(playlistId: String): String = "$BASE_URL/playlist/?list=$playlistId"

    /**
     * An album with a playlist id is shared as that playlist, which the app opens as the album. One without one
     * (a browse id like MPREb_...) has its own /album/ page; as a playlist link it would not load.
     */
    fun albumUrl(playlistId: String?, browseId: String): String =
        if (playlistId.isNullOrBlank()) "$BASE_URL/album/$browseId" else playlistUrl(playlistId)

    fun artistUrl(artistId: String): String = "$BASE_URL/channel/$artistId"
}
