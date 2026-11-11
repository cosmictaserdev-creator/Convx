/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.screens.wrapped

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.convx.music.R
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.NumberFormat
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Apple Music Replay 2025, as a story (60fps.design apple-music-replay-2025-*):
 * segmented progress on top, "Replay'yy" and a close button, a speaker and a share button at the
 * foot, a slowly flowing two-colour gradient that morphs between pages, and big left-aligned
 * white type that rises in. Tap the right two thirds to advance, the left third to go back, hold
 * to pause. Pages auto-advance.
 *
 * Genre stack is not ported: nothing in the listening history records genre.
 */
private data class ReplayPage(
    val kind: Kind,
    val a: Color,
    val b: Color,
    val durationMs: Long,
    val audio: WrappedScreenType,
) {
    enum class Kind { Loading, Intro, LotOfMusic, Minutes, Songs, TopSong, ArtistBubbles, TopArtist, ArtistTable, AlbumOrbit, TopAlbum, Summary }
}

private val Orange = Color(0xFFFF7A00)
private val Lime = Color(0xFF3DD68C)
private val Magenta = Color(0xFFD81B60)
private val Pink = Color(0xFFFF4F8B)
private val Graphite = Color(0xFF3A3A3C)
private val Ink = Color(0xFF1C1C1E)
private val Cobalt = Color(0xFF1437D6)
private val Sky = Color(0xFF2F6BFF)
private val Yellow = Color(0xFFE8E23A)

/** iOS-style ease the Replay type rides in on. */
private val ReplayEase = CubicBezierEasing(0.2f, 0.9f, 0.25f, 1f)

@Composable
fun ReplayStory(
    state: WrappedState,
    manager: WrappedManager,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onPageAudio: (WrappedScreenType) -> Unit,
    onClose: () -> Unit,
    onShare: () -> Unit,
) {
    val pages = remember {
        listOf(
            ReplayPage(ReplayPage.Kind.Intro, Graphite, Ink, 4_500, WrappedScreenType.Welcome),
            ReplayPage(ReplayPage.Kind.LotOfMusic, Orange, Lime, 5_500, WrappedScreenType.MinutesTease),
            ReplayPage(ReplayPage.Kind.Minutes, Orange, Yellow, 7_000, WrappedScreenType.MinutesReveal),
            ReplayPage(ReplayPage.Kind.Songs, Magenta, Pink, 6_000, WrappedScreenType.TotalSongs),
            ReplayPage(ReplayPage.Kind.TopSong, Magenta, Pink, 7_000, WrappedScreenType.TopSongReveal),
            ReplayPage(ReplayPage.Kind.ArtistBubbles, Graphite, Ink, 6_500, WrappedScreenType.TotalArtists),
            ReplayPage(ReplayPage.Kind.TopArtist, Graphite, Ink, 6_500, WrappedScreenType.TopArtistReveal),
            ReplayPage(ReplayPage.Kind.ArtistTable, Ink, Graphite, 7_000, WrappedScreenType.Top5Artists),
            ReplayPage(ReplayPage.Kind.AlbumOrbit, Cobalt, Sky, 6_000, WrappedScreenType.TotalAlbums),
            ReplayPage(ReplayPage.Kind.TopAlbum, Cobalt, Sky, 7_000, WrappedScreenType.TopAlbumReveal),
            ReplayPage(ReplayPage.Kind.Summary, Orange, Magenta, 9_000, WrappedScreenType.Conclusion),
        )
    }
    var index by remember { mutableIntStateOf(0) }
    var paused by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }
    val ready = state.isDataReady
    val page = if (ready) pages[index] else ReplayPage(ReplayPage.Kind.Loading, Lime, Yellow, 0, WrappedScreenType.Welcome)

    LaunchedEffect(index, ready, state.trackMap) { if (ready) onPageAudio(pages[index].audio) }
    // Which page the progress bar belongs to: a pause keeps its fill, a page change resets it.
    // Tracked here rather than in a separate snap effect, which raced this one on index change.
    val progressPage = remember { intArrayOf(-1) }
    LaunchedEffect(index, paused, ready) {
        if (!ready) return@LaunchedEffect
        if (progressPage[0] != index) { progress.snapTo(0f); progressPage[0] = index }
        if (paused) return@LaunchedEffect
        val remaining = ((1f - progress.value) * page.durationMs).toInt().coerceAtLeast(1)
        progress.animateTo(1f, tween(remaining, easing = LinearEasing))
        if (index < pages.lastIndex) index++
    }
    val go: (Int) -> Unit = { next ->
        index = next.coerceIn(0, pages.lastIndex)
    }

    val colorA by animateColorAsState(page.a, tween(900), label = "replayA")
    val colorB by animateColorAsState(page.b, tween(900), label = "replayB")

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .flowingGradient(colorA, colorB)
            .pointerInput(ready) {
                if (!ready) return@pointerInput
                detectTapGestures(
                    onPress = {
                        paused = true
                        tryAwaitRelease()
                        paused = false
                    },
                    onTap = { offset ->
                        if (offset.x < size.width / 3f) go(index - 1) else go(index + 1)
                    },
                )
            },
    ) {
        AnimatedContent(
            targetState = page.kind,
            transitionSpec = { fadeIn(tween(420, delayMillis = 120)) togetherWith fadeOut(tween(260)) },
            label = "replayPage",
            modifier = Modifier.fillMaxSize(),
        ) { kind ->
            Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
                PageContent(kind, state, manager)
            }
        }
        ReplayChrome(
            count = pages.size,
            index = index,
            progress = { progress.value },
            isMuted = isMuted,
            onToggleMute = onToggleMute,
            onClose = onClose,
            onShare = onShare,
            showBars = ready,
        )
    }
}

