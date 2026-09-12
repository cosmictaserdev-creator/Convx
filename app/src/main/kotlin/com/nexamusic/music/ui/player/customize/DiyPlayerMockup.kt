/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.player.customize

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexamusic.app.R
import com.nexamusic.app.constants.HidePlayerThumbnailKey
import com.nexamusic.app.constants.PlayerHorizontalPadding
import com.nexamusic.app.constants.PlayerArtworkStyle
import com.nexamusic.app.constants.PlayerArtworkStyleKey
import com.nexamusic.app.constants.PlayerBackgroundStyle
import com.nexamusic.app.constants.PlayerBackgroundStyleKey
import com.nexamusic.app.constants.PlayerButtonsStyle
import com.nexamusic.app.constants.PlayerButtonsStyleKey
import com.nexamusic.app.constants.PlayerGradientAngleKey
import com.nexamusic.app.constants.PlayerGradientStopsKey
import com.nexamusic.app.constants.PlayerStaticColorKey
import com.nexamusic.app.constants.SliderStyle
import com.nexamusic.app.constants.SliderStyleKey
import com.nexamusic.app.constants.ThumbnailCornerRadiusKey
import com.nexamusic.app.ui.component.LocalAppBackdrop
import com.nexamusic.app.ui.component.LocalGlassEffectConfig
import com.nexamusic.app.ui.component.PLAYER_BLUR_MULTIPLIER
import com.nexamusic.app.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.nexamusic.app.ui.component.isGlassAllowed
import com.nexamusic.app.ui.component.liquidGlass
import com.nexamusic.app.ui.component.shapes.ContinuousRoundedRectangle
import com.nexamusic.app.ui.theme.decodeGradientStops
import com.nexamusic.app.utils.rememberEnumPreference
import com.nexamusic.app.utils.rememberPreference
import kotlin.math.min
import kotlin.math.sin

/**
 * The mockup's virtual canvas, in dp. Everything lays out at this size and is then scaled as a
 * whole, so the editor, the icon picker and the preset thumbnail all show the same composition
 * rather than three different reflows of it. The ratio matches the frame the editor uses, so a
 * sticker's normalised position means the same thing in hit-testing as it does on screen.
 */
private val DESIGN_SHORT_EDGE = 360.dp
private val DESIGN_LONG_EDGE = 780.dp

/**
 * A still likeness of the player, used by the DIY editor, the custom-icon picker and preset
 * thumbnails.
 *
 * It is a mockup, not the live player — the real one is wired to playback, the queue, lyrics and
 * canvas video, none of which belong in an editor. What it *does* share is everything that decides
 * how the player looks: background style, artwork style and corner radius, button style, slider
 * style, and the user's custom icon slots are all read from the same preferences the real player
 * reads. Change your player theme and this changes with it.
 */
@Composable
fun DiyPlayerMockup(
    orientation: DiyOrientation,
    modifier: Modifier = Modifier,
    layout: DiyLayout = DiyLayout.EMPTY,
    stickerOverlay: @Composable (zFilter: (Int) -> Boolean) -> Unit = { zFilter ->
        DiyStickerLayer(layout = layout, orientation = orientation, zFilter = zFilter)
    },
    /**
     * Drawn last, inside the scaled design box. The editor puts its gesture surface here so that
     * touches are measured in the same coordinate space the stickers are positioned in. Receives
     * the design-to-screen scale factor, since a minimum touch-target size measured in this
     * pre-scale space needs to be divided by it to stay a constant physical size on screen.
     */
    topOverlay: @Composable BoxScope.(scale: Float) -> Unit = {},
) {
    val style = rememberMockupStyle()
    val glassConfig = LocalGlassEffectConfig.current
    val glassActive = isGlassAllowed()

    // Same frosted-chrome trick as ArtistScreen/AlbumScreen: this editor is a NavHost
    // destination, so liquidGlass must NOT sample the root appBackdrop (that capture
    // includes the screen itself -> native RenderNode cycle, SIGSEGV). An unattached
    // screen-local backdrop (never .layerBackdrop'd onto anything) gives real glass
    // material with zero self-reference, just without live artwork refraction.
    val mockupBackdrop = rememberLayerBackdrop()

    CompositionLocalProvider(LocalAppBackdrop provides mockupBackdrop) {
        Box(
            modifier = modifier.then(
                if (glassActive) {
                    // Same glass base + multiplier the real player's own background uses,
                    // so the editor is an honest preview of what glass looks like there.
                    Modifier.liquidGlass(
                        config = glassConfig,
                        applyEdgeEffects = false,
                        blurRadiusDp = (glassConfig.blurRadius * PLAYER_BLUR_MULTIPLIER).coerceAtMost(100f),
                    )
                } else {
                    Modifier.background(style.backdrop)
                },
            ),
        ) {
            // With glass on, the style backdrop (accent wash / static color / gradient) still
            // draws on top of the glass base, same as the real player layering its background
            // styles over its own glass — the glass base mostly shows at the unfilled edges
            // and through the specular highlight liquidGlass draws after this content.
            if (glassActive) {
                Box(Modifier.fillMaxSize().background(style.backdrop))
            }

            DiyDesignCanvas(orientation = orientation) { scale ->
                // Anything with a negative z sits behind the artwork. Nothing ever goes behind the
                // backdrop itself — on the real player that slot belongs to the Canvas video.
                stickerOverlay { it < 0 }

                if (orientation == DiyOrientation.PORTRAIT) {
                    PortraitMockup(style)
                } else {
                    LandscapeMockup(style)
                }

                stickerOverlay { it >= 0 }
                topOverlay(scale)
            }
        }
    }
}

