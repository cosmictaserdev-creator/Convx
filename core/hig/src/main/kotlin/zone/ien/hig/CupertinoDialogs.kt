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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kyant.capsule.ContinuousRoundedRectangle
import zone.ien.hig.CupertinoDialogsTokens.AlertDialogTitleMessageSpacing
import zone.ien.hig.section.CupertinoSectionTokens
import zone.ien.hig.theme.BrightSeparatorColor
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.isDark
import zone.ien.hig.theme.systemBlue
import zone.ien.hig.theme.systemGray7
import zone.ien.hig.theme.systemRed

/** Visual style for alert actions. */

enum class AlertActionStyle {
    /** Standard action button. */

    Default {
        override fun apply(
            style: TextStyle,
            dark: Boolean,
        ): TextStyle =
            style.copy(
                fontWeight = FontWeight.Normal,
                color = CupertinoColors.systemBlue(dark),
                textAlign = TextAlign.Center,
            )
    },

    /** Cancel action, displayed below the other buttons. */

    Cancel {
        override fun apply(
            style: TextStyle,
            dark: Boolean,
        ): TextStyle =
            style.copy(
                fontWeight = FontWeight.Bold,
                color = CupertinoColors.systemBlue(dark),
                textAlign = TextAlign.Center,
            )
    },

    /** Destructive action, styled in red. */

    Destructive {
        override fun apply(
            style: TextStyle,
            dark: Boolean,
        ): TextStyle =
            style.copy(
                fontWeight = FontWeight.Normal,
                color = CupertinoColors.systemRed(dark),
                textAlign = TextAlign.Center,
            )
    }, ;

    internal abstract fun apply(
        style: TextStyle,
        dark: Boolean,
    ): TextStyle
}

interface AlertDialogActionsScope {
    fun action(
        onClick: () -> Unit,
        style: AlertActionStyle = AlertActionStyle.Default,
        enabled: Boolean = true,
        title: @Composable () -> Unit,
    )
}

/** Adds an action with the default style. */

fun AlertDialogActionsScope.default(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Default,
    enabled = enabled,
    title = title,
)

/** Adds a destructive action. */

fun AlertDialogActionsScope.destructive(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Destructive,
    enabled = enabled,
    title = title,
)

/** Adds a cancel action. */

fun AlertDialogActionsScope.cancel(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
) = action(
    onClick = onClick,
    style = AlertActionStyle.Cancel,
    enabled = enabled,
    title = title,
)

/** Cupertino alert dialog with configurable button layout. */

@Composable
@ExperimentalCupertinoApi
fun CupertinoAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    message: (@Composable () -> Unit)? = null,
    containerColor: Color = CupertinoDialogsDefaults.ContainerColor,
    shape: Shape = CupertinoDialogsDefaults.Shape,
    shadowElevation: Dp = CupertinoDialogsTokens.AlertDialogElevation,
    properties: DialogProperties = DialogProperties(),
    buttonsOrientation: Orientation = CupertinoDialogsDefaults.ButtonOrientation,
    buttons: AlertDialogActionsScope.() -> Unit,
) {
    AnimatedDialog(
        properties = properties,
        onDismissRequest = onDismissRequest,
        enterTransition = scaleIn(initialScale = 1.2f) + fadeIn(),
    ) {
        CupertinoSurface(
            Modifier
                .align(Alignment.Center)
                .shadow(
                    elevation = shadowElevation,
                    shape = shape,
                    clip = true,
                ),
            color = containerColor,
        ) {
            Column(
                modifier =
                    Modifier
                        .width(CupertinoDialogsTokens.AlertDialogWidth)
                        .heightIn(min = CupertinoDialogsTokens.AlertDialogMinHeight),
            ) {
                Column(
                    Modifier
                        .padding(CupertinoDialogsTokens.AlertDialogPadding)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AlertDialogTitleMessageSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProvideTextStyle(
                        CupertinoTheme.typography.headline.copy(
                            textAlign = TextAlign.Center,
                        ),
                        content = title,
                    )
                    message?.let {
                        ProvideTextStyle(
                            CupertinoTheme.typography.footnote.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Normal,
                            ),
                            content = it,
                        )
                    }
                }

                val scope = CupertinoAlertDialogButtonsScopeImpl(buttonsOrientation).apply(buttons)

                scope.Content()
            }
        }
    }
}

