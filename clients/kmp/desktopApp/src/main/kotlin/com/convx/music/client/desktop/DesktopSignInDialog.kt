package com.convx.music.client.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.awt.EventQueue

@Composable
internal fun DesktopSignInDialog(
    accountIsWorking: Boolean,
    accountMessage: String?,
    onComplete: (cookie: String, visitorData: String, dataSyncId: String?) -> Unit,
    onRetryRequested: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = desktopPalette()
    val status = remember { mutableStateOf("Opening the sign-in window…") }
    val currentOnComplete = rememberUpdatedState(onComplete)
    val currentOnDismiss = rememberUpdatedState(onDismiss)
    val controller = remember {
        DesktopSignInController(
            onStatusChanged = { message -> EventQueue.invokeLater { status.value = message } },
            onComplete = { cookie, visitorData, dataSyncId ->
                EventQueue.invokeLater { currentOnComplete.value(cookie, visitorData, dataSyncId) }
            },
            onDismiss = { EventQueue.invokeLater { currentOnDismiss.value() } },
        )
    }

    DisposableEffect(controller) {
        controller.start()
        onDispose(controller::close)
    }

    val canRetry = accountMessage != null && !accountIsWorking
    LaunchedEffect(controller, canRetry) {
        if (canRetry) controller.allowRetryAfterFailure()
    }

    dev.nucleusframework.macoscompose.components.SmallDialog(
        visible = true, onDismissRequest = { controller.close(); currentOnDismiss.value() },
        title = "Sign in to YouTube Music",
        confirmText = if (accountIsWorking) "Checking account?" else if (canRetry) "Retry sign-in" else "Open sign-in window",
        onConfirm = { if (!accountIsWorking) { if (canRetry) onRetryRequested(); controller.openWindow() } },
    ) {
        Text("Complete sign-in in the browser window to connect your music library.", fontSize = 14.sp)
        Text(when { accountIsWorking -> "Checking your account?"; accountMessage != null -> accountMessage; else -> status.value },
            color = palette.muted, fontSize = 13.sp)
    }
}