/**
 * The same fixed virtual canvas + uniform centered scale [DiyPlayerMockup] lays its content out
 * against, factored out so the real player can wrap its sticker layer in it too. A sticker's
 * normalised (x, y) only means the same thing in the editor and on the real player if both
 * resolve it against this identical canvas — the real player has no other reason to match a
 * 360:780 aspect ratio on its own.
 */
/**
 * The mockup's actual rendered width for [orientation] within a [maxWidth]x[maxHeight] area —
 * the same scale formula [DiyDesignCanvas] computes internally, exposed so sibling UI (the
 * editor's floating control pills) can match the mockup's real on-screen bounds. Without this,
 * fillMaxWidth() controls stretch to the full physical screen while the mockup itself letterboxes
 * narrower and centered on anything wider than a phone (tab view, tablet) — the pills end up
 * pinned to the screen edges, visually disconnected from the canvas they control.
 */
fun diyMockupRenderedWidth(orientation: DiyOrientation, maxWidth: Dp, maxHeight: Dp): Dp {
    val design = if (orientation == DiyOrientation.PORTRAIT) {
        DpSize(DESIGN_SHORT_EDGE, DESIGN_LONG_EDGE)
    } else {
        DpSize(DESIGN_LONG_EDGE, DESIGN_SHORT_EDGE)
    }
    val scale = min(maxWidth / design.width, maxHeight / design.height)
    return design.width * scale
}

@Composable
fun DiyDesignCanvas(
    orientation: DiyOrientation,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(scale: Float) -> Unit,
) {
    val design = if (orientation == DiyOrientation.PORTRAIT) {
        DpSize(DESIGN_SHORT_EDGE, DESIGN_LONG_EDGE)
    } else {
        DpSize(DESIGN_LONG_EDGE, DESIGN_SHORT_EDGE)
    }

    BoxWithConstraints(modifier = modifier) {
        val scale = min(maxWidth / design.width, maxHeight / design.height)

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .requiredSize(design)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        ) {
            content(scale)
        }
    }
}

/** The subset of the player's appearance preferences the mockup can actually show. */
data class MockupStyle(
    val backdrop: Brush,
    val background: PlayerBackgroundStyle,
    val artwork: PlayerArtworkStyle,
    val artworkCorner: Dp,
    val showArtwork: Boolean,
    val slider: SliderStyle,
    val controlTint: Color,
    val onBackdrop: Color,
)