/** Cupertino action sheet with optional title, message, content, and buttons. */

@Composable
@ExperimentalCupertinoApi
fun CupertinoActionSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: (@Composable () -> Unit)? = null,
    message: (@Composable () -> Unit)? = null,
    containerColor: Color = CupertinoDialogsDefaults.ContainerColor,
    secondaryContainerColor: Color = CupertinoTheme.colorScheme.tertiarySystemBackground,
    properties: DialogProperties = DialogProperties(),
    content: (@Composable () -> Unit)? = null,
    buttons: AlertDialogActionsScope.() -> Unit,
) {
    CompositionLocalProvider(
        LocalContainerColor provides containerColor,
    ) {
        DialogSheet(
            visible = visible,
            onDismissRequest = onDismissRequest,
            dialogProperties = properties,
        ) {
            val hasTitle = title != null || message != null

            val scope =
                CupertinoActionSheetImpl(
                    hasTitle = hasTitle,
                    primaryContainerColor = containerColor,
                    secondaryContainerColor = secondaryContainerColor,
                ).apply(buttons)

            scope.run {
                Content {
                    Column {
                        if (hasTitle) {
                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            paddingValues =
                                                if (message != null && title != null) {
                                                    CupertinoDialogsTokens.ActionSheetTitleAndMessagePaddingValues
                                                } else {
                                                    CupertinoDialogsTokens.ActionSheetTitlePaddingValues
                                                },
                                        ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement =
                                    Arrangement
                                        .spacedBy(CupertinoDialogsTokens.ActionSheetTitleMessageSpacing),
                            ) {
                                CompositionLocalProvider(
                                    LocalContentColor provides CupertinoTheme.colorScheme.secondaryLabel,
                                ) {
                                    if (title != null) {
                                        ProvideTextStyle(
                                            CupertinoTheme.typography.footnote.copy(
                                                fontWeight =
                                                    if (message != null) {
                                                        FontWeight.SemiBold
                                                    } else {
                                                        FontWeight.Normal
                                                    },
                                                textAlign = TextAlign.Center,
                                            ),
                                        ) {
                                            title()
                                        }
                                    }
                                    if (message != null) {
                                        ProvideTextStyle(
                                            CupertinoTheme.typography.footnote.copy(
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Normal,
                                            ),
                                        ) {
                                            message()
                                        }
                                    }
                                }
                            }
                        }
                        if (content != null) {
                            if (hasTitle) {
                                CupertinoHorizontalDivider()
                            }
                            CompositionLocalProvider(
                                LocalContainerColor provides containerColor,
                                content = content,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Immutable
object CupertinoDialogsDefaults {
    val ScrimColor: Color
        @Composable
        @ReadOnlyComposable
        get() = Color.Black.copy(alpha = if (isDark()) .4f else .2f)

    val ButtonOrientation: Orientation = Orientation.Horizontal

    val ContainerColor: Color
        @Composable
        get() = CupertinoColors.systemGray7

    val Shape: ContinuousRoundedRectangle
        @Composable
        @ReadOnlyComposable
        get() = CupertinoTheme.shapes.medium
}

@Composable
@ReadOnlyComposable
internal fun FullscreenPopupProperties(
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = false,
    usePlatformDefaultWidth: Boolean = true,
): DialogProperties =
    DialogProperties(
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside,
        usePlatformDefaultWidth = usePlatformDefaultWidth,
    )

val DialogProperties.platformInsets: Boolean get() = true

@Composable
private fun AnimatedDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    enterTransition: EnterTransition,
    scrimColor: Color = CupertinoDialogsDefaults.ScrimColor,
    content: @Composable BoxScope.() -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            FullscreenPopupProperties(
                dismissOnBackPress = properties.dismissOnBackPress,
                dismissOnClickOutside = properties.dismissOnClickOutside,
                usePlatformDefaultWidth = false,
            ),
    ) {
        CompositionLocalProvider(LocalHapticFeedback provides haptic) {
            var visible by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(0) {
                visible = true
            }

            val animatedScrimColor by animateColorAsState(
                if (visible) scrimColor else scrimColor.copy(alpha = 0f),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawRect(animatedScrimColor)
                            drawContent()
                        }.then(
                            if (properties.dismissOnClickOutside) {
                                Modifier.pointerInput(0) {
                                    detectTapGestures {
                                        onDismissRequest()
                                    }
                                }
                            } else {
                                Modifier
                            },
                        ).then(
                            if (properties.platformInsets) {
                                Modifier
                                    .systemBarsPadding()
                                    .imePadding()
                            } else {
                                Modifier
                            },
                        ),
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = enterTransition,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        content = content,
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties,
    content: @Composable () -> Unit,
) {
    AnimatedSheet(
        visible = visible,
        onDismissRequest = onDismissRequest,
        properties = dialogProperties,
    ) {
        Box(
            Modifier
                .widthIn(max = CupertinoDialogsTokens.ActionSheetMaxWidth)
                .align(Alignment.BottomCenter),
        ) {
            content()
        }
    }
}

@Composable
private fun AnimatedSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    scrimColor: Color = CupertinoDialogsDefaults.ScrimColor,
    content: @Composable (BoxScope.() -> Unit),
) {
    val expandedStates = remember { MutableTransitionState(false) }

    expandedStates.targetState = visible

    if (expandedStates.currentState || expandedStates.targetState) {
        val haptic = LocalHapticFeedback.current
        Dialog(
            onDismissRequest = onDismissRequest,
            properties =
                FullscreenPopupProperties(
                    dismissOnBackPress = properties.dismissOnBackPress,
                    dismissOnClickOutside = properties.dismissOnClickOutside,
                    usePlatformDefaultWidth = false,
                ),
        ) {
            CompositionLocalProvider(LocalHapticFeedback provides haptic) {
                val transition = rememberTransition(expandedStates, "CupertinoSheet")

                val animatedScrimColor by transition.animateColor(
                    transitionSpec = {
                        sheetAnimation()
                    },
                ) {
                    if (it) scrimColor else scrimColor.copy(alpha = 0f)
                }

                val transitionProgress by transition.animateFloat(
                    transitionSpec = {
                        sheetAnimation()
                    },
                ) {
                    if (it) 0f else 1f
                }

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                drawRect(animatedScrimColor)
                                drawContent()
                            }.let {
                                if (properties.dismissOnClickOutside && visible) {
                                    it.pointerInput(0) {
                                        detectTapGestures {
                                            onDismissRequest()
                                        }
                                    }
                                } else {
                                    it
                                }
                            },
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationY = size.height * transitionProgress
                                },
                        content = content,
                    )
                }
            }
        }
    }
}

private class CupertinoAlertDialogButtonsScopeImpl(
    private val orientation: Orientation,
): AlertDialogActionsScope {
    private val buttons = mutableListOf<@Composable () -> Unit>()

    override fun action(
        onClick: () -> Unit,
        style: AlertActionStyle,
        enabled: Boolean,
        title: @Composable () -> Unit,
    ) {
        buttons.add {
            Box(
                Modifier
                    .clickable(
                        enabled = enabled,
                        onClick = onClick,
                        role = Role.Button,
                    ).fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = {
                    val s = style.apply(CupertinoTheme.typography.body, isDark())
                    ProvideTextStyle(
                        s.copy(
                            color =
                                if (enabled) {
                                    s.color
                                } else {
                                    CupertinoTheme.colorScheme.tertiaryLabel
                                },
                        ),
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides LocalTextStyle.current.color,
                        ) {
                            title()
                        }
                    }
                },
            )
        }
    }

    @Composable
    fun Content() {
        CompositionLocalProvider(
            LocalSeparatorColor provides BrightSeparatorColor,
        ) {
            Column {
                CupertinoHorizontalDivider()
                if (orientation == Orientation.Horizontal) {
                    Row(
                        modifier =
                            Modifier
                                .height(CupertinoDialogsTokens.AlertDialogButtonHeight),
                    ) {
                        buttons.fastForEachIndexed { i, btn ->
                            Box(Modifier.weight(1f)) {
                                btn()
                            }
                            if (i != buttons.lastIndex) {
                                CupertinoVerticalDivider()
                            }
                        }
                    }
                } else {
                    buttons.fastForEachIndexed { i, btn ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(CupertinoDialogsTokens.AlertDialogButtonHeight),
                        ) {
                            btn()
                        }
                        if (i != buttons.lastIndex) {
                            CupertinoHorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

private class CupertinoActionSheetImpl(
    private val hasTitle: Boolean,
    private val primaryContainerColor: Color,
    private val secondaryContainerColor: Color,
): AlertDialogActionsScope {
    private val buttons = mutableListOf<Pair<AlertActionStyle, @Composable () -> Unit>>()

    override fun action(
        onClick: () -> Unit,
        style: AlertActionStyle,
        enabled: Boolean,
        title: @Composable () -> Unit,
    ) {
        buttons.add(
            style to {
                Box(
                    modifier =
                        Modifier
                            .clickable(
                                enabled = enabled,
                                onClick = onClick,
                                role = Role.Button,
                            ).fillMaxWidth()
                            .heightIn(min = CupertinoDialogsTokens.ActionSheetButtonHeight),
                    contentAlignment = Alignment.Center,
                    content = {
                        val s = style.apply(CupertinoTheme.typography.title3, isDark())
                        ProvideTextStyle(
                            s.copy(
                                fontWeight =
                                    if (style == AlertActionStyle.Cancel) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    },
                                color =
                                    if (enabled) {
                                        s.color
                                    } else {
                                        CupertinoTheme.colorScheme.tertiaryLabel
                                    },
                            ),
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides LocalTextStyle.current.color,
                            ) {
                                title()
                            }
                        }
                    },
                )
            },
        )
    }

    @Composable
    fun Content(title: (@Composable ColumnScope.() -> Unit)? = null) {
        CompositionLocalProvider(
            LocalSeparatorColor provides BrightSeparatorColor,
        ) {
            Column(
                modifier =
                    Modifier
                        .windowInsetsPadding(CupertinoDialogsTokens.ActionSheetWindowInsets),
            ) {
                CupertinoSurface(
                    modifier =
                        Modifier
                            .padding(
                                start = CupertinoDialogsTokens.ActionSheetSidePadding,
                                end = CupertinoDialogsTokens.ActionSheetSidePadding,
                                top = CupertinoDialogsTokens.ActionSheetSidePadding,
                            ),
                    shape = CupertinoDialogsDefaults.Shape,
                    color = primaryContainerColor,
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    ) {
                        title?.invoke(this)

                        buttons
                            .filter { it.first != AlertActionStyle.Cancel }
                            .fastForEachIndexed { i, btn ->
                                if (i > 0 || hasTitle) {
                                    CupertinoHorizontalDivider()
                                }
                                btn.second()
                            }
                    }
                }

                buttons
                    .filter { it.first == AlertActionStyle.Cancel }
                    .fastForEach {
                        CupertinoSurface(
                            modifier =
                                Modifier
                                    .padding(
                                        start = CupertinoDialogsTokens.ActionSheetSidePadding,
                                        end = CupertinoDialogsTokens.ActionSheetSidePadding,
                                        top = CupertinoDialogsTokens.ActionSheetSidePadding,
                                    ),
                            shape = CupertinoDialogsDefaults.Shape,
                            color = secondaryContainerColor,
                        ) {
                            it.second()
                        }
                    }
            }
        }
    }
}

private fun <T> Transition.Segment<Boolean>.sheetAnimation(): FiniteAnimationSpec<T> =
    if (true isTransitioningTo false) {
        tween(
            durationMillis = 150,
            easing = EaseIn,
        )
    } else {
        cupertinoTween()
    }

internal object CupertinoDialogsTokens {
    val AlertDialogElevation: Dp = 1.dp
    val AlertDialogPadding = CupertinoSectionTokens.HorizontalPadding
    val AlertDialogWidth: Dp = 270.dp
    val AlertDialogMinHeight: Dp = 110.dp
    val AlertDialogTitleMessageSpacing: Dp = 4.dp
    val AlertDialogButtonHeight: Dp = CupertinoSectionTokens.MinHeight

    val ActionSheetTitlePaddingValues = PaddingValues(12.dp)

    val ActionSheetTitleAndMessagePaddingValues =
        PaddingValues(
            top = 12.dp,
            start = 12.dp,
            end = 12.dp,
            bottom = 24.dp,
        )

    val ActionSheetMaxWidth: Dp = 500.dp
    val ActionSheetSidePadding = 8.dp
    val ActionSheetButtonHeight: Dp = 56.dp
    val ActionSheetTitleMessageSpacing: Dp = 6.dp
    val ActionSheetWindowInsets: WindowInsets
        @Composable
        get() =
            WindowInsets.navigationBars.union(
                WindowInsets(
                    bottom =
                    ActionSheetSidePadding,
                ),
            )
}
