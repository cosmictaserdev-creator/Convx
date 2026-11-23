/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.studies

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.R
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

@Composable
internal fun PlayerStudyScreen(study: PlayerStudy, session: StudySession, modifier: Modifier = Modifier) {
    LaunchedEffect(session, session.playing) {
        if (!session.playing) return@LaunchedEffect
        var lastTick = TimeSource.Monotonic.markNow()
        while (session.playing) {
            delay(250)
            session.advance(lastTick.elapsedNow().inWholeMilliseconds / 1000f)
            lastTick = TimeSource.Monotonic.markNow()
        }
    }
    val background = when (study) {
        PlayerStudy.Halo -> Brush.verticalGradient(listOf(Color(0xFF344FC4), study.background, Color(0xFF101633)))
        PlayerStudy.Atlas -> Brush.verticalGradient(listOf(Color(0xFF28397E), study.background))
        PlayerStudy.Pocket -> Brush.verticalGradient(listOf(Color(0xFF1A3330), study.background))
        PlayerStudy.Arcade -> Brush.verticalGradient(listOf(Color(0xFF211735), study.background))
        else -> Brush.verticalGradient(listOf(study.background, study.background))
    }
    val edge = if (study == PlayerStudy.Cutout) 14.dp else 18.dp
    Column(modifier.background(background).padding(horizontal = edge)) {
        StudyHeader(study, session)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (session.page) {
                StudyPage.Player -> StudyNowPlaying(study, session)
                StudyPage.Lyrics -> StudyLyrics(study, session)
                StudyPage.Queue -> StudyQueue(study, session)
                StudyPage.More -> StudyMenu(study, session)
            }
        }
        StudyNavigation(study, session)
    }
}

@Composable
private fun StudyHeader(study: PlayerStudy, session: StudySession) {
    val title = when (session.page) {
        StudyPage.Player -> "Now playing"
        StudyPage.Lyrics -> "Lyrics"
        StudyPage.Queue -> "Queue"
        StudyPage.More -> if (session.details) "Track details" else "Track options"
    }
    Row(
        Modifier.fillMaxWidth().height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (session.page == StudyPage.Player) {
            Box(
                Modifier.size(36.dp).background(study.signal, RoundedCornerShape(minOf(study.corner, 12.dp))),
                contentAlignment = Alignment.Center,
            ) {
                StudyIcon(R.drawable.equalizer, null, study.onSignal, 20.dp)
            }
        } else {
            StudyButton(R.drawable.arrow_back, "Back", study.foreground, study.surface, size = 44.dp) {
                if (session.details && session.page == StudyPage.More) session.details = false
                else session.page = StudyPage.Player
            }
        }
        Column(Modifier.weight(1f)) {
            StudyLabel(if (session.page == StudyPage.Player) "AFTER HOURS" else "NOW PLAYING", study.secondary, 9)
            StudyText(title, study.foreground, 19, weight = FontWeight.Bold)
        }
        when (session.page) {
            StudyPage.Player -> StudyButton(R.drawable.more_horiz, "Track options", study.foreground, study.surface) {
                session.page = StudyPage.More
            }
            StudyPage.Lyrics -> StudyButton(
                if (session.autoFollow) R.drawable.lyrics_active else R.drawable.lyrics,
                "Follow lyrics",
                if (session.autoFollow) study.onSignal else study.foreground,
                if (session.autoFollow) study.signal else study.surface,
                toggled = session.autoFollow,
            ) { session.autoFollow = !session.autoFollow }
            StudyPage.Queue -> StudyLabel("${session.queue.size} NEXT", study.secondary)
            StudyPage.More -> Unit
        }
    }
}

