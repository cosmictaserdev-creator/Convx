/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.R
import com.convx.music.ui.component.IosPageSheet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.foundation.IosColors
import com.convx.music.ui.ios.foundation.IosMetrics
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.iosRowPress

/** What a settings row does when tapped. */
sealed interface SettingsAction {
    data class Route(val route: String) : SettingsAction
    data object PlayerDesign : SettingsAction
    data object LyricsStyle : SettingsAction
    data object AppleMusicImport : SettingsAction
}

/** One row: a coloured glyph tile, a name, and where it goes. */
data class SettingsLink(
    val title: String,
    val icon: Int,
    val tint: IosColors.() -> Color,
    val action: SettingsAction,
    val subtitle: String? = null,
    /** Extra words people search with that are not in the title. */
    val keywords: String = "",
)

data class SettingsLinkGroup(val title: String?, val links: List<SettingsLink>)

/** A root category and the page it opens. */
data class SettingsHub(
    val id: String,
    val title: String,
    val icon: Int,
    val tint: IosColors.() -> Color,
    val groups: List<SettingsLinkGroup>,
) {
    val route get() = "settings/hub/$id"
    fun asLink() = SettingsLink(title, icon, tint, SettingsAction.Route(route))
}

private fun route(r: String) = SettingsAction.Route(r)

/** The settings tree: root categories, the pages they open, and every destination under them. */
object SettingsHubs {
    val player = SettingsHub(
        "player", "Player & Lyrics", R.drawable.play, { systemPink },
        listOf(
            SettingsLinkGroup(
                "Player",
                listOf(
                    SettingsLink("Player design", R.drawable.play, { systemPink }, SettingsAction.PlayerDesign,
                        "Card, Vinyl, Clover, Apple Music — live previews", "theme layout background seek bar slider up next shadow volume bar"),
                    SettingsLink("Controls & layout", R.drawable.sliders, { systemIndigo }, route("settings/appearance/advanced"),
                        "Thumbnail, mini player, buttons and badges", "mini player comment button audio quality badge thumbnail"),
                    SettingsLink("Player icons", R.drawable.tune, { systemOrange }, route("settings/appearance/playericons"),
                        "Choose the playback control icons"),
                    SettingsLink("Canvas", R.drawable.canvas_art, { systemTeal }, route("settings/appearance/canvas"),
                        "Animated artwork behind the player", "video animated artwork"),
                ),
            ),
            SettingsLinkGroup(
                "Lyrics",
                listOf(
                    SettingsLink("Lyrics style", R.drawable.lyrics, { systemPink }, SettingsAction.LyricsStyle,
                        "Animation, position, scrolling and swipe — live preview", "animation karaoke word by word apple music position auto scroll swipe"),
                    SettingsLink("Lyrics effects", R.drawable.lyrics, { systemPink }, route("settings/lyrics"),
                        "Style, fonts, size, glow and blur", "convx lyrics font size glow blur apple music standard"),
                    SettingsLink("Text & layout", R.drawable.lyrics, { systemOrange }, route("settings/appearance"),
                        "Text size, spacing and full-screen lyrics", "size spacing fullscreen"),
                    SettingsLink("Sources & priority", R.drawable.lyrics, { systemBlue }, route("settings/content/advanced"),
                        "Choose and order lyric providers", "provider youlyplus paxsenix lrclib"),
                    SettingsLink("Romanization", R.drawable.translate, { systemTeal }, route("settings/content/romanization"),
                        "Show lyrics in Latin letters", "japanese korean chinese cyrillic"),
                    SettingsLink("AI translation", R.drawable.translate, { systemPurple }, route("settings/ai"),
                        "Provider, model and target language", "openrouter deepl api key"),
                ),
            ),
        ),
    )

