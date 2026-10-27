/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zone.ien.hig

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * The modal presenter compose-hig itself does not ship: [CupertinoSheetState] is only the
 * detent state machine and [CupertinoBottomSheetContent] is only the content chrome — nothing
 * in the library draws a scrim, drags the sheet, or positions it by detent. This fills that
 * gap using the same (module-internal) [SwipeableV2State] machinery [CupertinoSheetState]
 * already wraps, so a sheet built on it behaves like a real [PresentationStyle.Modal]: opens to
 * its first detent, drags between [PresentationDetent]s, and dismisses past the last one.
 *
 * Live-resizes the sheet as it drags ([PresentationContentInteraction.Resize]) rather than
 * translating a fixed-height card — a fixed-height card whose content is a lazy list would
 * otherwise measure that list with an unbounded height constraint and crash.
 */
@Composable
fun CupertinoModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: CupertinoSheetState = rememberCupertinoSheetState(),
    sheetMaxWidth: Dp = 640.dp,
    shape: Shape = CupertinoBottomSheetDefaults.shape,
    containerColor: Color = CupertinoBottomSheetDefaults.containerColor,
    contentColor: Color = CupertinoBottomSheetDefaults.contentColor,
    scrimColor: Color = Color.Black.copy(alpha = 0.32f),
    dragHandle: @Composable (() -> Unit)? = { CupertinoBottomSheetDefaults.DragHandle() },
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val modalStyle = sheetState.presentationStyle as? PresentationStyle.Modal
    val detents = remember(modalStyle) { modalStyle?.detents?.toList() ?: listOf(PresentationDetent.Large) }
    val dismissOnClickOutside = modalStyle?.dismissOnClickOutside ?: true

    // Extra margin above the status bar so the sheet's rounded top corner clears the notch
    // area with scrim visible above it — PresentationDetent.Large itself reserves none.
    val topInsetPx = with(density) { WindowInsets.statusBars.getTop(density).toFloat() + 24.dp.toPx() }

    val possibleValues = remember(detents) {
        buildSet {
            add(CupertinoSheetValue.Hidden)
            detents.forEach { d ->
                add(if (d is PresentationDetent.Large) CupertinoSheetValue.Expanded else CupertinoSheetValue.PartiallyExpanded(d))
            }
        }
    }

    val swipeableState = sheetState.swipeableState
    var hasExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(sheetState) {
        if (swipeableState.currentValue == CupertinoSheetValue.Hidden) {
            sheetState.show()
        }
        hasExpanded = true
    }

    // The swipe settling on Hidden (drag-dismiss or a tap on the scrim) is the single source
    // of truth for "the sheet is gone" — notify the caller only once that animation finishes,
    // not the instant a dismiss is requested, so it never disappears mid-flight.
    var settledHidden by remember { mutableStateOf(false) }
    LaunchedEffect(swipeableState.currentValue, hasExpanded) {
        if (hasExpanded && swipeableState.currentValue == CupertinoSheetValue.Hidden) {
            settledHidden = true
            onDismissRequest()
        }
    }
    if (settledHidden) return

    var containerHeightPx by remember { mutableFloatStateOf(0f) }

    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val offset = swipeableState.offset
                    alpha = if (offset == null || containerHeightPx <= 0f) {
                        0f
                    } else {
                        (1f - (offset / containerHeightPx).coerceIn(0f, 1f))
                    }
                }
                .background(scrimColor)
                .pointerInput(dismissOnClickOutside) {
                    detectTapGestures {
                        if (dismissOnClickOutside) scope.launch { sheetState.hide() }
                    }
                }
                .swipeAnchors(
                    state = swipeableState,
                    possibleValues = possibleValues,
                ) { value, layoutSize ->
                    val h = layoutSize.height.toFloat()
                    containerHeightPx = h
                    val detentHeight = when (value) {
                        CupertinoSheetValue.Hidden -> return@swipeAnchors h
                        CupertinoSheetValue.Expanded ->
                            (detents.firstOrNull { it is PresentationDetent.Large } ?: PresentationDetent.Large)
                                .calculate(density, h)
                        is CupertinoSheetValue.PartiallyExpanded -> value.detent.calculate(density, h)
                    }
                    h - detentHeight.coerceIn(0f, h - topInsetPx)
                },
        )

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = sheetMaxWidth)
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val offset = swipeableState.offset
                    val h = (containerHeightPx - (offset ?: containerHeightPx)).coerceAtLeast(0f).roundToInt()
                    val placeable = measurable.measure(constraints.copy(minHeight = h, maxHeight = h))
                    layout(placeable.width, h) { placeable.place(0, 0) }
                }
                .swipeableV2(swipeableState, Orientation.Vertical)
                .clip(shape)
                .background(containerColor),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                dragHandle?.invoke()
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    content()
                }
            }
        }
    }
}