@Composable
private fun StudyNavigation(study: PlayerStudy, session: StudySession) {
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 9.dp)
            .clip(RoundedCornerShape(study.corner))
            .background(study.surface)
            .border(1.dp, study.foreground.copy(alpha = .12f), RoundedCornerShape(study.corner))
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StudyPage.entries.forEach { page ->
            val selected = session.page == page
            val tint = if (selected) study.onSignal else study.foreground
            Column(
                Modifier.weight(1f).padding(3.dp).height(48.dp)
                    .clip(RoundedCornerShape((study.corner - 3.dp).coerceAtLeast(2.dp)))
                    .background(if (selected) study.signal else Color.Transparent)
                    .selectable(selected = selected, onClick = { session.page = page }, role = Role.Tab),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                StudyIcon(page.icon, null, tint, 17.dp)
                StudyText(page.label, tint, 10, weight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
internal fun StudyText(
    text: String,
    color: Color,
    size: Int,
    modifier: Modifier = Modifier,
    weight: FontWeight = FontWeight.Normal,
    family: FontFamily = FontFamily.SansSerif,
    lines: Int = 1,
    align: TextAlign = TextAlign.Start,
) {
    Text(text, modifier, color = color, fontSize = size.sp, fontWeight = weight, fontFamily = family,
        lineHeight = (size * 1.1f).sp, maxLines = lines, overflow = TextOverflow.Ellipsis, textAlign = align)
}

@Composable
internal fun StudyLabel(text: String, color: Color, size: Int = 10, modifier: Modifier = Modifier) {
    Text(text, modifier, color = color, fontFamily = FontFamily.Monospace, fontSize = size.sp,
        lineHeight = (size + 4).sp, letterSpacing = .7.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
internal fun StudyIcon(@DrawableRes icon: Int, label: String?, tint: Color, size: Dp) {
    Icon(painterResource(icon), contentDescription = label, tint = tint, modifier = Modifier.size(size))
}

@Composable
internal fun StudyButton(
    @DrawableRes icon: Int,
    label: String,
    tint: Color,
    fill: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 21.dp,
    shape: Shape = CircleShape,
    toggled: Boolean? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier.size(size).clip(shape).background(fill)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics {
                contentDescription = label
                if (toggled != null) stateDescription = if (toggled) "On" else "Off"
            },
        contentAlignment = Alignment.Center,
    ) {
        StudyIcon(icon, null, if (enabled) tint else tint.copy(alpha = .35f), iconSize)
    }
}

@Composable
internal fun StudyTransport(study: PlayerStudy, session: StudySession, blockKeys: Boolean = false) {
    val shape = if (blockKeys) RoundedCornerShape(study.corner) else CircleShape
    Row(
        Modifier.fillMaxWidth().height(62.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StudyButton(R.drawable.shuffle, "Shuffle", if (session.shuffle) study.onSignal else study.secondary,
            if (session.shuffle) study.signal else Color.Transparent, shape = shape, toggled = session.shuffle) {
            session.shuffle = !session.shuffle
        }
        StudyButton(R.drawable.skip_previous, "Previous track", study.foreground,
            if (blockKeys) study.surface else Color.Transparent, shape = shape, onClick = session::previous)
        StudyPlayButton(study, session, size = 60.dp, shape = shape)
        StudyButton(R.drawable.skip_next, "Next track", study.foreground,
            if (blockKeys) study.surface else Color.Transparent, shape = shape,
            enabled = session.queue.isNotEmpty(), onClick = session::next)
        StudyButton(if (session.repeat) R.drawable.repeat_one else R.drawable.repeat, "Repeat track",
            if (session.repeat) study.onSignal else study.secondary, if (session.repeat) study.signal else Color.Transparent,
            shape = shape, toggled = session.repeat) { session.repeat = !session.repeat }
    }
}

@Composable
internal fun StudyPlayButton(
    study: PlayerStudy,
    session: StudySession,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    shape: Shape = CircleShape,
) {
    StudyButton(
        if (session.playing) R.drawable.pause else R.drawable.play,
        if (session.playing) "Pause" else "Play",
        study.onSignal,
        study.signal,
        modifier = modifier,
        size = size,
        iconSize = if (size >= 54.dp) 28.dp else 21.dp,
        shape = shape,
        onClick = session::togglePlay,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StudySeek(study: PlayerStudy, session: StudySession, waveform: Boolean = false) {
    Column(Modifier.fillMaxWidth()) {
        Slider(
            value = session.position,
            onValueChange = session::seek,
            valueRange = 0f..session.track.duration.toFloat(),
            modifier = Modifier.fillMaxWidth().height(if (waveform) 50.dp else 42.dp)
                .semantics { contentDescription = "Playback position" },
            colors = SliderDefaults.colors(
                thumbColor = study.signal,
                activeTrackColor = study.signal,
                inactiveTrackColor = study.foreground.copy(alpha = .18f),
            ),
            thumb = {
                Box(Modifier.size(if (waveform) 3.dp else 10.dp, if (waveform) 30.dp else 10.dp)
                    .background(study.signal, CircleShape))
            },
            track = { state ->
                if (waveform) {
                    Waveform(state.value / session.track.duration, study.signal, study.foreground.copy(alpha = .27f),
                        Modifier.fillMaxWidth().height(30.dp))
                } else {
                    Box(Modifier.fillMaxWidth().height(3.dp).background(study.foreground.copy(alpha = .22f), CircleShape)) {
                        Box(Modifier.fillMaxWidth(state.value / session.track.duration).height(3.dp).background(study.signal, CircleShape))
                    }
                }
            },
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StudyLabel(studyTime(session.position.toInt()), study.foreground)
            StudyLabel(studyTime(session.track.duration), study.secondary)
        }
    }
}

@Composable
internal fun StudyTrackStrip(study: PlayerStudy, session: StudySession, caption: String = session.track.artist) {
    val gradient = when (study) {
        PlayerStudy.Pocket -> listOf(Color(0xFF246056), study.surface)
        PlayerStudy.Halo -> listOf(Color(0xFF405FD2), study.surface)
        PlayerStudy.Tachometer -> listOf(Color(0xFF35231E), Color(0xFF241A15))
        else -> listOf(study.surface, study.surface)
    }
    val text = if (study == PlayerStudy.Tachometer) study.onSignal else study.foreground
    val subtext = if (study == PlayerStudy.Tachometer) Color(0xFFE0AA96) else study.secondary
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(study.corner))
            .background(Brush.horizontalGradient(gradient)).padding(start = 10.dp, top = 9.dp, bottom = 9.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StudyCover(study, session.trackIndex, Modifier.size(42.dp).clip(RoundedCornerShape(5.dp)))
        Column(Modifier.weight(1f)) {
            StudyText(session.track.title, text, 15, weight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            StudyText(caption, subtext, 12)
        }
        StudyPlayButton(study, session, size = 48.dp, shape = RoundedCornerShape(minOf(24.dp, study.corner)))
    }
}
