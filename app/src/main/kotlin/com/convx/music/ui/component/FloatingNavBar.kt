/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.convx.music.ui.component

import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.utils.MenuIconChrome
import com.composables.sfsymbols.SfRenderingMode
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.activity.compose.BackHandler
import com.convx.music.LocalAmbientBackgroundColor
import com.convx.music.constants.AppBackgroundColorKey
import com.convx.music.constants.AppTextColorKey
import com.convx.music.ui.theme.isHueless
import com.convx.music.utils.rememberPreference
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.animateColorAsState
import com.convx.music.ui.utils.Motion
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.dualtone.SFSquareGrid2x2Fill
import com.composables.sfsymbols.monochrome.SFHouseFill
import com.composables.sfsymbols.monochrome.SFMusicNoteSquareStackFill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.R
import com.convx.music.constants.SearchSource
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.elasticStretchLayerBlock
import com.convx.music.ui.ios.component.IosContextMenuOverlay
import com.convx.music.ui.ios.component.IosMenuSection
import com.convx.music.ui.player.FloatingMiniPlayer
import com.convx.music.ui.screens.Screens
import com.convx.music.ui.screens.search.DynamicSearchPlaceholder
import com.convx.music.ui.component.floatingtabbar.FloatingTabBarDefaults
import com.convx.music.ui.component.floatingtabbar.FoldingTabBar
import com.convx.music.ui.component.floatingtabbar.FoldingTabCell
import com.convx.music.ui.component.floatingtabbar.FoldingTabGroup
import com.convx.music.ui.component.floatingtabbar.FloatingTabBarScrollConnection
import com.convx.music.ui.component.floatingtabbar.clipWhilePressed
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.additiveChrome
import com.convx.music.ui.utils.vibrantChrome
import kotlinx.coroutines.delay

// Continuous capsule shape supports the tab puck morph and the bar glass.
private val NavBarShape = com.convx.music.ui.component.shapes.NavContinuousCapsule

// Keep search mode the same height as the expanded tab row.
private val NavBarSearchBarHeight = 48.dp

// Open tab row height: 32dp glyph, label and the selector's inset.
private val NavBarRowHeight = 68.dp

// Let the player and tab icons clear before opening the keyboard.
private const val KeyboardOpenDelayMs = 260L

/**
 * Bottom navigation bar with scroll collapse, an optional player accessory,
 * and search mode driven by [LocalNavSearchState]. Glass surfaces sample the
 * app backdrop when liquid glass is enabled.
 */
