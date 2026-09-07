/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings

import com.convxy.music.R
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convxy.music.BuildConfig
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.Material3SettingsGroup
import com.convxy.music.ui.component.Material3SettingsItem
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.vivimusic.updater.getUpdateAvailableState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isUpdateAvailable = getUpdateAvailableState(context) &&
        com.convxy.music.vivimusic.updater.getAutoUpdateCheckSetting(context)

    var searchQuery by rememberSaveable { mutableStateOf("") }
    val searchLower = searchQuery.trim().lowercase()

    androidx.compose.foundation.lazy.LazyColumn(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal))
    ) {
        item(key = "top_spacer") {
            Spacer(
                Modifier.windowInsetsPadding(
                    LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
                )
            )
        }

        // Large title (iOS style)
        item(key = "title") {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)
            )
        }

        // Search field — filters the settings catalog and jumps to the matching screen.
        item(key = "search_field") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.search)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = stringResource(R.string.search)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = null
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
            )
        }

        if (searchLower.isEmpty()) {
        // Section: General
        item(key = "general_header") { SettingsSectionHeader("GENERAL") }
        item(key = "general_section") {
            SettingsSection {
                SettingsNavItem(
                    icon = painterResource(if (isUpdateAvailable) R.drawable.convxy_logo else R.drawable.network_update),
                    iconTint = if (isUpdateAvailable) MaterialTheme.colorScheme.error else Color(0xFF007AFF),
                    title = stringResource(R.string.system_update),
                    badge = if (isUpdateAvailable) stringResource(R.string.update_available) else null,
                    onClick = { navController.navigate("settings/update") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.palette),
                    iconTint = Color(0xFFAF52DE),
                    title = stringResource(R.string.appearance),
                    onClick = { navController.navigate("settings/appearance") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.play),
                    iconTint = Color(0xFFFF375F),
                    title = stringResource(R.string.player_and_audio),
                    onClick = { navController.navigate("settings/player") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.canvas_art),
                    iconTint = Color(0xFF5E5CE6),
                    title = stringResource(R.string.ambient_mode),
                    onClick = { navController.navigate("settings/ambient") },
                )
            }
        }

        item(key = "spacer_1") { Spacer(Modifier.height(24.dp)) }

        // Section: Account
        item(key = "account_header") { SettingsSectionHeader("ACCOUNT") }
        item(key = "account_section") {
            SettingsSection {
                SettingsNavItem(
                    icon = painterResource(R.drawable.account),
                    iconTint = Color(0xFF34C759),
                    title = stringResource(R.string.account),
                    onClick = { navController.navigate("settings/account") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.group),
                    iconTint = Color(0xFF5856D6),
                    title = stringResource(R.string.listen_together),
                    // Screens.ListenTogether.route ("listen_together") is a
                    // MainTabsPager page now, not its own NavHost destination, and
                    // this screen has no access to the pager state to scroll it.
                    // "listen_together_from_topbar" is the same screen (showTopBar =
                    // true) as a real, always-registered destination -- exactly what
                    // the top bar's own Listen Together entry point already uses.
                    onClick = { navController.navigate("listen_together_from_topbar") },
                )
            }
        }

        item(key = "spacer_activity") { Spacer(Modifier.height(24.dp)) }

        // Section: Activity — history/stats moved here from the top bar, which now
        // only shows the logo and this settings/profile pill.
        item(key = "activity_header") { SettingsSectionHeader("ACTIVITY") }
        item(key = "activity_section") {
            SettingsSection {
                SettingsNavItem(
                    icon = painterResource(R.drawable.music_history),
                    iconTint = Color(0xFF32ADE6),
                    title = stringResource(R.string.history),
                    onClick = { navController.navigate("history") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.stats),
                    iconTint = Color(0xFFFFCC00),
                    title = stringResource(R.string.stats),
                    onClick = { navController.navigate("stats") },
                )
            }
        }

        item(key = "spacer_2") { Spacer(Modifier.height(24.dp)) }

        // Section: Content
        item(key = "content_header") { SettingsSectionHeader("CONTENT") }
        item(key = "content_section") {
            SettingsSection {
                SettingsNavItem(
                    icon = painterResource(R.drawable.language),
                    iconTint = Color(0xFF007AFF),
                    title = stringResource(R.string.content),
                    onClick = { navController.navigate("settings/content") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.link),
                    iconTint = Color(0xFF5856D6),
                    title = stringResource(R.string.modules),
                    onClick = { navController.navigate("settings/modules") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.translate),
                    iconTint = Color(0xFFFF9500),
                    title = stringResource(R.string.ai_lyrics_translation),
                    onClick = { navController.navigate("settings/ai") },
                )
            }
        }

        item(key = "spacer_3") { Spacer(Modifier.height(24.dp)) }

        // Section: Data & Privacy
        item(key = "privacy_header") { SettingsSectionHeader("DATA & PRIVACY") }
        item(key = "privacy_section") {
            SettingsSection {
                SettingsNavItem(
                    icon = painterResource(R.drawable.security),
                    iconTint = Color(0xFF007AFF),
                    title = stringResource(R.string.privacy),
                    onClick = { navController.navigate("settings/privacy") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.storage),
                    iconTint = Color(0xFF8E8E93),
                    title = stringResource(R.string.storage),
                    onClick = { navController.navigate("settings/storage") },
                )
                SettingsDivider()
                SettingsNavItem(
                    icon = painterResource(R.drawable.restore),
                    iconTint = Color(0xFF34C759),
                    title = stringResource(R.string.backup_restore),
                    onClick = { navController.navigate("settings/backup_restore") },
                )
            }
        }

        item(key = "spacer_4") { Spacer(Modifier.height(24.dp)) }

        // Section: About
        item(key = "about_section") {
            SettingsSection {
                SettingsNavItem(
                    icon = painterResource(R.drawable.info),
                    iconTint = Color(0xFF007AFF),
                    title = stringResource(R.string.about),
                    onClick = { navController.navigate("settings/about") },
                )
            }
        }

        item(key = "bottom_spacer") {
            Spacer(
                Modifier
                    .height(50.dp)
                    .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom))
            )
        }
        } else {
            item(key = "search_results") {
                val results = getAllSearchableSettings()
                    .filter {
                        it.title.lowercase().contains(searchLower) ||
                            it.description?.lowercase()?.contains(searchLower) == true
                    }
                if (results.isEmpty()) {
                    Text(
                        text = "No settings found for \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 32.dp)
                    )
                } else {
                    Material3SettingsGroup(
                        items = results.map { setting ->
                            Material3SettingsItem(
                                icon = painterResource(R.drawable.search),
                                title = { Text(setting.title) },
                                description = { Text(setting.category) },
                                onClick = { navController.navigate(setting.route) }
                            )
                        }
                    )
                }
            }
        }
    }

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null
                )
            }
        },
        windowInsets = appTopBarWindowInsets(),
    )
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(
            start = 20.dp,
            top = 8.dp,
            bottom = 8.dp,
        ),
    )
}

@Composable
private fun SettingsSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(AppleTokens.CardCorner))
            // Row content (title/icon) already reads MaterialTheme.colorScheme —
            // a fixed-dark AppleTokens.Card background went black-on-black in
            // light theme (same root cause as Material3SettingsGroup).
            .background(MaterialTheme.colorScheme.surfaceContainer),
        content = content,
    )
}

@Composable
private fun SettingsNavItem(
    icon: Painter,
    iconTint: Color,
    title: String,
    badge: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon in rounded square
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(Modifier.width(16.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )

        // Badge or chevron
        if (badge != null) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.width(8.dp))
        }

        Icon(
            painter = painterResource(R.drawable.chevron_right_px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 64.dp),
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 0.5.dp,
    )
}
