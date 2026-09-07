/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.playback.audio.DjMixTier
import com.convxy.music.playback.dj.DjState
import com.convxy.music.playback.dj.TransitionStyle
import kotlin.math.roundToInt

/**
 * One line of DJ telemetry, shown only while Auto-DJ is on and something is
 * actually known.
 *
 * Reports what happened, not what was intended: if a pair fell back to a plain
 * crossfade the row says so, rather than leaving the listener to wonder why a
 * transition sounded ordinary.
 */
@Composable
fun DjReadout(modifier: Modifier = Modifier) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val state by playerConnection.djState.collectAsStateWithLifecycle()

    if (!state.enabled) return
    // Analysis needs the track to play a while before it reports anything, so
    // say so rather than showing an empty row — otherwise "no BPM" is
    // indistinguishable from "broken".
    val text = state.describe() ?: stringResource(R.string.dj_readout_analyzing)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** `128 BPM · 8A · beatmatched → 124 BPM · 9A`, skipping anything unknown. */
private fun DjState.describe(): String? {
    val current = buildList {
        if (currentBpm > 0f) add("${currentBpm.roundToInt()} BPM")
        currentKey?.let(::add)
        tierLabel()?.let(::add)
    }
    if (current.isEmpty()) return null

    val next = buildList {
        if (nextBpm > 0f) add("${nextBpm.roundToInt()} BPM")
        nextKey?.let(::add)
    }

    val head = current.joinToString(" · ")
    return if (next.isEmpty()) head else "$head → ${next.joinToString(" · ")}"
}

private fun DjState.tierLabel(): String? = when (tier) {
    DjMixTier.FULL_DJ -> when (style) {
        TransitionStyle.ECHO_FREEZE -> "echo"
        TransitionStyle.TAPE_STOP -> "brake"
        else -> "beatmatched"
    }
    DjMixTier.SMART_CROSSFADE -> when (style) {
        TransitionStyle.ECHO_FREEZE -> "echo"
        TransitionStyle.TAPE_STOP -> "brake"
        else -> "tempo matched"
    }
    DjMixTier.PLAIN_CROSSFADE -> when (style) {
        TransitionStyle.TAPE_STOP -> "brake"
        TransitionStyle.ECHO_FREEZE -> "echo"
        else -> "crossfade"
    }
    null -> null
}
