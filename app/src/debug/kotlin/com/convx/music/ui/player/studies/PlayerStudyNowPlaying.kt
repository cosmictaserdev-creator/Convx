/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.studies

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.convx.music.R
import kotlin.math.roundToInt

@Composable
internal fun StudyNowPlaying(study: PlayerStudy, session: StudySession) {
    when (study) {
        PlayerStudy.Tachometer -> TachometerPlayer(study, session)
        PlayerStudy.Reels -> ReelsPlayer(study, session)
        PlayerStudy.Cutout -> CutoutPlayer(study, session)
        PlayerStudy.HiFi -> HiFiPlayer(study, session)
        PlayerStudy.Arcade -> ArcadePlayer(study, session)
        PlayerStudy.Pocket -> PocketPlayer(study, session)
        PlayerStudy.Halo -> HaloPlayer(study, session)
        PlayerStudy.Atlas -> AtlasPlayer(study, session)
    }
}

@Composable
private fun TachometerPlayer(study: PlayerStudy, session: StudySession) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                StudyLabel("CURRENT TRACK", study.secondary)
                StudyText(session.track.title, study.foreground, 42, weight = FontWeight.Black, lines = 2)
                StudyText(session.track.artist, study.foreground, 15, family = study.type)
            }
            FavoriteKey(study, session)
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            StudyGauge(study, session.trackIndex, session.fraction, Modifier.fillMaxSize())
            StudyLabel("PLAYHEAD", study.secondary, 9, Modifier.align(Alignment.TopStart).padding(top = 9.dp))
            Box(
                Modifier.align(Alignment.BottomStart).padding(bottom = 4.dp)
                    .background(study.signal, RoundedCornerShape(4.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                StudyLabel(studyTime(session.position.toInt()), study.onSignal, 16)
            }
        }
        StudySeek(study, session)
        StudyTransport(study, session, blockKeys = true)
    }
}

@Composable
private fun ReelsPlayer(study: PlayerStudy, session: StudySession) {
    val shape = RoundedCornerShape(study.corner)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(
            Modifier.weight(1f).fillMaxWidth().border(1.dp, study.signal.copy(alpha = .46f), shape)
                .background(study.surface, shape).padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StudyLabel("TAPE TRANSPORT", study.secondary, 9)
                StudyLabel("A / ${(session.trackIndex + 1).toString().padStart(2, '0')}", study.foreground, 9)
            }
            ReelsArtwork(session.fraction, Modifier.weight(1f).fillMaxWidth())
            StudyText(session.track.title.uppercase(), study.foreground, 25, weight = FontWeight.Bold, family = study.type)
            StudyText(session.track.artist.uppercase(), study.secondary, 13, family = study.type)
            StudySeek(study, session, waveform = true)
        }
        StudyTransport(study, session, blockKeys = true)
    }
}

private val cutoutShell = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(0f, h * .10f)
    quadraticTo(0f, 0f, w * .10f, 0f)
    lineTo(w * .39f, 0f)
    cubicTo(w * .50f, 0f, w * .51f, h * .16f, w * .65f, h * .16f)
    lineTo(w * .90f, h * .16f)
    quadraticTo(w, h * .16f, w, h * .26f)
    lineTo(w, h * .65f)
    quadraticTo(w, h * .74f, w * .91f, h * .74f)
    lineTo(w * .72f, h * .74f)
    cubicTo(w * .56f, h * .74f, w * .56f, h * .87f, w * .72f, h * .87f)
    lineTo(w * .90f, h * .87f)
    quadraticTo(w, h * .87f, w, h * .95f)
    quadraticTo(w, h, w * .90f, h)
    lineTo(w * .10f, h)
    quadraticTo(0f, h, 0f, h * .91f)
    close()
}

@Composable
private fun CutoutPlayer(study: PlayerStudy, session: StudySession) {
    val cream = Color(0xFFFFECDC)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            Box(
                Modifier.fillMaxSize().clip(cutoutShell).background(
                    Brush.linearGradient(listOf(Color(0xFFFF572D), Color(0xFFE84029), Color(0xFFB92328))),
                ),
            ) {
                StudyCover(study, session.trackIndex, Modifier.fillMaxWidth(.82f).fillMaxHeight(.48f)
                    .align(Alignment.Center).clip(RoundedCornerShape(26.dp, 18.dp, 55.dp, 18.dp)))
                StudyLabel("SIDE A  /  ${session.trackIndex + 1}", cream, 10,
                    Modifier.align(Alignment.TopStart).padding(16.dp))
                StudyLabel(studyTime(session.position.toInt()), cream, 15,
                    Modifier.align(Alignment.BottomStart).padding(start = 16.dp, bottom = 12.dp))
            }
            FavoriteKey(study, session, Modifier.align(Alignment.TopEnd))
            Box(Modifier.align(Alignment.BottomEnd).fillMaxHeight(.31f), contentAlignment = Alignment.CenterEnd) {
                Row(
                    Modifier.clip(CircleShape).background(study.surface)
                        .clickable(role = Role.Button) { session.page = StudyPage.Queue }.height(48.dp).padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StudyText("Up next", study.foreground, 12, weight = FontWeight.SemiBold)
                    StudyIcon(R.drawable.queue_music, null, study.foreground, 17.dp)
                }
            }
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 6.dp)) {
            StudyText(session.track.title, study.foreground, 34, weight = FontWeight.Black)
            StudyText(session.track.artist, study.secondary, 14)
            StudySeek(study, session)
        }
        StudyTransport(study, session)
    }
}

