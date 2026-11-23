/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.studies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.convx.music.R

@Composable
internal fun StudyLyrics(study: PlayerStudy, session: StudySession) {
    val active = session.activeLine
    val halo = study == PlayerStudy.Halo
    val lined = study == PlayerStudy.HiFi || study == PlayerStudy.Reels
    val coordinates = study == PlayerStudy.Atlas || study == PlayerStudy.Tachometer || study == PlayerStudy.Arcade
    val list = key(session.trackIndex) {
        rememberLazyListState(initialFirstVisibleItemIndex = (active - if (coordinates) 1 else 2).coerceAtLeast(0))
    }
    val align = if (halo) TextAlign.Center else TextAlign.Start
    LaunchedEffect(active, session.trackIndex, session.autoFollow) {
        if (session.autoFollow && active >= 0) {
            val shown = list.layoutInfo.visibleItemsInfo
            if (active <= list.firstVisibleItemIndex || (shown.isNotEmpty() && active >= shown.last().index)) {
                list.scrollToItem((active - 1).coerceAtLeast(0))
            }
        }
    }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        StudyTrackStrip(study, session)
        if (study == PlayerStudy.Atlas) {
            Box(Modifier.fillMaxWidth().height(62.dp).clip(RoundedCornerShape(6.dp))) {
                StudyCover(study, session.trackIndex, Modifier.fillMaxSize())
                StudyLabel("LYRIC TIMELINE", study.foreground, 10,
                    Modifier.align(Alignment.CenterStart).padding(8.dp).background(study.background).padding(6.dp))
            }
        } else {
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                StudyLabel(if (session.autoFollow) "SYNCED LYRICS" else "MANUAL SCROLL", study.secondary)
                StudyLabel(studyTime(session.position.toInt()), study.foreground)
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = list,
            verticalArrangement = Arrangement.spacedBy(if (lined) 2.dp else 7.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            itemsIndexed(session.lines, key = { _, line -> line.start }) { index, line ->
                val current = index == active
                val color = if (current) study.onSignal else if (index < active) study.secondary else study.foreground
                val shape = RoundedCornerShape(
                    topStart = study.corner,
                    topEnd = study.corner,
                    bottomStart = if (study == PlayerStudy.Cutout) 5.dp else study.corner,
                    bottomEnd = study.corner,
                )
                val horizontal = if (coordinates) 7.dp else 13.dp
                val labelColor = if (current) study.onSignal.copy(alpha = .84f) else study.secondary
                Row(
                    Modifier.fillMaxWidth().clip(shape)
                        .background(if (current) study.signal else if (halo) study.surface.copy(alpha = .55f) else Color.Transparent)
                        .then(if (lined && !current) Modifier.border(1.dp, study.foreground.copy(alpha = .14f), shape) else Modifier)
                        .selectable(selected = current, role = Role.Button, onClick = { session.seek(line.start.toFloat()) })
                        .heightIn(min = 49.dp).padding(horizontal = horizontal, vertical = if (current) 16.dp else 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    if (coordinates) StudyLabel(studyTime(line.start), labelColor, 9)
                    StudyText(
                        line.text, color,
                        if (current) (if (study == PlayerStudy.Reels) 24 else 29) else 20,
                        Modifier.weight(1f), if (current) FontWeight.Bold else FontWeight.Medium,
                        study.type, lines = 4, align = align,
                    )
                    if (current && lined) StudyIcon(R.drawable.equalizer, null, study.onSignal, 17.dp)
                }
            }
        }
        StudySeek(study, session, waveform = study == PlayerStudy.Reels || study == PlayerStudy.Arcade)
    }
}

@Composable
internal fun StudyQueue(study: PlayerStudy, session: StudySession) {
    var editing by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        StudyTrackStrip(study, session, "Now playing · ${session.track.artist}")
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val heading = when (study) {
                PlayerStudy.HiFi -> "Next on the record"
                PlayerStudy.Reels -> "Tape sequence"
                PlayerStudy.Atlas -> "Listening route"
                PlayerStudy.Arcade -> "Cartridge queue"
                else -> "Up next"
            }
            StudyText(heading, study.foreground, 24, Modifier.weight(1f), weight = FontWeight.Bold, family = study.type, lines = 2)
            if (session.queue.isNotEmpty()) {
                UtilityPill(study, if (editing) "Done" else "Edit", editing) { editing = !editing }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(if (study == PlayerStudy.HiFi || study == PlayerStudy.Reels) 3.dp else 7.dp),
        ) {
            itemsIndexed(session.queue, key = { _, track -> track }) { index, trackIndex ->
                val track = studyTracks[trackIndex]
                val reels = study == PlayerStudy.Reels
                val atlas = study == PlayerStudy.Atlas
                val square = study == PlayerStudy.Arcade || atlas
                val lined = study == PlayerStudy.HiFi || study == PlayerStudy.Reels
                val priority = index == 0 && (study == PlayerStudy.Arcade || study == PlayerStudy.Tachometer)
                val shape = if (study == PlayerStudy.Cutout) {
                    RoundedCornerShape(24.dp, 24.dp, 24.dp, 6.dp)
                } else RoundedCornerShape(study.corner)
                Row(
                    Modifier.fillMaxWidth().clip(shape)
                        .background(if (lined) study.background else study.surface)
                        .border(1.dp, if (priority) study.signal else study.foreground.copy(alpha = .17f), shape)
                        .padding(start = 5.dp, end = if (editing) 2.dp else 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        Modifier.weight(1f).heightIn(min = 62.dp).clickable(role = Role.Button) { session.playFromQueue(trackIndex) }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(if (editing) 6.dp else 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!editing) {
                            val number = (index + 1).toString().padStart(2, '0')
                            if (atlas) {
                                Box(
                                    Modifier.size(24.dp).border(1.dp, study.signal.copy(alpha = .6f), CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) { StudyLabel(number, study.signal, 9) }
                            } else {
                                StudyLabel(if (reels) "A${index + 2}" else number,
                                    if (priority) study.signal else study.secondary, 10)
                            }
                        }
                        if (!reels || editing) {
                            StudyCover(study, trackIndex, Modifier.size(if (editing) 29.dp else 36.dp)
                                .clip(if (study == PlayerStudy.HiFi) CircleShape else RoundedCornerShape(if (square) 2.dp else 7.dp)))
                        } else {
                            Box(Modifier.width(1.dp).height(28.dp).background(study.foreground.copy(alpha = .25f)))
                        }
                        Column(Modifier.weight(1f)) {
                            StudyText(track.title, study.foreground, 14, weight = FontWeight.SemiBold, family = study.type, lines = 2)
                            StudyText(track.artist, study.secondary, 11)
                        }
                        if (!editing) StudyLabel(studyTime(track.duration), study.secondary, 9)
                    }
                    if (editing) {
                        StudyButton(R.drawable.arrow_upward, "Move ${track.title} up", study.foreground, iconSize = 17.dp,
                            enabled = index > 0) { session.moveInQueue(trackIndex, -1) }
                        StudyButton(R.drawable.arrow_downward, "Move ${track.title} down", study.foreground, iconSize = 17.dp,
                            enabled = index < session.queue.lastIndex) { session.moveInQueue(trackIndex, 1) }
                    }
                }
            }
            if (session.queue.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().background(study.surface, RoundedCornerShape(study.corner)).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        StudyText("You’re at the end of the queue", study.foreground, 17, weight = FontWeight.SemiBold, lines = 2)
                        UtilityPill(study, "Restore demo queue", false, session::restoreQueue)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            UtilityPill(study, "Shuffle", session.shuffle) { session.shuffle = !session.shuffle }
            UtilityPill(study, "Repeat", session.repeat) { session.repeat = !session.repeat }
        }
    }
}

@Composable
internal fun StudyMenu(study: PlayerStudy, session: StudySession) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MenuArtwork(study, session)
        if (session.details) {
            val facts = listOf("Title" to session.track.title, "Artist" to session.track.artist,
                "Collection" to "After Hours", "Duration" to studyTime(session.track.duration))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(study.corner)).background(study.surface).padding(13.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                facts.forEach { (label, value) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StudyText(label, study.secondary, 14)
                        StudyText(value, study.foreground, 15, weight = FontWeight.SemiBold, family = study.type)
                    }
                }
            }
        } else {
            MenuGroup(study, "TRACK") {
                MenuAction(study, if (session.saved) R.drawable.favorite else R.drawable.favorite_border,
                    if (session.saved) "Remove from favorites" else "Save to favorites", if (session.saved) "Saved" else null,
                    session::toggleFavorite)
                MenuAction(study, R.drawable.info, "Track details") { session.details = true }
            }
            MenuGroup(study, "PLAYBACK") {
                MenuAction(study, R.drawable.shuffle, "Shuffle", if (session.shuffle) "On" else "Off") {
                    session.shuffle = !session.shuffle
                }
                MenuAction(study, R.drawable.repeat_one, "Repeat track", if (session.repeat) "On" else "Off") {
                    session.repeat = !session.repeat
                }
                MenuAction(study, R.drawable.queue_music, "Open queue", "${session.queue.size} next") { session.page = StudyPage.Queue }
            }
            MenuGroup(study, "LYRICS") {
                MenuAction(study, R.drawable.lyrics, "Follow playback", if (session.autoFollow) "On" else "Off") {
                    session.autoFollow = !session.autoFollow
                }
                MenuAction(study, R.drawable.lyrics_active, "Open lyrics") { session.page = StudyPage.Lyrics }
            }
        }
    }
}

