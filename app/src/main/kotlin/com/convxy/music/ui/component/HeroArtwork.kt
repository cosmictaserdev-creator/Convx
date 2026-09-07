package com.convxy.music.ui.component

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.convxy.music.R
import com.convxy.music.constants.DynamicThemeKey
import com.convxy.music.constants.PureBlackHeroBackgroundKey
import com.convxy.music.constants.SelectedThemeColorKey
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.DefaultThemeColor
import com.convxy.music.ui.theme.LocalAccentColor
import com.convxy.music.ui.theme.extractThemeColor
import com.convxy.music.utils.rememberPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Resolved hero image source with priority chain:
 * existing static artwork → animated/canvas artwork → first song artwork →
 * next song artwork → default music image as last resort.
 */
sealed class HeroSource {
    data class Artwork(val url: String, val isAnimated: Boolean = false) : HeroSource()
    data object Default : HeroSource()
}

/** Blur radius for [HeroBackground]'s Apple-Music-style blurred artwork. */
private val HeroBlurRadius = 48.dp

/**
 * Hero tints keyed by artwork URL (the image's stable id): the Coil decode +
 * palette extraction is expensive, so the same artwork never recomputes on
 * revisit or recomposition. Bounded for a long session.
 */
private val heroTintCache = android.util.LruCache<String, Color>(128)

/**
 * 0..1 scroll progress for [HeroBackground]'s `topBlurProgress`: ramps as the first
 * screenful scrolls up, then pins at 1. Pass a hero screen's list state.
 */
@Composable
fun rememberHeroTopBlur(
    state: androidx.compose.foundation.lazy.LazyListState,
    // Blur reaches full strength once the hero header has scrolled this fraction
    // of its OWN height. Deriving the span from the real header size (instead of a
    // fixed px threshold) keeps the ramp continuous through the moment the header
    // scrolls off — a fixed 700px threshold snapped when it didn't match the
    // header's actual height.
    fraction: Float = 0.6f,
): Float {
    val progress by remember(state) {
        androidx.compose.runtime.derivedStateOf {
            if (state.firstVisibleItemIndex > 0) return@derivedStateOf 1f
            val header = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == 0 }
            val span = (header?.size ?: 0) * fraction
            if (span <= 0f) 0f
            else (state.firstVisibleItemScrollOffset / span).coerceIn(0f, 1f)
        }
    }
    return progress
}

/**
 * Resolves the hero image per the priority chain.
 *
 * @param staticArt Primary artwork URL (e.g. album/playlist/artist art)
 * @param animatedArt Optional animated/canvas artwork URL
 * @param songs List of songs to fall back to for artwork
 */
@Composable
fun rememberHeroSource(
    staticArt: String?,
    animatedArt: String? = null,
    songs: List<Pair<String?, Boolean>> = emptyList(), // (thumbnailUrl, isAnimated)
): HeroSource {
    return remember(staticArt, animatedArt, songs) {
        when {
            !staticArt.isNullOrBlank() -> HeroSource.Artwork(staticArt, isAnimated = false)
            !animatedArt.isNullOrBlank() -> HeroSource.Artwork(animatedArt, isAnimated = true)
            else -> {
                val firstSongArt = songs.firstOrNull { !it.first.isNullOrBlank() }
                if (firstSongArt != null) {
                    HeroSource.Artwork(firstSongArt.first!!, isAnimated = firstSongArt.second)
                } else {
                    HeroSource.Default
                }
            }
        }
    }
}

/**
 * Extracts the dominant tint color from a hero artwork URL.
 * Returns [AppleTokens.AccentRed] as fallback until extraction completes.
 */
/**
 * Clamps an extracted artwork color into a pleasant deep background tint:
 * caps lightness (pale/skin/whitish colors become rich, legible) and floors
 * saturation (avoids washed-out grey). Keeps the hue.
 */
fun Color.asDeepTint(): Color {
    val hsl = FloatArray(3)
    androidx.core.graphics.ColorUtils.colorToHSL(toArgb(), hsl)
    hsl[1] = hsl[1].coerceAtLeast(0.28f)   // floor saturation
    hsl[2] = hsl[2].coerceIn(0.14f, 0.34f) // cap lightness → deep, not pale
    return Color(androidx.core.graphics.ColorUtils.HSLToColor(hsl))
}

