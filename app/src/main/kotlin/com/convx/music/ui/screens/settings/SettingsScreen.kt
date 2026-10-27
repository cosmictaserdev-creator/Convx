/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.core.updater.getAutoUpdateCheckSetting
import com.convx.music.core.updater.getUpdateAvailableState
import com.convx.music.ui.component.AccountAvatar
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.shapes.ContinuousCapsule
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.iosRowPress
import com.convx.music.ui.ios.foundation.IosMetrics
import com.convx.music.constants.LocalOnlyModeKey
import com.convx.music.utils.rememberPreference
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Switch
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import com.convx.music.viewmodels.HomeViewModel

/** Root rows that open a page directly rather than a category. */
private val historyLink = SettingsLink("History", R.drawable.music_history, { systemCyan }, SettingsAction.Route("history"))
private val statsLink = SettingsLink("Stats", R.drawable.stats, { systemYellow }, SettingsAction.Route("stats"))
private val listenTogetherLink = SettingsLink("Listen Together", R.drawable.group, { systemCyan }, SettingsAction.Route("listen_together_from_topbar"))
private val updateLink = SettingsLink("Software Update", R.drawable.network_update, { systemGray }, SettingsAction.Route("settings/update"))

/** The root, grouped the way the categories are read: what you hear, what you see, your stuff, help. */
private val rootGroups = listOf(
    listOf(listenTogetherLink),
    listOf(SettingsHubs.player.asLink(), SettingsHubs.playback.asLink(), SettingsHubs.appearance.asLink()),
    listOf(SettingsHubs.library.asLink(), SettingsHubs.accounts.asLink()),
    listOf(SettingsHubs.privacy.asLink(), historyLink, statsLink),
    listOf(updateLink, SettingsHubs.help.asLink()),
)

/** Wider than this, the root becomes a sidebar with the chosen category beside it. */
private val SplitWidth = 840.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val updateAvailable = getUpdateAvailableState(context) && getAutoUpdateCheckSetting(context)
    val homeViewModel: HomeViewModel = koinViewModel()
    val accountName by homeViewModel.accountName.collectAsStateWithLifecycle()
    val accountImageUrl by homeViewModel.accountImageUrl.collectAsStateWithLifecycle()
    val onAction = rememberSettingsActionHandler(navController)

    var query by rememberSaveable { mutableStateOf("") }
    var selectedHubId by rememberSaveable { mutableStateOf(SettingsHubs.player.id) }
    val searchEntries = rememberSearchEntries()

    BoxWithConstraints(Modifier.fillMaxSize().background(IosTheme.colors.systemGroupedBackground)) {
        val split = maxWidth >= SplitWidth
        val trailing: (SettingsLink) -> String? = { if (it == updateLink && updateAvailable) "1" else null }

        val rootList: @Composable (Boolean) -> Unit = { sidebar ->
            SettingsLazyPage(
                navController = navController,
                title = stringResource(R.string.settings),
                horizontalPadding = 16.dp,
                maxContentWidth = if (sidebar) 10_000.dp else 720.dp,
                listState = rememberLazyListState(),
            ) {
                item("profile") {
                    ProfileCard(accountName, accountImageUrl) { navController.navigate("settings/account") }
                }
                if (query.isBlank()) item("localOnly") {
                    Spacer(Modifier.height(20.dp))
                    LocalOnlyRow()
                }
                if (query.isBlank()) {
                    rootGroups.forEachIndexed { i, links ->
                        item("group$i") {
                            Spacer(Modifier.height(if (i == 0) 20.dp else 28.dp))
                            SettingsLinkCard(
                                title = null,
                                links = links,
                                trailing = trailing,
                                selected = if (sidebar) links.firstOrNull { it.action == SettingsAction.Route("settings/hub/$selectedHubId") } else null,
                                onAction = { action ->
                                    val hub = (action as? SettingsAction.Route)?.route?.removePrefix("settings/hub/")?.let(SettingsHubs::byId)
                                    if (sidebar && hub != null) selectedHubId = hub.id else onAction(action)
                                },
                            )
                        }
                    }
                } else {
                    item("results") {
                        Spacer(Modifier.height(20.dp))
                        SearchResults(searchEntries, query, onAction)
                    }
                }
                // Clears the docked search field.
                item("searchSpace") { Spacer(Modifier.height(96.dp)) }
            }
        }

        if (split) {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.width(380.dp).fillMaxHeight()) {
                    rootList(true)
                    SearchBar(query, { query = it }, Modifier.align(Alignment.BottomCenter))
                }
                Crossfade(selectedHubId, Modifier.weight(1f), tween(200), label = "settingsDetail") { id ->
                    val hub = SettingsHubs.byId(id) ?: SettingsHubs.player
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top + WindowInsetsSides.End))
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                    ) {
                        Text(
                            hub.title,
                            style = IosTheme.typography.largeTitle,
                            color = IosTheme.colors.label,
                            modifier = Modifier.padding(start = 4.dp, top = 40.dp, bottom = 20.dp),
                        )
                        Column(Modifier.widthIn(max = 720.dp)) { SettingsHubBody(hub, onAction) }
                    }
                }
            }
        } else {
            rootList(false)
            SearchBar(query, { query = it }, Modifier.align(Alignment.BottomCenter))
        }
    }
}

private data class SearchEntry(val link: SettingsLink, val category: String)