    val playback = SettingsHub(
        "playback", "Playback & Audio", R.drawable.equalizer, { systemRed },
        listOf(
            SettingsLinkGroup(
                "Sound",
                listOf(
                    SettingsLink("Equalizer", R.drawable.equalizer, { systemGreen }, route("settings/equalizer"), "Shape the sound", "eq bass treble"),
                    SettingsLink("Quality & sound", R.drawable.tune, { systemBlue }, route("settings/player"),
                        "Audio quality, normalization and skip silence", "quality normalization loudness silence"),
                    SettingsLink("Song transitions", R.drawable.shuffle, { systemPurple }, route("settings/song_transitions"),
                        "Crossfade, gapless and Automix", "crossfade gapless automix dj mixing"),
                ),
            ),
            SettingsLinkGroup(
                "Queue & behaviour",
                listOf(
                    SettingsLink("Queue & autoplay", R.drawable.queue_music, { systemOrange }, route("settings/player"),
                        "Persistent queue, similar songs, shuffle memory", "persistent queue autoplay similar shuffle repeat"),
                    SettingsLink("Bluetooth & output", R.drawable.cast, { systemIndigo }, route("settings/player"),
                        "Resume on connect, pause when muted, Cast", "bluetooth cast chromecast mute"),
                ),
            ),
            SettingsLinkGroup(
                "Advanced",
                listOf(
                    SettingsLink("Advanced playback", R.drawable.settings, { systemGray }, route("settings/player/advanced"),
                        "Offload, bit-perfect and queue rules", "offload bit perfect duplicates history duration"),
                    SettingsLink("JioSaavn", R.drawable.music_note, { systemTeal }, route("settings/player/jio"), "Streaming source settings"),
                    SettingsLink("AI DJ analysis", R.drawable.equalizer, { systemPurple }, route("ai_dj_set"),
                        "Analyze songs and build a mix", "bpm key mix analysis"),
                ),
            ),
        ),
    )

    val appearance = SettingsHub(
        "appearance", "Appearance", R.drawable.palette, { systemPurple },
        listOf(
            SettingsLinkGroup(
                "Theme & colors",
                listOf(
                    SettingsLink("Theme", R.drawable.palette, { systemPurple }, route("settings/appearance/theme"), "Accent, dark mode and pure black", "dark light accent color"),
                    SettingsLink("Presets", R.drawable.palette, { systemPink }, route("settings/appearance/presets"), "Ready-made looks"),
                    SettingsLink("Make your own", R.drawable.edit, { systemOrange }, route("settings/appearance/diy"), "Design a theme from scratch", "diy"),
                    SettingsLink("Liquid Glass", R.drawable.palette, { systemCyan }, route("settings/appearance/liquidglass"), "Blur, tint, highlights and lens", "glass blur"),
                ),
            ),
            SettingsLinkGroup(
                "App",
                listOf(
                    SettingsLink("Font", R.drawable.alphabet_cyrillic, { systemGray }, route("settings/appearance/font"), "App typeface"),
                    SettingsLink("App icon", R.drawable.convx_logo, { systemBlue }, route("settings/appearance/appicon"), "Home screen icon"),
                    SettingsLink("Layout & home", R.drawable.sliders, { systemGreen }, route("settings/appearance"),
                        "Language, default tab, density, home", "language default tab density refresh rate"),
                    SettingsLink("Home feed order", R.drawable.sliders, { systemOrange }, route("settings/home_feed_order"), "Arrange home sections"),
                ),
            ),
            SettingsLinkGroup(
                "Advanced",
                listOf(
                    SettingsLink("Advanced appearance", R.drawable.settings, { systemGray }, route("settings/appearance/advanced"),
                        "Grids, corners, blur and overscroll", "grid corner radius blur overscroll pure black"),
                ),
            ),
        ),
    )

