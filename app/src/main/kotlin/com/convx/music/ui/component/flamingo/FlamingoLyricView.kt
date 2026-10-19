/** Adapted from Flamingo by Yos-X, GPL-3.0. See NOTICE. */
package com.convx.music.ui.component.flamingo

import android.os.Build
import com.convx.music.ui.component.followLyricLine
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToInt

val yosEasing = CubicBezierEasing(0.75f, 0.0f, 0.25f, 1.0f)

@Composable
fun YosLyricView(

    lrcEntriesLambda: () -> List<List<Pair<Float, String>>>,
    lineEndTimesLambda: () -> List<Float> = { emptyList() },
    lineTransliterationsLambda: () -> List<String?> = { emptyList() },
    lineSubtitlesLambda: () -> List<String?> = { emptyList() },
    isTtmlLyricsLambda: () -> Boolean = { false },
    liveTimeLambda: () -> Int,
    mediaEvent: YosMediaEvent,
    translationLambda: () -> Boolean = { true },
    blurLambda: () -> Boolean = { false },

    uiConfig: YosUIConfig = YosUIConfig(),
    isSynced: Boolean = true,
    autoScrollEnabled: Boolean = true,
    weightLambda: () -> Boolean,
    modifier: Modifier,
    textStyle: TextStyle = MainTextStyle,
    otherSideForLines: List<Boolean>,
    currentLyricIndex: androidx.compose.runtime.MutableIntState,
    onBackClick: () -> Unit,
    wordGlowEnabled: Boolean = true,
) {
    val view = androidx.compose.ui.platform.LocalView.current
    val mainTextBasicColor = Color(uiConfig.mainTextBasicColor)
    val subTextBasicColor = Color(uiConfig.subTextBasicColor)

    val lrcEntries = lrcEntriesLambda()
    val lineEndTimes = lineEndTimesLambda()
    val lineTransliterations = lineTransliterationsLambda()
    val lineSubtitles = lineSubtitlesLambda()
    val isTtmlLyrics = isTtmlLyricsLambda()

    if (lrcEntries.isEmpty() || otherSideForLines.isEmpty() ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight(if (weightLambda()) 0.56f else 1f)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onBackClick()
                }
        ) {
            Text(
                text = uiConfig.noLrcText,
                fontSize = 18.sp,
                color = Color(uiConfig.mainTextBasicColor)
            )
        }
    } else {
        val scrollState = rememberLazyListState()

        val ttmlLiveTime = remember("YosLyricView_ttmlLiveTime") {
            mutableIntStateOf(liveTimeLambda())
        }

        YosWrapper {
            LaunchedEffect(isTtmlLyrics, lrcEntries) {
                while (isTtmlLyrics) {
                    ttmlLiveTime.intValue = liveTimeLambda()
                    androidx.compose.runtime.withFrameNanos { }
                }
            }
        }

        val focusedLyricIndices = remember(
            "YosLyricView_focusedLyricIndices",
            isTtmlLyrics,
            isSynced,
            lrcEntries,
            lineEndTimes
        ) {
            derivedStateOf {
                if (!isSynced) return@derivedStateOf lrcEntries.indices.toList()
                if (!isTtmlLyrics) {
                    return@derivedStateOf listOf(currentLyricIndex.intValue)
                }

                val liveTime = ttmlLiveTime.intValue
                val activeIndices = lrcEntries.mapIndexedNotNull { index, line ->
                    val lineStart = line.firstOrNull()?.first ?: return@mapIndexedNotNull null
                    val lineEnd = lineEndTimes.getOrNull(index)
                        ?: lrcEntries.getOrNull(index + 1)?.firstOrNull()?.first
                        ?: lineStart

                    if (liveTime >= lineStart && liveTime < lineEnd.coerceAtLeast(lineStart + 1f)) {
                        index
                    } else {
                        null
                    }
                }

                activeIndices.ifEmpty {
                    if (currentLyricIndex.intValue >= 0) listOf(currentLyricIndex.intValue) else emptyList()
                }
            }
        }

        val focusedLyricAnchorIndex = remember(
            "YosLyricView_focusedLyricAnchorIndex",
            focusedLyricIndices
        ) {
            derivedStateOf {
                currentLyricIndex.intValue
            }
        }

        val blankSpacer: (LazyListScope.() -> Unit) = {
            item {
                Box(
                    modifier = Modifier
                        .height(uiConfig.blankHeight.dp)
                ) {
                }
            }
        }

        val enableLyricScroll = remember("YosLyricView_enableLyricScroll") {
            mutableStateOf(true)
        }

        val height = rememberSaveable(key = "YosLyricView_height") { mutableIntStateOf(0) }

        val measurer = rememberTextMeasurer(
            cacheSize = 32
        )

        val visibleItems = remember("YosLyricView_visibleItems") {
            derivedStateOf {
                scrollState.layoutInfo.visibleItemsInfo
            }
        }
        val targetItem = remember("YosLyricView_targetItem") {
            derivedStateOf {
                visibleItems.value.find {
                    it.index == focusedLyricAnchorIndex.value + 1
                }
            }
        }
        val supportBlur = rememberSaveable(key = "supportBlur") {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        }

        val isUserScrolling = remember { mutableStateOf(false) }
        val nestedScrollConnection = remember {
            @Stable
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (source == NestedScrollSource.UserInput) isUserScrolling.value = true
                    return Offset.Zero
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity
                ): Velocity {
                    isUserScrolling.value = false
                    return super.onPostFling(consumed, available)
                }
            }
        }

        YosWrapper {
            LaunchedEffect(isUserScrolling.value, scrollState.isScrollInProgress) {
                if (isUserScrolling.value && scrollState.isScrollInProgress) {
                    enableLyricScroll.value = false
                } else {
                    delay(2000)
                    isUserScrolling.value = false
                    enableLyricScroll.value = true
                }
            }
        }

        YosWrapper {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(vertical = with(LocalDensity.current) { (height.intValue / 2f).toDp() }),
                modifier =
                modifier
                    .fillMaxSize()

                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onBackClick()
                    }
                    .nestedScroll(nestedScrollConnection)
                    .onSizeChanged {
                        if (height.intValue != it.height && it.height > 0) {
                            height.intValue = it.height

                        }
                    }
            ) {

                blankSpacer()
                itemsIndexed(
                    items = lrcEntries,
                    key = { index, _ -> index }
                ) { index, lines ->
                    val isCurrent = remember(lines) {
                        derivedStateOf {
                            focusedLyricIndices.value.contains(index)
                        }
                    }

                    val isTop = remember(lines) {
                        derivedStateOf {
                            index == (focusedLyricAnchorIndex.value - 1)
                        }
                    }

                    val showStateAnimation = remember(index) {
                        derivedStateOf {
                            (focusedLyricAnchorIndex.value in scrollState.layoutInfo.visibleItemsInfo.map { it.index - 1 } && focusedLyricAnchorIndex.value >= 0) && enableLyricScroll.value
                        }
                    }

                    val isLyricEmpty = rememberSaveable(lines) {
                        mutableStateOf(
                            lines.all { it.second.isBlank() }
                        )
                    }

                    key(lines) {
                        val translation = remember(
                            index,
                            lines,
                            isTtmlLyrics,
                            translationLambda(),
                            lineTransliterations,
                            lineSubtitles
                        ) {
                            if (isTtmlLyrics) {
                                val secondaryText = if (translationLambda()) {
                                    lineSubtitles.getOrNull(index)
                                } else {
                                    lineTransliterations.getOrNull(index)
                                }
                                secondaryText?.ifBlank { null }
                            } else {
                                val str = lines.last().second
                                str.ifBlank { null }
                            }
                        }

                        val blur = remember(index) {
                            derivedStateOf {
                                if (!showStateAnimation.value || focusedLyricIndices.value.contains(index) || !blurLambda() || !supportBlur) {
                                    0f
                                } else {
                                    (abs(index - focusedLyricAnchorIndex.value) * 2.5f).coerceAtMost(
                                        8f
                                    )
                                }
                            }
                        }

                        val otherSide = remember(index) {
                            otherSideForLines.getOrElse(index) { false }
                        }

                        YosWrapper {
                            LyricItem(
                                mainTextStyle = textStyle,
                                isCurrentLambda = {
                                    isCurrent.value
                                },
                                isTopLambda = {
                                    isTop.value
                                },
                                mainLyric = lines.dropLast(1),
                                translation,
                                if (isTtmlLyrics) translation != null else translationLambda(),

                                subTextSize = uiConfig.subTextSize,
                                blur = { blur.value },
                                mainTextBasicColor,
                                subTextBasicColor,
                                otherSide = otherSide,
                                wordGlowEnabled = wordGlowEnabled,
                                liveTimeLambda = liveTimeLambda,
                                measurer = measurer,
                                isLyricEmpty = { isLyricEmpty.value },
                                nextTime = {
                                    if (index + 1 > lrcEntries.size - 1) {
                                        0f
                                    } else {
                                        lrcEntries[(index + 1)].first().first
                                    }
                                }
                            ) {
                                view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                                mediaEvent.onSeek(lines.first().first.toInt())
                            }
                        }
                    }



                }
                blankSpacer()
                item("extra_blank") {
                    Spacer(modifier = Modifier.height(500.dp))
                }
            }
        }

        YosWrapper {

            LaunchedEffect(focusedLyricAnchorIndex.value, translationLambda(), enableLyricScroll.value,
                autoScrollEnabled, isSynced, height.intValue, targetItem.value?.size) {
                if (autoScrollEnabled && isSynced && enableLyricScroll.value && focusedLyricAnchorIndex.value >= 0) {
                    scrollState.followLyricLine(focusedLyricAnchorIndex.value + 1)
                }
            }
        }

    }
}