@Composable
private fun MenuArtwork(study: PlayerStudy, session: StudySession) {
    val shape = RoundedCornerShape(topStart = study.corner, topEnd = study.corner,
        bottomStart = study.corner, bottomEnd = if (study == PlayerStudy.Cutout) 49.dp else study.corner)
    val wideArt = study == PlayerStudy.Atlas || study == PlayerStudy.Cutout
    val fill = when (study) {
        PlayerStudy.Tachometer -> listOf(Color(0xFF221B19), Color(0xFF39231B))
        PlayerStudy.Pocket -> listOf(Color(0xFF23594F), study.surface)
        PlayerStudy.Halo -> listOf(Color(0xFF3A5BD5), study.surface)
        else -> listOf(study.surface, study.surface)
    }
    val color = if (study == PlayerStudy.Tachometer) study.onSignal else study.foreground
    Column(Modifier.fillMaxWidth().clip(shape).background(Brush.linearGradient(fill))
        .border(1.dp, color.copy(alpha = .16f), shape)) {
        if (wideArt) StudyCover(study, session.trackIndex, Modifier.fillMaxWidth().height(72.dp))
        Row(
            Modifier.fillMaxWidth().padding(11.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (!wideArt) {
                StudyCover(study, session.trackIndex, Modifier.size(56.dp)
                    .clip(if (study == PlayerStudy.HiFi || study == PlayerStudy.Halo) CircleShape else RoundedCornerShape(5.dp)))
            }
            Column(Modifier.weight(1f)) {
                StudyText(session.track.title, color, 21, weight = FontWeight.Bold, family = study.type)
                StudyText("${session.track.artist} · ${studyTime(session.track.duration)}", color.copy(alpha = .87f), 12)
            }
        }
    }
}

@Composable
private fun MenuGroup(study: PlayerStudy, label: String, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(study.corner)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        StudyLabel(label, study.secondary, 10, Modifier.padding(horizontal = 3.dp))
        Column(
            Modifier.fillMaxWidth().clip(shape).background(study.surface)
                .border(1.dp, study.foreground.copy(alpha = .15f), shape),
        ) { content() }
    }
}

@Composable
private fun MenuAction(study: PlayerStudy, icon: Int, title: String, value: String? = null, action: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 49.dp).clickable(role = Role.Button, onClick = action)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(29.dp).background(study.signal, RoundedCornerShape(minOf(study.corner, 10.dp))),
            contentAlignment = Alignment.Center,
        ) { StudyIcon(icon, null, study.onSignal, 17.dp) }
        StudyText(title, study.foreground, 14, Modifier.weight(1f), weight = FontWeight.Medium, lines = 2)
        if (value != null) StudyLabel(value, study.secondary, 10)
    }
}

@Composable
private fun UtilityPill(study: PlayerStudy, text: String, selected: Boolean, onClick: () -> Unit) {
    val fill = if (selected) study.signal else study.surface
    Box(
        Modifier.height(48.dp).clip(RoundedCornerShape(study.corner)).background(fill)
            .clickable(role = Role.Button, onClick = onClick).padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) { StudyText(text, if (selected) study.onSignal else study.foreground, 12, weight = FontWeight.Bold) }
}
