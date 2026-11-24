/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import com.convx.music.ui.utils.MenuIconChrome
import com.convx.music.ui.utils.additiveChrome
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.convx.music.ui.theme.isHueless
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import com.convx.music.LocalAmbientBackgroundColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.ui.utils.sharedArtworkSource
import coil3.compose.AsyncImage
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.dualtone.SFSquareGrid2x2Fill
import com.convx.music.R
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.backdrop.drawBackdrop
import com.convx.music.ui.component.backdrop.effects.blur
import com.convx.music.ui.component.backdrop.effects.lens
import com.convx.music.ui.component.backdrop.highlight.Highlight
import com.convx.music.ui.component.backdrop.shadow.Shadow
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.player.FloatingMiniPlayer
import com.convx.music.ui.screens.Screens
import com.convx.music.ui.screens.library.apple.SfStyle
import com.convx.music.ui.theme.LocalAccentColor
import com.convx.music.ui.utils.fadingEdge
import com.convx.music.designsystem.ConvxDesignTokens

/** Panel corner. A tall panel, so a rounded rect rather than the phone bar's capsule. */
private val SideBarShape = ContinuousRoundedRectangle(20.dp)

/** Row/puck corner inside the panel: a full capsule. */
private val SideRowShape = ContinuousRoundedRectangle(percent = 50)

/** Width of the whole floating panel. */
val SideBarWidth: Dp = ConvxDesignTokens.Metrics.SidebarWidth

/** Width when collapsed to an icon-only rail: just enough for the icon and padding. */
val SideBarCollapsedWidth: Dp = ConvxDesignTokens.Metrics.SidebarCollapsedWidth

/**
 * Container width at or above which the app lays itself out for tab view on its
 * own. Below it the side bar only appears if the Appearance toggle forces it.
 */
val TabletWidthThreshold: Dp = ConvxDesignTokens.Metrics.TabletWidthThreshold

/** Panel inset from the screen edge, and the gap it leaves to the content. */
val SideBarMargin: Dp = ConvxDesignTokens.Metrics.SidebarInset
private val SideBarDesignHeight: Dp = 600.dp

/** Content padding keeps rows clear of the panel at rest while letting them scroll underneath. */
val SideBarContentInset: Dp = SideBarWidth + 20.dp

/** Keep the puck's rim and shadow visible when it is not pressed. */
private const val PuckRestHighlightAlpha = 0.5f
private const val PuckRestShadowAlpha = 0.35f

private val SideTabHeight = ConvxDesignTokens.Metrics.SidebarRowHeight
private val SideBarContentPadding = PaddingValues(horizontal = 6.dp)

/** Fixed leading slot keeps labels aligned across icons, artwork, and avatars. */
private val SideRowLeadingSlot = ConvxDesignTokens.Metrics.SidebarLeadingSlot
private val SideRowIconSize = ConvxDesignTokens.Metrics.SidebarIconSize

/** Width of the floating mini player as a fraction of the available content width. */
const val FloatingMiniPlayerWidthFraction = ConvxDesignTokens.Metrics.MiniPlayerWidthFraction

/** Height as a share of the pill's own width, clamped to stay a bar. */
private const val FloatingMiniPlayerAspect = 0.11f
private val FloatingMiniPlayerMinHeight = ConvxDesignTokens.Metrics.MiniPlayerHeight
private val FloatingMiniPlayerMaxHeight = ConvxDesignTokens.Metrics.MiniPlayerHeight

/** Scopes that let the side panel and the top pill morph into each other as one shared element. */
@OptIn(ExperimentalSharedTransitionApi::class)
class NavMorph(val scope: SharedTransitionScope, val visibility: AnimatedVisibilityScope)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun Modifier.navMorph(
    morph: NavMorph?,
    key: String,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
): Modifier {
    if (morph == null) return this
    return with(morph.scope) {
        this@navMorph.sharedBounds(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = morph.visibility,
            resizeMode = resizeMode,
            boundsTransform = { _, _ -> spring(dampingRatio = 0.85f, stiffness = 380f) },
        )
    }
}