    val library = SettingsHub(
        "library", "Library & Content", R.drawable.music_note, { systemOrange },
        listOf(
            SettingsLinkGroup(
                "Content",
                listOf(
                    SettingsLink("Region & filters", R.drawable.language, { systemBlue }, route("settings/content"),
                        "Language, country, explicit, Shorts, data saver", "language country explicit shorts data saver video"),
                    SettingsLink("Artist pages & home", R.drawable.music_note, { systemPink }, route("settings/content/advanced"),
                        "Artist videos, quick picks, chart length", "artist background video quick picks"),
                ),
            ),
            SettingsLinkGroup(
                "Local music",
                listOf(
                    SettingsLink("Music folders", R.drawable.music_note, { systemOrange }, route("settings/player/local_folders"), "Choose folders to scan", "local files"),
                    SettingsLink("Scan local files", R.drawable.search, { systemGreen }, route("settings/scan_music"), "Find songs on this device"),
                    SettingsLink("Network storage", R.drawable.storage, { systemGray }, route("settings/network_storage"), "SMB and WebDAV shares", "smb webdav nas"),
                ),
            ),
            SettingsLinkGroup(
                null,
                listOf(SettingsLink("Modules", R.drawable.link, { systemBlue }, route("settings/modules"), "Music sources and add-ons", "plugins extensions")),
            ),
        ),
    )

    val accounts = SettingsHub(
        "accounts", "Accounts & Services", R.drawable.account, { systemGreen },
        listOf(
            SettingsLinkGroup(
                "Accounts",
                listOf(
                    SettingsLink("YouTube Music", R.drawable.account, { systemRed }, route("settings/account"), "Sign-in, sync and ListenBrainz", "login youtube sync listenbrainz"),
                    SettingsLink("Spotify", R.drawable.spotify, { systemGreen }, route("settings/spotify"), "Connect and import playlists", "import"),
                    SettingsLink("Apple Music import", R.drawable.playlist_add, { systemRed }, SettingsAction.AppleMusicImport, "Import playlists from a link or export", "apple music playlist import export"),
                    SettingsLink("Discord", R.drawable.discord, { systemIndigo }, route("settings/integrations/discord"), "Listening status", "rich presence"),
                    SettingsLink("Last.fm", R.drawable.music_note, { systemRed }, route("settings/integrations/lastfm"), "Scrobbling", "scrobble"),
                ),
            ),
            SettingsLinkGroup(
                "Services",
                listOf(
                    SettingsLink("Network & proxy", R.drawable.wifi_proxy, { systemGray }, route("settings/network"), "IP version and proxy", "proxy ipv6 ipv4 network"),
                ),
            ),
        ),
    )

    val privacy = SettingsHub(
        "privacy", "Privacy & Storage", R.drawable.security, { systemBlue },
        listOf(
            SettingsLinkGroup(
                null,
                listOf(
                    SettingsLink("Privacy", R.drawable.security, { systemBlue }, route("settings/privacy"), "History and screenshots", "pause history clear history screenshot"),
                    SettingsLink("Storage", R.drawable.storage, { systemGray }, route("settings/storage"), "Downloads and caches", "cache downloads clear"),
                    SettingsLink("Backup & restore", R.drawable.restore, { systemGreen }, route("settings/backup_restore"), "Save or restore your library", "backup import export"),
                ),
            ),
        ),
    )

    val help = SettingsHub(
        "help", "Help & About", R.drawable.info, { systemGray },
        listOf(
            SettingsLinkGroup(
                null,
                listOf(
                    SettingsLink("Crash report", R.drawable.bug_report, { systemRed }, route("settings/help/crash_logs"), "View or share the latest crash"),
                    SettingsLink("Playback logs", R.drawable.bug_report, { systemOrange }, route("settings/content/advanced"), "Troubleshoot playback", "logs client probe"),
                    SettingsLink("Changelog", R.drawable.network_update, { systemBlue }, route("settings/changelog"), "What's new"),
                    SettingsLink("About", R.drawable.info, { systemGray }, route("settings/about"), "Version, licenses and project"),
                ),
            ),
        ),
    )

    val all = listOf(player, playback, appearance, library, accounts, privacy, help)

    fun byId(id: String?) = all.firstOrNull { it.id == id }
}

/** Which settings sheet is open; one app-wide slot so no two screens can each open their own. */
object SettingsSheets {
    var open by mutableStateOf<SettingsAction?>(null)
}

