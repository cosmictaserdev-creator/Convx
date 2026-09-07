/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player.comments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.convxy.music.comments.CommentTimeline
import com.convxy.music.comments.TimestampedComment

/**
 * Folds the player's existing playback position into "which comment is live right now".
 *
 * The position is read through a lambda rather than passed as a value so this can be a
 * `derivedStateOf`: the calculation re-runs as often as the player reports a new position (100ms in
 * `Player`), but it only *invalidates* its readers when the answer changes. During a twenty-second
 * stretch between two comments that is one recomposition, not two hundred — which is the whole reason
 * the highlight is derived rather than observed with its own polling loop or a coroutine per tick.
 *
 * No second player and no second clock are involved: [positionProvider] is the same
 * `{ effectivePosition }` the player already hands to `InlineLyricsView`.
 *
 * @return a [State] so callers can delegate with `by` and stay inside the snapshot system.
 */
@Composable
fun rememberActiveCommentGroup(
    comments: List<TimestampedComment>,
    positionProvider: () -> Long,
): State<CommentTimeline.CommentGroup?> {
    // rememberUpdatedState keeps the lambda fresh without making it a key of the derivedStateOf —
    // a caller that passes a new lambda instance every recomposition (the normal thing to do) would
    // otherwise throw the derived state away on every frame and defeat the point of it.
    val currentProvider by rememberUpdatedState(positionProvider)
    return remember(comments) {
        derivedStateOf {
            if (comments.isEmpty()) null else CommentTimeline.activeGroup(comments, currentProvider())
        }
    }
}

/**
 * The seek-bar marker the live comment belongs to, or null when playback is in a gap between
 * comments. Recomputed only when the group or the marker list actually changes.
 */
@Composable
fun rememberActiveCommentMarker(
    markers: List<CommentTimeline.CommentMarker>,
    group: CommentTimeline.CommentGroup?,
): CommentTimeline.CommentMarker? =
    remember(markers, group) {
        if (group == null) null else CommentTimeline.markerForGroup(markers, group)
    }