/** A non-tab destination in the side bar: history, stats, a playlist, and so on. */
@Immutable
data class SideBarLink(
    val label: String,
    val iconRes: Int? = null,
    /** Playlist/album art, shown instead of [iconRes] when set. */
    val thumbnailUrl: String? = null,
    /** Enables the artwork morph when this row opens an album or playlist. */
    val sharedArtworkId: String? = null,
    val onClick: () -> Unit,
)

@Immutable
data class SideBarSection(
    /** null renders the rows with no heading. */
    val title: String? = null,
    val links: List<SideBarLink>,
)

@Immutable
private data class SideTab(
    val screen: Screens,
    val selected: Boolean,
    val onClick: () -> Unit,
)

/** Full-height floating glass navigation with fixed header/footer and scrollable tabs over screen content. */
@Composable
fun AppFloatingSideBar(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    sections: List<SideBarSection>,
    footer: @Composable (collapsed: Boolean) -> Unit,
    collapsed: Boolean,
    onToggleCollapsed: () -> Unit,
    onEditClick: () -> Unit,
    puckColorIsDefault: Boolean = true,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    morph: NavMorph? = null,
) {
    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.SIDE_PANEL) && isGlassAllowed()
    val animationScope = rememberCoroutineScope()
    val targetPanelWidth = if (collapsed) SideBarCollapsedWidth else SideBarWidth
    val panelWidth by animateDpAsState(
        targetValue = targetPanelWidth,
        animationSpec = spring(0.9f, 400f),
        label = "sideBarWidth",
    )
    // The glow is measured against the whole panel, so keep its radius small enough for one row.
    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(animationScope = animationScope, radiusScale = 0.3f)
    }

    val panelChrome = com.convx.music.ui.ios.foundation.IosTheme.colors.label
    val backgroundColor = when {
        useGlass -> Color.Transparent
        pureBlack -> Color.Black
        else -> if (AppTheme.colorScheme.surface.luminance() <= 0.5f) Color(0xFF0A0A0A) else Color(0xFFF2F2F7)
    }
    // A capsule when collapsed; the fixed panel radius would look square at this width.
    val panelShape = if (collapsed) SideRowShape else SideBarShape
    // Freeze the shared backdrop while the panel width animates, and honor the app-level freeze.
    val appBackdropFrozen = LocalAppBackdropFrozen.current
    val backdropFrozen: () -> Boolean = remember(appBackdropFrozen, panelWidth, targetPanelWidth) {
        { panelWidth != targetPanelWidth || appBackdropFrozen() }
    }
    val panelSurface = if (useGlass) {
        Modifier.liquidGlass(
            config = glassConfig.forSidePanel(isDark = glassUsesDarkAppearance()),
            shape = panelShape,
            highlightAlpha = 0.3f,
            frozen = backdropFrozen,
        )
    } else {
        Modifier
    }

    // A short landscape screen scales the whole panel down so the header, tabs and footer all fit.
    BoxWithConstraints(modifier) {
    com.convx.music.ui.utils.ProvideFittedDensity(maxHeight, SideBarDesignHeight, minScale = 0.72f) {
    Column(
        Modifier
            .navMorph(morph, "navContainer")
            .width(panelWidth)
            .shadow(shape = panelShape, elevation = 10.dp)
            .background(backgroundColor, panelShape)
            .clip(panelShape)
            .then(panelSurface)
            .then(interactiveHighlight.modifier)
            .then(interactiveHighlight.gestureModifier),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(start = if (collapsed) 0.dp else 16.dp, end = if (collapsed) 0.dp else 6.dp),
            horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.SpaceBetween,
        ) {
            if (!collapsed) {
                Text(
                    text = stringResource(R.string.edit),
                    style = SfStyle(17.sp, 22.sp, FontWeight.SemiBold),
                    color = panelChrome,
                    modifier = Modifier.height(44.dp).clickable(onClick = onEditClick).padding(top = 11.dp),
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onToggleCollapsed),
            ) {
                SymbolIcon(
                    symbol = sf("sidebar.left"),
                    contentDescription = stringResource(
                        if (collapsed) R.string.expand_side_panel else R.string.collapse_side_panel
                    ),
                    size = 20.dp,
                    tint = panelChrome,
                )
            }
        }

        // Pinned above the scroll area: the primary tabs stay reachable however
        // far the library list below them is scrolled.
        SideNavTabs(
            navigationItems = navigationItems,
            currentRoute = currentRoute,
            onItemClick = onItemClick,
            useGlass = useGlass,
            backgroundColor = backgroundColor,
            collapsed = collapsed,
            frozen = backdropFrozen,
            puckColorIsDefault = puckColorIsDefault,
            morph = morph,
            modifier = Modifier.padding(horizontal = 10.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                // Rows dissolve into the pinned wordmark and footer instead of
                // being clipped by a hard edge.
                .fadingEdge(vertical = 18.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            // The library sections (playlists, history, ...) need a text label to
            // mean anything, so the icon-only rail keeps just the primary tabs.
            if (!collapsed) {
                sections.forEach { section ->
                    section.title?.let { title ->
                        Text(
                            text = title,
                            style = SfStyle(17.sp, 22.sp, FontWeight.SemiBold, (-0.43).sp),
                            color = panelChrome.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 12.dp, top = 24.dp, bottom = 14.dp),
                        )
                    }
                    section.links.forEach { link ->
                        SideBarLinkRow(link = link, contentColor = panelChrome)
                    }
                }
            }
        }

        footer(collapsed)
    }
    }
    }
}

