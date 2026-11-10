/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.lyrics

object LyricsProviderRegistry {
    private val providers = listOf(
        YouTubeSubtitleLyricsProvider,
        YouTubeLyricsProvider,
    ).plus(BitChordLyricsProviders.all).associateBy(LyricsProvider::id)

    private val legacyDisplayNames = providers.values.associate { it.displayName to it.id } + mapOf(
        "YouLy+" to "YouLyPlus", "YouLyPlus" to "YouLyPlus", "LrcLib" to "LrcLib", "Kugou" to "Kugou",
    )

    val providerIds = providers.keys.toList()

    fun getProvider(id: String): LyricsProvider? = providers[id]

    fun getProviderForStoredName(value: String): LyricsProvider? =
        providers[value] ?: legacyDisplayNames[value]?.let(providers::get)

    fun deserializeProviderOrder(orderString: String): List<String> {
        if (orderString.isBlank()) return getDefaultProviderOrder()
        return orderString.split(",").map { it.trim() }.filter { it in providerIds }.distinct()
    }

    fun serializeProviderOrder(providers: List<String>): String =
        providers.filter { it in providerIds }.distinct().joinToString(",")

    // Word timing providers lead so the default renderer receives real word
    // timestamps. The saved order always takes precedence over this default.
    fun getDefaultProviderOrder(): List<String> = listOf(
        "LrcRed",
        "BiniLyrics",
        "BetterLyrics",
        "BetterLyricsPortato",
        "Paxsenix",
        "PaxsenixSpotify",
        "PaxsenixMusixmatch",
        "YouLyPlus",
        "Unison",
        "SimpMusic",
        "YouTubeSubtitle",
        "YouTubeMusic",
        "Megalobiz",
        "Kugou",
        "LrcLib",
        "Musixmatch",
        "Genius",
    )

    fun getOrderedProviders(orderString: String): List<LyricsProvider> =
        completeProviderOrder(orderString).mapNotNull(::getProvider)

    fun completeProviderOrder(orderString: String): List<String> {
        val saved = deserializeProviderOrder(orderString)
        return saved + getDefaultProviderOrder().filterNot { it in saved }
    }

    fun getDisplayName(id: String): String = getProviderForStoredName(id)?.displayName ?: id
}
