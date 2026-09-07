/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.ui.component.Material3SettingsGroup
import com.convxy.music.ui.component.Material3SettingsItem
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.viewmodels.AccountSettingsViewModel

/**
 * Shown right after a WebView login when the signed-in Google Account has more
 * than one YouTube channel — [PendingChannelLogin] carries the cookie and the
 * channel list over from [LoginScreen]. Picking one finishes the login the same
 * way a single-channel account always has (name/email lookup, then restart).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelPickerScreen(navController: NavController) {
    val context = LocalContext.current
    val accountSettingsViewModel: AccountSettingsViewModel = hiltViewModel()
    val channels = PendingChannelLogin.channels

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = appTopBarWindowInsets(),
                title = { Text(stringResource(R.string.choose_channel_title)) },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .windowInsetsPadding(
                    LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.choose_channel_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Material3SettingsGroup(
                items = channels.map { channel ->
                    val handle = channel.channelHandle
                    val descriptionContent: (@Composable () -> Unit)? =
                        if (handle != null) {
                            { Text(handle) }
                        } else {
                            null
                        }
                    Material3SettingsItem(
                        leadingContent = {
                            AsyncImage(
                                model = channel.thumbnailUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.person),
                                error = painterResource(R.drawable.person),
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                            )
                        },
                        title = { Text(channel.name) },
                        description = descriptionContent,
                        onClick = {
                            accountSettingsViewModel.applyChannelAndRestart(
                                context = context,
                                cookie = PendingChannelLogin.cookie,
                                visitorData = PendingChannelLogin.visitorData,
                                chosenDataSyncId = channel.dataSyncId,
                                allChannels = channels,
                            )
                        },
                    )
                },
            )
        }
    }
}
