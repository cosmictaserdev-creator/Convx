package com.music.innertube.soundcloud

import java.net.URI

object SoundCloudIds {
    fun isSoundCloudId(id: String): Boolean = toUrl(id) != null

    fun toId(url: String): String? {
        val uri = runCatching { URI(url) }.getOrNull() ?: return null
        if (uri.scheme !in listOf("https", "http") ||
            uri.host?.lowercase() !in listOf("soundcloud.com", "www.soundcloud.com") ||
            uri.userInfo != null || uri.port != -1
        ) return null
        val path = uri.rawPath.orEmpty().removePrefix("/").removeSuffix("/")
        return path.takeIf(::isTrackPath)?.let { "sc:$it" }
    }

    fun toUrl(id: String): String? {
        if (!id.startsWith("sc:")) return null
        return id.removePrefix("sc:").takeIf(::isTrackPath)?.let { "https://soundcloud.com/$it" }
    }

    private fun isTrackPath(path: String): Boolean {
        val segments = path.split('/')
        return segments.size == 2 && segments.all { it.matches(Regex("[A-Za-z0-9_-]+")) } &&
            segments.first() !in setOf("discover", "charts", "search") &&
            segments.last() !in setOf("tracks", "albums", "sets", "reposts", "likes", "popular-tracks", "followers", "following")
    }
}