/**
 * The phone's floating nav bar, stood on its end — plain glass puck, no gestures.
 *
 * The puck springs to whichever row is selected and nothing else drives it. It
 * also draws UNDER the icons instead of sampling a hidden tinted copy of them, so
 * every row has exactly one icon on screen: sampling icon art through a lens at
 * this row width read as a smeared ghost, not as material.
 */
@Composable
private fun SideNavTabs(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    useGlass: Boolean,
    backgroundColor: Color,
    collapsed: Boolean,
    frozen: () -> Boolean,
    puckColorIsDefault: Boolean,
    morph: NavMorph? = null,
    modifier: Modifier = Modifier,
) {
    // The tab group uses the phone nav bar's glass settings; only the panel uses side-panel tuning.
    val glassConfig = LocalGlassEffectConfig.current
    val appleMusicUi = LocalAppleMusicUi.current

    val sideThemeDark = AppTheme.colorScheme.surface.luminance() <= 0.5f
    val sideAmbient = Color.Unspecified // plain: no artwork-driven tinting

    val selectedContentColor = com.convx.music.ui.ios.foundation.IosTheme.tint
    val inactiveContentColor = if (sideThemeDark) Color(0xFF8E8E93) else Color(0xFF6C6C70)

    val puckWash = if (appleMusicUi && sideThemeDark && puckColorIsDefault) {
        Color(0xFF333333)
    } else if (glassConfig.puckColor.isSpecified) {
        glassConfig.puckColor
    } else if (sideAmbient.isSpecified && !sideAmbient.isHueless()) {
        sideAmbient.asPuckTint(sideThemeDark)
    } else {
        com.convx.music.ui.theme.GlassTintTokens.neutralTintColor(sideThemeDark)
    }
    val isHeroContext = sideAmbient.isSpecified && !sideAmbient.isHueless()
    val puckRestAlpha = if (appleMusicUi && sideThemeDark && puckColorIsDefault) {
        0.80f
    } else if (!glassConfig.puckColor.isSpecified) {
        if (sideThemeDark) {
            if (isHeroContext) 0.80f else 0.55f
        } else {
            if (isHeroContext) 0.70f else 0.45f
        }
    } else {
        glassConfig.puckOpacity.coerceIn(0f, 1f)
    }

    // Keep the last selected tab highlighted on detail routes; clicks still use the current route.
    val selectedRoute = rememberStickySelectedRoute(currentRoute, navigationItems)
    val tabs = navigationItems.map { screen ->
        SideTab(
            screen = screen,
            selected = screen.route == selectedRoute,
            onClick = { onItemClick(screen, isRouteSelected(currentRoute, screen.route, navigationItems)) },
        )
    }
    val tabsCount = tabs.size
    if (tabsCount == 0) return

    val density = LocalDensity.current
    val backdrop = if (useGlass) LocalAppBackdrop.current else null
    val tabHeightPx = with(density) { SideTabHeight.toPx() }

    val selectedIndex = tabs.indexOfFirst { it.selected }.coerceAtLeast(0)

    // Use an underdamped spring to match the phone nav bar's puck motion.
    val puckPosition = remember { Animatable(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        puckPosition.animateTo(selectedIndex.toFloat(), spring(0.6f, 380f, 0.001f))
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(SideTabHeight * tabsCount + SideBarContentPadding.calculateTopPadding() * 2)
    ) {
        // Draw the puck under the real icons so the backdrop does not duplicate them.
        Box(
            Modifier
                .padding(SideBarContentPadding)
                .graphicsLayer { translationY = puckPosition.value * tabHeightPx }
                .fillMaxWidth()
                .height(SideTabHeight)
                .then(
                    if (backdrop != null) {
                        Modifier.drawBackdrop(
                            backdrop = backdrop,
                            shape = { SideRowShape },
                            effects = {
                                // The panel's own blur, not the global one: the puck
                                // is cut from the panel it sits in, and reading the
                                // shared radius made it a window onto sharper content
                                // than the frost around it.
                                blur(glassConfig.sidePanelBlurRadius.dp.toPx())
                                lens(
                                    32f.dp.toPx(),
                                    28f.dp.toPx(),
                                    chromaticAberration = true,
                                )
                            },
                            highlight = { null },
                            shadow = { Shadow(alpha = PuckRestShadowAlpha) },
                            onDrawSurface = { drawRect(puckWash.copy(alpha = puckRestAlpha)) },
                            frozen = frozen,
                        )
                    } else {
                        Modifier
                            .shadow(shape = SideRowShape, elevation = 3.dp)
                            .background(if (sideThemeDark) Color(0xFF282828) else Color(0xFFD1D1D6), SideRowShape)
                            .clip(SideRowShape)
                    }
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(SideBarContentPadding),
        ) {
            tabs.forEach { tab ->
                SideTabRow(
                    tab = tab,
                    appleMusicUi = appleMusicUi,
                    contentColor = if (tab.selected) selectedContentColor else inactiveContentColor,
                    alpha = 1f,
                    collapsed = collapsed,
                    morph = morph,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SideTabHeight)
                        .clip(SideRowShape)
                        .tapClickable(onClick = tab.onClick),
                )
            }
        }
    }
}

/**
 * The now playing pill for tab view: the same [FloatingMiniPlayer] the phone bar
 * docks as its accessory, on the same glass and the same capsule, floating free
 * in the content rather than attached to anything.
 *
 * Place it inside a container that has already padded out [SideBarContentInset]
 * — it fills that container's width by [FloatingMiniPlayerWidthFraction] and
 * centres itself in it, so "centred" means the content area, not the screen.
 */
@Composable
fun BoxWithConstraintsScope.AppFloatingNowPlayingPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    onLyricsClick: (() -> Unit)? = null,
    onQueueClick: (() -> Unit)? = null,
) {
    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.SIDE_PANEL) && isGlassAllowed()
    val pillShape = ContinuousRoundedRectangle(percent = 50)

    val width = maxWidth * FloatingMiniPlayerWidthFraction
    val height = (width * FloatingMiniPlayerAspect)
        .coerceIn(FloatingMiniPlayerMinHeight, FloatingMiniPlayerMaxHeight)

    val background = when {
        useGlass -> Color.Transparent
        pureBlack -> Color.Black
        else -> AppTheme.colorScheme.surfaceContainerLow
    }
    val surface = if (useGlass) {
        // The mini player uses global glass settings, independently of the side panel rail.
        Modifier.liquidGlass(config = glassConfig, shape = pillShape, highlightAlpha = 0.3f)
    } else {
        Modifier.background(background, pillShape)
    }

    FloatingMiniPlayer(
        isInline = false,
        // See AppNavigation: hardcoded white disappeared on light themes.
        contentColor = if (useGlass) com.convx.music.ui.ios.foundation.IosTheme.colors.label else AppTheme.colorScheme.onSurface,
        onClick = onClick,
        onLyricsClick = onLyricsClick,
        onQueueClick = onQueueClick,
        modifier = modifier
            .width(width)
            .height(height)
            .shadow(shape = pillShape, elevation = 10.dp)
            .clip(pillShape)
            .then(surface),
    )
}

