/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.ambient

import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.convxy.music.R
import com.convxy.music.constants.AmbientCanvasAnchorSide
import com.convxy.music.constants.AmbientCanvasFitMode
import com.convxy.music.ui.theme.AppleTokens
import kotlin.math.roundToInt

/**
 * A live preview of Ambient Mode's background, for Settings → Ambient Mode.
 *
 * The background is not a sketch of the feature: the panel width, which edge it sits on, what
 * each fit mode does to the frame, the edge feather and both ends of the veil come from the
 * same functions the Ambient screen calls, on the same 16:9 stage. So a slider dragged here
 * moves the same geometry it moves there, and the preview can drift from the app only if those
 * functions change — which the unit tests cover.
 *
 * The foreground is deliberately fake but placed where the real thing is: album artwork in the
 * first half, lyrics in the second, and the progress ring's bezel at the edge, because the
 * judgement being made with this preview is "is the canvas eating the lyrics' space".
 *
 * The canvas stand-in is a moving gradient with a circle and a horizon line in it. A circle is
 * the cheapest honest way to read the three fit modes at this size: Fit keeps it whole, Fill
 * side crops it against the panel, and Stretch squashes it into an ellipse.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AmbientCanvasFitPreview(
    videoCanvasEnabled: Boolean,
    positionFitEnabled: Boolean,
    anchor: AmbientCanvasAnchorSide,
    fitMode: AmbientCanvasFitMode,
    sideWidth: Float,
    sideGradient: Float,
    gradientSpread: Float,
    farVeil: Float,
    edgeFeather: Float,
    dim: Float,
    modifier: Modifier = Modifier,
) {
    // Which fake source is being simulated. Not a preference: it only exists to let the panel
    // behaviour be checked against the shapes canvases actually come in, including a landscape
    // one, which is how the "landscape keeps the full-width background" rule becomes visible.
    var shape by rememberSaveable { mutableStateOf(AmbientCanvasPreviewShape.PORTRAIT_9_16) }
    val anchoredRight = ambientCanvasAnchoredRight(
        anchor = anchor,
        isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl,
    )
    // The stage is 16:9 by construction, so the screen aspect is known without measuring, and
    // the geometry below can be shared with the caption outside the layout scope.
    val panelFraction = ambientCanvasPanelFraction(
        videoAspect = shape.aspect,
        screenAspect = PREVIEW_SCREEN_ASPECT,
        requestedFraction = sideWidth,
        fitMode = fitMode,
    )
    val useSidePanel = positionFitEnabled && ambientCanvasUsesSidePanel(panelFraction)
    val effectiveFraction = if (positionFitEnabled) panelFraction else 1f
    val clampedDim = dim.coerceIn(0f, 0.75f)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.ambient_canvas_fit_preview).uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, top = 4.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppleTokens.CardCorner))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(12.dp),
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(PREVIEW_SCREEN_ASPECT)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MockScreenBase),
            ) {
                val panelWidth = maxWidth * effectiveFraction
                val panelHeight = maxWidth / PREVIEW_SCREEN_ASPECT
                val contentSize = ambientCanvasPreviewContentSize(
                    panelWidth = panelWidth,
                    panelHeight = panelHeight,
                    videoAspect = shape.aspect,
                    fitMode = fitMode,
                )

                // The glow the panel dissolves into. Static on purpose: the Ambient screen
                // animates its own, and this card should not add a second always-on animation
                // to a settings list people scroll through.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Brush.linearGradient(MockGlowColors)),
                )

                if (videoCanvasEnabled) {
                    Box(
                        modifier = Modifier
                            .align(if (anchoredRight) Alignment.CenterEnd else Alignment.CenterStart)
                            .size(width = panelWidth, height = panelHeight)
                            .clipToBounds()
                            .ambientCanvasEdgeFeather(
                                fraction = if (useSidePanel) edgeFeather else 0f,
                                anchoredRight = anchoredRight,
                            ),
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(width = contentSize.width, height = contentSize.height)
                                .graphicsLayer {
                                    scaleX = contentSize.scaleX
                                    scaleY = contentSize.scaleY
                                },
                        ) {
                            // Only composed while the canvas background is on, so the loop
                            // costs nothing for everyone who never enables it.
                            MockCanvasContent(modifier = Modifier.fillMaxSize())
                        }
                    }

                    val (nearAlpha, farAlpha) = ambientCanvasVeilAlphas(
                        dim = clampedDim,
                        sideGradient = sideGradient,
                        farVeil = farVeil,
                    )
                    if (useSidePanel) {
                        AmbientCanvasVeil(
                            nearAlpha = nearAlpha,
                            farAlpha = farAlpha,
                            spread = gradientSpread,
                            anchoredRight = anchoredRight,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = clampedDim)),
                        )
                    }
                }

                // Where the real foreground goes: artwork first, lyrics second.
                Row(
                    modifier = Modifier.matchParentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(start = 8.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.85f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Brush.linearGradient(MockCoverColors)),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(start = 4.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            MockLyricLine(widthFraction = 0.55f, alpha = 0.30f)
                            MockLyricLine(widthFraction = 0.72f, alpha = 0.92f, current = true)
                            MockLyricLine(widthFraction = 0.48f, alpha = 0.26f)
                            MockLyricLine(widthFraction = 0.60f, alpha = 0.18f)
                        }
                    }
                }

                // The progress ring reads as the screen's bezel on a real device.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(2.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(8.dp)),
                )
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.ambient_canvas_fit_preview_shape),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AmbientCanvasPreviewShape.entries.forEach { option ->
                    FilterChip(
                        selected = option == shape,
                        onClick = { shape = option },
                        label = { Text(stringResource(option.labelRes)) },
                    )
                }
            }

            Text(
                text = when {
                    !videoCanvasEnabled ->
                        stringResource(R.string.ambient_canvas_fit_preview_canvas_off)

                    !positionFitEnabled ->
                        stringResource(R.string.ambient_canvas_fit_preview_off)

                    !useSidePanel ->
                        stringResource(R.string.ambient_canvas_fit_preview_full_width)

                    else -> stringResource(
                        R.string.ambient_canvas_fit_preview_panelled,
                        (effectiveFraction * 100f).roundToInt(),
                        stringResource(fitNoteRes(fitMode)),
                    )
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

/**
 * Which fake canvas the preview simulates. The aspects are the ones Apple/YouTube canvases
 * really ship, and [LANDSCAPE_16_9] is here to show the one case Position & Fit steps back from.
 */