@Composable
private fun rememberMockupStyle(): MockupStyle {
    val accent = MaterialTheme.colorScheme.primary
    val background by rememberEnumPreference(
        PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.APPLE_MUSIC,
    )
    val artwork by rememberEnumPreference(
        PlayerArtworkStyleKey,
        defaultValue = PlayerArtworkStyle.CARD,
    )
    val buttons by rememberEnumPreference(
        PlayerButtonsStyleKey,
        defaultValue = PlayerButtonsStyle.DEFAULT,
    )
    val slider by rememberEnumPreference(SliderStyleKey, defaultValue = SliderStyle.SLIM)
    val (staticColor) = rememberPreference(PlayerStaticColorKey, defaultValue = 0)
    val (gradientStops) = rememberPreference(PlayerGradientStopsKey, defaultValue = "")
    val (gradientAngle) = rememberPreference(PlayerGradientAngleKey, defaultValue = 90f)
    val (cornerRadius) = rememberPreference(ThumbnailCornerRadiusKey, defaultValue = 3f)
    val (hideThumbnail) = rememberPreference(HidePlayerThumbnailKey, defaultValue = false)

    val tertiary = MaterialTheme.colorScheme.tertiary
    return remember(
        accent, background, artwork, buttons, slider,
        staticColor, gradientStops, gradientAngle, cornerRadius, hideThumbnail, tertiary,
    ) {
        MockupStyle(
            backdrop = mockBackdrop(background, accent, staticColor, gradientStops, gradientAngle),
            background = background,
            artwork = artwork,
            // Real player's Thumbnail.kt doubles the raw preference for its clip radius
            // (calculateThumbnailDimensions); match that here or the mockup under-rounds.
            artworkCorner = (cornerRadius * 2).dp,
            showArtwork = !hideThumbnail,
            slider = slider,
            controlTint = when (buttons) {
                PlayerButtonsStyle.PRIMARY -> accent
                PlayerButtonsStyle.TERTIARY -> tertiary
                PlayerButtonsStyle.DEFAULT -> Color.White
            },
            onBackdrop = Color.White,
        )
    }
}

/**
 * Approximates each background style with fake artwork.
 *
 * The styles that sample real album art — blur, mesh, glow — have no artwork to sample here, so
 * they fall back to the same accent wash. That is honest for a mockup: it shows the tone the
 * player will have without pretending to preview a song that is not playing.
 */
private fun mockBackdrop(
    style: PlayerBackgroundStyle,
    accent: Color,
    staticColor: Int,
    gradientStops: String,
    gradientAngle: Float,
): Brush = when (style) {
    PlayerBackgroundStyle.STATIC -> {
        val color = if (staticColor == 0) Color(0xFF101014) else Color(staticColor)
        Brush.verticalGradient(listOf(color, color))
    }

    PlayerBackgroundStyle.CUSTOM_GRADIENT -> {
        val stops = decodeGradientStops(gradientStops)
        // The real player tilts this by an arbitrary angle; the mockup only distinguishes
        // "mostly vertical" from "mostly horizontal", which is all that reads at this size.
        val vertical = sin(Math.toRadians(gradientAngle.toDouble())).let { it * it } > 0.5
        if (vertical) Brush.verticalGradient(stops) else Brush.horizontalGradient(stops)
    }

    else -> Brush.verticalGradient(
        listOf(
            accent.flattenOnBlack(0.55f),
            accent.flattenOnBlack(0.18f),
            Color(0xFF0B0B0D),
        ),
    )
}

@Composable
private fun PortraitMockup(style: MockupStyle) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Matches the real Player's own PlayerHorizontalPadding — this used
            // to be a hardcoded 24.dp, drifting from the real screen's proportions.
            .padding(horizontal = PlayerHorizontalPadding, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Spacer(Modifier.weight(0.2f))
        if (style.showArtwork) {
            MockArtwork(style, isPortrait = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.weight(0.15f))
        } else {
            Spacer(Modifier.weight(1f))
        }
        MockTitleRow(style)
        MockSeekBar(style)
        MockTransportRow(style)
        Spacer(Modifier.weight(0.25f))
    }
}

@Composable
private fun LandscapeMockup(style: MockupStyle) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PlayerHorizontalPadding, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (style.showArtwork) {
            MockArtwork(style, isPortrait = false, modifier = Modifier.fillMaxHeight().aspectRatio(1f))
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MockTitleRow(style)
            MockSeekBar(style)
            MockTransportRow(style)
        }
    }
}

@Composable
private fun MockArtwork(style: MockupStyle, isPortrait: Boolean, modifier: Modifier = Modifier) {
    // Real player: APPLE_MUSIC draws full-bleed unclipped artwork in portrait, ignoring the
    // artwork-style shape entirely (Thumbnail.kt hides the shaped carousel in that case). Match
    // that here or the mockup shows a circle/clover for a look that never renders one for real.
    val shape = if (style.background == PlayerBackgroundStyle.APPLE_MUSIC && isPortrait) {
        RoundedCornerShape(0.dp)
    } else when (style.artwork) {
        PlayerArtworkStyle.CARD -> ContinuousRoundedRectangle(style.artworkCorner)
        PlayerArtworkStyle.VINYL -> CircleShape
        // The real clover is a four-petal squircle; a heavily rounded square reads the same at
        // mockup scale without duplicating the shape maths.
        PlayerArtworkStyle.CLOVER -> RoundedCornerShape(42)
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(Brush.linearGradient(listOf(Color(0xFF3A3A42), Color(0xFF1E1E24)))),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.convx_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(0.34f),
        )
        if (shape == CircleShape && style.artwork == PlayerArtworkStyle.VINYL) {
            Box(
                Modifier
                    .fillMaxSize(0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF0B0B0D)),
            )
        }
    }
}

