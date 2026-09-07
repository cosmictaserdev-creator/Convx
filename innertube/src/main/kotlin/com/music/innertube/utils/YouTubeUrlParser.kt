package com.music.innertube.utils

import com.music.innertube.models.WatchEndpoint

/**
 * Utility class for parsing YouTube and YouTube Music URLs.
 * Extracts video IDs, playlist IDs, and creates WatchEndpoints from URLs.
 */
object YouTubeUrlParser {
    /**
     * Represents the type of YouTube link parsed.
     */
    sealed class ParsedUrl {
        abstract val id: String

        data class Video(
            override val id: String,
            /** True for /shorts/ links — same playable video, vertical presentation. */
            val isShort: Boolean = false,
        ) : ParsedUrl()

        data class Artist(
            override val id: String,
        ) : ParsedUrl()

        /** A regular YouTube channel (UC… id or @handle). */
        data class Channel(
            override val id: String,
        ) : ParsedUrl()

        /** A playlist id from ?list=… or /playlist URLs (OLAK5uy_ music albums keep their own flow). */
        data class Playlist(
            override val id: String,
        ) : ParsedUrl()
    }

    /**
     * Pattern for matching YouTube video URLs.
     */
    private val VIDEO_URL_PATTERNS =
        listOf(
            Regex("""(?:https?://)?(?:www\.|m\.)?(?:music\.)?youtube\.com/watch\?.*v=([a-zA-Z0-9_-]{11})"""),
            Regex("""(?:https?://)?(?:www\.)?(?:music\.)?youtube\.com/watch\?v=([a-zA-Z0-9_-]{11})"""),
            Regex("""(?:https?://)?youtu\.be/([a-zA-Z0-9_-]{11})"""),
            Regex("""(?:https?://)?(?:www\.|m\.)?youtube\.com/shorts/([a-zA-Z0-9_-]{11})"""),
            Regex("""(?:https?://)?(?:www\.)?youtube\.com/(?:v|embed|live)/([a-zA-Z0-9_-]{11})"""),
        )

    /**
     * Pattern for matching YouTube Music artist URLs.
     */
    private val ARTIST_URL_PATTERNS =
        listOf(
            Regex("""(?:https?://)?(?:www\.)?music\.youtube\.com/channel/([a-zA-Z0-9_-]+)"""),
            Regex("""(?:https?://)?(?:www\.)?music\.youtube\.com/browse/(MPRE[a-zA-Z0-9_-]+)"""),
        )

    /** Regular YouTube channel URLs: /channel/UC…, /@handle, /c/name, /user/name. */
    private val CHANNEL_URL_PATTERNS =
        listOf(
            Regex("""(?:https?://)?(?:www\.|m\.)?youtube\.com/channel/([a-zA-Z0-9_-]{10,})"""),
            Regex("""(?:https?://)?(?:www\.|m\.)?youtube\.com/@([a-zA-Z0-9._-]+)"""),
            Regex("""(?:https?://)?(?:www\.|m\.)?youtube\.com/c/([a-zA-Z0-9._-]+)"""),
            Regex("""(?:https?://)?(?:www\.|m\.)?youtube\.com/user/([a-zA-Z0-9._-]+)"""),
        )

    /** Playlist URLs — the plain ?list=… form is handled separately (it rides along with watch links). */
    private val PLAYLIST_URL_PATTERNS =
        listOf(
            Regex("""(?:https?://)?(?:www\.|m\.)?youtube\.com/playlist\?.*list=([a-zA-Z0-9_-]+)"""),
        )

    /**
     * Checks if the given text is a YouTube URL.
     */
    fun isYouTubeUrl(text: String): Boolean = parse(text) != null || parsePlaylistId(text) != null

    /**
     * Extracts a bare playlist id from any URL that carries ?list=….
     */
    fun parsePlaylistId(url: String): String? =
        Regex("""[?&]list=([a-zA-Z0-9_-]+)""").find(url.trim())?.groupValues?.getOrNull(1)

    /**
     * Parses a YouTube URL and returns the parsed result.
     *
     * @param url The URL to parse
     * @return ParsedUrl if valid, null otherwise
     */
    fun parse(url: String): ParsedUrl? {
        val trimmedUrl = url.trim()

        // Check for video URLs
        for (pattern in VIDEO_URL_PATTERNS) {
            pattern.find(trimmedUrl)?.let { matchResult ->
                matchResult.groupValues.getOrNull(1)?.let { videoId ->
                    return ParsedUrl.Video(
                        id = videoId,
                        isShort = trimmedUrl.contains("/shorts/"),
                    )
                }
            }
        }

        // Check for artist URLs
        if (trimmedUrl.contains("music.youtube.com")) {
            for (pattern in ARTIST_URL_PATTERNS) {
                pattern.find(trimmedUrl)?.let { matchResult ->
                    matchResult.groupValues.getOrNull(1)?.let { artistId ->
                        return ParsedUrl.Artist(artistId)
                    }
                }
            }
            return null
        }

        // Regular YouTube: channel and playlist URLs.
        for (pattern in CHANNEL_URL_PATTERNS) {
            pattern.find(trimmedUrl)?.let { matchResult ->
                matchResult.groupValues.getOrNull(1)?.let { channelId ->
                    val id = if (matchResult.value.contains("/@") || matchResult.value.contains("/c/") ||
                        matchResult.value.contains("/user/")
                    ) {
                        "@$channelId"
                    } else {
                        channelId
                    }
                    return ParsedUrl.Channel(id)
                }
            }
        }

        for (pattern in PLAYLIST_URL_PATTERNS) {
            pattern.find(trimmedUrl)?.let { matchResult ->
                matchResult.groupValues.getOrNull(1)?.let { playlistId ->
                    return ParsedUrl.Playlist(playlistId)
                }
            }
        }

        return null
    }

    /**
     * Extracts video ID from a YouTube URL.
     */
    fun extractVideoId(url: String): String? = (parse(url) as? ParsedUrl.Video)?.id

    /**
     * Creates a WatchEndpoint from a YouTube video URL.
     */
    fun createWatchEndpoint(url: String): WatchEndpoint? =
        extractVideoId(url)?.let { videoId ->
            WatchEndpoint(videoId = videoId)
        }
}
