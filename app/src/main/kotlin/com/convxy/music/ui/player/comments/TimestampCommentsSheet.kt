/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.convxy.music.R
import com.convxy.music.comments.youtube.YouTubeCommentParser
import com.convxy.music.ui.screens.YouTubeCommentList
import com.convxy.music.comments.CommentTimeline
import com.convxy.music.comments.CommentsStatus
import com.convxy.music.comments.TimestampedComment
import com.convxy.music.viewmodels.TrackCommentsUiState
import kotlinx.coroutines.launch

/**
 * The timed-comments sheet: every comment on the now-playing track, in playback order, with the one
 * the playhead is currently inside picked out and tap-to-seek on every timestamp.
 *
 * Deliberately *not* a second comments sheet bolted next to [com.convxy.music.ui.screens.CommentSheet].
 * That one renders YouTube discussion threads for a video; this one renders timed reactions to a
 * recording. They share a widget (`ModalBottomSheet`) and nothing else — different model, different
 * provider, different failure modes — so keeping them separate is what stops a change to one from
 * breaking the other.
 *
 * The sheet never touches the player. It reports intent through [onSeekTo] and reads the clock
 * through [positionProvider], both supplied by `Player`, which owns the one `PlayerConnection`. There
 * is no player, no Media3 type and no coroutine touching playback anywhere below this signature.
 *
 * @param uiState the whole answer for one track, from [com.convxy.music.viewmodels.TrackCommentsViewModel].
 * @param durationMs track length in milliseconds; the timeline bar is hidden when this is unknown.
 * @param positionProvider the player's existing position state, read lazily — see [rememberActiveCommentGroup].
 * @param onSeekTo seeks the *existing* player to an absolute millisecond offset.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimestampCommentsSheet(
    uiState: TrackCommentsUiState,
    trackTitle: String?,
    durationMs: Long,
    positionProvider: () -> Long,
    onSeekTo: (Long) -> Unit,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
    onOpenProviderSettings: (() -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val comments = uiState.comments
    val groups = remember(comments) { CommentTimeline.displayGroups(comments) }
    val markers = remember(comments, durationMs) { CommentTimeline.markers(comments, durationMs) }
    val activeGroup by rememberActiveCommentGroup(comments, positionProvider)
    val activeGroupIndex = remember(groups, activeGroup) {
        val group = activeGroup ?: return@remember -1
        groups.indexOfFirst { it.startIndex == group.startIndex }
    }

    val accent = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    // Two kinds of comment behind one door: the timed ones this sheet was built for, and the plain
    // YouTube thread the app already had. That thread had no reachable entry point of its own — its
    // button sat in the same dead player-design branch this sheet's button was just rescued from — so
    // rather than add a fourth control to a row already at its width budget, both live here.
    var timedSelected by rememberSaveable { mutableStateOf(true) }

    // "All" is YouTube-only, because that thread is fetched by video id. `trackId` is the same
    // MediaMetadata.id the standalone sheet was handed, and the shape test is the one the YouTube
    // comment source already uses to decide whether a request is worth spending at all.
    val youTubeVideoId = uiState.trackId?.takeIf { YouTubeCommentParser.looksLikeVideoId(it) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    // Same two-step dismissal the YouTube comment sheet uses: animate down, then
                    // report dismissed, so the sheet is never torn out from under its own animation.
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) onDismiss()
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = stringResource(R.string.close),
                        tint = muted,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        // The sheet now hosts two threads, so its title follows the tab: "Timed
                        // comments" over the moments, plain "Comments" over the YouTube thread.
                        text = stringResource(
                            if (timedSelected) R.string.timestamped_comments else R.string.comments
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // Count first, then the track, then which provider answered. Resolved as plain
                    // statements in the composable body — `stringResource` needs a composable
                    // context, so none of this goes inside a `remember` block.
                    // Both of these describe the timed thread only. On the All tab the subtitle
                    // keeps the track title and drops them: a count of timed comments and the
                    // provider that supplied them say nothing about a YouTube thread.
                    val countLine = if (timedSelected) {
                        when {
                            comments.size == 1 -> stringResource(R.string.timestamped_comments_one)
                            comments.size > 1 ->
                                stringResource(R.string.timestamped_comments_count, comments.size)
                            else -> null
                        }
                    } else {
                        null
                    }
                    val subtitleParts = mutableListOf<String>()
                    countLine?.let { subtitleParts.add(it) }
                    trackTitle?.takeIf { it.isNotBlank() }?.let { subtitleParts.add(it) }
                    val provider = uiState.sourceName?.takeIf { timedSelected }
                    if (provider != null) {
                        subtitleParts.add(stringResource(R.string.timestamped_comments_source, provider))
                    }
                    Text(
                        text = subtitleParts.joinToString("  ·  "),
                        style = MaterialTheme.typography.labelMedium,
                        color = muted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (uiState.status != null && uiState.status != CommentsStatus.NOT_CONFIGURED) {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            painter = painterResource(R.drawable.refresh),
                            contentDescription = stringResource(R.string.retry),
                            tint = muted,
                        )
                    }
                }
            }

            HorizontalDivider()

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SegmentedButton(
                    selected = timedSelected,
                    onClick = { timedSelected = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                ) { Text(stringResource(R.string.comments_tab_timed)) }
                SegmentedButton(
                    selected = !timedSelected,
                    onClick = { timedSelected = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                ) { Text(stringResource(R.string.comments_tab_all)) }
            }

            HorizontalDivider()

            if (timedSelected) {
                if (durationMs > 0L && comments.isNotEmpty()) {
                    CommentTimelineBar(
                        markers = markers,
                        comments = comments,
                        durationMs = durationMs,
                        positionProvider = positionProvider,
                        activeColor = accent,
                        inactiveColor = muted.copy(alpha = 0.45f),
                        trackColor = muted.copy(alpha = 0.18f),
                        onSeekFraction = { fraction -> onSeekTo((fraction * durationMs).toLong()) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                    HorizontalDivider()
                }

                when {
                    comments.isNotEmpty() -> CommentList(
                        comments = comments,
                        groups = groups,
                        activeGroupIndex = activeGroupIndex,
                        onSeekTo = onSeekTo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )

                    uiState.isLoading -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularWavyProgressIndicator()
                    }

                    else -> CommentsUnavailable(
                        status = uiState.status,
                        onRetry = onRefresh,
                        onOpenProviderSettings = onOpenProviderSettings,
                    )
                }
            } else if (youTubeVideoId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    YouTubeCommentList(videoId = youTubeVideoId)
                }
            } else {
                // Not an error state. A local file, or a track matched through Audius or SoundCloud,
                // simply has no YouTube thread to read — saying so beats a spinner that never
                // resolves, and beats spending a request on an id that was never a video id.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.info),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = muted,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.comments_all_unavailable),
                            style = MaterialTheme.typography.bodyMedium,
                            color = muted,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

/**
 * The chronological list, with the live moment highlighted and followed.
 *
 * Auto-follow works until the user scrolls. Detecting "the user scrolled" is done off the list state's
 * interaction source rather than `isScrollInProgress`, because that flag cannot tell a finger from a
 * programmatic `animateScrollToItem` — keying on it would make the list resume following itself the
 * instant it finished following, i.e. never stop. Drag and fling interactions only ever come from a
 * real gesture, so they are the honest signal.
 *
 * Following also stops being *eager*: the list only moves when the live group has scrolled off. A
 * group that is already on screen is already readable, and re-centring it every few seconds would
 * yank the text out from under someone who is mid-sentence.
 */