@Composable
fun Float.toDp(): Dp {
    val density = LocalDensity.current
    return (this / density.density).dp
}

@Composable
private fun LazyItemScope.Line(
    lines: List<Pair<Float, String>>,
    style: TextStyle,
    measurer: TextMeasurer,
    modifier: Modifier,
    viewAlign: Alignment.Horizontal,
    draw: CacheDrawScope.(Constraints, TextLayoutResult) -> DrawResult
) =
    YosWrapper {

        val styledString = remember(style, lines) {
            buildString {
                lines.forEach { char ->
                    if (char.second.isNotEmpty()) {
                        append(char.second)
                    }
                }
            }
        }

        Column(
            horizontalAlignment = viewAlign,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.ModulateAlpha
                }
        ) {
            SubcomposeLayout(modifier = modifier.semantics { text = AnnotatedString(styledString) }) { constraints ->

                val measureResult = measurer.measure(
                    text = styledString,
                    style = style,
                    constraints = Constraints(
                        minWidth = 0,
                        maxWidth = constraints.maxWidth,
                    ),
                    layoutDirection = LayoutDirection.Ltr
                )

                val height = (style.lineHeight * measureResult.lineCount)

                val width = runCatching {
                    (0 until measureResult.lineCount).maxOf {
                        measureResult.getBoundingBox(
                            measureResult.getLineEnd(it, visibleEnd = true) - 1
                        ).right
                    }
                }.getOrDefault(constraints.maxWidth.toFloat())

                val content = subcompose(lines) {
                    Spacer(
                        Modifier
                            .fillMaxSize()
                            .drawWithCache { draw(constraints, measureResult) }
                    )
                }.first()

                val placeable = content.measure(
                    Constraints.fixed(width.roundToInt(), height.roundToPx())
                )

                layout(placeable.width, placeable.height) {
                    placeable.place(0, 0)
                }

            }
        }
    }

