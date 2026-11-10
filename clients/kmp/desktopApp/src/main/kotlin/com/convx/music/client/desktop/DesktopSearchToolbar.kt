package com.convx.music.client.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import dev.nucleusframework.macoscompose.components.IconButton
import dev.nucleusframework.macoscompose.components.IconButtonStyle
import zone.ien.hig.glass.LiquidGlassMaterials
import zone.ien.hig.glass.liquidGlass

@Composable
internal fun DesktopSearchToolbar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    canGoBack: Boolean,
    onBack: () -> Unit,
    canGoForward: Boolean,
    onForward: () -> Unit,
    onReload: () -> Unit,
    backdrop: LayerBackdrop,
    onCloseWindow: () -> Unit,
    onMinimizeWindow: () -> Unit,
    onZoomWindow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = desktopPalette()
    Box(modifier.fillMaxWidth().height(72.dp)) {
        Box(Modifier.matchParentSize()
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                drawRect(Brush.verticalGradient(0f to Color.White, .7f to Color.White, 1f to Color.Transparent), blendMode = BlendMode.DstIn)
            }
            .liquidGlass(backdrop, RectangleShape, LiquidGlassMaterials.MiniPlayer.copy(
                tint = palette.background.copy(alpha = .72f), blurRadius = 24.dp,
                saturation = 1.5f, refractionAmount = 0.dp, chromaticAberration = false)))
        Row(Modifier.fillMaxWidth().height(56.dp).padding(start = 16.dp, end = 16.dp)
            .then(dev.nucleusframework.macoscompose.components.LocalNoWindowDragModifier.current),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(icon = dev.nucleusframework.macoscompose.icons.Icons.ChevronLeft,
                onClick = onBack, enabled = canGoBack, style = IconButtonStyle.Borderless,
                modifier = Modifier.size(34.dp).semantics { contentDescription = "Back" })
            IconButton(icon = dev.nucleusframework.macoscompose.icons.Icons.ChevronRight,
                onClick = onForward, enabled = canGoForward, style = IconButtonStyle.Borderless,
                modifier = Modifier.size(34.dp).semantics { contentDescription = "Forward" })
            Spacer(Modifier.weight(1f))
            SearchField(query, onQueryChange, Modifier.widthIn(max = 560.dp).weight(8f), onSubmit,
                placeholder = "Search music", compact = true)
            Spacer(Modifier.weight(1f))
            PlayerGlyphButton(DesktopGlyphKind.RELOAD, "Reload", size = 32, icon = 16, onClick = onReload)
            Spacer(Modifier.weight(.7f))
            DesktopWindowControlPill(onCloseWindow, onMinimizeWindow, onZoomWindow)
        }
    }
}