@Composable
private fun HiFiPlayer(study: PlayerStudy, session: StudySession) {
    val shape = RoundedCornerShape(21.dp)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            Modifier.weight(1f).fillMaxWidth().clip(shape)
                .background(Brush.verticalGradient(listOf(Color(0xFFEDE5D5), Color(0xFFD5CBB7))))
                .border(1.dp, Color(0xFFCAC0AE), shape).padding(10.dp),
        ) {
            TurntableArtwork(session.trackIndex, session.fraction, Modifier.fillMaxSize().padding(top = 13.dp))
            StudyLabel("AFTER HOURS", study.secondary, 9, Modifier.align(Alignment.TopStart).padding(5.dp))
            StudyLabel("SIDE A", study.foreground, 9,
                Modifier.align(Alignment.BottomEnd).background(study.background, RoundedCornerShape(3.dp)).padding(5.dp))
        }
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 42.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                StudyText(session.track.title, study.foreground, 34, weight = FontWeight.SemiBold, family = study.type, align = TextAlign.Center)
                StudyText(session.track.artist, study.secondary, 14, family = study.type)
            }
            FavoriteKey(study, session, Modifier.align(Alignment.CenterEnd))
        }
        StudySeek(study, session)
        StudyTransport(study, session)
    }
}

@Composable
private fun ArcadePlayer(study: PlayerStudy, session: StudySession) {
    val edge = Color(0xFF554480)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            Modifier.weight(1f).fillMaxWidth().border(1.dp, edge, RoundedCornerShape(4.dp))
                .background(Color(0xFF151025), RoundedCornerShape(4.dp)).padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StudyLabel("CARTRIDGE ${session.trackIndex + 1}", study.signal, 10)
                StudyLabel("STEREO", study.secondary, 9)
            }
            Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(3.dp))) {
                StudyCover(study, session.trackIndex, Modifier.fillMaxSize())
                Canvas(Modifier.fillMaxSize()) {
                    for (line in 0..28) {
                        val y = size.height * line / 28
                        drawLine(Color.Black.copy(alpha = .18f), Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                    }
                }
            }
            StudyText(session.track.title.uppercase(), study.foreground, 20, weight = FontWeight.Bold, family = study.type)
            StudyText(session.track.artist, study.secondary, 12, family = study.type)
        }
        StudySeek(study, session, waveform = true)
        RemoteWheel(study, session, Modifier.fillMaxWidth().height(172.dp), arcade = true)
    }
}

@Composable
private fun PocketPlayer(study: PlayerStudy, session: StudySession) {
    val display = RoundedCornerShape(24.dp)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(
            Modifier.fillMaxWidth().clip(display).background(
                Brush.linearGradient(listOf(Color(0xFF2B6357), Color(0xFF183C37), Color(0xFF152826))),
            ).border(1.dp, Color(0xFF4B7468), display).padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StudyLabel("CONVX POCKET", study.foreground, 10)
                StudyLabel(if (session.playing) "PLAYING" else "PAUSED", study.secondary, 9)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                StudyCover(study, session.trackIndex, Modifier.size(66.dp).clip(RoundedCornerShape(10.dp)))
                Column(Modifier.weight(1f)) {
                    StudyText(session.track.title, study.foreground, 22, weight = FontWeight.Bold, lines = 2)
                    StudyText(session.track.artist, study.secondary, 13)
                }
                FavoriteKey(study, session)
            }
            StudySeek(study, session)
        }
        RemoteWheel(study, session, Modifier.weight(1f).fillMaxWidth(), arcade = false)
    }
}

