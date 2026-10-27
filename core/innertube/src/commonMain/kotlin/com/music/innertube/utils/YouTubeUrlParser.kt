package com.music.innertube.utils

import com.music.innertube.models.WatchEndpoint

/** Parses supported YouTube video URLs and YouTube Music channel or browse links. */
object YouTubeUrlParser {
    sealed class ParsedUrl {
        abstract val id: String

        data class Video(
            override val id: String,
        ) : ParsedUrl()

        data class Artist(
            override val id: String,
        ) : ParsedUrl()
    }

    private val VIDEO_URL_PATTERNS =
        listOf(
            Regex("""(?:https?://)?(?:www\.)?(?:music\.)?youtube\.com/watch\?.*v=([a-zA-Z0-9_-]{11})"""),
            Regex("""(?:https?://)?youtu\.be/([a-zA-Z0-9_-]{11})"""),
            Regex("""(?:https?://)?(?:www\.)?youtube\.com/shorts/([a-zA-Z0-9_-]{11})"""),
        )

    private val ARTIST_URL_PATTERNS =
        listOf(
            Regex("""(?:https?://)?(?:www\.)?music\.youtube\.com/channel/([a-zA-Z0-9_-]+)"""),
            Regex("""(?:https?://)?(?:www\.)?music\.youtube\.com/browse/(MPRE[a-zA-Z0-9_-]+)"""),
        )

    fun isYouTubeUrl(text: String): Boolean = parse(text) != null

    fun parse(url: String): ParsedUrl? {
        val trimmedUrl = url.trim()

        for (pattern in VIDEO_URL_PATTERNS) {
            pattern.find(trimmedUrl)?.let { matchResult ->
                matchResult.groupValues.getOrNull(1)?.let { videoId ->
                    return ParsedUrl.Video(videoId)
                }
            }
        }

        if (trimmedUrl.contains("music.youtube.com")) {
            for (pattern in ARTIST_URL_PATTERNS) {
                pattern.find(trimmedUrl)?.let { matchResult ->
                    matchResult.groupValues.getOrNull(1)?.let { artistId ->
                        return ParsedUrl.Artist(artistId)
                    }
                }
            }
        }

        return null
    }

    fun extractVideoId(url: String): String? = (parse(url) as? ParsedUrl.Video)?.id

    fun createWatchEndpoint(url: String): WatchEndpoint? =
        extractVideoId(url)?.let { videoId ->
            WatchEndpoint(videoId = videoId)
        }
}
