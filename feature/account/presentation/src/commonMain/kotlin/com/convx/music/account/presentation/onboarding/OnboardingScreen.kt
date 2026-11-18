package com.convx.music.account.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.designsystem.convxFontFamily
import com.convx.music.designsystem.convxLogoFontFamily
import com.convx.music.designsystem.convxWordmarkFontFamily
import com.convx.music.ui.component.AnimatedMesh

/** One line of the animated feature tour on the first page. */
class OnboardingFeature(val title: String, val body: String)

/** Every string the welcome shows, so each client feeds it from its own localisation. */
class OnboardingStrings(
    val welcomeTagline: String,
    val getStarted: String,
    val features: List<OnboardingFeature>,
    val nameTitle: String,
    val namePrompt: String,
    val nameHint: String,
    val nameFooter: String,
    val next: String,
    val connectTitle: String,
    val connectBody: String,
    val signIn: String,
    val skipSignIn: String,
    val modeTitle: String,
    val modeBody: String,
    val modeOnline: String,
    val modeLocal: String,
    val notificationsTitle: String,
    val notificationsBody: String,
    val notificationsAllow: String,
    val notificationsSkip: String,
)

/** Where the welcome can be. Mode and Notifications are skipped when they don't apply. */
private enum class Step { Welcome, Name, Connect, Mode, Notifications }

private const val NameLimit = 40

/** How long each feature holds the stage. */
private const val FeatureMillis = 3_000

private val CardShape = RoundedCornerShape(28.dp)
private val ControlShape = RoundedCornerShape(18.dp)

private val LocalWordmarkFont = compositionLocalOf<FontFamily?> { null }
private val LocalLogoFont = compositionLocalOf<FontFamily?> { null }

@Composable
private fun wordmarkFont(): FontFamily = LocalWordmarkFont.current ?: convxWordmarkFontFamily()

@Composable
private fun logoFont(): FontFamily = LocalLogoFont.current ?: convxLogoFontFamily()

/**
 * First-run welcome: an animated tour of what Convx does, then a name for Listen Together, the
 * account, how the library is fed, and notifications. Stateless apart from the page and the
 * typed name; the host owns every side effect.
 *
 * Always drawn dark, whatever the theme, because white type is what sits on colour.
 *
 * @param background drawn behind every page; receives the page index so it can shift palette.
 * @param wordmarkFont the display face for titles; the shared one when null.
 * @param logoFont the face of the CONVX logo on the first page; the shared one when null.
 * @param notificationsAvailable false where there is no runtime permission to ask for.
 * @param onAllowNotifications asks the OS; call `done` once it has answered either way.
 * @param imeVisible whether the keyboard is up; the name page drops its heading to make room.
 * @param backHandler intercepts the system back gesture while enabled; a no-op by default.
 */
