package com.music.innertube

/** Browser-backed BotGuard tokens needed by web player clients for full adaptive streams. */
data class InnertubeWebPoTokens(
    val playerRequest: String,
    val streamingData: String,
)

fun interface InnertubeWebPoTokenProvider {
    suspend fun generate(videoId: String, visitorData: String): InnertubeWebPoTokens?
}

object NoOpInnertubeWebPoTokenProvider : InnertubeWebPoTokenProvider {
    override suspend fun generate(videoId: String, visitorData: String): InnertubeWebPoTokens? = null
}
