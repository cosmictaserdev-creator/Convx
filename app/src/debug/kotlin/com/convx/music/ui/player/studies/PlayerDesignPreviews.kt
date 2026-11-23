/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.studies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val desk = Color(0xFFECECE8)
private val deskInk = Color(0xFF20221E)
private val deskMuted = Color(0xFF585B51)
private val deskRule = Color(0xFFCCCEC4)
private val frame = RoundedCornerShape(22.dp)

/** Select Interactive Mode or Run Preview to try the eight studies with one demo session. */
@Preview(name = "Interactive gallery", group = "Gallery", widthDp = 390, heightDp = 850)
@Composable
fun PlayerDesignGalleryPreview() {
    MaterialTheme {
        var study by rememberSaveable { mutableStateOf(PlayerStudy.Tachometer) }
        val session = remember { StudySession() }
        BoxWithConstraints(Modifier.fillMaxSize().background(desk)) {
            val phoneHeight = (maxHeight - 128.dp).coerceAtLeast(615.dp)
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, top = 11.dp, bottom = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StudyText("Convx Player Designs", deskInk, 18, weight = FontWeight.Bold)
                    StudyLabel("DEMO", deskMuted, 9)
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth().selectableGroup(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(PlayerStudy.entries, key = { it.name }) { option ->
                        val selected = study == option
                        Row(
                            Modifier.height(48.dp).padding(vertical = 4.dp).clip(RoundedCornerShape(22.dp))
                                .background(if (selected) deskInk else Color.White)
                                .selectable(selected, role = Role.Tab, onClick = { study = option }).padding(horizontal = 11.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(Modifier.size(9.dp).background(option.background, CircleShape).border(1.dp, option.signal, CircleShape))
                            StudyText(option.title, if (selected) Color.White else deskInk, 12, weight = FontWeight.SemiBold)
                        }
                    }
                }
                PlayerStudyScreen(study, session,
                    Modifier.fillMaxWidth().height(phoneHeight).padding(horizontal = 8.dp)
                        .clip(frame).border(1.dp, deskRule, frame))
                StudyText(study.direction, deskMuted, 11, Modifier.padding(start = 13.dp, end = 13.dp, top = 7.dp, bottom = 6.dp), lines = 2)
            }
        }
    }
}

@Composable
private fun StudyBoard(study: PlayerStudy) {
    MaterialTheme {
        Column(
            Modifier.fillMaxSize().background(desk).padding(horizontal = 22.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    StudyText(study.title, deskInk, 34, weight = FontWeight.Black)
                    StudyText(study.direction, deskMuted, 14, lines = 2)
                }
                StudyLabel("CONVX PLAYER DESIGNS", deskMuted, 10)
            }
            Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StudyPage.entries.forEach { page ->
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        val session = remember(page) { StudySession(page) }
                        StudyLabel(if (page == StudyPage.More) "TRACK OPTIONS" else page.label.uppercase(), deskMuted)
                        PlayerStudyScreen(study, session, Modifier.fillMaxSize().clip(frame).border(1.dp, deskRule, frame))
                    }
                }
            }
        }
    }
}

@Preview(name = "01 — Tachometer", group = "Four-view boards", widthDp = 1460, heightDp = 814)
@Composable
fun TachometerStudyPreview() = StudyBoard(PlayerStudy.Tachometer)

@Preview(name = "02 — Reels", group = "Four-view boards", widthDp = 1460, heightDp = 814)
@Composable
fun ReelsStudyPreview() = StudyBoard(PlayerStudy.Reels)

@Preview(name = "03 — Cutout", group = "Four-view boards", widthDp = 1460, heightDp = 814)
@Composable
fun CutoutStudyPreview() = StudyBoard(PlayerStudy.Cutout)

@Preview(name = "04 — Hi-Fi", group = "Four-view boards", widthDp = 1460, heightDp = 814)
@Composable
fun HiFiStudyPreview() = StudyBoard(PlayerStudy.HiFi)

@Preview(name = "05 — Arcade", group = "Four-view boards", widthDp = 1460, heightDp = 814)
@Composable
fun ArcadeStudyPreview() = StudyBoard(PlayerStudy.Arcade)

@Preview(name = "06 — Pocket", group = "Four-view boards", widthDp = 1460, heightDp = 814)
@Composable
fun PocketStudyPreview() = StudyBoard(PlayerStudy.Pocket)

@Preview(name = "07 — Halo", group = "Four-view boards", widthDp = 1460, heightDp = 814)
@Composable
fun HaloStudyPreview() = StudyBoard(PlayerStudy.Halo)

@Preview(name = "08 — Atlas", group = "Four-view boards", widthDp = 1460, heightDp = 814)
@Composable
fun AtlasStudyPreview() = StudyBoard(PlayerStudy.Atlas)