/** Every row of every category, plus the individual settings, searchable by name, description and synonyms. */
@Composable
private fun rememberSearchEntries(): List<SearchEntry> {
    val settings = getAllSearchableSettings()
    return remember(settings) {
        val fromTree = SettingsHubs.all.flatMap { hub ->
            listOf(SearchEntry(hub.asLink(), "Settings")) +
                hub.groups.flatMap { g -> g.links.map { SearchEntry(it, hub.title) } }
        }
        val treeTitles = fromTree.map { it.link.title.lowercase() }.toSet()
        fromTree + settings.filter { it.title.lowercase() !in treeTitles }.map { s ->
            SearchEntry(
                SettingsLink(
                    s.title,
                    R.drawable.search,
                    { systemGray },
                    if (s.opensLyricsSheet) SettingsAction.LyricsStyle else SettingsAction.Route(s.route),
                    s.description,
                ),
                s.category,
            )
        }
    }
}

@Composable
private fun SearchResults(entries: List<SearchEntry>, query: String, onAction: (SettingsAction) -> Unit) {
    val q = query.trim().lowercase()
    val hits = entries.filter { e ->
        e.link.title.lowercase().contains(q) ||
            e.link.subtitle?.lowercase()?.contains(q) == true ||
            e.link.keywords.contains(q) ||
            e.category.lowercase().contains(q)
    }
    if (hits.isEmpty()) {
        Text(
            "No settings found for “${query.trim()}”",
            style = IosTheme.typography.body,
            color = IosTheme.colors.secondaryLabel,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        )
        return
    }
    // Results keep their category so "Background" under Player reads differently from a home background.
    SettingsLinkCard(
        title = "${hits.size} result${if (hits.size == 1) "" else "s"}",
        links = hits.map { it.link.copy(subtitle = it.category + (it.link.subtitle?.let { s -> " · $s" } ?: "")) },
        onAction = onAction,
    )
}

/** Local only mode: a root-level switch, so it is one tap from the top of Settings. */
@Composable
private fun LocalOnlyRow() {
    val (localOnly, setLocalOnly) = rememberPreference(LocalOnlyModeKey, defaultValue = false)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(ContinuousRoundedRectangle(IosMetrics.GroupedCornerRadius))
            .background(IosTheme.colors.secondarySystemGroupedBackground)
            .clickable { setLocalOnly(!localOnly) }
            .defaultMinSize(minHeight = 56.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(30.dp).clip(ContinuousRoundedRectangle(7.dp)).background(IosTheme.colors.systemOrange),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(R.drawable.music_note), null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(stringResource(R.string.local_only_mode), style = IosTheme.typography.body, color = IosTheme.colors.label)
            Text(
                stringResource(R.string.local_only_mode_desc),
                style = IosTheme.typography.footnote,
                color = IosTheme.colors.secondaryLabel,
            )
        }
        Switch(checked = localOnly, onCheckedChange = setLocalOnly)
    }
}

@Composable
private fun ProfileCard(accountName: String, accountImageUrl: String?, onClick: () -> Unit) {
    val signedIn = accountName.isNotBlank() && !accountName.equals("Guest", ignoreCase = true)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(ContinuousRoundedRectangle(20.dp))
            .background(IosTheme.colors.secondarySystemGroupedBackground)
            .iosRowPress(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccountAvatar(
            imageUrl = accountImageUrl.takeIf { signedIn },
            name = accountName.takeIf { signedIn }.orEmpty(),
            size = 60.dp,
        )
        Column(Modifier.weight(1f).padding(start = 16.dp)) {
            Text(
                if (signedIn) accountName else "Sign in",
                style = IosTheme.typography.title3,
                fontWeight = FontWeight.SemiBold,
                color = IosTheme.colors.label,
                maxLines = 1,
            )
            Text(
                if (signedIn) "YouTube Music, sync and more" else "YouTube Music, Spotify and more",
                style = IosTheme.typography.footnote,
                color = IosTheme.colors.secondaryLabel,
                maxLines = 1,
            )
        }
        SymbolIcon(symbol = sf("chevron.right"), contentDescription = null, size = 14.dp, tint = IosTheme.colors.tertiaryLabel)
    }
}

/**
 * The docked search field: an opaque raised pill over a fade of the page colour, so rows
 * scrolling beneath it never show through. Rides above the keyboard when focused.
 */
@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = IosTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    Box(
        modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(colors.systemGroupedBackground.copy(alpha = 0f), colors.systemGroupedBackground)))
            .imePadding()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(Modifier.widthIn(max = 680.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier
                    .weight(1f)
                    .height(52.dp)
                    .shadow(10.dp, ContinuousCapsule(), clip = false)
                    .clip(ContinuousCapsule())
                    .background(colors.secondarySystemGroupedBackground)
                    // The whole pill focuses the field, not just the line of text inside it.
                    .clickable(interactionSource = null, indication = null) {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SymbolIcon(symbol = sf("magnifyingglass"), contentDescription = null, size = 18.dp, tint = colors.secondaryLabel)
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(stringResource(R.string.search), style = IosTheme.typography.body, color = colors.secondaryLabel)
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        interactionSource = interaction,
                        textStyle = IosTheme.typography.body.copy(color = colors.label),
                        cursorBrush = SolidColor(IosTheme.tint),
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    )
                }
            }
            AnimatedVisibility(
                visible = focused || value.isNotEmpty(),
                enter = expandHorizontally(expandFrom = Alignment.Start) + fadeIn(),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut(),
            ) {
                Text(
                    stringResource(android.R.string.cancel),
                    style = IosTheme.typography.body,
                    color = IosTheme.tint,
                    modifier = Modifier
                        .padding(start = 14.dp)
                        .clickable {
                            onValueChange("")
                            focusManager.clearFocus()
                        },
                )
            }
        }
    }
}