/**
 * The artwork tint for [url], from cache when it is there and by extraction when it is not.
 *
 * Cheap on a repeat call even when the cache misses: the 100x100 decode Coil does here
 * lands in its own memory cache, so a second caller pays a lookup rather than a fetch.
 */
suspend fun heroTintOf(context: Context, url: String): Color? {
    heroTintCache.get(url)?.let { return it }
    return withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .size(100, 100)
                .allowHardware(false)
                .build()
            context.imageLoader.execute(request).image?.toBitmap()
                ?.extractThemeColor()?.asDeepTint()
                // Not cached on a miss: a later visit with a working network should still
                // get the real artwork colour rather than a stand-in pinned forever.
                ?.also { heroTintCache.put(url, it) }
        } catch (_: Exception) {
            null
        }
    }
}

/**
 * Starts extracting [url]'s tint now, off any composition.
 *
 * Called as navigation into a hero screen begins, so the colour is already in the cache by
 * the time that screen composes -- otherwise the extraction starts on arrival and the
 * background visibly washes in a beat after the artwork.
 */
fun prewarmHeroTint(context: Context, url: String?) {
    if (url.isNullOrEmpty() || heroTintCache.get(url) != null) return
    val appContext = context.applicationContext
    heroTintScope.launch { heroTintOf(appContext, url) }
}

private val heroTintScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

@Composable
fun rememberHeroTint(url: String?): Color {
    val context = LocalContext.current

    // Where a failed extraction lands. The doc above claimed the fallback was
    // AccentRed, but nothing ever assigned it: black is the *pending* value, and
    // every failure path below simply left it there — a device that couldn't fetch
    // the artwork (offline, 403, decode failure, image evicted under memory
    // pressure) got a permanently black hero plane instead of a colour, which is
    // the "generated colour falls to black on some phones" report. Resolves to the
    // user's own theme colour now, so a miss degrades to their accent, not to black.
    val fallbackTint = LocalAccentColor.current.asDeepTint()

    // Black until the artwork color is extracted (no accent flash); seeded from the
    // cache so a revisited image shows its tint at once.
    var tint by remember(url) { mutableStateOf(url?.let { heroTintCache.get(it) } ?: Color.Black) }

    LaunchedEffect(url) {
        // No early return on a cache hit. Extraction can already be in flight from
        // prewarmHeroTint, and bailing out because "someone is handling it" left the
        // tint stuck on its pending black forever.
        tint = (url?.let { heroTintOf(context, it) }) ?: fallbackTint
    }

    val animatedTint = tint
    // Root-cause fix, not per-screen: every hero-tinted screen (Artist, Album,
    // Playlist, Search, ...) reads its background straight off this value, but
    // only HeroBackground's own separate PureBlackHeroBackgroundKey check
    // honored the setting — every screen that composes its own tint plane
    // instead of going through HeroBackground silently ignored it. Applying the
    // override here fixes every caller at once instead of repeating this check
    // in 17 files.
    val (pureBlack) = rememberPreference(PureBlackHeroBackgroundKey, false)
    return if (pureBlack) Color.Black else animatedTint
}

/**
 * The screen background tint actually used — [contentTint] (per-screen, derived
 * from artwork/thumbnails) when the user is on system/dynamic color, or their
 * own picked color (Theme settings) as a flat override when they're not. Ties
 * "custom theme color" directly to what screens paint behind their content,
 * not just Material's accent colors.
 */
@Composable
fun rememberAppBackgroundTint(contentTint: Color): Color {
    val (isDynamic) = rememberPreference(DynamicThemeKey, defaultValue = true)
    val (selectedColorInt) = rememberPreference(SelectedThemeColorKey, defaultValue = DefaultThemeColor.toArgb())
    return if (isDynamic) contentTint else Color(selectedColorInt)
}

/**
 * Album-screen style hero header: a full-width square artwork that fades
 * (DstIn vertical gradient) into whatever tint plane sits behind it, so the
 * image dissolves into the color. Meant to be the FIRST item of a LazyColumn
 * whose parent paints [com.convxy.music.ui.theme.AppleTokens] tint behind it.
 *
 * Falls back to the default music image when [artworkUrl] is null/blank.
 */