@Composable
private fun BoxScope.PageContent(kind: ReplayPage.Kind, s: WrappedState, manager: WrappedManager) {
    val nf = remember { NumberFormat.getIntegerInstance() }
    when (kind) {
        ReplayPage.Kind.Loading -> LoadingPulse()
        ReplayPage.Kind.Intro -> IntroPage(s.topSongs.map { it.thumbnailUrl }.take(6))
        ReplayPage.Kind.LotOfMusic -> Headline("A lot of music\ncan happen in\na year.")
        ReplayPage.Kind.Minutes -> MinutesPage(s.totalMinutes, nf)
        ReplayPage.Kind.Songs -> SongsGridPage(s.uniqueSongCount, s.topSongs.map { it.thumbnailUrl }, nf)
        ReplayPage.Kind.TopSong -> HeroPage(
            label = "Top Song",
            title = s.topSongs.firstOrNull()?.title ?: "—",
            subtitle = s.topSongs.firstOrNull()?.artistName.orEmpty(),
            detail = "${nf.format(s.topSongs.firstOrNull()?.songCountListened ?: 0)} Plays",
            art = s.topSongs.firstOrNull()?.thumbnailUrl,
            circle = false,
        )
        ReplayPage.Kind.ArtistBubbles -> ArtistBubblesPage(s.topArtists.map { it.artist.thumbnailUrl })
        ReplayPage.Kind.TopArtist -> HeroPage(
            label = "Top Artist",
            title = s.topArtists.firstOrNull()?.artist?.name ?: "—",
            subtitle = null,
            detail = "${nf.format((s.topArtists.firstOrNull()?.timeListened ?: 0) / 60000)} Minutes",
            art = s.topArtists.firstOrNull()?.artist?.thumbnailUrl,
            circle = true,
        )
        ReplayPage.Kind.ArtistTable -> ArtistTablePage(s)
        ReplayPage.Kind.AlbumOrbit -> AlbumOrbitPage(s.top5Albums.map { it.thumbnailUrl } + s.topSongs.map { it.thumbnailUrl }.take(6))
        ReplayPage.Kind.TopAlbum -> HeroPage(
            label = "Top Album",
            title = s.topAlbum?.album?.title ?: "—",
            subtitle = s.topAlbum?.artists?.firstOrNull()?.name,
            detail = "${nf.format((s.topAlbum?.timeListened ?: 0L) / 60000)} Minutes",
            art = s.topAlbum?.thumbnailUrl,
            circle = false,
        )
        ReplayPage.Kind.Summary -> SummaryPage(s, nf, manager)
    }
}

// Chrome

