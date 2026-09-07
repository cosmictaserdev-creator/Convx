package com.music.innertube.models

/**
 * Domain models for the regular (non-music) YouTube experience.
 *
 * These are intentionally small, immutable and independent from the InnerTube
 * response shapes: parsers map the (messy, ever-changing) WEB payloads into
 * these, and the UI/playback layers only ever see them. Nothing here holds on
 * to raw JSON.
 */

/** A regular YouTube video as shown in feeds, search results and the watch page. */
data class WebVideo(
    val id: String,
    val title: String,
    val channelId: String?,
    val channelName: String?,
    /** Thumbnail URL, already upgraded to a reasonably sharp variant. */
    val thumbnail: String?,
    /** Duration in seconds; null for live/upcoming/premiere content. */
    val durationSeconds: Int?,
    /** Human readable view count as YouTube renders it ("1.2M views"), may be null. */
    val viewsText: String?,
    /** Human readable relative date ("3 days ago"), may be null. */
    val publishedText: String?,
    val isShort: Boolean = false,
    /** Raw search/usage context text YouTube attaches to some results (e.g. "New", "4K"). */
    val badges: List<String> = emptyList(),
) {
    val isLive: Boolean get() = durationSeconds == null && !badges.none { it.contains("live", ignoreCase = true) }

    val watchUrl: String get() = "https://www.youtube.com/watch?v=$id"
}

/** A YouTube channel (used by search results and channel pages). */
data class WebChannel(
    /** UC… channel id when known, otherwise an @handle that still browses fine. */
    val id: String,
    val title: String,
    val avatarUrl: String?,
    val subscriberText: String?,
    val videoCountText: String?,
    val description: String? = null,
)

/** A YouTube playlist (public playlists only — no authenticated editing). */
data class WebPlaylist(
    /** Playlist id without the "VL" prefix. */
    val id: String,
    val title: String,
    val thumbnail: String?,
    val itemCountText: String?,
    val ownerName: String?,
    val ownerId: String? = null,
) {
    val shareLink: String get() = "https://www.youtube.com/playlist?list=$id"
}

/** One page of search results for a given filter. */
data class WebSearchPage(
    val videos: List<WebVideo>,
    val channels: List<WebChannel>,
    val playlists: List<WebPlaylist>,
    val continuation: String?,
)

/** A horizontal section of the home feed. */
data class WebFeedSection(
    val title: String,
    val videos: List<WebVideo>,
)

/** The YouTube home ("what to watch") feed page. */
data class WebFeed(
    val sections: List<WebFeedSection>,
    val shorts: List<WebVideo>,
    val continuation: String?,
)

/** Watch page metadata for a video (player/stream resolution is Convxy's existing pipeline). */
data class WebWatchPage(
    val video: WebVideo,
    /** Channel avatar for the owner row. */
    val channelAvatarUrl: String? = null,
    /** Expanded, plain-text description with entities decoded. */
    val description: String?,
    /** Like count text when YouTube exposes one; null when likes are hidden/disabled. */
    val likeCountText: String?,
    /** Human readable subscriber count for the owner, when available. */
    val subscriberText: String? = null,
    val isLive: Boolean = false,
    val related: List<WebVideo> = emptyList(),
    val relatedContinuation: String? = null,
)

/** A channel page: header metadata plus the contents of the initially selected tab. */
data class WebChannelPage(
    val channel: WebChannel,
    /** Tabs that actually exist for this channel, in YouTube's order. */
    val tabs: List<WebChannelTab>,
    val defaultTabIndex: Int = 0,
    /** Parsed shelves of the Home tab, which ships inline (no continuation params). */
    val homeContent: WebChannelTabPage? = null,
)

data class WebChannelTab(
    val title: String,
    /** params value that selects this tab via browse(browseId, params). */
    val params: String?,
)

/** Contents of one channel tab. */
data class WebChannelTabPage(
    val videos: List<WebVideo>,
    val shorts: List<WebVideo>,
    val playlists: List<WebPlaylist>,
    val continuation: String?,
)

/** A playlist page: header plus the first page of items. */
data class WebPlaylistPage(
    val playlist: WebPlaylist,
    val videos: List<WebVideo>,
    val continuation: String?,
)

/** Search filter for the WEB search endpoint. */
enum class WebSearchFilter(val params: String?) {
    /** Unfiltered "Top" results. */
    NONE(null),
    VIDEOS("EgIQAQ=="),
    CHANNELS("EgIQAg=="),
    PLAYLISTS("EgIQAw=="),
}