val easing: Easing = EaseInOutQuad

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun LazyItemScope.LyricItem(
    mainTextStyle: TextStyle,
    isCurrentLambda: () -> Boolean,
    isTopLambda: () -> Boolean,
    mainLyric: List<Pair<Float, String>>,
    translation: String?,
    showTranslation: Boolean,

    subTextSize: Int,
    blur: () -> Float,

    mainTextBasicColor: Color,
    subTextBasicColor: Color,
    measurer: TextMeasurer,
    isLyricEmpty: () -> Boolean,
    nextTime: () -> Float,
    otherSide: Boolean,
    liveTimeLambda: () -> Int,
    wordGlowEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val viewAlign = if (otherSide) Alignment.End else Alignment.Start

    val focusedColor = Color(0xFFFFFFFF)
    val unfocusedColor = Color(0x2EFFFFFF)

    val unfocusedSolidBrush = SolidColor(unfocusedColor)
    val currentLineChrome = animateFloatAsState(
        targetValue = if (isCurrentLambda()) 1f else 0f,
        animationSpec = tween(durationMillis = 240, easing = yosEasing),
        label = "currentLyricChrome",
    )

    val isNotOneByOne = rememberSaveable(mainLyric) {
        mutableStateOf(
            mainLyric.all { it.first == mainLyric.firstOrNull()?.first }
        )

    }

    val liveTime = remember(mainLyric) { mutableIntStateOf(liveTimeLambda()) }

    YosWrapper {
        val launch = remember(mainLyric) {
            derivedStateOf {
                isLyricEmpty() || !isNotOneByOne.value
            }
        }
        if (launch.value) {
            LaunchedEffect(isCurrentLambda()) {
                liveTime.intValue = liveTimeLambda()
                if (isCurrentLambda()) while (true) {
                    androidx.compose.runtime.withFrameNanos { }
                    liveTime.intValue = liveTimeLambda()
                }
            }
        }
    }

    YosWrapper {
        Column(
            Modifier
                .padding(horizontal = 9.dp),
            horizontalAlignment = viewAlign
        ) {
            val otherSideAnimate = if (otherSide) {
                TransformOrigin(1f, 0.25f)
            } else {
                TransformOrigin(0f, 0.25f)
            }

            val otherSideTransformOrigin =
                if (otherSide) TransformOrigin(
                    1f,
                    0.5f
                ) else TransformOrigin(
                    0f,
                    0.5f
                )

            val tweenSpecWithDelay: AnimationSpec<Float> = remember(mainLyric) {
                TweenSpec(
                    durationMillis = 270,
                    easing = yosEasing,
                    delay =   110
                )
            }

            val tweenSpecWithoutDelay: AnimationSpec<Float> = remember(mainLyric) {
                TweenSpec(durationMillis =  300, easing = yosEasing,delay = 45)
            }

            val scale = animateFloatAsState(
                targetValue = if (isCurrentLambda()) 1.005f else 1f,
                animationSpec = if (isCurrentLambda()) tweenSpecWithDelay else tweenSpecWithoutDelay
            )

            val cardPadding = if (otherSide) {
                Modifier.padding(start = 28.dp)
            } else {
                Modifier.padding(end = 28.dp)
            }

            if (isLyricEmpty()) {
                Column(Modifier.animateContentSize()) {
                    val percent = remember(mainLyric) {
                        derivedStateOf {
                            val m = mainLyric.first().first
                            ((liveTime.intValue - m).coerceAtLeast(0f) / (nextTime() - m))
                            .coerceAtMost(1f)
                        }
                    }
                    val show = remember(mainLyric) {
                        derivedStateOf { (isLyricEmpty() && isCurrentLambda() && percent.value != 0f) }
                    }
                    AnimatedVisibility(
                        show.value,
                        enter = fadeIn(animationSpec = TweenSpec(
                            durationMillis = 550,
                            easing = yosEasing,
                            delay = 300
                        )) + scaleIn(
                            initialScale = 0.85f,
                            transformOrigin = otherSideAnimate,
                            animationSpec = TweenSpec(
                                durationMillis = 550,
                                easing = yosEasing,
                                delay = 300
                            )
                        ),
                        exit = fadeOut() + scaleOut(
                            targetScale = 0.85f,
                            transformOrigin = otherSideAnimate,
                            animationSpec = TweenSpec(
                                durationMillis = 340,
                                easing = yosEasing
                            )
                        )
                    ) {
                        YosWrapper {
                            LyricCard(
                                { scale.value },
                                cardPadding,
                                otherSideTransformOrigin,
                                viewAlign,

                            ) {

                                Column(
                                    Modifier
                                        .padding(start = 20.dp, end = 20.dp)
                                        .padding(top = 8.dp, bottom = 10.dp),
                                    horizontalAlignment = viewAlign
                                ) {
                                    CountdownAnimation(
                                        { percent.value },
                                        colorLambda = { mainTextBasicColor })
                                }

                            }
                        }
                    }
                }
            } else {
                YosWrapper {
                    LyricCard(
                        { scale.value },
                        cardPadding,
                        otherSideTransformOrigin,
                        viewAlign,

                    ) {

                        val blurValue = animateDpAsState(
                            blur().dp, SnapSpec(delay = if (isTopLambda()) 260 else 0)
                        )

                        val blurModifier = remember(mainLyric) {
                            derivedStateOf {
                                val thisBlur = blur()
                                if (thisBlur == 0f) {
                                    Modifier
                                } else {
                                    Modifier.blur(
                                        blurValue.value,

                                        edgeTreatment = BlurredEdgeTreatment.Unbounded,
                                    )
                                }
                            }
                        }

                        YosWrapper {
                            Column(
                                Modifier
                                    .then(blurModifier.value)
                                    .fillMaxWidth(),
                                horizontalAlignment = viewAlign
                            ) {
                                val textAlign = if (otherSide) TextAlign.End else TextAlign.Start

                                val alphaTweenSpecWithDelay: AnimationSpec<Float> =
                                    remember(mainLyric) {
                                        TweenSpec(
                                            durationMillis = 350,
                                            easing = yosEasing,
                                            delay = 145
                                        )
                                    }

                                val alphaTweenSpecWithoutDelay: AnimationSpec<Float> =
                                    remember(mainLyric) {
                                        TweenSpec(
                                            durationMillis = 350,
                                            easing = yosEasing,
                                            delay = 80
                                        )
                                    }

                                YosWrapper {
                                    val thisAlphaAnimated = animateFloatAsState(
                                        targetValue = if (isCurrentLambda())  1f else 0.14f,
                                        animationSpec = if (isCurrentLambda()) alphaTweenSpecWithDelay else alphaTweenSpecWithoutDelay
                                    )

                                    val thisAlpha = remember(mainLyric) {
                                        derivedStateOf {
                                            if (isNotOneByOne.value) {
                                                thisAlphaAnimated.value
                                            } else {
                                                1f
                                            }
                                        }
                                    }

                                    val otherSidePadding = remember(mainLyric) {
                                        derivedStateOf {
                                            if (otherSide) {
                                                Modifier.padding(
                                                    start = 20.dp,
                                                    end = if (mainLyric.last().second.endsWith("：")) 3.dp else 20.dp
                                                )
                                            } else {
                                                Modifier.padding(
                                                    start = 20.dp,
                                                    end = 20.dp
                                                )
                                            }
                                        }
                                    }

                                    val showHighLight = remember(mainLyric, translation) {
                                        derivedStateOf {
                                            if (isNotOneByOne.value) {
                                                true
                                            } else {
                                                val highlightIndex = (mainLyric.size - if (translation != null && mainLyric.size >= 3) 3 else 1)
                                                    .coerceIn(0, mainLyric.lastIndex)
                                                liveTime.intValue >= mainLyric[highlightIndex].first
                                            }
                                        }
                                    }

                                    Line(
                                        lines = mainLyric,
                                        style = if (otherSide) mainTextStyle.copy(textAlign = TextAlign.End) else mainTextStyle,
                                        measurer = measurer,
                                        modifier = Modifier
                                            .graphicsLayer {
                                                this.alpha = thisAlpha.value
                                                compositingStrategy =
                                                    CompositingStrategy.ModulateAlpha
                                            }
                                            .padding(vertical = 4.dp)
                                            .then(otherSidePadding.value)
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                            .drawBehind {
                                                val intensity = currentLineChrome.value
                                                if (intensity > 0.001f) {
                                                    val radius = CornerRadius(size.height / 2f)
                                                    val accent = mainTextBasicColor
                                                    drawRoundRect(
                                                        brush = Brush.verticalGradient(
                                                            colors = listOf(
                                                                Color.White.copy(alpha = 0.025f * intensity),
                                                                accent.copy(alpha = 0.075f * intensity),
                                                                accent.copy(alpha = 0.035f * intensity),
                                                            ),
                                                        ),
                                                        cornerRadius = radius,
                                                    )
                                                    drawRoundRect(
                                                        brush = Brush.verticalGradient(
                                                            colors = listOf(
                                                                Color.White.copy(alpha = 0.30f * intensity),
                                                                accent.copy(alpha = 0.11f * intensity),
                                                                Color.White.copy(alpha = 0.04f * intensity),
                                                            ),
                                                        ),
                                                        cornerRadius = radius,
                                                        style = Stroke(width = 1.dp.toPx()),
                                                    )
                                                    drawLine(
                                                        brush = Brush.horizontalGradient(
                                                            colors = listOf(
                                                                Color.Transparent,
                                                                Color.White.copy(alpha = 0.38f * intensity),
                                                                accent.copy(alpha = 0.22f * intensity),
                                                                Color.Transparent,
                                                            ),
                                                        ),
                                                        start = Offset(12.dp.toPx(), size.height - 1.dp.toPx()),
                                                        end = Offset(size.width - 12.dp.toPx(), size.height - 1.dp.toPx()),
                                                        strokeWidth = 1.5.dp.toPx(),
                                                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                                    )
                                                }
                                            }
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() }
                                            ) {
                                                onClick()
                                            },
                                        viewAlign = viewAlign
                                    ) { parentConstraints, measureResult ->

                                        if (isNotOneByOne.value) {

                                            return@Line onDrawWithContent {
                                                drawText(
                                                    textLayoutResult = measureResult,
                                                    color = focusedColor
                                                )
                                            }
                                        }

                                        if (!isCurrentLambda()) {

                                            if (showHighLight.value) {

                                                return@Line onDrawWithContent {
                                                    drawText(
                                                        textLayoutResult = measureResult,
                                                        color = focusedColor,
                                                        topLeft = Offset(0F, -4F)
                                                    )
                                                }
                                            } else {

                                                return@Line onDrawWithContent {
                                                    drawText(
                                                        textLayoutResult = measureResult,
                                                        color = unfocusedColor
                                                    )
                                                }
                                            }
                                        }

                                        var sum = 0
                                        var lastTime = 0f

                                        val wordsToDraw = arrayListOf<DrawWord>()

                                        var averageTime = 0f

                                        lastTime = mainLyric.first().first

                                        mainLyric.fastForEachIndexed { wordIndex, word ->

                                            val thisWord = word.second

                                            if (thisWord.isEmpty()) {
                                                return@fastForEachIndexed
                                            }

                                            averageTime = ((word.first - lastTime) / thisWord.length).coerceAtLeast(1f)

                                            val thisWordGroupLastTime = if (wordIndex - 1 < 0) {
                                                mainLyric.first().first
                                            } else {
                                                mainLyric[(wordIndex - 1)].first
                                            }
                                            val wordSpan = (word.first - thisWordGroupLastTime).coerceAtLeast(1f)
                                            val lift = {
                                                val progress = ((liveTime.intValue - thisWordGroupLastTime) / wordSpan).coerceIn(0f, 1f)
                                                4f * easing.transform(progress)
                                            }

                                            thisWord.forEach { char ->

                                                val charWord = char.toString()

                                                val layout = measurer.measure(
                                                    text = charWord,
                                                    style = if (otherSide) mainTextStyle.copy(
                                                        textAlign = TextAlign.End
                                                    ) else mainTextStyle,
                                                    constraints = measureResult.layoutInput.constraints
                                                )

                                                val thisWordLastTime = lastTime
                                                val thisWordAverageTime = averageTime

                                                wordsToDraw += DrawWord(
                                                    time = lastTime + averageTime,
                                                    word = charWord,
                                                    layout = layout,
                                                    topLeft = run {
                                                        val origin = measureResult.getBoundingBox(sum.coerceIn(0, (mainLyric.sumOf { it.second.length } - 1).coerceAtLeast(0))).topLeft
                                                        val position: () -> Offset = { origin.minus(Offset(0f, lift())) }
                                                        position
                                                    },
                                                    brush = { px, percent ->
                                                        if (thisWord == " ") {
                                                            return@DrawWord unfocusedSolidBrush
                                                        }

                                                        val beforeColor = if (percent <= -0.5f) {
                                                            unfocusedColor
                                                        } else {
                                                            focusedColor
                                                        }

                                                        val afterColor = if (percent >= 1f) {
                                                            focusedColor
                                                        } else {
                                                            unfocusedColor
                                                        }
                                                        Brush.horizontalGradient(
                                                            0f to beforeColor,
                                                            (percent - px).coerceIn(
                                                                0f,
                                                                1f
                                                            ) to beforeColor,
                                                            (percent + px).coerceIn(
                                                                0f,
                                                                1f
                                                            ) to afterColor
                                                        )
                                                    },
                                                    percent = {
                                                        if (thisWord == " ") {
                                                            return@DrawWord 0f
                                                        }

                                                        ((liveTime.intValue - thisWordLastTime) / thisWordAverageTime)

                                                    }
                                                ).also {
                                                    sum += charWord.length
                                                    lastTime += averageTime
                                                }
                                            }
                                        }

                                        onDrawBehind {
                                            wordsToDraw.fastForEach { l ->
                                                val progress = l.percent()
                                                val wordGlow = if (
                                                    wordGlowEnabled && l.word.isNotBlank() && progress in 0f..1f
                                                ) {
                                                    Shadow(
                                                        color = focusedColor.copy(alpha = (0.18f + 0.4f * progress).coerceAtMost(0.62f)),
                                                        offset = Offset.Zero,
                                                        blurRadius = 8f + 10f * progress,
                                                    )
                                                } else {
                                                    null
                                                }
                                                drawText(
                                                    textLayoutResult = l.layout,
                                                    topLeft = l.topLeft(),
                                                    brush = l.brush(0.3f, progress),
                                                    shadow = wordGlow,
                                                )
                                            }
                                        }
                                    }
                                }
                                YosWrapper {
                                    AnimatedVisibility(showTranslation && translation != null) {
                                        translation?.let {
                                            val translationAlpha = animateFloatAsState(
                                                targetValue = if (isCurrentLambda()) 0.5f else 0.14f,
                                                animationSpec = if (isCurrentLambda()) alphaTweenSpecWithDelay else alphaTweenSpecWithoutDelay
                                            )

                                            val translationOtherSidePadding = if (otherSide) {
                                                Modifier.padding(
                                                    start = 20.dp,
                                                    end = 20.dp
                                                )
                                            } else {
                                                Modifier.padding(
                                                    start = 20.dp,
                                                    end = 20.dp
                                                )
                                            }

                                            Text(
                                                text = it,
                                                fontSize = subTextSize.sp,
                                                color = subTextBasicColor,
                                                fontWeight = FontWeight.Normal,
                                                modifier = Modifier
                                                    .graphicsLayer {
                                                        this.alpha =
                                                            translationAlpha.value
                                                        compositingStrategy =
                                                            CompositingStrategy.ModulateAlpha
                                                    }
                                                    .then(translationOtherSidePadding)
                                                    .padding(top = 5.dp),
                                                lineHeight = (subTextSize + 5).sp,
                                                letterSpacing = 0.3.sp,
                                                textAlign = textAlign
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LyricCard(
    scale: () -> Float,
    cardPadding: Modifier,
    otherSideTransformOrigin: TransformOrigin,
    viewAlign: Alignment.Horizontal,

    content: @Composable () -> Unit,
) =
    YosWrapper {
        Column(
            modifier = Modifier
                .graphicsLayer {

                    val scaleValue = scale()
                    scaleX = scaleValue
                    scaleY = scaleValue
                    transformOrigin = otherSideTransformOrigin
                }
                .fillMaxWidth()
                .then(cardPadding)
                .padding(top = 9.dp, bottom = 9.dp),
            horizontalAlignment = viewAlign
        ) {
            content()
        }
    }

@Composable
fun CountdownAnimation(progress: () -> Float, colorLambda: () -> Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = yosEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.graphicsLayer {

            scaleX = scale.value
            scaleY = scale.value
            alpha = 0.8f
        },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            for (i in 1..3) {

                val average = 1f / 3f
                val beforePadding = (i-1) * average
                val thisPercent = (progress() - beforePadding)  / ((i * average) - beforePadding)
                val alpha = 0.2f + (0.8f * thisPercent).coerceIn(0f, 0.8f)

                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .background(
                            colorLambda().copy(alpha = alpha),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

val MainTextStyle = TextStyle(
    fontSize = 30.5.sp,
    lineHeight = 40.5.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = 0.05.sp,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    ),
    lineBreak = LineBreak(
        strategy = LineBreak.Strategy.Balanced,
        LineBreak.Strictness.Default,
        LineBreak.WordBreak.Default
    )
)

@Stable
private data class DrawWord(
    val time: Float,
    val word: String,
    val layout: TextLayoutResult,
    val topLeft: () -> Offset,
    val brush: (px: Float, percent: Float) -> Brush,
    val percent: () -> Float
)