@Composable
private fun CommentList(
    comments: List<TimestampedComment>,
    groups: List<CommentTimeline.CommentGroup>,
    activeGroupIndex: Int,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    // Reset when the comment list is replaced — i.e. on a track change — so a new track starts
    // following the playhead again instead of inheriting the previous one's "I scrolled away".
    var following by remember(comments) { mutableStateOf(true) }

    LaunchedEffect(listState) {
        listState.interactionSource.interactions.collect { interaction ->
            // A fling always follows a drag, so DragInteraction.Start is the whole signal — Compose
            // has no separate fling interaction to look for.
            if (interaction is DragInteraction.Start) {
                following = false
            }
        }
    }

    val isLiveGroupOnScreen by remember(activeGroupIndex) {
        derivedStateOf {
            activeGroupIndex >= 0 &&
                listState.layoutInfo.visibleItemsInfo.any { it.index == activeGroupIndex }
        }
    }

    LaunchedEffect(following, activeGroupIndex, groups) {
        if (!following || activeGroupIndex < 0) return@LaunchedEffect
        if (listState.isIndexVisible(activeGroupIndex)) return@LaunchedEffect
        listState.animateScrollToItem(activeGroupIndex)
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            itemsIndexed(
                items = groups,
                key = { _, group -> "${group.startIndex}:${group.timestampMs}" },
            ) { index, group ->
                CommentGroupItem(
                    group = group,
                    comments = comments,
                    isActive = index == activeGroupIndex,
                    onSeekTo = onSeekTo,
                    modifier = Modifier.animateItem(),
                )
            }
        }

        // Only offered once the user has genuinely left the live moment; otherwise it is a button
        // that does nothing sitting on top of the thing it would scroll to.
        if (!following && activeGroupIndex >= 0 && !isLiveGroupOnScreen) {
            FilledTonalButton(
                onClick = { following = true },
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
            ) {
                Text(
                    text = stringResource(R.string.timestamped_comments_jump_to_current),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

private fun LazyListState.isIndexVisible(index: Int): Boolean =
    layoutInfo.visibleItemsInfo.any { it.index == index }

/** One moment in the track: a `m:ss` heading plus every comment stacked at it. */
@Composable
private fun CommentGroupItem(
    group: CommentTimeline.CommentGroup,
    comments: List<TimestampedComment>,
    isActive: Boolean,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) accent.copy(alpha = 0.10f) else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = CommentTimeline.formatTimestamp(group.timestampMs),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) accent else muted,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onSeekTo(CommentTimeline.seekTargetMs(comments[group.startIndex])) }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
            if (group.size > 1) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${group.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = muted,
                )
            }
            if (isActive) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.timestamped_comments_now_playing_marker),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        for (index in group.startIndex..group.endIndexInclusive) {
            CommentRow(
                comment = comments[index],
                isActive = isActive,
                onSeekTo = onSeekTo,
            )
            if (index < group.endIndexInclusive) {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: TimestampedComment,
    isActive: Boolean,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSeekTo(CommentTimeline.seekTargetMs(comment)) }
            .padding(vertical = 4.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.Top,
    ) {
        CommentAvatar(
            url = comment.avatarUrl,
            name = comment.authorName,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = CommentTimeline.formatTimestamp(comment.timestampMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = muted,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive) MaterialTheme.colorScheme.onSurface else muted,
            )
        }
    }
}

