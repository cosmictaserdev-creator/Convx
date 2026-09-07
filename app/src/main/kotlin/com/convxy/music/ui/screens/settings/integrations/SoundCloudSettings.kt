/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings.integrations

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.constants.SoundCloudAccessTokenKey
import com.convxy.music.constants.SoundCloudClientIdKey
import com.convxy.music.constants.SoundCloudClientSecretKey
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.Material3SettingsGroup
import com.convxy.music.ui.component.Material3SettingsItem
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.SoundCloudSettingsViewModel

/**
 * Where the timed-comments feature gets its SoundCloud application credentials.
 *
 * Credentials only. The feature's master switch and the choice of which source is asked first both
 * live one level up, on `CommentSourceSettings`, because neither is a SoundCloud property — Audius and
 * YouTube answer with nothing configured at all, so a switch that governs all three cannot live on the
 * screen for one of them.
 *
 * Follows `LastFMSettings` closely — same insets handling, same `Material3SettingsGroup` rows, same
 * dialog-per-value editing, same `TopAppBar` — because it is the same kind of screen: one third-party
 * API, one set of keys, nothing to sync.
 *
 * Why this screen exists at all rather than a key baked into the build: SoundCloud's API is free but
 * per-application and rate-limited per client_id, so a key shipped inside a public APK would be a key
 * that stops working for every user the moment it is throttled or revoked. Nothing is prefilled here,
 * and the feature reports itself unavailable rather than guessing until something real is entered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundCloudSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val viewModel: SoundCloudSettingsViewModel = hiltViewModel()

    var clientId by rememberPreference(SoundCloudClientIdKey, "")
    var clientSecret by rememberPreference(SoundCloudClientSecretKey, "")
    var accessToken by rememberPreference(SoundCloudAccessTokenKey, "")

    // A pasted token alone is enough; a client id/secret pair has to be complete to mint one.
    // Mirrors SoundCloudCredentials.Config.isUsable so the screen and the client never disagree about
    // whether the feature is live.
    val isConfigured = accessToken.isNotBlank() || (clientId.isNotBlank() && clientSecret.isNotBlank())

    // A String rather than an enum because rememberSaveable has no saver for one.
    var editing by rememberSaveable { mutableStateOf<String?>(null) }

    // No "off" branch any more: whether the feature as a whole is on is the master switch on the
    // Timed comments screen, not a property of these credentials.
    val statusText = if (isConfigured) {
        stringResource(R.string.soundcloud_status_ready)
    } else {
        stringResource(R.string.soundcloud_status_incomplete)
    }

    editing?.let { field ->
        CredentialDialog(
            title = when (field) {
                FIELD_CLIENT_ID -> stringResource(R.string.soundcloud_client_id)
                FIELD_CLIENT_SECRET -> stringResource(R.string.soundcloud_client_secret)
                else -> stringResource(R.string.soundcloud_access_token)
            },
            initial = when (field) {
                FIELD_CLIENT_ID -> clientId
                FIELD_CLIENT_SECRET -> clientSecret
                else -> accessToken
            },
            // The id identifies the application and is not secret; the other two are.
            masked = field != FIELD_CLIENT_ID,
            onDismiss = { editing = null },
            onSave = { value ->
                when (field) {
                    FIELD_CLIENT_ID -> clientId = value
                    FIELD_CLIENT_SECRET -> clientSecret = value
                    else -> accessToken = value
                }
                // The client is a singleton holding a token minted from the previous credentials;
                // without this the change would not take effect for up to an hour.
                viewModel.onCredentialsChanged()
                editing = null
            },
        )
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
            title = stringResource(R.string.soundcloud_integration),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(if (isConfigured) R.drawable.link else R.drawable.info),
                    title = { Text(statusText) },
                    description = { Text(stringResource(R.string.soundcloud_integration_summary)) },
                ),
            )
        )

        Material3SettingsGroup(
            title = stringResource(R.string.soundcloud_credentials_title),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.info),
                    title = { Text(stringResource(R.string.soundcloud_credentials_summary)) },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.key),
                    title = { Text(stringResource(R.string.soundcloud_client_id)) },
                    description = {
                        Text(
                            text = if (clientId.isBlank()) {
                                stringResource(R.string.not_set)
                            } else {
                                clientId
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = { editing = FIELD_CLIENT_ID },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.key),
                    title = { Text(stringResource(R.string.soundcloud_client_secret)) },
                    description = {
                        Text(
                            text = if (clientSecret.isBlank()) {
                                stringResource(R.string.not_set)
                            } else {
                                MASK
                            },
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    onClick = { editing = FIELD_CLIENT_SECRET },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.token),
                    title = { Text(stringResource(R.string.soundcloud_access_token)) },
                    description = {
                        Text(
                            text = if (accessToken.isBlank()) {
                                stringResource(R.string.soundcloud_access_token_summary)
                            } else {
                                MASK
                            },
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    onClick = { editing = FIELD_ACCESS_TOKEN },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.link),
                    title = { Text(stringResource(R.string.soundcloud_howto)) },
                    onClick = {
                        // Handled by whatever browser the user has; a failure to resolve one is simply
                        // a no-op rather than a crash in a settings screen.
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APPS_URL)))
                        }
                    },
                ),
            )
        )

        Spacer(modifier = Modifier.height(50.dp))
    }

    TopAppBar(
        windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.soundcloud_integration)) },
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

private const val FIELD_CLIENT_ID = "clientId"
private const val FIELD_CLIENT_SECRET = "clientSecret"
private const val FIELD_ACCESS_TOKEN = "accessToken"
private const val MASK = "••••••••"
private const val APPS_URL = "https://soundcloud.com/you/apps"

/**
 * Single-field editor shared by all three credentials.
 *
 * The stored value is trimmed on save rather than on every keystroke: trimming while typing would eat
 * the space a user is mid-way through pasting around, and a credential never contains one anyway.
 */
@Composable
private fun CredentialDialog(
    title: String,
    initial: String,
    masked: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var value by rememberSaveable(title) { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                singleLine = true,
                visualTransformation =
                    if (masked) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(value.trim()) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