@Composable
private fun ReplayChrome(
    count: Int,
    index: Int,
    progress: () -> Float,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onClose: () -> Unit,
    onShare: () -> Unit,
    showBars: Boolean,
) {
    val year = remember { (Calendar.getInstance().get(Calendar.YEAR) % 100).toString().padStart(2, '0') }
    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars).padding(horizontal = 14.dp)) {
        Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBars) {
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(count) { i ->
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(2.5.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f))
                                    .drawBehind {
                                        val f = when {
                                            i < index -> 1f
                                            i == index -> progress()
                                            else -> 0f
                                        }
                                        drawRect(Color.White, size = size.copy(width = size.width * f))
                                    },
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.width(10.dp))
                ChromeCircle(onClose) {
                    SymbolIcon(symbol = sf("xmark"), contentDescription = "Close", size = 16.dp, tint = Color.White)
                }
            }
            if (showBars) {
                Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                    Text("Replay’$year", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Text("Music", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        if (showBars) {
            Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 10.dp)) {
                ChromeCircle(onToggleMute) {
                    SymbolIcon(symbol = if (isMuted) sf("speaker.slash.fill") else sf("speaker.wave.3.fill"), contentDescription = "Mute", size = 16.dp, tint = Color.White)
                }
                Spacer(Modifier.weight(1f))
                ChromeCircle(onShare) {
                    SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = "Share", size = 16.dp, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ChromeCircle(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier.size(30.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.22f)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { content() }
}

// Background

/**
 * Two soft colour fields drifting past each other, Replay's summary-background look. Plain radial
 * gradients moved in draw -- no shader, no offscreen layer.
 */
@Composable
private fun Modifier.flowingGradient(a: Color, b: Color): Modifier {
    val t by rememberInfiniteTransition(label = "replayFlow").animateFloat(
        0f, 1f, infiniteRepeatable(tween(14_000, easing = LinearEasing), RepeatMode.Restart), label = "flow",
    )
    return drawBehind {
        drawRect(a)
        val angle = t * 2f * Math.PI.toFloat()
        val c1 = Offset(size.width * (0.5f + 0.45f * cos(angle)), size.height * (0.62f + 0.25f * sin(angle)))
        val c2 = Offset(size.width * (0.5f - 0.4f * cos(angle * 1.3f)), size.height * (0.3f - 0.2f * sin(angle * 0.7f)))
        drawRect(Brush.radialGradient(listOf(b, b.copy(alpha = 0f)), center = c1, radius = size.maxDimension * 0.55f))
        drawRect(Brush.radialGradient(listOf(lerp(a, b, 0.5f), Color.Transparent), center = c2, radius = size.maxDimension * 0.45f))
    }
}

// Type

private val HeadlineStyle = TextStyle(color = Color.White, fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold)

/** Text rises ~28dp and fades in on the Replay ease. */
@Composable
private fun Rise(delayMs: Int = 0, content: @Composable () -> Unit) {
    val p = remember { Animatable(0f) }
    LaunchedEffect(Unit) { delay(delayMs.toLong()); p.animateTo(1f, tween(650, easing = ReplayEase)) }
    Box(Modifier.graphicsLayer { alpha = p.value; translationY = (1f - p.value) * 28.dp.toPx() }) { content() }
}

@Composable
private fun BoxScope.Headline(text: String, delayMs: Int = 150) {
    Box(Modifier.align(Alignment.TopStart).padding(start = 20.dp, end = 20.dp, top = 72.dp)) {
        Rise(delayMs) { Text(text, style = HeadlineStyle) }
    }
}

@Composable
private fun BoxScope.LoadingPulse() {
    val a by rememberInfiniteTransition(label = "pulse").animateFloat(
        0.35f, 1f, infiniteRepeatable(tween(1_100, easing = LinearEasing), RepeatMode.Reverse), label = "a",
    )
    Text("This is your Replay.", style = HeadlineStyle.copy(fontSize = 24.sp), modifier = Modifier.align(Alignment.Center).graphicsLayer { alpha = a })
}

// Pages

@Composable
private fun BoxScope.IntroPage(arts: List<String>) {
    // Blurred artwork cycling behind a big "Replay'yy" (text-album-transitions opening).
    var i by remember { mutableIntStateOf(0) }
    LaunchedEffect(arts) { while (isActive && arts.size > 1) { delay(600); i = (i + 1) % arts.size } }
    if (arts.isNotEmpty()) {
        AnimatedContent(arts[i], transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) }, label = "introArt") { url ->
            AsyncImage(
                model = url, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer { renderEffect = BlurEffect(40f, 40f); alpha = 0.85f },
            )
        }
    }
    val year = remember { (Calendar.getInstance().get(Calendar.YEAR) % 100).toString().padStart(2, '0') }
    Box(Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 110.dp)) {
        Rise(200) { Text("Replay’$year", style = HeadlineStyle.copy(fontSize = 52.sp)) }
    }
}

@Composable
private fun BoxScope.MinutesPage(minutes: Long, nf: NumberFormat) {
    Headline("This year you\nlistened for\n${nf.format(minutes)} minutes.")
    // The giant number slides across from the right and off the left (text-album-transitions).
    BoxWithConstraints(Modifier.fillMaxWidth().align(Alignment.Center).padding(top = 120.dp)) {
        val w = constraints.maxWidth.toFloat()
        val x = remember { Animatable(1f) }
        LaunchedEffect(Unit) {
            delay(900)
            x.animateTo(0f, tween(900, easing = ReplayEase))
            delay(1_400)
            x.animateTo(-1.6f, tween(1_600, easing = CubicBezierEasing(0.5f, 0f, 0.8f, 0.6f)))
        }
        Text(
            nf.format(minutes),
            style = HeadlineStyle.copy(fontSize = 110.sp, lineHeight = 110.sp, fontWeight = FontWeight.Black),
            softWrap = false,
            modifier = Modifier.offset { IntOffset((x.value * w).roundToInt(), 0) }.padding(start = 16.dp),
        )
    }
}

@Composable
private fun BoxScope.SongsGridPage(count: Int, arts: List<String>, nf: NumberFormat) {
    Headline("${nf.format(count)} songs\nmade up your\nsoundtrack.")
    // Rows of covers drifting sideways in opposite directions (top-song-zoom opening).
    Column(Modifier.align(Alignment.BottomStart).padding(bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val drift by rememberInfiniteTransition(label = "rows").animateFloat(
            0f, 1f, infiniteRepeatable(tween(9_000, easing = LinearEasing)), label = "d",
        )
        repeat(3) { row ->
            val dir = if (row % 2 == 0) -1f else 1f
            Row(
                Modifier.graphicsLayer { translationX = dir * drift * 240.dp.toPx() - if (dir > 0) 240.dp.toPx() else 0f },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(8) { col ->
                    val url = arts.getOrNull((row * 3 + col) % arts.size.coerceAtLeast(1))
                    Rise(100 + row * 120 + col * 40) {
                        AsyncImage(url, null, contentScale = ContentScale.Crop, modifier = Modifier.size(96.dp).clip(RoundedCornerShape(6.dp)))
                    }
                }
            }
        }
    }
}

/** Top song / artist / album: art zooms up from small into a hero, title set big beneath. */
@Composable
private fun BoxScope.HeroPage(label: String, title: String, subtitle: String?, detail: String, art: String?, circle: Boolean) {
    val zoom = remember { Animatable(0.35f) }
    LaunchedEffect(Unit) { zoom.animateTo(1f, spring(dampingRatio = 0.85f, stiffness = 120f)) }
    Column(Modifier.align(Alignment.TopStart).padding(start = 20.dp, end = 20.dp, top = 64.dp)) {
        Rise(250) {
            Column {
                Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(title, style = HeadlineStyle.copy(fontSize = 40.sp, lineHeight = 44.sp), maxLines = 2)
                subtitle?.let { Text(it, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium) }
                Text(detail, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
        }
    }
    AsyncImage(
        art, null, contentScale = ContentScale.Crop,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 90.dp)
            .size(280.dp)
            .graphicsLayer { scaleX = zoom.value; scaleY = zoom.value; alpha = zoom.value.coerceIn(0f, 1f) }
            .clip(if (circle) CircleShape else RoundedCornerShape(8.dp)),
    )
}

@Composable
private fun BoxScope.ArtistBubblesPage(arts: List<String?>) {
    Headline("A new artist\nrose to the top.")
    // Bubbles float up from below at staggered sizes, then scatter (artist-bubble-animation).
    val spots = remember {
        listOf(0.18f to 0.55f, 0.72f to 0.5f, 0.45f to 0.68f, 0.2f to 0.82f, 0.78f to 0.78f, 0.55f to 0.9f)
    }
    val sizes = listOf(110, 90, 150, 80, 120, 70)
    BoxWithConstraints(Modifier.fillMaxSize()) {
        spots.forEachIndexed { i, (fx, fy) ->
            val url = arts.getOrNull(i % arts.size.coerceAtLeast(1))
            val rise = remember { Animatable(0f) }
            LaunchedEffect(Unit) { delay(150L + i * 110); rise.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 90f)) }
            val bob by rememberInfiniteTransition(label = "bob$i").animateFloat(
                -1f, 1f, infiniteRepeatable(tween(2_400 + i * 300, easing = LinearEasing), RepeatMode.Reverse), label = "b",
            )
            val d = sizes[i].dp
            AsyncImage(
                url, null, contentScale = ContentScale.Crop,
                modifier = Modifier
                    .offset(maxWidth * fx - d / 2, maxHeight * fy - d / 2)
                    .size(d)
                    .graphicsLayer {
                        translationY = (1f - rise.value) * 500.dp.toPx() + bob * 6.dp.toPx()
                        alpha = rise.value.coerceIn(0f, 1f)
                    }
                    .clip(CircleShape),
            )
        }
    }
}

@Composable
private fun BoxScope.ArtistTablePage(s: WrappedState) {
    // Avatars settle into a ranked table (top-artist-table-animation).
    Column(Modifier.align(Alignment.TopStart).padding(start = 20.dp, end = 20.dp, top = 64.dp)) {
        Rise(100) { Text("Top Artists", style = HeadlineStyle) }
        Spacer(Modifier.height(18.dp))
        s.topArtists.take(5).forEachIndexed { i, a ->
            Rise(350 + i * 140) {
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${i + 1}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                    AsyncImage(a.artist.thumbnailUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.size(56.dp).clip(CircleShape))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(a.artist.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        Text("${(a.timeListened ?: 0) / 60000} Minutes", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.AlbumOrbitPage(arts: List<String?>) {
    // Covers burst outward onto a circle and orbit it (top-album-circular-path-animation).
    val burst = remember { Animatable(0f) }
    LaunchedEffect(Unit) { delay(200); burst.animateTo(1f, spring(dampingRatio = 0.75f, stiffness = 70f)) }
    val spin by rememberInfiniteTransition(label = "orbit").animateFloat(
        0f, 360f, infiniteRepeatable(tween(24_000, easing = LinearEasing)), label = "s",
    )
    val n = arts.size.coerceIn(1, 10)
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val radius = maxWidth * 0.36f
        repeat(n) { i ->
            val a = Math.toRadians((spin + i * 360.0 / n))
            AsyncImage(
                arts.getOrNull(i), null, contentScale = ContentScale.Crop,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = (cos(a) * radius.toPx() * burst.value).toFloat()
                        translationY = (sin(a) * radius.toPx() * burst.value).toFloat()
                        scaleX = 0.4f + 0.6f * burst.value; scaleY = scaleX
                    }
                    .size(92.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
        }
    }
    Headline("You kept coming\nback to these.")
}

@Composable
private fun BoxScope.SummaryPage(s: WrappedState, nf: NumberFormat, manager: WrappedManager) {
    Column(Modifier.align(Alignment.TopStart).padding(start = 20.dp, end = 20.dp, top = 64.dp)) {
        Rise(100) { Text("Your Replay", style = HeadlineStyle) }
        Spacer(Modifier.height(20.dp))
        val rows = listOf(
            "Minutes" to nf.format(s.totalMinutes),
            "Songs" to nf.format(s.uniqueSongCount),
            "Artists" to nf.format(s.uniqueArtistCount),
            "Albums" to nf.format(s.totalAlbums),
        )
        rows.forEachIndexed { i, (k, v) ->
            Rise(300 + i * 120) {
                Column(Modifier.padding(vertical = 6.dp)) {
                    Text(k, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(v, style = HeadlineStyle.copy(fontSize = 40.sp))
                }
            }
        }
    }
    val creation = s.playlistCreationState
    val imageName = remember { if (kotlin.random.Random.nextBoolean()) "convx_wrapped_v1" else "convx_wrapped_v2" }
    Box(
        Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 210.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(enabled = creation == PlaylistCreationState.Idle) { manager.createPlaylist(imageName) }
            .padding(horizontal = 22.dp, vertical = 12.dp),
    ) {
        Text(
            when (creation) {
                is PlaylistCreationState.Idle -> "Add Replay Playlist"
                is PlaylistCreationState.Creating -> "Adding…"
                is PlaylistCreationState.Success -> "Added to Library"
            },
            color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
        )
    }
    // Drifting collage of top covers along the bottom.
    Row(
        Modifier.align(Alignment.BottomStart).padding(bottom = 70.dp),
        horizontalArrangement = Arrangement.spacedBy((-18).dp),
    ) {
        s.topSongs.take(5).forEachIndexed { i, song ->
            Rise(900 + i * 110) {
                AsyncImage(
                    song.thumbnailUrl, null, contentScale = ContentScale.Crop,
                    modifier = Modifier.size(110.dp).graphicsLayer { rotationZ = (i - 2) * 6f }.clip(RoundedCornerShape(8.dp)),
                )
            }
        }
    }
}