@Composable
fun OnboardingScreen(
    strings: OnboardingStrings,
    signedIn: Boolean,
    initialName: String,
    notificationsAvailable: Boolean,
    onNameChosen: (String) -> Unit,
    onSignIn: () -> Unit,
    onLocalOnlyChosen: (Boolean) -> Unit,
    onAllowNotifications: (done: () -> Unit) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    wordmarkFont: FontFamily? = null,
    logoFont: FontFamily? = null,
    background: @Composable (page: Int) -> Unit = { DefaultBackdrop() },
    imeVisible: @Composable () -> Boolean = { false },
    backHandler: @Composable (enabled: Boolean, onBack: () -> Unit) -> Unit = { _, _ -> },
) {
    var step by rememberSaveable { mutableIntStateOf(Step.Welcome.ordinal) }
    var name by rememberSaveable { mutableStateOf(initialName) }
    val focusManager = LocalFocusManager.current

    fun after(from: Step): Step? {
        var next = Step.entries.getOrNull(from.ordinal + 1)
        if (next == Step.Mode && signedIn) next = Step.Notifications
        if (next == Step.Notifications && !notificationsAvailable) next = null
        return next
    }

    fun advance(from: Step) {
        val next = after(from)
        if (next == null) onFinish() else step = next.ordinal
    }

    fun previous(from: Step): Step = when (from) {
        Step.Notifications -> if (signedIn) Step.Connect else Step.Mode
        else -> Step.entries[(from.ordinal - 1).coerceAtLeast(0)]
    }

    val current = Step.entries[step]
    backHandler(current != Step.Welcome) { step = previous(current).ordinal }
    // The account landing is what ends the connect page; the sign-in itself runs in the host.
    LaunchedEffect(signedIn, step) {
        if (signedIn && current == Step.Connect) advance(Step.Connect)
    }

    CompositionLocalProvider(LocalWordmarkFont provides wordmarkFont, LocalLogoFont provides logoFont) {
        Box(
            modifier
                .fillMaxSize()
                .background(Color.Black)
                // Swallows every touch the pages don't, and puts the keyboard away on a backdrop tap.
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
        ) {
            background(step)
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.10f),
                        0.45f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.55f),
                    ),
                ),
            )
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val forward = targetState > initialState
                    val slide = tween<IntOffset>(420)
                    (slideInHorizontally(slide) { if (forward) it / 4 else -it / 4 } + fadeIn(tween(320, 100)))
                        .togetherWith(slideOutHorizontally(slide) { if (forward) -it / 4 else it / 4 } + fadeOut(tween(200)))
                },
                label = "onboardingPage",
                modifier = Modifier.fillMaxSize().systemBarsPadding().imePadding(),
            ) { shown ->
                val page = Step.entries[shown]
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Column(Modifier.fillMaxSize().widthIn(max = 520.dp).padding(horizontal = 24.dp)) {
                        when (page) {
                            Step.Welcome -> WelcomePage(strings, onStart = { advance(Step.Welcome) })
                            Step.Name -> NamePage(
                                strings = strings,
                                keyboardUp = imeVisible(),
                                name = name,
                                onNameChange = { name = it.take(NameLimit) },
                                onBack = { step = previous(page).ordinal },
                                onNext = next@{
                                    val chosen = name.trim()
                                    if (chosen.isEmpty()) return@next
                                    focusManager.clearFocus()
                                    onNameChosen(chosen)
                                    advance(Step.Name)
                                },
                            )
                            Step.Connect -> InfoPage(
                                title = strings.connectTitle,
                                body = strings.connectBody,
                                onBack = { step = previous(page).ordinal },
                                primary = strings.signIn,
                                onPrimary = onSignIn,
                                secondary = strings.skipSignIn,
                                onSecondary = { advance(Step.Connect) },
                            )
                            Step.Mode -> InfoPage(
                                title = strings.modeTitle,
                                body = strings.modeBody,
                                onBack = { step = previous(page).ordinal },
                                primary = strings.modeOnline,
                                onPrimary = { onLocalOnlyChosen(false); advance(Step.Mode) },
                                secondary = strings.modeLocal,
                                onSecondary = { onLocalOnlyChosen(true); advance(Step.Mode) },
                            )
                            Step.Notifications -> InfoPage(
                                title = strings.notificationsTitle,
                                body = strings.notificationsBody,
                                onBack = { step = previous(page).ordinal },
                                primary = strings.notificationsAllow,
                                onPrimary = { onAllowNotifications(onFinish) },
                                secondary = strings.notificationsSkip,
                                onSecondary = onFinish,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Shown by clients that don't supply their own backdrop: two drifting colour blobs on black. */
@Composable
private fun DefaultBackdrop() {
    AnimatedMesh(
        primary = Color(0xFFFA2D48),
        secondary = Color(0xFF5E5CE6),
        modifier = Modifier.fillMaxSize(),
        alpha = 0.55f,
        fadeAtBottom = false,
    )
}

/**
 * The tour: the wordmark, then one feature at a time on a large stage that slides up as it
 * changes, with a story-style progress strip. Tapping a segment jumps to that feature.
 */
@Composable
private fun ColumnScope.WelcomePage(strings: OnboardingStrings, onStart: () -> Unit) {
    val features = strings.features
    var active by rememberSaveable { mutableIntStateOf(0) }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(active, features.size) {
        if (features.isEmpty()) return@LaunchedEffect
        progress.snapTo(0f)
        progress.animateTo(1f, tween(FeatureMillis, easing = LinearEasing))
        active = (active + 1) % features.size
    }

    Spacer(Modifier.height(36.dp))
    BasicText(
        text = "CONVX",
        style = TextStyle(
            fontFamily = logoFont(),
            fontWeight = FontWeight.Normal,
            fontSize = 30.sp,
            color = Color.White,
        ),
    )
    Spacer(Modifier.height(6.dp))
    BasicText(text = strings.welcomeTagline, style = bodyStyle(15.sp, 0.7f))

    Spacer(Modifier.weight(1f))

    if (features.isNotEmpty()) {
        AnimatedContent(
            targetState = active,
            transitionSpec = {
                (slideInVertically(tween(520)) { it / 3 } + fadeIn(tween(420, 120)))
                    .togetherWith(slideOutVertically(tween(360)) { -it / 4 } + fadeOut(tween(240)))
            },
            label = "featureStage",
            modifier = Modifier.fillMaxWidth(),
        ) { index ->
            val feature = features[index % features.size]
            Column {
                BasicText(
                    text = (index + 1).toString().padStart(2, '0'),
                    style = TextStyle(
                        fontFamily = wordmarkFont(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = 3.sp,
                        color = Color.White.copy(alpha = 0.6f),
                    ),
                )
                Spacer(Modifier.height(10.dp))
                BasicText(
                    text = feature.title,
                    style = TextStyle(
                        fontFamily = wordmarkFont(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 46.sp,
                        lineHeight = 50.sp,
                        letterSpacing = (-1.4).sp,
                        color = Color.White,
                    ),
                )
                Spacer(Modifier.height(12.dp))
                BasicText(text = feature.body, style = bodyStyle(18.sp, 0.78f, lineHeight = 25.sp))
            }
        }
    }

    Spacer(Modifier.weight(1f))

    Row(Modifier.fillMaxWidth().height(24.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        features.indices.forEach { index ->
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { active = index },
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f))) {
                    val fill = when {
                        index < active -> 1f
                        index == active -> progress.value
                        else -> 0f
                    }
                    Box(Modifier.fillMaxHeight().fillMaxWidth(fill).background(Color.White))
                }
            }
        }
    }
    Spacer(Modifier.height(14.dp))
    ActionButton(strings.getStarted, onStart, primary = true)
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun ColumnScope.NamePage(
    strings: OnboardingStrings,
    keyboardUp: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Spacer(Modifier.height(16.dp))
    BackButton(onBack)
    // With the keyboard up there is room for the field, not the heading.
    if (!keyboardUp) {
        Spacer(Modifier.height(24.dp))
        Title(strings.nameTitle)
    }
    Spacer(Modifier.weight(1f))
    SheetCard {
        BasicText(text = strings.namePrompt, style = bodyStyle(16.sp, 0.85f, FontWeight.Medium))
        Spacer(Modifier.height(12.dp))
        NameField(strings.nameHint, name, onNameChange, onNext)
        Spacer(Modifier.height(10.dp))
        BasicText(text = strings.nameFooter, style = bodyStyle(13.sp, 0.6f))
        Spacer(Modifier.height(18.dp))
        ActionButton(strings.next, onNext, primary = true, enabled = name.isNotBlank())
    }
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun ColumnScope.InfoPage(
    title: String,
    body: String,
    onBack: () -> Unit,
    primary: String,
    onPrimary: () -> Unit,
    secondary: String,
    onSecondary: () -> Unit,
) {
    Spacer(Modifier.height(16.dp))
    BackButton(onBack)
    Spacer(Modifier.height(24.dp))
    Title(title)
    Spacer(Modifier.weight(1f))
    SheetCard {
        BasicText(text = body, style = bodyStyle(16.sp, 0.85f, lineHeight = 22.sp))
        Spacer(Modifier.height(20.dp))
        ActionButton(primary, onPrimary, primary = true)
        Spacer(Modifier.height(10.dp))
        ActionButton(secondary, onSecondary, primary = false)
    }
    Spacer(Modifier.height(20.dp))
}

/** The frosted panel the page's controls sit in, anchored to the foot of the screen. */
@Composable
private fun SheetCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White.copy(alpha = 0.13f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), CardShape)
            .padding(20.dp),
        content = content,
    )
}

@Composable
private fun Title(text: String) {
    BasicText(
        text = text,
        style = TextStyle(
            fontFamily = wordmarkFont(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 42.sp,
            lineHeight = 46.sp,
            letterSpacing = (-1.2).sp,
            color = Color.White,
        ),
    )
}

@Composable
private fun bodyStyle(
    size: TextUnit,
    alpha: Float,
    weight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = convxFontFamily(),
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    color = Color.White.copy(alpha = alpha),
)

/** The name, set in the heading's own face so what you type reads as part of the page. */
@Composable
private fun NameField(hint: String, value: String, onValueChange: (String) -> Unit, onDone: () -> Unit) {
    val style = TextStyle(
        fontFamily = wordmarkFont(),
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.5).sp,
        color = Color.White,
    )
    Box(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(ControlShape)
            .background(Color.Black.copy(alpha = 0.28f))
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) {
            BasicText(hint, style = style.copy(color = Color.White.copy(alpha = 0.38f)), maxLines = 1)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = style,
            cursorBrush = SolidColor(Color.White),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { onDone() }),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** A full-width rounded button: white when primary, frosted when it is the alternative. */
@Composable
private fun ActionButton(label: String, onClick: () -> Unit, primary: Boolean, enabled: Boolean = true) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, spring(stiffness = 900f), label = "buttonScale")
    val alpha by animateFloatAsState(if (enabled) 1f else 0.4f, tween(180), label = "buttonAlpha")
    Box(
        Modifier
            .fillMaxWidth()
            .height(54.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(ControlShape)
            .background(if (primary) Color.White else Color.White.copy(alpha = 0.16f))
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            maxLines = 1,
            style = TextStyle(
                fontFamily = convxFontFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                color = if (primary) Color.Black else Color.White,
            ),
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.16f)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(18.dp)) {
            val width = 2.2.dp.toPx()
            val tip = Offset(size.width * 0.3f, size.height / 2)
            drawLine(Color.White, Offset(size.width * 0.62f, size.height * 0.12f), tip, width, StrokeCap.Round)
            drawLine(Color.White, tip, Offset(size.width * 0.62f, size.height * 0.88f), width, StrokeCap.Round)
        }
    }
}
