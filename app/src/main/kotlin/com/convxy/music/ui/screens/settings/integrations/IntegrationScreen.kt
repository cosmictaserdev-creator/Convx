/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings.integrations

import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.rememberScrollState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.verticalScroll
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBarScrollBehavior
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.stringResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.R
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.IntegrationCard
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.IntegrationCardItem
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegrationScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        IntegrationCard(
            title = stringResource(R.string.general),
            items = listOf(
                IntegrationCardItem(
                    icon = painterResource(R.drawable.discord),
                    title = { Text(stringResource(R.string.discord_integration)) },
                    onClick = {
                        navController.navigate("settings/integrations/discord")
                    }
                ),
                IntegrationCardItem(
                    icon = painterResource(R.drawable.music_note),
                    title = { Text(stringResource(R.string.lastfm_integration)) },
                    onClick = {
                        navController.navigate("settings/integrations/lastfm")
                    }
                ),
                // Timed comments. Listed as an integration rather than an appearance toggle because
                // that is still what it is: the comments come from other services' catalogues. It is no
                // longer synonymous with SoundCloud though — Audius and YouTube need no registration at
                // all — so this opens the source list, and SoundCloud's credentials are one row down
                // from there for the users who have them.
                IntegrationCardItem(
                    icon = painterResource(R.drawable.chat_timestamp),
                    title = { Text(stringResource(R.string.comment_sources)) },
                    onClick = {
                        navController.navigate("settings/integrations/comments")
                    }
                )
            )
        )
    }

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.integrations)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        }
    )
}