private enum class AmbientCanvasPreviewShape(
    @param:StringRes val labelRes: Int,
    val aspect: Float,
) {
    PORTRAIT_9_16(R.string.ambient_canvas_fit_shape_916, 9f / 16f),
    PORTRAIT_3_4(R.string.ambient_canvas_fit_shape_34, 3f / 4f),
    LANDSCAPE_16_9(R.string.ambient_canvas_fit_shape_169, 16f / 9f),
}

/** The video's own box inside the panel, and the scale that turns it into what is shown. */
private class PreviewContentSize(
    val width: Dp,
    val height: Dp,
    val scaleX: Float,
    val scaleY: Float,
)

/**
 * Mirrors what Media3's AspectRatioFrameLayout does with [AmbientCanvasFitMode] inside a panel
 * of [panelWidth] x [panelHeight]: FIT keeps the frame inside, ZOOM grows it until the panel is
 * covered, and STRETCH leaves the frame as measured and scales it non-uniformly to fit — which
 * is why only the last one needs a scale, and why Stretch is the only mode that turns the mock
 * circle into an ellipse.
 */
private fun ambientCanvasPreviewContentSize(
    panelWidth: Dp,
    panelHeight: Dp,
    videoAspect: Float,
    fitMode: AmbientCanvasFitMode,
): PreviewContentSize {
    if (panelWidth <= 0.dp || panelHeight <= 0.dp || videoAspect <= 0f) {
        return PreviewContentSize(panelWidth, panelHeight, 1f, 1f)
    }
    val fitHeight = minOf(panelHeight, panelWidth / videoAspect)
    val fitWidth = fitHeight * videoAspect
    return when (fitMode) {
        AmbientCanvasFitMode.FIT -> PreviewContentSize(fitWidth, fitHeight, 1f, 1f)

        AmbientCanvasFitMode.ZOOM -> {
            val zoomHeight = maxOf(panelHeight, panelWidth / videoAspect)
            PreviewContentSize(zoomHeight * videoAspect, zoomHeight, 1f, 1f)
        }

        AmbientCanvasFitMode.STRETCH -> PreviewContentSize(
            width = fitWidth,
            height = fitHeight,
            scaleX = panelWidth.value / fitWidth.value,
            scaleY = panelHeight.value / fitHeight.value,
        )
    }
}

