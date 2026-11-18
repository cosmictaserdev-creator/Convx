package com.convx.music.wear

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.playback.next
import com.convx.music.playback.watch.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WearActivity : ComponentActivity() {
    private val link by lazy { (application as WearApplication).graph.koin.get<WearPhoneLink>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.post { window.insetsController?.hide(android.view.WindowInsets.Type.systemBars()) }
        setContent {
            val state by link.state.collectAsState()
            WearScreen(state, link)
        }
    }

    override fun onStart() { super.onStart(); link.start() }
    override fun onStop() { link.stop(); super.onStop() }
}

private val Accent = Color(0xFFFA435C)
private val Muted = Color(0xFF9C9CA6)
private val Surface = Color(0xFF202127)

@Composable
internal fun WearScreen(state: WearLinkState, link: WearPhoneLink, initialLyrics: Boolean = false) {
    var showLyrics by rememberSaveable { mutableStateOf(initialLyrics) }
    BackHandler(enabled = showLyrics) { showLyrics = false }
    var now by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(state.connected, state.snapshot?.isPlaying) {
        while (state.connected && state.snapshot?.isPlaying == true) {
            now = SystemClock.elapsedRealtime()
            delay(100)
        }
    }
    val snapshot = state.snapshot
    val position = snapshot?.positionAfter(if (state.connected) now - state.receivedAtMs else 0) ?: 0
    val list = rememberLazyListState()
    val focus = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(showLyrics) {
        focus.requestFocus()
        if (!showLyrics) list.scrollToItem(0)
    }
    val active = snapshot?.activeLyric(position) ?: -1
    LaunchedEffect(showLyrics, active, snapshot?.mediaId, snapshot?.lyrics?.firstOrNull()?.timeMs) {
        if (showLyrics && active >= 0) list.scrollToItem((active + 1).coerceAtLeast(0))
    }
    LazyColumn(
        state = list,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize().background(Color(0xFF08090C))
            .onRotaryScrollEvent { event -> scope.launch { list.scrollBy(event.verticalScrollPixels) }; true }
            .focusRequester(focus).focusable(),
    ) {
        if (snapshot?.mediaId == null) item {
            Label(stringResource(R.string.app_name), color = Accent, size = 13)
        }
        if (!state.connected) item {
            Label(stringResource(if (state.connecting) R.string.connecting else R.string.disconnected), size = 13, color = Muted)
            if (!state.connecting) ActionPill(stringResource(R.string.retry), onClick = link::retry)
        }
        if (snapshot?.mediaId == null) {
            item {
                Label(stringResource(R.string.nothing_playing), size = 21)
                Spacer(Modifier.height(12.dp))
                Label(stringResource(if (state.connected) R.string.open_phone else R.string.phone_unavailable), size = 14, color = Muted)
            }
        } else {
            if (showLyrics) {
                item { ActionPill(stringResource(R.string.player)) { showLyrics = false } }
                if (snapshot.lyrics.isEmpty()) item { Label(stringResource(R.string.no_lyrics), color = Muted, size = 15) }
                itemsIndexed(snapshot.lyrics, key = { index, line -> "${line.timeMs}:${line.background}:$index" }) { index, line ->
                    val text = buildAnnotatedString {
                        if (line.words.isEmpty()) append(line.text)
                        else line.words.forEachIndexed { wordIndex, word ->
                            if (wordIndex > 0) append(' ')
                            val start = length
                            append(word.text)
                            addStyle(SpanStyle(color = if (position + snapshot.lyricsOffsetMs >= word.startMs) Color.White else Muted), start, length)
                        }
                    }
                    BasicText(text, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        style = TextStyle(color = if (index == active) Color.White else Muted,
                            fontSize = (if (line.background) 15 else 20).sp,
                            fontWeight = if (index == active) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center))
                }
            } else {
                item {
                    Label(snapshot.title, size = 20, maxLines = 2)
                    Label(snapshot.artist, color = Muted, size = 13, maxLines = 1)
                }
                item {
                    val progress = snapshot.durationMs?.takeIf { it > 0 }?.let { position.toFloat() / it } ?: 0f
                    Canvas(Modifier.fillMaxWidth().height(6.dp)) {
                        drawRoundRect(Surface, cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()))
                        drawRoundRect(Accent, size = Size(size.width * progress.coerceIn(0f, 1f), size.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()))
                    }
                    Label(time(position), size = 12, color = Muted)
                }
                item {
                    val enabled = state.connected && snapshot.canControl
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        TransportButton(stringResource(R.string.previous), enabled && snapshot.canSkipPrevious, "previous") { link.send(WearAction.PREVIOUS) }
                        TransportButton(stringResource(if (snapshot.isPlaying) R.string.pause else R.string.play), enabled,
                            if (snapshot.isPlaying) "pause" else "play", primary = true) {
                            link.send(if (snapshot.isPlaying) WearAction.PAUSE else WearAction.PLAY)
                        }
                        TransportButton(stringResource(R.string.next), enabled && snapshot.canSkipNext, "next") { link.send(WearAction.NEXT) }
                    }
                }
                item { ActionPill(stringResource(R.string.lyrics)) { showLyrics = true } }
                if (!snapshot.canControl) item { Label(stringResource(R.string.room_controls_locked), color = Muted, size = 13) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionPill(stringResource(R.string.shuffle), state.connected && snapshot.canControl,
                            selected = snapshot.shuffle,
                            stateLabel = stringResource(if (snapshot.shuffle) R.string.on else R.string.off)) {
                            link.send(WearAction.SHUFFLE, shuffle = !snapshot.shuffle)
                        }
                        val repeatLabel = stringResource(when (snapshot.repeatMode) {
                            PlaybackRepeatMode.OFF -> R.string.repeat_off
                            PlaybackRepeatMode.ALL -> R.string.repeat_all
                            PlaybackRepeatMode.ONE -> R.string.repeat_one
                        })
                        ActionPill(stringResource(R.string.repeat), state.connected && snapshot.canControl,
                            snapshot.repeatMode != PlaybackRepeatMode.OFF, repeatLabel) {
                            link.send(WearAction.REPEAT, repeatMode = snapshot.repeatMode.next())
                        }
                    }
                }
                item {
                    Label(stringResource(R.string.volume, (snapshot.volume * 100).toInt()), color = Muted, size = 13)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionPill("−", state.connected && snapshot.canControl && snapshot.canAdjustVolume,
                            accessibilityLabel = stringResource(R.string.volume_down)) {
                            link.send(WearAction.VOLUME, volume = (snapshot.volume - .05f).coerceAtLeast(0f))
                        }
                        ActionPill("+", state.connected && snapshot.canControl && snapshot.canAdjustVolume,
                            accessibilityLabel = stringResource(R.string.volume_up)) {
                            link.send(WearAction.VOLUME, volume = (snapshot.volume + .05f).coerceAtMost(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Label(text: String, color: Color = Color.White, size: Int = 16, maxLines: Int = Int.MAX_VALUE) {
    BasicText(text, style = TextStyle(color = color, fontSize = size.sp, textAlign = TextAlign.Center),
        maxLines = maxLines, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun ActionPill(text: String, enabled: Boolean = true, selected: Boolean = false,
    stateLabel: String? = null, accessibilityLabel: String = text, onClick: () -> Unit) {
    Box(Modifier.heightIn(min = 48.dp).widthIn(min = 48.dp).clip(CircleShape)
        .background(if (selected) Accent.copy(alpha = .2f) else Surface)
        .semantics { contentDescription = accessibilityLabel; stateLabel?.let { stateDescription = it } }
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick).padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center) {
        BasicText(text, style = TextStyle(color = if (!enabled) Muted.copy(alpha = .4f) else if (selected) Accent else Color.White,
            fontSize = 13.sp, textAlign = TextAlign.Center))
    }
}

@Composable
private fun TransportButton(label: String, enabled: Boolean, icon: String, primary: Boolean = false, onClick: () -> Unit) {
    Box(Modifier.size(if (primary) 52.dp else 48.dp).clip(CircleShape).background(if (primary) Accent else Surface)
        .semantics { contentDescription = label }
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(22.dp)) {
            val ink = if (enabled) Color.White else Color.White.copy(alpha = .3f)
            if (icon == "pause") {
                drawRect(ink, Offset(size.width * .2f, size.height * .15f), Size(size.width * .2f, size.height * .7f))
                drawRect(ink, Offset(size.width * .6f, size.height * .15f), Size(size.width * .2f, size.height * .7f))
            } else {
                val forward = icon != "previous"
                val path = Path().apply {
                    moveTo(size.width * (if (forward) .2f else .8f), size.height * .15f)
                    lineTo(size.width * (if (forward) .85f else .15f), size.height * .5f)
                    lineTo(size.width * (if (forward) .2f else .8f), size.height * .85f)
                    close()
                }
                drawPath(path, ink)
                if (icon != "play") {
                    val x = size.width * if (forward) .85f else .15f
                    drawLine(ink, Offset(x, size.height * .15f), Offset(x, size.height * .85f), 3.dp.toPx())
                }
            }
        }
    }
}

private fun time(ms: Long): String = "${ms.coerceAtLeast(0) / 60_000}:${(ms.coerceAtLeast(0) / 1_000 % 60).toString().padStart(2, '0')}"
