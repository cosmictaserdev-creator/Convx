/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings.integrations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
// Routed to the glass switch, as every other settings screen does: the liquid
// toggle when it can honour the call, the house glass switch otherwise.
import com.convxy.music.ui.component.GlassSwitchCompat as Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.comments.CommentSource
import com.convxy.music.constants.CommentSourceOrderKey
import com.convxy.music.constants.TimestampCommentsEnabledKey
import com.convxy.music.ui.component.IconButton as AppIconButton
import com.convxy.music.ui.component.Material3SettingsGroup
import com.convxy.music.ui.component.Material3SettingsItem
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.CommentSourceSettingsViewModel

/**
 * Which sources timed comments may come from, and which is asked first.
 *
 * This screen exists because the three sources are not interchangeable, and the right order is
 * different for different people:
 *
 *  - **Audius** and **YouTube** need nothing — no account, no key, no registration — so they are on by
 *    default and answer on a fresh install.
 *  - **SoundCloud** has the cleanest data (a real millisecond timestamp on every comment) but needs a
 *    registered API application, so it is last by default and only answers once someone has entered
 *    credentials. A user who went to that trouble may well want it first, which is the whole reason
 *    the order is a setting rather than a constant.
 *
 * The list is both the ranking and the on/off state: a source that is switched off is simply not in it.
 * One string in DataStore holds both, so rank and membership cannot drift apart — see
 * [CommentSource.serializeEnabled] for why "everything off" is stored as a sentinel and not as "".
 *
 * Any change here clears the comment cache through [CommentSourceSettingsViewModel]. Without that a
 * freshly promoted source would be asked about new tracks while every recently played one kept being
 * served from the cache the previous order filled, which reads as the setting not working.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSourceSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val viewModel: CommentSourceSettingsViewModel = hiltViewModel()

    val (enabled, onEnabledChange) = rememberPreference(TimestampCommentsEnabledKey, true)
    var orderRaw by rememberPreference(CommentSourceOrderKey, "")

    // Enabled sources in priority order, then the switched-off ones below them in default order so
    // every source always has a row to turn back on.
    val enabledSources = CommentSource.parseEnabled(orderRaw)
    val rows = enabledSources + CommentSource.DEFAULT_ORDER.filterNot { it in enabledSources }

    fun persist(next: List<CommentSource>) {
        orderRaw = CommentSource.serializeEnabled(next)
        viewModel.onPriorityChanged()
    }

    Column(
        Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
            )
        )

        Material3SettingsGroup(
            title = stringResource(R.string.comment_sources),
            items = buildList {
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.chat_timestamp),
                        title = { Text(stringResource(R.string.comment_sources_enabled)) },
                        description = { Text(stringResource(R.string.comment_sources_enabled_summary)) },
                        trailingContent = {
                            Switch(checked = enabled, onCheckedChange = onEnabledChange)
                        },
                    )
                )
                if (enabledSources.isEmpty()) {
                    // Reachable: switching off all three sources does not switch off the feature, so
                    // the player's button would still be there with nothing behind it.
                    add(
                        Material3SettingsItem(
                            icon = painterResource(R.drawable.info),
                            title = {
                                Text(
                                    text = stringResource(R.string.comment_sources_all_off),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                        )
                    )
                }
            }
        )

        Material3SettingsGroup(
            title = stringResource(R.string.comment_sources_priority_title),
            items = buildList {
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.info),
                        title = {
                            Text(
                                text = stringResource(R.string.comment_sources_priority_summary),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        },
                    )
                )
                rows.forEach { source ->
                    val rank = enabledSources.indexOf(source)
                    val isOn = rank >= 0
                    add(
                        Material3SettingsItem(
                            icon = painterResource(iconFor(source)),
                            title = { Text(source.displayName) },
                            description = {
                                val summary = when (source) {
                                    CommentSource.AUDIUS ->
                                        stringResource(R.string.comment_sources_audius_summary)
                                    CommentSource.YOUTUBE ->
                                        stringResource(R.string.comment_sources_youtube_summary)
                                    CommentSource.SOUNDCLOUD ->
                                        stringResource(R.string.comment_sources_soundcloud_summary)
                                }
                                Text(
                                    text = if (rank == 0) {
                                        "${stringResource(R.string.comment_sources_first_choice)} · $summary"
                                    } else {
                                        summary
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Reordering only means something for a source that is on, and only
                                    // within the enabled range — hiding the arrows otherwise is clearer
                                    // than showing two that do nothing.
                                    if (isOn) {
                                        IconButton(
                                            onClick = { persist(move(enabledSources, rank, rank - 1)) },
                                            enabled = rank > 0,
                                        ) {
                                            Icon(
                                                painterResource(R.drawable.arrow_upward),
                                                contentDescription = stringResource(R.string.comment_sources_move_up),
                                            )
                                        }
                                        IconButton(
                                            onClick = { persist(move(enabledSources, rank, rank + 1)) },
                                            enabled = rank < enabledSources.lastIndex,
                                        ) {
                                            Icon(
                                                painterResource(R.drawable.arrow_downward),
                                                contentDescription = stringResource(R.string.comment_sources_move_down),
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = isOn,
                                        onCheckedChange = { on ->
                                            persist(CommentSource.toggled(enabledSources, source, on))
                                        },
                                    )
                                }
                            },
                        )
                    )
                }
            }
        )

        // SoundCloud is the only source that needs anything from the user, so its credentials live one
        // level down rather than getting their own card on the integrations list.
        Material3SettingsGroup(
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.key),
                    title = { Text(stringResource(R.string.comment_sources_soundcloud_credentials)) },
                    description = {
                        Text(
                            text = stringResource(R.string.soundcloud_credentials_summary),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    onClick = { navController.navigate(SOUNDCLOUD_ROUTE) },
                ),
            )
        )

        Spacer(modifier = Modifier.height(50.dp))
    }

    TopAppBar(
        windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.comment_sources)) },
        navigationIcon = {
            AppIconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

private const val SOUNDCLOUD_ROUTE = "settings/integrations/soundcloud"

private fun iconFor(source: CommentSource): Int = when (source) {
    CommentSource.AUDIUS -> R.drawable.music_note
    CommentSource.YOUTUBE -> R.drawable.chat_msg
    CommentSource.SOUNDCLOUD -> R.drawable.chat_timestamp
}

/**
 * Moves [from] to [to] within a priority list, or returns it unchanged when either index is outside
 * the list. The bounds check is what lets the arrow handlers call this unconditionally — a disabled
 * arrow cannot fire, but a stale recomposition handing over an old rank should not corrupt the order.
 */
private fun move(list: List<CommentSource>, from: Int, to: Int): List<CommentSource> {
    if (from == to || from !in list.indices || to !in list.indices) return list
    val copy = list.toMutableList()
    copy.add(to, copy.removeAt(from))
    return copy
}
