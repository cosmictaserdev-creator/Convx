/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.convxy.music.R
import com.convxy.music.listentogether.ListenTogetherEvent
import com.convxy.music.listentogether.ListenTogetherManager
import com.convxy.music.playback.PlayerConnection
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.utils.bounceClick

/** How far the music is pulled down while a request is waiting. Not silence —
 *  the point is that someone with the phone in their pocket notices the dip
 *  and looks, not that playback stops. */
private const val DUCK_VOLUME = 0.35f
private const val DUCK_RAMP_MS = 220

/** A queue notice is informational, so it clears itself. */
private const val NOTICE_DURATION_MS = 4200L

private val SheetShape = ContinuousRoundedRectangle(28.dp)

/**
 * Room events that must reach the user wherever they are in the app.
 *
 * Hosted once at the activity root rather than on the Listen Together screen: a
 * join request is worthless if it only appears on the one screen the host is
 * probably not looking at. Slides up from the bottom edge and back down.
 */
@Composable
fun BoxScope.ListenTogetherOverlay(
    manager: ListenTogetherManager?,
    playerConnection: PlayerConnection?,
) {
    if (manager == null) return

    val pendingRequests by manager.pendingJoinRequests.collectAsState()
    val request = pendingRequests.firstOrNull()

    // Latest queue change, cleared on a timer.
    var notice by remember { mutableStateOf<Pair<String, String?>?>(null) }
    LaunchedEffect(manager) {
        manager.events.collect { event ->
            if (event is ListenTogetherEvent.QueueTrackAdded) {
                notice = event.title to event.addedBy
            }
        }
    }
    LaunchedEffect(notice) {
        if (notice != null) {
            kotlinx.coroutines.delay(NOTICE_DURATION_MS)
            notice = null
        }
    }

    // Duck while a request is waiting, restore when it clears. Driven off the
    // same state the card is, so the volume can never be left down by a card
    // that got dismissed some other way.
    val ducked = request != null
    val volume by animateFloatAsState(
        targetValue = if (ducked) DUCK_VOLUME else 1f,
        animationSpec = tween(DUCK_RAMP_MS),
        label = "ltDuck",
    )
    LaunchedEffect(volume, playerConnection) {
        runCatching { playerConnection?.player?.volume = volume }
    }

    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnimatedVisibility(
            visible = notice != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(340, easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)),
            ) + fadeIn(tween(200)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(260),
            ) + fadeOut(tween(160)),
        ) {
            QueueNotice(title = notice?.first.orEmpty(), addedBy = notice?.second)
        }

        AnimatedVisibility(
            visible = request != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                // Long ease-out so it settles rather than snaps — the same curve
                // the home background blur uses.
                animationSpec = tween(420, easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)),
            ) + fadeIn(tween(220)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300),
            ) + fadeOut(tween(180)),
        ) {
            // Held so the card keeps its text through the exit animation instead
            // of blanking the instant the request is answered.
            val shown = remember(request?.userId) { request }
            JoinRequestCard(
                username = shown?.username.orEmpty(),
                onApprove = { shown?.let { manager.approveJoin(it.userId) } },
                onReject = { shown?.let { manager.rejectJoin(it.userId, "Rejected by host") } },
            )
        }
    }
}

@Composable
private fun JoinRequestCard(
    username: String,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SheetShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = username.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.listen_together_wants_to_join),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OverlayButton(
                label = stringResource(R.string.reject),
                container = MaterialTheme.colorScheme.surfaceContainerHighest,
                content = MaterialTheme.colorScheme.onSurface,
                onClick = onReject,
                modifier = Modifier.weight(1f),
            )
            OverlayButton(
                label = stringResource(R.string.approve),
                container = MaterialTheme.colorScheme.primary,
                content = MaterialTheme.colorScheme.onPrimary,
                onClick = onApprove,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun QueueNotice(title: String, addedBy: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SheetShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.playlist_add),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (addedBy.isNullOrBlank()) {
                stringResource(R.string.listen_together_added_to_queue, title)
            } else {
                stringResource(R.string.listen_together_added_to_queue_by, addedBy, title)
            },
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun OverlayButton(
    label: String,
    container: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(ContinuousRoundedRectangle(percent = 50))
            .background(container)
            .bounceClick(onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = content,
        )
    }
}