/** Runs a row's action: a page, or one of the sheets that open over the current screen. */
@Composable
fun rememberSettingsActionHandler(navController: NavController): (SettingsAction) -> Unit = { action ->
    when (action) {
        is SettingsAction.Route -> navController.navigate(action.route)
        SettingsAction.PlayerDesign, SettingsAction.LyricsStyle, SettingsAction.AppleMusicImport -> SettingsSheets.open = action
    }
}

/** Hosted once at the app root; shows whichever settings sheet is open. */
@Composable
fun SettingsSheetHost(navController: NavController) {
    val close = { SettingsSheets.open = null }
    when (SettingsSheets.open) {
        SettingsAction.PlayerDesign -> IosPageSheet("Player design", close) {
            PlayerDesignSheetContent(onMoreOptions = {
                close()
                navController.navigate("settings/appearance/playertheme")
            })
        }
        SettingsAction.LyricsStyle -> IosPageSheet("Lyrics style", close) { LyricsAnimationSheetContent() }
        SettingsAction.AppleMusicImport -> AppleMusicImportDialogHost(
            navController = navController,
            onCloseSheet = { SettingsSheets.open = null },
        )
        else -> Unit
    }
}

/** A category's page: its groups of rows, iOS inset-grouped. */
@Composable
fun SettingsHubScreen(navController: NavController, hub: SettingsHub) {
    val onAction = rememberSettingsActionHandler(navController)
    SettingsPage(navController = navController, title = hub.title, horizontalPadding = 16.dp) {
        SettingsHubBody(hub, onAction)
    }
}

@Composable
fun ColumnScope.SettingsHubBody(hub: SettingsHub, onAction: (SettingsAction) -> Unit) {
    hub.groups.forEachIndexed { index, group ->
        if (index > 0) Spacer(Modifier.height(28.dp))
        SettingsLinkCard(group.title, group.links, onAction)
    }
}

@Composable
fun SettingsLinkCard(
    title: String?,
    links: List<SettingsLink>,
    onAction: (SettingsAction) -> Unit,
    trailing: (SettingsLink) -> String? = { null },
    selected: SettingsLink? = null,
) {
    Column(Modifier.fillMaxWidth()) {
        if (title != null) Text(
            title,
            style = IosTheme.typography.footnote,
            fontWeight = FontWeight.SemiBold,
            color = IosTheme.colors.secondaryLabel,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
        )
        Column(
            Modifier
                .fillMaxWidth()
                .clip(ContinuousRoundedRectangle(IosMetrics.GroupedCornerRadius))
                .background(IosTheme.colors.secondarySystemGroupedBackground),
        ) {
            links.forEachIndexed { i, link ->
                if (i > 0) HorizontalDivider(
                    Modifier.padding(start = 58.dp),
                    color = IosTheme.colors.separator,
                    thickness = IosMetrics.SeparatorThickness,
                )
                SettingsLinkRow(link, trailing(link), selected == link) { onAction(link.action) }
            }
        }
    }
}

@Composable
fun SettingsLinkRow(link: SettingsLink, trailing: String?, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (selected) Modifier.background(IosTheme.colors.systemFill) else Modifier)
            .iosRowPress(onClick = onClick)
            .defaultMinSize(minHeight = 52.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(ContinuousRoundedRectangle(7.dp))
                .background(link.tint(IosTheme.colors)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(link.icon), null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(link.title, style = IosTheme.typography.body, color = IosTheme.colors.label)
            if (link.subtitle != null) Text(
                link.subtitle,
                style = IosTheme.typography.footnote,
                color = IosTheme.colors.secondaryLabel,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (trailing != null) {
            Text(trailing, style = IosTheme.typography.body, color = IosTheme.colors.secondaryLabel, maxLines = 1)
            Spacer(Modifier.width(8.dp))
        }
        SymbolIcon(symbol = sf("chevron.right"), contentDescription = null, size = 14.dp, tint = IosTheme.colors.tertiaryLabel)
    }
}
