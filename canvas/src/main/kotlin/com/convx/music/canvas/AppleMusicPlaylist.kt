package com.convx.music.canvas

import kotlinx.serialization.json.*

object AppleMusicPlaylist {
    suspend fun fetch(link: ApplePlaylistLink): ApplePlaylist = fetch(link) {
        AppleMusicCatalog.request(it, priority = true)
    }

    internal suspend fun fetch(link: ApplePlaylistLink, request: suspend (String) -> JsonObject?): ApplePlaylist =
        AppleMusicPlaylistMapper.fetch(link, request)
}