/**
 * The pinned bottom row: the account avatar when signed in, otherwise a settings
 * cog. Same destination as the phone top bar's trailing button.
 */
@Composable
fun SideBarAccountRow(
    accountImageUrl: String?,
    accountName: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    collapsed: Boolean = false,
) {
    val contentColor = com.convx.music.ui.ios.foundation.IosTheme.colors.label
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .clip(SideRowShape)
            .clickable(onClick = onClick)
            .padding(horizontal = if (collapsed) 20.dp else 25.dp),
    ) {
        AccountAvatar(accountImageUrl, accountName, size = 32.dp,
            description = stringResource(R.string.account))
        if (!collapsed) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = accountName.ifBlank { stringResource(R.string.account) },
                color = contentColor,
                style = SfStyle(13.sp, 18.sp, FontWeight.SemiBold, (-0.08).sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SideTabRow(
    tab: SideTab,
    appleMusicUi: Boolean,
    contentColor: Color,
    alpha: Float,
    modifier: Modifier = Modifier,
    collapsed: Boolean = false,
    morph: NavMorph? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.Start,
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .padding(horizontal = if (collapsed) 0.dp else 12.dp),
    ) {
        Box(
            modifier = Modifier.size(SideRowLeadingSlot),
            contentAlignment = Alignment.Center,
        ) {
            val navName = tab.screen.navSymbol()
            if (navName != null) {
                SymbolIcon(
                    symbol = sf(navName),
                    contentDescription = stringResource(tab.screen.titleId),
                    size = SideRowIconSize,
                    chrome = MenuIconChrome,
                    tint = LocalAccentColor.current,
                    mode = com.composables.sfsymbols.SfRenderingMode.Monochrome,
                    bounceOnEnter = tab.selected,
                    selected = tab.selected,
                )
            } else {
                Icon(painter = painterResource(tab.screen.icon(appleMusicUi)), contentDescription = stringResource(tab.screen.titleId), modifier = Modifier.size(SideRowIconSize), tint = LocalAccentColor.current)
            }
        }
        if (!collapsed) {
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(tab.screen.titleId),
                color = contentColor,
                style = SfStyle(
                    ConvxDesignTokens.Type.SidebarLabel,
                    ConvxDesignTokens.Type.SidebarLabelLineHeight,
                    tracking = ConvxDesignTokens.Type.SidebarLabelTracking,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.navMorph(morph, "tab_${tab.screen.route}"),
            )
        }
    }
}

@Composable
private fun SideBarLinkRow(link: SideBarLink, contentColor: Color) {
    com.convx.music.ui.utils.ArtworkSourceHost(Modifier) { sourceModifier ->
    val artworkOwner = com.convx.music.ui.utils.rememberArtworkSourceOwner()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = sourceModifier
            .fillMaxWidth()
            .height(SideTabHeight)
            .clip(SideRowShape)
            .clickable(onClick = link.onClick)
            .padding(horizontal = 12.dp),
    ) {
        Box(
            modifier = Modifier.size(SideRowLeadingSlot),
            contentAlignment = Alignment.Center,
        ) {
            when {
                link.thumbnailUrl != null -> AsyncImage(
                    model = link.thumbnailUrl,
                    onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(artworkOwner, link.thumbnailUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .sharedArtworkSource(link.sharedArtworkId, link.thumbnailUrl, artworkOwner)
                        .fillMaxSize()
                        .clip(ContinuousRoundedRectangle(6.dp)),
                )

                link.iconRes != null -> Icon(
                    painter = painterResource(link.iconRes),
                    contentDescription = null,
                    tint = LocalAccentColor.current,
                    modifier = Modifier.size(SideRowIconSize),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = link.label,
            color = contentColor,
            style = SfStyle(
                ConvxDesignTokens.Type.SidebarLabel,
                ConvxDesignTokens.Type.SidebarLabelLineHeight,
                tracking = ConvxDesignTokens.Type.SidebarLabelTracking,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    }
}

/** Below this width the pill keeps icons for every tab and a label only for the selected one. */
private val PillLabelsMinWidth = 520.dp

/** The closed side bar: menu button, primary tabs and search in one glass capsule, centred at the top. */
@Composable
fun AppTabViewTopPill(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    onOpenSidebar: () -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    morph: NavMorph? = null,
) {
    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.SIDE_PANEL) && isGlassAllowed()
    val shape = ContinuousRoundedRectangle(percent = 50)
    val chrome = com.convx.music.ui.ios.foundation.IosTheme.colors.label
    val accent = com.convx.music.ui.ios.foundation.IosTheme.tint
    val selectedRoute = rememberStickySelectedRoute(currentRoute, navigationItems)
    val tabs = navigationItems.filter { it != Screens.Search }
    val search = navigationItems.firstOrNull { it == Screens.Search }

    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier.fillMaxWidth().padding(horizontal = 12.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        val labels = maxWidth >= PillLabelsMinWidth
        val surface = if (useGlass) {
            Modifier.liquidGlass(config = glassConfig, shape = shape, highlightAlpha = 0.3f)
        } else {
            Modifier.background(if (pureBlack) Color.Black else AppTheme.colorScheme.surfaceContainerLow, shape)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .navMorph(morph, "navContainer")
                .shadow(shape = shape, elevation = 10.dp)
                .clip(shape)
                .then(surface)
                .padding(6.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onOpenSidebar),
            ) {
                SymbolIcon(
                    symbol = sf("line.3.horizontal"),
                    contentDescription = stringResource(R.string.expand_side_panel),
                    size = 20.dp,
                    tint = chrome,
                )
            }
            tabs.forEach { screen ->
                PillTab(
                    screen = screen,
                    selected = screen.route == selectedRoute,
                    labels = labels,
                    chrome = chrome,
                    morph = morph,
                    onClick = { onItemClick(screen, isRouteSelected(currentRoute, screen.route, navigationItems)) },
                )
            }
            search?.let { screen ->
                Spacer(Modifier.width(4.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(accent.copy(alpha = 0.2f), CircleShape)
                        .clip(CircleShape)
                        .clickable { onItemClick(screen, isRouteSelected(currentRoute, screen.route, navigationItems)) },
                ) {
                    SymbolIcon(
                        symbol = sf("magnifyingglass"),
                        contentDescription = stringResource(screen.titleId),
                        size = 18.dp,
                        tint = accent,
                    )
                }
            }
        }
    }
}

@Composable
private fun PillTab(
    screen: Screens,
    selected: Boolean,
    labels: Boolean,
    chrome: Color,
    morph: NavMorph?,
    onClick: () -> Unit,
) {
    val symbol = screen.navSymbol()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .height(40.dp)
            .background(if (selected) chrome.copy(alpha = 0.14f) else Color.Transparent, SideRowShape)
            .clip(SideRowShape)
            .clickable(onClick = onClick)
            .padding(horizontal = if (labels) 14.dp else 11.dp),
    ) {
        if (!labels && symbol != null) {
            SymbolIcon(
                symbol = sf(symbol),
                contentDescription = stringResource(screen.titleId),
                size = 20.dp,
                tint = if (selected) chrome else chrome.copy(alpha = 0.7f),
                mode = com.composables.sfsymbols.SfRenderingMode.Monochrome,
                selected = selected,
            )
        }
        if (labels || selected) {
            Text(
                text = stringResource(screen.titleId),
                color = if (selected) chrome else chrome.copy(alpha = 0.85f),
                style = SfStyle(17.sp, 22.sp, if (selected) FontWeight.SemiBold else FontWeight.Medium, (-0.43).sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.navMorph(morph, "tab_${screen.route}"),
            )
        }
    }
}

/**
 * Release-triggered [clickable], same as the phone bar's: firing on finger-DOWN
 * navigates even when the touch turns into a puck drag or a scroll.
 */
@Composable
private fun Modifier.tapClickable(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return clickable(
        onClick = onClick,
        indication = null,
        interactionSource = interactionSource,
    )
}
