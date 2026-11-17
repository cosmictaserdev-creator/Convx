package com.music.innertube.models

data class AccountInfo(
    val name: String,
    val email: String?,
    val channelHandle: String?,
    val thumbnailUrl: String?,
    /** Only populated by [com.music.innertube.YouTube.getAccountChannels] — selects this channel. */
    val dataSyncId: String? = null,
)