@Composable
fun AlbumStyleHeroImage(
    artworkUrl: String?,
    modifier: Modifier = Modifier,
    // Extra pull-up beyond the status bar, to lift the square higher like AlbumScreen.
    extraPullUp: androidx.compose.ui.unit.Dp = 48.dp,
    // Driven by rememberHeroZoom() for pull-to-zoom. Applied to the drawn image
    // only, so growing it never re-lays-out the list underneath.
    heroScale: Float = 1f,
) {
    val topInset = androidx.compose.foundation.layout.WindowInsets.systemBars
        .asPaddingValues().calculateTopPadding()
    val density = LocalDensity.current
    val pullUpPx = with(density) { (topInset + extraPullUp).roundToPx() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            // Report a shorter height and draw shifted up: the image top tucks
            // under the status bar and following content sits tight (no gap).
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val h = (placeable.height - pullUpPx).coerceAtLeast(0)
                layout(placeable.width, h) {
                    placeable.place(0, -pullUpPx)
                }
            }
            .graphicsLayer {
                scaleX = heroScale
                scaleY = heroScale
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, Color.Transparent),
                        startY = size.height * 0.45f,
                        endY = size.height,
                    ),
                    blendMode = BlendMode.DstIn,
                )
            },
    ) {
        if (!artworkUrl.isNullOrBlank()) {
            AsyncImage(
                model = artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Image(
                painter = painterResource(R.drawable.music_note),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize(0.4f)
                    .align(Alignment.Center)
                    .graphicsLayer { alpha = 0.3f },
            )
        }
    }
}

/**
 * Full-bleed hero background: flat tint plane with the hero image on top,
 * fading vertically into the tint at its bottom edge.
 *
 * NO gradient scrim — the image dissolves into the color plane.
 * Adaptive contrast: descendants should use [AppleTokens.onColor] for text.
 */
@Composable
fun HeroBackground(
    tint: Color,
    heroSource: HeroSource,
    modifier: Modifier = Modifier,
    // Apple Music browse/player style: full-bleed heavily-blurred artwork with a
    // darkening scrim, instead of the default sharp top-hero fading to tint.
    blurArtwork: Boolean = false,
    // With [blurArtwork], drop the crisp upper layer so the whole background is
    // uniformly blurred (no sharp-top/blurred-bottom split).
    fullBlur: Boolean = false,
    // The [HeroSource.Default] placeholder music-note. Off for screens that want
    // a clean flat tint behind glass (e.g. search).
    showDefaultIcon: Boolean = true,
    // Decorative primary-color wash fading in at the bottom of the screen.
    bottomGradient: Boolean = false,
    // 0..1 — extra blur ramped onto the crisp upper artwork as the list scrolls up.
    topBlurProgress: Float = 0f,
    // Pull-to-zoom scale for the hero artwork (1 = rest; >1 while overscrolling the top).
    heroScale: Float = 1f,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val (pureBlack) = rememberPreference(PureBlackHeroBackgroundKey, false)
    // Reuses the existing Default branch (flat tint, no image) to suppress the
    // artwork backdrop entirely rather than adding a second code path.
    val effectiveTint = if (pureBlack) Color.Black else tint
    val effectiveHeroSource = if (pureBlack) HeroSource.Default else heroSource
    Box(modifier = modifier.background(effectiveTint)) {
        when (val heroSource = effectiveHeroSource) {
            is HeroSource.Artwork -> {
                // Fade-in animation for the hero image (the "smooth transition"
                // as the artwork resolves).
                var visible by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(durationMillis = 600),
                    label = "heroFadeIn",
                )

                if (blurArtwork) {
                    // Blurred lower layer: fills behind the list and dissolves
                    // into the tint (the primary-color gradient) toward the
                    // bottom. Decoded at low resolution — a 48dp blur erases all
                    // detail, so a ~160px source is visually identical to full-res
                    // while cutting decode time and bitmap memory dramatically.
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(heroSource.url)
                            .size(160)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(HeroBlurRadius)
                            .graphicsLayer {
                                this.alpha = alpha
                                scaleX = heroScale
                                scaleY = heroScale
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Black, Color.Transparent),
                                        startY = size.height * 0.5f,
                                        endY = size.height,
                                    ),
                                    blendMode = BlendMode.DstIn,
                                )
                            },
                        onSuccess = { visible = true },
                    )
                    // Sharp upper layer: the top half stays crisp, then dissolves
                    // so the blur takes over from roughly where the list begins.
                    // Skipped entirely in fullBlur mode (uniformly blurred background).
                    if (!fullBlur) {
                    AsyncImage(
                        model = heroSource.url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                // Cross-fade to the pre-blurred layer underneath as you
                                // scroll — no per-frame RenderEffect, so it stays smooth.
                                this.alpha = alpha * (1f - topBlurProgress.coerceIn(0f, 1f))
                                scaleX = heroScale
                                scaleY = heroScale
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Black, Color.Transparent),
                                        startY = size.height * 0.35f,
                                        endY = size.height * 0.6f,
                                    ),
                                    blendMode = BlendMode.DstIn,
                                )
                            },
                    )
                    }
                } else {
                    AsyncImage(
                        model = heroSource.url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                this.alpha = alpha
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Black, Color.Transparent),
                                        startY = size.height * 0.4f,
                                        endY = size.height
                                    ),
                                    blendMode = BlendMode.DstIn
                                )
                            },
                        onSuccess = { visible = true },
                    )
                }
            }
            is HeroSource.Default -> {
                // No placeholder icon: a clean flat tint plane behind the content.
            }
        }
        if (bottomGradient) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.55f to Color.Transparent,
                            1f to primaryColor.copy(alpha = 0.55f),
                        ),
                    ),
            )
        }
        content()
    }
}

