package com.convx.music.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.convx.music.R
import com.convx.music.ui.component.BottomSheetState

@Composable
internal fun PlayerHandle(sheet: BottomSheetState, tint: Color, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.player_collapse)
    Canvas(
        modifier.size(width = 64.dp, height = 24.dp).clickable(
            enabled = sheet.isExpanded,
            onClickLabel = label,
            role = Role.Button,
            onClick = { sheet.collapseSoft() },
        ),
    ) {
        val halfWidth = 24.dp.toPx()
        val expanded = sheet.progress.coerceIn(0f, 1f)
        val bend = 4.dp.toPx() * (1f - expanded)
        val left = Offset(center.x - halfWidth, center.y - bend)
        val right = Offset(center.x + halfWidth, center.y - bend)
        val middle = Offset(center.x, center.y + bend)
        val stroke = 5.dp.toPx()
        val color = tint.copy(alpha = 0.3f * expanded)
        drawLine(color, left, middle, strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, middle, right, strokeWidth = stroke, cap = StrokeCap.Round)
    }
}
