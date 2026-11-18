/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text

/**
 * Shared layout for pushed screens. The scrolling content is the Haze source;
 * the fade blur and top bar stay as siblings so neither samples itself. The bar
 * keeps its back button visible and reveals the compact title after scrolling.
 */
@Composable
fun ScreenChromeBox(
    title: String,
    titleAlpha: Float,
    pageColor: Color,
    chrome: GlassScreenChrome,
    onBack: (() -> Unit)?,
    onBackLongClick: (() -> Unit)?,
    actions: @Composable () -> Unit,
    titleAlphaProvider: (() -> Float)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Explicit rather than inherited from whatever the NavHost paints:
            // a page that paints nothing shows the app background through the
            // blur's own tint, which is what makes a top strip read as a
            // different colour from the rest of the screen.
            .background(pageColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Inside the source, so the blur samples a filled canvas rather
                // than transparency at the top of the page.
                .background(pageColor)
                .then(chrome.screenModifier()),
            content = content,
        )

        TopFadeBlur(
            hazeState = chrome.hazeState,
            pageColor = pageColor,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        FrostedTopBar(
            onBack = onBack,
            onBackLongClick = onBackLongClick,
            title = title,
            titleAlpha = titleAlpha,
            titleAlphaProvider = titleAlphaProvider,
            actions = actions,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

/**
 * The chrome around a body the caller lays out itself — a grid, a WebView, a
 * two-pane layout, an existing list you do not want to restructure.
 *
 * The body is handed the screen's [GlassScreenChrome]: put
 * [GlassScreenChrome.contentModifier] on the scrolling root so the bar's glass
 * has something to refract. The caller also owns [titleAlpha], since only it
 * knows what scrolling means on that layout — pass
 * [rememberTitleCollapseProgress] where there is a list, `1f` where the page
 * does not scroll and the title should simply stay.
 */
@Composable
fun ScreenScaffold(
    title: String,
    titleAlpha: Float,
    onBack: (() -> Unit)? = null,
    onBackLongClick: (() -> Unit)? = null,
    pageColor: Color = AppTheme.colorScheme.background,
    actions: @Composable () -> Unit = {},
    titleAlphaProvider: (() -> Float)? = null,
    content: @Composable BoxScope.(GlassScreenChrome) -> Unit,
) {
    val chrome = rememberGlassScreenChrome(background = pageColor)
    CompositionLocalProvider(LocalAppBackdrop provides chrome.backdrop) {
        ScreenChromeBox(
            title = title,
            titleAlpha = titleAlpha,
            titleAlphaProvider = titleAlphaProvider,
            pageColor = pageColor,
            chrome = chrome,
            onBack = onBack,
            onBackLongClick = onBackLongClick,
            actions = actions,
        ) {
            content(chrome)
        }
    }
}

/** A screen's large title, the one the compact bar takes over from. */
@Composable
fun ScreenLargeTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = AppTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = AppTheme.colorScheme.onSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 14.dp),
    )
}

/**
 * A page whose body is a plain scrolling [Column].
 *
 * The status-bar clearance is a [Spacer] *inside* the scroll rather than
 * padding around it, which is the whole point: content has to travel under the
 * bar for the blur to have anything to frost.
 */
@Composable
fun ScreenPage(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onBackLongClick: (() -> Unit)? = null,
    pageColor: Color = AppTheme.colorScheme.background,
    horizontalPadding: Dp = 0.dp,
    actions: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    scrollState: ScrollState = rememberScrollState(),
    largeTitle: @Composable () -> Unit = { ScreenLargeTitle(title) },
    content: @Composable ColumnScope.() -> Unit,
) {
    val chrome = rememberGlassScreenChrome(background = pageColor)
    // Measured, not guessed: the title block's height is the exact distance the
    // page has to travel before the compact title should be fully in.
    var titleBlockHeight by remember { mutableIntStateOf(1) }

    CompositionLocalProvider(LocalAppBackdrop provides chrome.backdrop) {
        ScreenChromeBox(
            title = title,
            titleAlpha = 0f,
            titleAlphaProvider = {
                // Read ScrollState in draw phase via provider chain:
                // FrostedTopBar invokes this only inside graphicsLayer/derivedStateOf.
                (scrollState.value / titleBlockHeight.toFloat()).coerceIn(0f, 1f)
            },
            pageColor = pageColor,
            chrome = chrome,
            onBack = onBack,
            onBackLongClick = onBackLongClick,
            actions = actions,
        ) {
            Column(
                modifier = modifier
                    .then(chrome.contentModifier())
                    .verticalScroll(scrollState)
                    .windowInsetsPadding(
                        LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal),
                    )
                    .padding(horizontal = horizontalPadding),
                verticalArrangement = verticalArrangement,
            ) {
                Column(Modifier.onSizeChanged { titleBlockHeight = it.height.coerceAtLeast(1) }) {
                    Spacer(
                        Modifier.windowInsetsPadding(
                            LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top),
                        ),
                    )
                    largeTitle()
                }

                content()

                Spacer(
                    Modifier
                        .height(80.dp)
                        .windowInsetsPadding(
                            LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom),
                        ),
                )
            }
        }
    }
}

/**
 * The [LazyColumn] flavour, for a page whose body is a real list.
 *
 * Emits the title block as `item("title")`, so [rememberTitleCollapseProgress]
 * can measure it off the list's own layout info instead of the size callback
 * [ScreenPage] needs.
 */
@Composable
fun ScreenLazyPage(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onBackLongClick: (() -> Unit)? = null,
    pageColor: Color = AppTheme.colorScheme.background,
    horizontalPadding: Dp = 0.dp,
    actions: @Composable () -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    largeTitle: @Composable () -> Unit = { ScreenLargeTitle(title) },
    content: LazyListScope.() -> Unit,
) {
    val chrome = rememberGlassScreenChrome(background = pageColor)
    val lazyTitleProgress = rememberTitleCollapseProgressState(listState, titleItemKey = "title")

    CompositionLocalProvider(LocalAppBackdrop provides chrome.backdrop) {
        ScreenChromeBox(
            title = title,
            titleAlpha = 0f,
            titleAlphaProvider = { lazyTitleProgress.value },
            pageColor = pageColor,
            chrome = chrome,
            onBack = onBack,
            onBackLongClick = onBackLongClick,
            actions = actions,
        ) {
            val sideInsets = LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal).asPaddingValues()
            val direction = androidx.compose.ui.platform.LocalLayoutDirection.current
            LazyColumn(
                state = listState,
                modifier = modifier.then(chrome.contentModifier()),
                contentPadding = PaddingValues(
                    start = sideInsets.calculateStartPadding(direction) + horizontalPadding,
                    end = sideInsets.calculateEndPadding(direction) + horizontalPadding,
                ),
            ) {
                item(key = "title") {
                    Column {
                        Spacer(
                            Modifier.windowInsetsPadding(
                                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top),
                            ),
                        )
                        largeTitle()
                    }
                }

                content()

                item(key = "screen_lazy_page_bottom_spacer") {
                    Spacer(
                        Modifier
                            .height(80.dp)
                            .windowInsetsPadding(
                                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom),
                            ),
                    )
                }
            }
        }
    }
}