/** Side of the hero card in tab view. Matches the reference's ~1/4-width square. */
val HeroCardSize: Dp = 200.dp

/**
 * Top clearance the wide hero reserves for the floating Back button, which lives
 * at the top-left of the content area in tab view and would otherwise sit over
 * the artwork. One number — raise it if the button still crowds the card.
 */
val HeroBackButtonClearance: Dp = 56.dp

/**
 * Wide-layout hero: the artwork as a card with the title beside it.
 *
 * The phone hero is a full-bleed square with the title overlaid at its bottom —
 * on a wide layout that wastes the whole right half of the screen and pushes the
 * actual content below the fold. The reference instead makes the artwork a
 * bounded card, sets the title, subtitle and actions in a column next to it, and
 * starts the list directly underneath.
 *
 * Screens choose between this and their phone header on [LocalTabView].
 */
@Composable
fun HeroCardHeader(
    artworkUrl: String?,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    cardSize: Dp = HeroCardSize,
    // Artist pages are circular in the reference; albums and playlists are square.
    circular: Boolean = false,
    subtitle: (@Composable () -> Unit)? = null,
    actions: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val shape = if (circular) {
        ContinuousRoundedRectangle(percent = 50)
    } else {
        ContinuousRoundedRectangle(16.dp)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            // Top clearance drops the card below the floating Back button.
            .padding(start = 24.dp, end = 24.dp, top = HeroBackButtonClearance, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(cardSize)
                .shadow(elevation = 12.dp, shape = shape)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            if (!artworkUrl.isNullOrBlank()) {
                AsyncImage(
                    model = artworkUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.music_note),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize(0.4f)
                        .align(Alignment.Center)
                        .graphicsLayer { alpha = 0.3f },
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            title()
            subtitle?.invoke()
            actions?.invoke(this)
        }
    }
}

/**
 * Convenience overload for the common "big title, quieter subtitle line" pair,
 * so each screen doesn't restate the same two Text styles.
 */
@Composable
fun HeroCardHeader(
    artworkUrl: String?,
    titleText: String,
    modifier: Modifier = Modifier,
    subtitleText: String? = null,
    cardSize: Dp = HeroCardSize,
    circular: Boolean = false,
    actions: (@Composable ColumnScope.() -> Unit)? = null,
) {
    HeroCardHeader(
        artworkUrl = artworkUrl,
        modifier = modifier,
        cardSize = cardSize,
        circular = circular,
        title = {
            Text(
                text = titleText,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = subtitleText?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        actions = actions,
    )
}