/**
 * Provider avatar, or the author's initial when there is none.
 *
 * Avatars are the one part of this sheet that reaches the network on its own (through Coil, like every
 * other image in the app). A failed or absent avatar degrades to the initial — it never blocks the
 * comment text, which is the thing the user actually came for.
 */
@Composable
private fun CommentAvatar(
    url: String?,
    name: String,
    modifier: Modifier = Modifier,
) {
    if (url.isNullOrBlank()) {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.trim().firstOrNull()?.uppercaseChar()?.toString().orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier.clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 * The empty / unavailable / failed copy, chosen by [CommentsStatus] rather than by guessing from an
 * empty list.
 *
 * These states are genuinely different situations and get different treatments: "no provider is set
 * up" is actionable and links to the settings screen, "this track isn't on SoundCloud" is a fact about
 * the song and needs no button, and "the request failed" offers a retry. Showing all three as
 * "No comments" would make a missing API key look like a quiet track.
 */
@Composable
private fun CommentsUnavailable(
    status: CommentsStatus?,
    onRetry: () -> Unit,
    onOpenProviderSettings: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val isError = status == CommentsStatus.FAILED
    val text = when (status) {
        CommentsStatus.NOT_CONFIGURED -> stringResource(R.string.timestamped_comments_not_configured)
        CommentsStatus.NO_MATCH -> stringResource(R.string.timestamped_comments_no_match)
        CommentsStatus.UNSUPPORTED -> stringResource(R.string.timestamped_comments_unsupported)
        CommentsStatus.FAILED -> stringResource(R.string.timestamped_comments_failed)
        CommentsStatus.EMPTY, CommentsStatus.LOADED, null ->
            stringResource(R.string.timestamped_comments_none)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(if (isError) R.drawable.error else R.drawable.chat_timestamp),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(18.dp))
            when {
                status == CommentsStatus.NOT_CONFIGURED && onOpenProviderSettings != null ->
                    FilledTonalButton(onClick = onOpenProviderSettings) {
                        Icon(
                            painter = painterResource(R.drawable.settings),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.timestamped_comments_open_settings))
                    }

                isError -> Button(onClick = onRetry) {
                    Text(text = stringResource(R.string.retry))
                }
            }
        }
    }
}