@Composable
fun AppFloatingNavBar(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    scrollConnection: FloatingTabBarScrollConnection,
    tabMotion: GraphicsLayerScope.() -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    showPlayerAccessory: Boolean = false,
    onAccessoryClick: () -> Unit = {},
    onAccessoryLyricsClick: (() -> Unit)? = null,
    onAccessoryQueueClick: (() -> Unit)? = null,
    onAccessoryLongClick: (() -> Unit)? = null,
    // Builds each tab menu at the call site, where the navigation controller is available.
    tabContextMenu: (@Composable (Screens) -> List<IosMenuSection>)? = null,
) {
    val navSearch = LocalNavSearchState.current
    // Plain chrome: the old backdrop-pixel sampler flipped the bar between light and dark while scrolling.
    androidx.compose.runtime.CompositionLocalProvider(LocalSampledChromeDark provides (if (com.convx.music.ui.theme.LocalForcedDark.current) true else null)) {

    AnimatedContent(
        targetState = navSearch.keyboardActive,
        modifier = modifier,
        transitionSpec = {
            if (targetState) {
                (slideInVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                ) { it / 2 } + fadeIn()) togetherWith
                    (slideOutVertically(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                    ) { it } + fadeOut())
            } else {
                (slideInVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                ) { it } + fadeIn()) togetherWith
                    (slideOutVertically(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                    ) { it / 2 } + fadeOut())
            }
        },
        label = "navBarKeyboardActive",
    ) { keyboardActive ->
        if (keyboardActive) {
            NavBarSearchInputBar(
                state = navSearch,
                pureBlack = pureBlack,
                modifier = Modifier.fillMaxWidth().graphicsLayer(tabMotion),
            )
        } else {
            AppFloatingNavBarChrome(
                navigationItems = navigationItems,
                currentRoute = currentRoute,
                onItemClick = onItemClick,
                scrollConnection = scrollConnection,
                tabMotion = tabMotion,
                pureBlack = pureBlack,
                showPlayerAccessory = showPlayerAccessory,
                onAccessoryClick = onAccessoryClick,
                onAccessoryLyricsClick = onAccessoryLyricsClick,
                onAccessoryQueueClick = onAccessoryQueueClick,
                onAccessoryLongClick = onAccessoryLongClick,
                tabContextMenu = tabContextMenu,
                searchModeActive = navSearch.visualActive,
                navSearch = navSearch,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    }
}

@Composable
private fun AppFloatingNavBarChrome(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    scrollConnection: FloatingTabBarScrollConnection,
    tabMotion: GraphicsLayerScope.() -> Unit,
    pureBlack: Boolean,
    showPlayerAccessory: Boolean,
    onAccessoryClick: () -> Unit,
    onAccessoryLyricsClick: (() -> Unit)?,
    onAccessoryQueueClick: (() -> Unit)?,
    onAccessoryLongClick: (() -> Unit)?,
    tabContextMenu: (@Composable (Screens) -> List<IosMenuSection>)?,
    searchModeActive: Boolean,
    navSearch: NavSearchState,
    modifier: Modifier,
) {
    // System back while search-expanded/search-inline (keyboard not active yet,
    // that state has its own BackHandler in NavBarSearchInputBar) -> same
    // animate-then-navigate exit as the bar's own back arrow / icon tap.
    BackHandler(enabled = searchModeActive, onBack = navSearch.onExit)

    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val appleMusicUi = LocalAppleMusicUi.current

    val (appBackgroundColorInt) = rememberPreference(AppBackgroundColorKey, 0)
    val (appTextColorInt) = rememberPreference(AppTextColorKey, 0)
    // A custom color overrides the system surface; glass stays transparent.
    val backgroundColor = when {
        useGlass -> Color.Transparent
        pureBlack -> Color.Black
        appBackgroundColorInt != 0 -> Color(appBackgroundColorInt)
        else -> AppTheme.colorScheme.surfaceContainerLow
    }
    // Theme-only: the sampled backdrop appearance flips while scrolling and made the tabs flicker.
    val isDark = AppTheme.colorScheme.surface.luminance() <= 0.5f
    val ambientColor = Color.Unspecified // plain: no artwork-driven tinting
    // The Library screens' accent, so the selected tab matches the icons on those screens.
    val selectedIconColor = IosTheme.tint
    val selectedLabelColor = selectedIconColor
    // Keep every tab readable, including after restoring an older theme backup.
    val unselectedIconColor = AppTheme.colorScheme.onSurface.copy(alpha = 0.82f)
    val unselectedLabelColor = unselectedIconColor

    val targetRimColor = com.convx.music.ui.theme.GlassTintTokens.pluckColor(isDark, ambientColor, glassConfig.highlightColor)
    val rimTintColor by animateColorAsState(
        targetValue = targetRimColor,
        animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "navBarRimTint",
    )
    val navHighlightAlpha = com.convx.music.ui.theme.GlassTintTokens.pluckOpacity(isDark, ambientColor.isSpecified && !ambientColor.isHueless())

    val backdropFrozen = LocalAppBackdropFrozen.current
    val combinedBackdropFrozen: () -> Boolean = remember(backdropFrozen) {
        { com.convx.music.ui.player.PlayerMorph.glassFrozen || backdropFrozen() }
    }
    val tabBarContentModifier = if (useGlass) {
        Modifier.liquidGlass(
            config = glassConfig,
            shape = NavBarShape,
            highlightAlpha = navHighlightAlpha,
            frozen = combinedBackdropFrozen,
            rimTintColor = rimTintColor,
            outerShadow = false,
        )
    } else {
        Modifier
    }

    val searchScreen = navigationItems.firstOrNull { it == Screens.Search }
    val tabScreens = remember(navigationItems) { navigationItems.filter { it != Screens.Search } }

    // Search is not a content tab; detail routes keep the indicator on the last tab.
    val selectedTabKey = rememberStickySelectedRoute(currentRoute, tabScreens)

    val accessoryContentColor = when {
        appTextColorInt != 0 -> Color(appTextColorInt)
        useGlass -> IosTheme.colors.label
        pureBlack -> Color.White
        else -> AppTheme.colorScheme.onSurface
    }
    // One mini player for every fold state; [isInline] only restyles it.
    val playerAccessory: (@Composable (Modifier, Boolean) -> Unit)? =
        if (showPlayerAccessory) {
            { accessoryModifier, isInline ->
                FloatingMiniPlayer(
                    isInline = isInline,
                    contentColor = accessoryContentColor,
                    onClick = onAccessoryClick,
                    onLyricsClick = onAccessoryLyricsClick,
                    onQueueClick = onAccessoryQueueClick,
                    onLongClick = onAccessoryLongClick,
                    modifier = accessoryModifier.then(tabBarContentModifier),
                )
            }
        } else {
            null
        }

    // Fit tabs to the available width and anchor a held tab menu to its cell.
    var heldTab by remember { mutableStateOf<Pair<Screens, Rect>?>(null) }
    val heldSections = heldTab?.let { (screen, _) -> tabContextMenu?.invoke(screen) }.orEmpty()

    IosContextMenuOverlay(
        sections = heldSections,
        anchor = heldTab?.second?.takeIf { heldSections.isNotEmpty() },
        onDismissRequest = { heldTab = null },
    )

    val barTabs = remember(tabScreens, searchScreen) { tabScreens + listOfNotNull(searchScreen) }
    val appBackdrop = LocalAppBackdrop.current

    FoldingTabBar(
        selectedTabKey = selectedTabKey,
        scrollConnection = scrollConnection,
        tabMotion = tabMotion,
        rowHeight = NavBarRowHeight,
        tabBarContentModifier = tabBarContentModifier,
        colors = FloatingTabBarDefaults.colors(
            backgroundColor = backgroundColor,
            accessoryBackgroundColor = backgroundColor,
        ),
        shapes = FloatingTabBarDefaults.shapes(
            tabBarShape = NavBarShape,
            accessoryShape = NavBarShape,
        ),
        sizes = FloatingTabBarDefaults.sizes(),
        elevations = FloatingTabBarDefaults.elevations(),
        modifier = modifier,
        accessory = playerAccessory,
        searchMode = searchModeActive,
        searchBarContent = { contentModifier ->
            SearchBarPlaceholder(
                state = navSearch,
                contentColor = accessoryContentColor,
                modifier = contentModifier,
                useGlass = useGlass,
            )
        },
        expandedTabs = { tabs, surfaceBackdrop ->
            val selectedIndex = tabs.indexOfFirst { it.key == selectedTabKey }.coerceAtLeast(0)
            val selectedIndexState = androidx.compose.runtime.rememberUpdatedState(selectedIndex)
            FoldingTabGroup(
                selectedTabIndex = { selectedIndexState.value },
                onTabSelected = { index -> tabs.getOrNull(index)?.onClick?.invoke() },
                backdrop = if (useGlass) appBackdrop else surfaceBackdrop,
                surfaceBackdrop = surfaceBackdrop,
                tabsCount = tabs.size,
                accentColor = selectedIconColor,
                containerColor = Color.Transparent,
                height = NavBarRowHeight,
                indicatorVisible = tabs.any { it.key == selectedTabKey },
            ) {
                tabs.forEach { tab ->
                    FoldingTabCell(onClick = tab.onClick, onLongClick = tab.onLongClick) {
                        tab.icon()
                        tab.title()
                    }
                }
            }
        },
    ) {
        barTabs.forEach { screen ->
            val isSelected = screen.route == selectedTabKey
            tab(
                key = screen.route,
                title = {
                    // Inactive labels get the same chrome as the glyphs: light ink, colour-dodged onto the glass.
                    Text(
                        text = stringResource(screen.titleId),
                        color = if (isSelected) selectedLabelColor else unselectedLabelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                },
                icon = {
                    val name = screen.navSymbol()
                    val tint = if (isSelected) selectedIconColor else unselectedIconColor
                    if (name != null) {
                        // Headroom: the bounce scales the glyph up, so it is drawn at 80% inside the slot.
                        Box(Modifier.size(screen.glyphSize()), contentAlignment = Alignment.Center) {
                            SymbolIcon(
                                symbol = sf(name),
                                contentDescription = stringResource(screen.titleId),
                                size = screen.glyphSize() * 0.8f,
                                chrome = 0f,
                                tint = tint,
                                mode = SfRenderingMode.Monochrome,
                                bounceOnEnter = true,
                                // The whole row bounces as the bar folds inline or expands.
                                replayOn = scrollConnection.isInline,
                                selected = isSelected,
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(screen.icon(appleMusicUi)),
                            contentDescription = stringResource(screen.titleId),
                            tint = tint,
                            modifier = Modifier.size(screen.glyphSize()),
                        )
                    }
                },
                onClick = {
                    when {
                        screen == Screens.Search -> if (searchModeActive) navSearch.onTapBar() else navSearch.onTapSearchIcon()
                        searchModeActive -> navSearch.onExit()
                        else -> onItemClick(screen, isRouteSelected(currentRoute, screen.route, navigationItems))
                    }
                },
                onLongClick = tabContextMenu?.let { { bounds: Rect -> heldTab = screen to bounds } },
            )
        }
    }
}

private val NavInactiveGreyDark = Color(0xFF8E8E93)
private val NavInactiveGreyLight = Color(0xFF6C6C70)

/** Glyphs that fill their whole viewbox are drawn smaller so every tab reads the same size. */
private fun Screens.glyphSize() = when (this) {
    Screens.Explore, Screens.Search -> 28.dp
    else -> 32.dp
}

/** SF Symbol name for destinations that have one; null keeps the destination's own drawable. */
internal fun Screens.navSymbol(): String? = when (this) {
    Screens.Home -> "house.fill"
    Screens.Library -> "music.note.square.stack.fill"
    Screens.Explore -> "square.grid.2x2.fill"
    Screens.Search -> "magnifyingglass"
    Screens.Songs -> "music.note"
    Screens.Radio -> "dot.radiowaves.left.and.right"
    Screens.ListenTogether -> "person.2.fill"
    else -> null
}

/**
 * Pre-keyboard search bar content — the wide slot [FloatingTabBar] expands the
 * search circle into once [searchMode] is on. Unfocused, placeholder-only; a tap
 * anywhere but the leading back arrow / trailing source toggle requests focus via
 * [NavSearchState.onTapBar], which flips [NavSearchState.keyboardActive] and swaps
 * the whole nav bar for [NavBarSearchInputBar]. Same back arrow / placeholder /
 * source-toggle layout as that keyboard-active pill, just unfocused — reads as
 * one continuous bar rather than a different-looking intermediate step.
 */
@Composable
private fun SearchBarPlaceholder(
    state: NavSearchState,
    contentColor: Color,
    modifier: Modifier,
    useGlass: Boolean = true,
) {
    // Plus-blend chrome only reads on dark: added to a light surface it disappears.
    val additiveOk = glassUsesDarkAppearance()
    // Same finger-tracking glow as the keyboard-active pill (NavBarSearchInputBar)
    // and the normal search circle (ExpandedStandaloneTab/InlineStandaloneTab).
    val glowScope = rememberCoroutineScope()
    val glow = remember(glowScope) { InteractiveHighlight(animationScope = glowScope) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(glow.gestureModifier)
            // Clip the sheen to the bar shape while pressed.
            .clipWhilePressed(glow, NavBarShape)
            .then(glow.modifier)
            .bounceClick(onClick = state.onTapBar)
            .padding(start = 4.dp, end = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .bounceClick { state.onExit() }
                .padding(8.dp)
        ) {
            SymbolIcon(bounceOnEnter = true, symbol = sf("arrow.left"), contentDescription = stringResource(R.string.dismiss), modifier = Modifier.fillMaxSize().additiveChrome(useGlass && additiveOk), tint = contentColor)
        }
        Box(Modifier.weight(1f)) {
            // After submit the keyboard closes and this bar takes over: keep showing
            // what was searched instead of falling back to the hint.
            if (state.query.text.isNotEmpty()) {
                Text(
                    text = state.query.text,
                    style = TextStyle(color = contentColor, fontSize = 17.sp),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            } else {
                DynamicSearchPlaceholder(
                    searchSource = state.searchSource,
                    style = TextStyle(color = contentColor.copy(alpha = 0.6f), fontSize = 17.sp),
                )
            }
        }
        if (state.canToggleSource) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .bounceClick { state.onToggleSource() }
                    .padding(9.dp)
            ) {
                SymbolIcon(bounceOnEnter = true, symbol = 
                        when (state.searchSource) {
                            SearchSource.LOCAL -> sf("music.note.list")
                            SearchSource.ONLINE -> sf("globe")
                        }
                    , contentDescription = null, modifier = Modifier.fillMaxSize().additiveChrome(useGlass && additiveOk), tint = contentColor)
            }
        }
    }
}

/** Search input that replaces the navigation bar while focused and follows the keyboard. */
@Composable
fun NavBarSearchInputBar(
    state: NavSearchState,
    pureBlack: Boolean,
    modifier: Modifier,
) {
    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val ambientColor = Color.Unspecified // plain: no artwork-driven tinting
    val isDark = glassUsesDarkAppearance()
    // On glass, take the adaptive glass content colour rather than a flat white —
    // the search field is the one piece of chrome the user is actively reading.
    val onTint = when {
        useGlass -> IosTheme.colors.label
        pureBlack -> Color.White
        else -> AppTheme.colorScheme.onSurface
    }
    val searchIconTint = when {
        !isDark -> com.convx.music.ui.theme.GlassTintTokens.IconOpaqueBlackishLight
        useGlass && ambientColor.isSpecified -> ambientColor.asSoftIconTint()
        else -> onTint
    }
    val searchRimTint = com.convx.music.ui.theme.GlassTintTokens.pluckColor(isDark, ambientColor)
    val searchHighlightAlpha = com.convx.music.ui.theme.GlassTintTokens.pluckOpacity(isDark, ambientColor.isSpecified && !ambientColor.isHueless())
    val pillShape = ContinuousRoundedRectangle(percent = 50)
    val coroutineScope = rememberCoroutineScope()
    // Finger-tracking glow, same as the nav bar puck's InteractiveHighlight: a
    // soft radial light follows the touch point across the glass pill.
    val pillGlow = remember(coroutineScope) { InteractiveHighlight(animationScope = coroutineScope) }

    // Keyboard's own back press just closes the keyboard (back to search-expanded)
    // rather than exiting search entirely — the visible back arrow does that.
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    BackHandler {
        focusManager.clearFocus()
        keyboardController?.hide()
        state.onCloseKeyboard()
    }

    // Let the mini player/icon finish sliding down and out (the AnimatedContent
    // transition in AppFloatingNavBar) before pulling the keyboard up — opening
    // it immediately made the two animations fight for the same screen space.
    LaunchedEffect(Unit) {
        delay(KeyboardOpenDelayMs)
        state.focusRequester.requestFocus()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .imePadding()
            .navigationBarsPadding()
            // Tight above the keyboard (top-only breathing room) rather than
            // padded on both sides — it should read as docked to the keyboard,
            // not floating with a gap above it.
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp, bottom = 2.dp)
            .height(NavBarSearchBarHeight)
            .graphicsLayer(elasticStretchLayerBlock(pillGlow))
            .clip(pillShape)
            .then(
                if (useGlass) {
                    Modifier.liquidGlass(
                        config = glassConfig.copy(
                            surfaceOpacity = if (ambientColor.isSpecified) 0.36f else 0.12f,
                        ),
                        shape = pillShape,
                        highlightAlpha = searchHighlightAlpha,
                        rimTintColor = searchRimTint,
                    )
                } else {
                    Modifier.background(onTint.copy(alpha = 0.15f))
                }
            )
            .then(pillGlow.gestureModifier)
            .then(pillGlow.modifier)
            .padding(start = 11.dp, end = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .bounceClick { state.onExit() }
                .padding(12.dp)
        ) {
            SymbolIcon(bounceOnEnter = true, symbol = sf("arrow.left"), contentDescription = stringResource(R.string.dismiss), modifier = Modifier.fillMaxSize().additiveChrome(useGlass && isDark), tint = searchIconTint)
        }
        Box(modifier = Modifier.weight(1f)) {
            if (state.query.text.isEmpty()) {
                DynamicSearchPlaceholder(
                    searchSource = state.searchSource,
                    style = TextStyle(color = onTint.copy(alpha = 0.6f), fontSize = 17.sp),
                )
            }
            BasicTextField(
                value = state.query,
                onValueChange = state.onQueryChange,
                singleLine = true,
                textStyle = TextStyle(color = onTint, fontSize = 17.sp),
                cursorBrush = SolidColor(onTint),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                // Some IMEs render the requested Search action as Done/Go instead
                // (or don't reliably report it at all) — handle every plausible
                // submit action so the button always does something.
                keyboardActions = KeyboardActions(
                    onSearch = { state.onSubmit(state.query.text) },
                    onDone = { state.onSubmit(state.query.text) },
                    onGo = { state.onSubmit(state.query.text) },
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        // The tablet always mounts this input, so its automatic
                        // focus must enter the same state as tapping the phone field.
                        if (it.isFocused && !state.keyboardActive) state.onTapBar()
                    }
                    .focusRequester(state.focusRequester)
                    // Some OEM keyboards (Samsung's included) don't reliably report
                    // the IME search action through KeyboardActions.onSearch — catch
                    // the raw Enter key too so submit isn't only reachable by tapping
                    // a suggestion.
                    .onKeyEvent {
                        if (it.key == Key.Enter) {
                            state.onSubmit(state.query.text)
                            true
                        } else {
                            false
                        }
                    }
            )
        }
        if (state.query.text.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .bounceClick { state.onQueryChange(TextFieldValue("")) }
                    .padding(12.dp)
            ) {
                SymbolIcon(bounceOnEnter = true, symbol = sf("xmark"), contentDescription = null, modifier = Modifier.fillMaxSize().additiveChrome(useGlass && isDark), tint = searchIconTint)
            }
        }
        if (state.canToggleSource) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .bounceClick { state.onToggleSource() }
                    .padding(12.dp)
            ) {
                SymbolIcon(bounceOnEnter = true, symbol = 
                        when (state.searchSource) {
                            SearchSource.LOCAL -> sf("music.note.list")
                            SearchSource.ONLINE -> sf("globe")
                        }
                    , contentDescription = null, modifier = Modifier.fillMaxSize().additiveChrome(useGlass && isDark), tint = searchIconTint)
            }
        }
    }
}
