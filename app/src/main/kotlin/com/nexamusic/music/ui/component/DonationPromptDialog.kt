/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexamusic.app.R
import com.nexamusic.app.constants.DonationKofiUrl
import com.nexamusic.app.constants.DonationPromptDismissedKey
import com.nexamusic.app.constants.DonationPromptLastShownKey
import com.nexamusic.app.constants.DonationUpiUri
import com.nexamusic.app.constants.FirstLaunchTimestampKey
import com.nexamusic.app.ui.utils.safeOpenUri
import com.nexamusic.app.utils.rememberPreference
import java.util.concurrent.TimeUnit

private val MIN_DAYS_BEFORE_FIRST_PROMPT = TimeUnit.DAYS.toMillis(3)
private val MIN_DAYS_BETWEEN_PROMPTS = TimeUnit.DAYS.toMillis(30)

/**
 * Gentle, occasional ask to support the project — never on a fresh install, never more
 * than once a month, and gone for good the moment the user says not to ask again.
 * Mount this once near the app root; it decides on its own whether to actually show.
 */
@Composable
fun DonationPromptHost() {
    var firstLaunch by rememberPreference(FirstLaunchTimestampKey, 0L)
    var lastShown by rememberPreference(DonationPromptLastShownKey, 0L)
    var dismissedForever by rememberPreference(DonationPromptDismissedKey, false)

    // Stamp the very first composition, once, so "used for a while" has a start line.
    LaunchedEffect(Unit) {
        if (firstLaunch == 0L) {
            firstLaunch = System.currentTimeMillis()
        }
    }

    val now = remember { System.currentTimeMillis() }
    val shouldShow = remember(firstLaunch, lastShown, dismissedForever, now) {
        !dismissedForever &&
            firstLaunch != 0L &&
            (now - firstLaunch) >= MIN_DAYS_BEFORE_FIRST_PROMPT &&
            (lastShown == 0L || (now - lastShown) >= MIN_DAYS_BETWEEN_PROMPTS)
    }

    var visible by remember(shouldShow) { mutableStateOf(shouldShow) }
    if (!visible) return

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    fun dismiss() {
        visible = false
        lastShown = System.currentTimeMillis()
    }

    AlertDialog(
        onDismissRequest = ::dismiss,
        icon = { Icon(painterResource(R.drawable.favorite), contentDescription = null) },
        title = { Text(stringResource(R.string.donation_prompt_title)) },
        text = { Text(stringResource(R.string.donation_prompt_message)) },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        uriHandler.safeOpenUri(context, DonationUpiUri)
                        dismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.donation_prompt_upi))
                }
                OutlinedButton(
                    onClick = {
                        uriHandler.safeOpenUri(context, DonationKofiUrl)
                        dismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.donation_prompt_kofi))
                }
            }
        },
        dismissButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(onClick = ::dismiss) {
                    Text(stringResource(R.string.donation_prompt_later))
                }
                TextButton(
                    onClick = {
                        dismissedForever = true
                        visible = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ) {
                    Text(stringResource(R.string.donation_prompt_dont_ask))
                }
            }
        },
    )
}
