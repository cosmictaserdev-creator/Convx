/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

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
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Scaffold
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.viewmodels.AccountSettingsViewModel
import com.convx.music.ui.component.ScreenPage

/**
 * Shown after a WebView login when the signed-in Google Account has more than one
 * YouTube channel; picking one finishes the login that [LoginScreen] otherwise completes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelPickerScreen(navController: NavController) {
    val context = LocalContext.current
    val accountSettingsViewModel: AccountSettingsViewModel = koinViewModel()
    val channels = PendingChannelLogin.channels

    ScreenPage(
        title = stringResource(R.string.choose_channel_title),
        // No back button: a channel must be picked before anything else can continue.
        horizontalPadding = 16.dp,
    ) {
            Text(
                text = stringResource(R.string.choose_channel_subtitle),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            IosSettingsGroup(
                items = channels.map { channel ->
                    val handle = channel.channelHandle
                    val descriptionContent: (@Composable () -> Unit)? =
                        if (handle != null) {
                            { Text(handle) }
                        } else {
                            null
                        }
                    IosSettingsItem(
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