@StringRes
private fun fitNoteRes(fitMode: AmbientCanvasFitMode): Int = when (fitMode) {
    AmbientCanvasFitMode.FIT -> R.string.ambient_canvas_fit_preview_note_fit
    AmbientCanvasFitMode.ZOOM -> R.string.ambient_canvas_fit_preview_note_zoom
    AmbientCanvasFitMode.STRETCH -> R.string.ambient_canvas_fit_preview_note_stretch
}

@Composable
private fun MockLyricLine(
    widthFraction: Float,
    alpha: Float,
    current: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(if (current) 8.dp else 5.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = alpha)),
    )
}

/**
 * A stand-in for the moving canvas: two drifting pools of light over a dark base, with a
 * "subject" circle and a horizon so crop and stretch stay legible. Only ever composed while
 * the canvas background is enabled.
 */
@Composable
private fun MockCanvasContent(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "mockCanvas")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "mockCanvasPhase",
    )

    Box(
        modifier = modifier.drawBehind {
            // The animated phase is read here, in the draw block, so the mock moves without
            // recomposing the settings list it sits in.
            fun oscillate(min: Float, max: Float, offset: Float): Float {
                val wave = kotlin.math.sin(2f * kotlin.math.PI.toFloat() * (progress + offset))
                return min + (max - min) * ((wave + 1f) * 0.5f)
            }

            val warm = Brush.radialGradient(
                colors = listOf(
                    MockCanvasColors[0].copy(alpha = 0.85f),
                    MockCanvasColors[1].copy(alpha = 0.40f),
                    Color.Transparent,
                ),
                center = Offset(
                    x = size.width * oscillate(0.22f, 0.78f, 0f),
                    y = size.height * oscillate(0.22f, 0.48f, 0.25f),
                ),
                radius = size.maxDimension * 0.75f,
            )
            val cool = Brush.radialGradient(
                colors = listOf(
                    MockCanvasColors[3].copy(alpha = 0.80f),
                    MockCanvasColors[2].copy(alpha = 0.38f),
                    Color.Transparent,
                ),
                center = Offset(
                    x = size.width * oscillate(0.80f, 0.20f, 0.5f),
                    y = size.height * oscillate(0.78f, 0.40f, 0.1f),
                ),
                radius = size.maxDimension * 0.85f,
            )

            drawRect(color = MockCanvasBase)
            drawRect(brush = warm)
            drawRect(brush = cool)
            drawCircle(
                color = Color.White.copy(alpha = 0.72f),
                radius = size.height * 0.13f,
                center = Offset(size.width * 0.5f, size.height * 0.55f),
            )
            drawLine(
                color = Color.White.copy(alpha = 0.26f),
                start = Offset(0f, size.height * 0.75f),
                end = Offset(size.width, size.height * 0.75f),
                strokeWidth = (size.height * 0.015f).coerceAtLeast(1f),
            )
        },
    )
}

private const val PREVIEW_SCREEN_ASPECT = 16f / 9f

private val MockScreenBase = Color(0xFF050505)

// Cool and dim, so it reads as the ambient glow rather than as a second copy of the canvas.
private val MockGlowColors = listOf(
    Color(0xFF241A3C),
    Color(0xFF12283B),
    Color(0xFF33202C),
)

private val MockCoverColors = listOf(Color(0xFFE9DCC2), Color(0xFFB08C5A), Color(0xFF6C4B39))
private val MockCanvasBase = Color(0xFF15101A)
private val MockCanvasColors = listOf(
    Color(0xFFFFB86B),
    Color(0xFFE4735F),
    Color(0xFF7A4FA0),
    Color(0xFF2E6F8E),
)
