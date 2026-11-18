package com.convx.music.client.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.playback.PlayerPage

/** Matches V2's lyrics / audio route / queue bar; auxiliary tabs toggle back to the cover. */
@Composable
internal fun DesktopPlayerTabs(
    page: PlayerPage,
    onLyrics: (() -> Unit)?,
    onQueue: () -> Unit,
    onAudioOutput: (() -> Unit)?,
    outputName: String,
    modifier: Modifier = Modifier,
) {
    val palette = desktopPalette()
    Row(modifier.widthIn(max = 560.dp).fillMaxWidth().heightIn(min = 56.dp).selectableGroup(),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        PlayerTab("Lyrics", DesktopGlyphKind.LYRICS, page == PlayerPage.LYRICS, onLyrics)
        Column(Modifier.widthIn(max = 180.dp).weight(1f).padding(horizontal = 12.dp)
            .clip(CircleShape).desktopInteractive(enabled = onAudioOutput != null) { onAudioOutput?.invoke() }
            .padding(vertical = 6.dp).semantics { contentDescription = "Audio output: $outputName" },
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DesktopGlyph(DesktopGlyphKind.VOLUME, palette.foreground, 20.dp)
            Text(outputName, color = palette.muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        PlayerTab("Queue", DesktopGlyphKind.QUEUE, page == PlayerPage.QUEUE, onQueue)
    }
}

@Composable
private fun PlayerTab(label: String, glyph: DesktopGlyphKind, selected: Boolean, onClick: (() -> Unit)?) {
    val palette = desktopPalette()
    val fill by animateColorAsState(
        if (selected) palette.foreground.copy(alpha = .14f) else androidx.compose.ui.graphics.Color.Transparent,
        tween(if (LocalDesktopReducedMotion.current) 0 else 140), label = "Player tab selection")
    Column(Modifier.width(64.dp).heightIn(min = 56.dp).clip(CircleShape)
        .selectable(selected, enabled = onClick != null, role = Role.Tab, onClick = { onClick?.invoke() })
        .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(32.dp).clip(CircleShape)
            .background(fill),
            contentAlignment = Alignment.Center) {
            DesktopGlyph(glyph, if (selected) palette.accent else palette.foreground, 21.dp)
        }
        Text(label, color = if (selected) palette.accent else palette.muted, fontSize = 11.sp)
    }
}