@Composable
private fun RemoteWheel(study: PlayerStudy, session: StudySession, modifier: Modifier, arcade: Boolean) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val height = minOf(maxHeight, if (arcade) 166.dp else 244.dp)
        val width = minOf(maxWidth - 8.dp, height + if (arcade) 0.dp else 28.dp)
        val body = if (arcade) Color(0xFF342453) else Color(0xFF19302D)
        val key = if (arcade) Color(0xFF4A356D) else Color(0xFF294842)
        val shape = RoundedCornerShape(if (arcade) 34.dp else 48.dp)
        val keyShape = if (arcade) RoundedCornerShape(8.dp) else CircleShape
        Box(Modifier.size(width, height).clip(shape).background(body).border(1.dp, key, shape).padding(8.dp)) {
            StudyButton(R.drawable.skip_previous, "Previous track", study.foreground, key,
                Modifier.align(Alignment.CenterStart), shape = keyShape, onClick = session::previous)
            StudyButton(R.drawable.skip_next, "Next track", study.foreground, key,
                Modifier.align(Alignment.CenterEnd), shape = keyShape, enabled = session.queue.isNotEmpty(), onClick = session::next)
            StudyButton(R.drawable.queue_music, "Open queue", study.foreground, key,
                Modifier.align(Alignment.TopCenter), shape = keyShape) { session.page = StudyPage.Queue }
            StudyButton(R.drawable.lyrics, "Open lyrics", study.foreground, key,
                Modifier.align(Alignment.BottomCenter), shape = keyShape) { session.page = StudyPage.Lyrics }
            StudyPlayButton(study, session, Modifier.align(Alignment.Center), if (arcade) 52.dp else 65.dp, keyShape)
        }
    }
}

@Composable
private fun HaloPlayer(study: PlayerStudy, session: StudySession) {
    val shape = RoundedCornerShape(24.dp)
    val active = session.lines.getOrNull(session.activeLine)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            StudyGauge(study, session.trackIndex, session.fraction, Modifier.fillMaxSize())
            StudyLabel(studyTime(session.position.toInt()), Color(0xFFE4EBFF), 13,
                Modifier.align(Alignment.BottomEnd).padding(5.dp)
                    .background(Color(0xFF182D71), CircleShape).padding(horizontal = 8.dp, vertical = 3.dp))
        }
        Column(
            Modifier.fillMaxWidth().clip(shape).background(
                Brush.verticalGradient(listOf(Color(0xFF3656C9), Color(0xFF273FA8))),
            ).border(1.dp, Color(0xFF7D96FF).copy(alpha = .46f), shape).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(Color(0xFF203885))
                    .clickable(role = Role.Button) { session.page = StudyPage.Lyrics }.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                StudyLabel("LIVE LYRICS", study.secondary, 9)
                StudyText(active?.text ?: "Instrumental intro", study.foreground, 20,
                    weight = FontWeight.SemiBold, lines = 2, align = TextAlign.Center)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StudyCover(study, session.trackIndex, Modifier.size(34.dp).clip(CircleShape))
                Column(Modifier.weight(1f)) {
                    StudyText(session.track.title, study.foreground, 16, weight = FontWeight.Bold)
                    StudyText(session.track.artist, study.secondary, 12)
                }
                FavoriteKey(study, session)
            }
            StudySeek(study, session)
            StudyTransport(study, session)
        }
    }
}

@Composable
private fun AtlasPlayer(study: PlayerStudy, session: StudySession) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                StudyText(session.track.title, study.foreground, 33, weight = FontWeight.Black)
                StudyText(session.track.artist, study.secondary, 14)
            }
            FavoriteKey(study, session)
        }
        Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(study.surface)) {
            StudyCover(study, session.trackIndex, Modifier.fillMaxSize())
            AtlasPlayhead(session.fraction, Modifier.fillMaxSize())
            StudyText(
                "${(session.fraction * 100).roundToInt()}%", Color.White, 36,
                Modifier.align(Alignment.BottomEnd).padding(10.dp).background(Color(0xFF141E45), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp), weight = FontWeight.Black,
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            studyChapters.forEach { chapter ->
                val selected = studyChapters.last { it.start <= session.position } == chapter
                val text = if (selected) study.onSignal else study.foreground
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(5.dp))
                        .background(if (selected) study.signal else study.surface)
                        .clickable(role = Role.Button) { session.seek(chapter.start.toFloat()) }
                        .height(49.dp).padding(horizontal = 6.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    StudyLabel(studyTime(chapter.start), text, 9)
                    StudyText(chapter.label, text, 11, weight = FontWeight.SemiBold)
                }
            }
        }
        StudySeek(study, session)
        StudyTransport(study, session)
    }
}

@Composable
private fun AtlasPlayhead(progress: Float, modifier: Modifier) {
    Canvas(modifier) {
        val marker = Color(0xFFEBF1FF)
        val x = size.width * (.07f + progress * .86f)
        val y = size.height * (.77f - progress * .52f)
        drawLine(marker.copy(alpha = .56f), Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
        drawLine(marker.copy(alpha = .56f), Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
        drawCircle(marker, 4.dp.toPx(), Offset(x, y))
        drawCircle(marker, 13.dp.toPx(), Offset(x, y), style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
    }
}

@Composable
private fun FavoriteKey(study: PlayerStudy, session: StudySession, modifier: Modifier = Modifier) {
    StudyButton(
        if (session.saved) R.drawable.favorite else R.drawable.favorite_border,
        if (session.saved) "Remove from favorites" else "Save to favorites",
        if (session.saved) study.onSignal else study.foreground,
        if (session.saved) study.signal else study.surface,
        modifier = modifier,
        toggled = session.saved,
    ) { session.toggleFavorite() }
}
