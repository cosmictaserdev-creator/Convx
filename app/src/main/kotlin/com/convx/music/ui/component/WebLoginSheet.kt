/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.R
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Text
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val WebLoginSheetSpring =
    spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow,
        visibilityThreshold = 0.0005f,
    )

private val WebLoginBarSolidHeight = 76.dp
private val WebLoginPeek = 8.dp

@Composable
internal fun WebLoginSheet(
    title: String,
    onDismiss: () -> Unit,
    onProgress: (Float) -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    // Shared between the slide-in, slide-out and the returned spring of a drag.
    val presence = remember { Animatable(0f) }
    var dragPx by remember { mutableFloatStateOf(0f) }
    var dragDistance by remember { mutableFloatStateOf(0f) }
    var expanded by remember { mutableStateOf(false) }
    var returnJob by remember { mutableStateOf<Job?>(null) }
    var sheetHeightPx by remember { mutableFloatStateOf(1f) }
    // True while the sheet is wanted: a close request must not run twice or re-open after.
    val wanted = remember { mutableStateOf(true) }

    val dismiss by rememberUpdatedState(onDismiss)
    val reportProgress by rememberUpdatedState(onProgress)
    DisposableEffect(Unit) {
        onDispose { reportProgress(0f) }
    }
    LaunchedEffect(presence) {
        snapshotFlow { presence.value }.collect { reportProgress(it) }
    }
    LaunchedEffect(Unit) {
        presence.animateTo(1f, WebLoginSheetSpring)
    }

    val close: () -> Unit = {
        if (wanted.value) {
            wanted.value = false
            returnJob?.cancel()
            scope.launch {
                presence.animateTo(0f, WebLoginSheetSpring)
                dismiss()
            }
        }
    }

    BackHandler(onBack = close)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + WebLoginPeek
        val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
        val expandedHeight = (maxHeight - topInset).coerceAtLeast(0.dp)
        val sheetHeight by animateDpAsState(
            targetValue = if (expanded || keyboardVisible) expandedHeight else maxHeight * 0.70f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
            label = "loginSheetHeight",
        )
        Box(
            Modifier.fillMaxSize()
                .graphicsLayer { alpha = presence.value.coerceIn(0f, 1f) }
                .background(Color.Black.copy(alpha = 0.18f))
                .clickable(onClick = close),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(sheetHeight)
                .semantics { paneTitle = title }
                .onSizeChanged { sheetHeightPx = it.height.toFloat().coerceAtLeast(1f) }
                .graphicsLayer {
                    translationY = (1f - presence.value) * size.height + dragPx
                }
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF1C1C1C)),
        ) {
            // Keep the browser below the drag handle so the header does not cover page controls.
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = WebLoginBarSolidHeight)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                    .imePadding(),
            ) {
                content()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WebLoginBarSolidHeight)
                    .background(Color(0xFF3A3A3A))
                    .pointerInput(expanded, keyboardVisible) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                returnJob?.cancel()
                                dragDistance = dragPx
                            },
                            onDragCancel = {
                                returnJob = scope.launch {
                                    animate(dragPx, 0f, animationSpec = WebLoginSheetSpring) { v, _ -> dragPx = v }
                                }
                            },
                            onDragEnd = {
                                if (dragDistance < -sheetHeightPx * 0.08f) {
                                    expanded = true
                                }
                                if (dragPx > sheetHeightPx * 0.22f && !expanded && !keyboardVisible) {
                                    close()
                                } else {
                                    if (dragPx > sheetHeightPx * 0.12f && expanded && !keyboardVisible) expanded = false
                                    returnJob = scope.launch {
                                        val start = dragPx
                                        animate(start, 0f, animationSpec = WebLoginSheetSpring) { v, _ -> dragPx = v }
                                    }
                                }
                            },
                        ) { change, delta ->
                            change.consume()
                            dragDistance += delta
                            dragPx = dragDistance.coerceAtLeast(0f)
                        }
                    },
            ) {
                Box(Modifier.fillMaxWidth().padding(top = 10.dp), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .width(120.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f)),
                    )
                }
                Row(
                    modifier = Modifier.padding(start = 22.dp, end = 14.dp, top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        title,
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.28f))
                            .clickable(onClick = close),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painterResource(R.drawable.close),
                            contentDescription = stringResource(android.R.string.cancel),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}