@Composable
private fun MockTitleRow(style: MockupStyle) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.diy_mock_title),
                color = style.onBackdrop,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Text(
                text = stringResource(R.string.diy_mock_artist),
                color = style.onBackdrop.copy(alpha = 0.65f),
                fontSize = 15.sp,
            )
        }
        MockIconButton(PlayerIconSlot.LIKE, 26.dp, style)
        Spacer(Modifier.width(12.dp))
        MockIconButton(PlayerIconSlot.MORE, 26.dp, style)
    }
}

@Composable
private fun MockSeekBar(style: MockupStyle) {
    val progress = 0.38f
    val track = style.onBackdrop.copy(alpha = 0.22f)
    val filled = style.onBackdrop.copy(alpha = 0.85f)

    Column {
        Box(Modifier.fillMaxWidth().height(20.dp), contentAlignment = Alignment.CenterStart) {
            when (style.slider) {
                SliderStyle.WAVY -> MockSquiggle(progress, track, filled)
                SliderStyle.WAVEFORM -> MockWaveform(progress, track, filled)
                SliderStyle.SLIM, SliderStyle.DEFAULT -> {
                    val height = if (style.slider == SliderStyle.SLIM) 3.dp else 6.dp
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(height)
                            .clip(CircleShape)
                            .background(track),
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(filled),
                        )
                    }
                }
            }
            MockSeekThumb(progress, style)
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("1:24", color = style.onBackdrop.copy(alpha = 0.55f), fontSize = 11.sp)
            Text("-2:11", color = style.onBackdrop.copy(alpha = 0.55f), fontSize = 11.sp)
        }
    }
}

/** Only drawn once the user supplies an image — the stock design has no visible handle. */
@Composable
private fun MockSeekThumb(progress: Float, style: MockupStyle) {
    val thumb = rememberPlayerIcon(PlayerIconSlot.SEEK_THUMB)
    if (!thumb.isCustom) return
    Box(Modifier.fillMaxWidth(progress), contentAlignment = Alignment.CenterEnd) {
        Image(
            painter = thumb.painter,
            contentDescription = null,
            colorFilter = thumb.colorFilterFor(style.onBackdrop),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun MockSquiggle(progress: Float, track: Color, filled: Color) {
    Canvas(Modifier.fillMaxWidth().height(16.dp)) {
        val mid = size.height / 2f
        val split = size.width * progress
        val wave = Path()
        var x = 0f
        wave.moveTo(0f, mid)
        while (x <= split) {
            wave.lineTo(x, mid + sin(x / 9f).toFloat() * (size.height / 2.6f))
            x += 2f
        }
        drawPath(wave, filled, style = Stroke(width = 5f, cap = StrokeCap.Round))
        drawLine(track, Offset(split, mid), Offset(size.width, mid), 5f, StrokeCap.Round)
    }
}

@Composable
private fun MockWaveform(progress: Float, track: Color, filled: Color) {
    Canvas(Modifier.fillMaxWidth().height(18.dp)) {
        val bars = 48
        val gap = size.width / bars
        val split = size.width * progress
        repeat(bars) { i ->
            val x = i * gap + gap / 2f
            val amplitude = (0.35f + 0.65f * kotlin.math.abs(sin(i * 1.7f).toFloat()))
            val half = size.height / 2f * amplitude
            drawLine(
                color = if (x <= split) filled else track,
                start = Offset(x, size.height / 2f - half),
                end = Offset(x, size.height / 2f + half),
                strokeWidth = gap * 0.45f,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun MockTransportRow(style: MockupStyle) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MockIconButton(PlayerIconSlot.PREVIOUS, 32.dp, style)
        MockIconButton(PlayerIconSlot.PLAY, 46.dp, style)
        MockIconButton(PlayerIconSlot.NEXT, 32.dp, style)
    }
}

@Composable
private fun MockIconButton(slot: PlayerIconSlot, size: Dp, style: MockupStyle) {
    PlayerGlyph(
        slot = slot,
        fallback = slot.fallback,
        tint = style.controlTint,
        modifier = Modifier.size(size),
    )
}

/** Flattens a translucent accent onto black so the wash reads the same on any theme. */
private fun Color.flattenOnBlack(alpha: Float): Color =
    Color(red * alpha, green * alpha, blue * alpha, 1f)
