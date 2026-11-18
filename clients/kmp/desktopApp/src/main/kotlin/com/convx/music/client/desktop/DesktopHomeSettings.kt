package com.convx.music.client.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.catalog.HomeSection
import com.convx.music.catalog.HomeSectionOrder
import com.convx.music.playback.AppearanceSettings
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder

private fun HomeSection.label() = when (this) {
    HomeSection.MadeForYou -> "Made for you"
    HomeSection.TopPicks -> "Top picks"
    HomeSection.NewFromYourArtists -> "New from your artists"
    HomeSection.NewReleases -> "New releases"
    HomeSection.Trending -> "Trending"
    HomeSection.Hero -> "Hero card"
    HomeSection.ListenAgain -> "Listen again"
    HomeSection.QuickPicks -> "Quick picks"
    HomeSection.SpeedDial -> "Speed dial"
    HomeSection.FromTheCommunity -> "From the community"
    HomeSection.DailyDiscover -> "Daily discover"
    HomeSection.AccountPlaylists -> "Your playlists"
    HomeSection.ForgottenFavorites -> "Forgotten favorites"
    HomeSection.TopSongs -> "Top songs"
    HomeSection.LiveShows -> "Live shows"
    HomeSection.MusicVideos -> "Music videos"
    HomeSection.ArtistList -> "Artist list"
    HomeSection.AllTimeHits -> "All-time hits"
    HomeSection.MoodAndGenres -> "Mood and genres"
    else -> id
}

private fun lines(text: String) = text.lines().map(String::trim).filter(String::isNotEmpty)

/** The saved order with sections it does not list appended in default order. */
private fun effectiveOrder(saved: String): List<String> =
    (lines(saved) + HomeSectionOrder.configurable.map { it.id }).distinct()

@Composable
internal fun DesktopHomeSettings(appearance: AppearanceSettings, settings: PlaybackSettingsStateHolder) {
    val hidden = lines(appearance.homeHiddenSections).toSet()
    val order = effectiveOrder(appearance.homeSectionOrder)
    val byId = HomeSectionOrder.configurable.associateBy { it.id }
    fun move(id: String, by: Int) {
        val from = order.indexOf(id)
        val to = (from + by).coerceIn(0, order.lastIndex)
        if (from == to) return
        val next = order.toMutableList().apply { add(to, removeAt(from)) }
        settings.updateAppearance { it.copy(homeSectionOrder = next.joinToString("\n"), randomizeHomeOrder = false) }
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Home", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        ThemeToggle("Shuffle section order", appearance.randomizeHomeOrder) { value -> settings.updateAppearance { it.copy(randomizeHomeOrder = value) } }
        order.mapNotNull(byId::get).forEach { section ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f)) {
                    ThemeToggle(section.label(), section.id !in hidden) { shown ->
                        val next = if (shown) hidden - section.id else hidden + section.id
                        settings.updateAppearance { it.copy(homeHiddenSections = next.joinToString("\n")) }
                    }
                }
                MediaButton("↑") { move(section.id, -1) }
                MediaButton("↓") { move(section.id, 1) }
            }
        }
    }
}
