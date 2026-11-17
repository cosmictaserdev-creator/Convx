/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music

import com.convx.music.ui.component.sf
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFChartBar
import com.composables.sfsymbols.monochrome.SFClock
import com.composables.sfsymbols.monochrome.SFPlus
import com.convx.music.R
import com.convx.music.ui.component.AccountAvatar
import com.convx.music.ui.component.CreatePlaylistDialog
import com.convx.music.ui.component.FrostedTopBar
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.TopFadeBlur
import com.convx.music.ui.component.rememberAppBackgroundTint
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.ios.component.IosGlyph
import com.convx.music.ui.ios.compat.DropdownMenu
import com.convx.music.ui.ios.compat.DropdownMenuItem
import com.convx.music.ui.ios.compat.IosMenuDivider
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.menuTrigger
import com.convx.music.ui.ios.foundation.IosGlassSpec
import com.convx.music.ui.ios.foundation.iosGlass
import com.convx.music.ui.screens.Screens
import com.convx.music.ui.theme.LocalAccentTextColor
import com.convx.music.ui.theme.ConvxLogoFontFamily
import com.convx.music.ui.theme.SfProFontFamily
import com.convx.music.ui.theme.isHueless
import dev.chrisbanes.haze.HazeState

/** The persistent top bar: edge blur plus the large title and the library/profile actions. */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
fun AppTopBar(
    visible: Boolean,
    showRail: Boolean,
    startInset: Dp,
    hazeState: HazeState,
    backdrop: Backdrop?,
    cutoutInsets: WindowInsets,
    statusBarTop: Dp,
    effectiveRoute: String?,
    titleRes: Int?,
    accountImageUrl: String?,
    accountName: String,
    navController: NavHostController,
    onAccountClick: () -> Unit,
    chromeVisible: Boolean = visible,
) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Keep the blur fixed during scrolling; route visibility controls when it hides.
                TopFadeBlur(
                    hazeState = hazeState,
                    pageColor = rememberAppBackgroundTint(
                        AppTheme.colorScheme.background
                    ),
                    backdrop = backdrop,
                    startFade = if (showRail) startInset else 0.dp,
                )
                // Titles/wordmark stay neutral per theme; only hero-tinted
                // screens keep the artwork-derived accent text.
                val topBarAmbient = LocalAmbientBackgroundColor.current.value
                val topBarTitleColor = if (topBarAmbient.isSpecified && !topBarAmbient.isHueless()) {
                    LocalAccentTextColor.current
                } else {
                    AppTheme.colorScheme.onSurface
                }
                if (chromeVisible) FrostedTopBar(
                    // The blur above spans the full width; only the content clears the side bar and cutouts.
                    modifier = Modifier
                        .windowInsetsPadding(cutoutInsets.only(WindowInsetsSides.Start + WindowInsetsSides.End))
                        .padding(start = startInset)
                        .padding(top = if (showRail) 0.dp else {
                            val titleTop = if (effectiveRoute == Screens.Library.route) 61.dp else 62.dp
                            maxOf(statusBarTop, titleTop) - statusBarTop
                        }),
                    leadingInset = if (showRail) 12.dp else 16.dp,
                    largeTitle = !showRail,
                    leading = {
                        // SF Pro Text is wider than Apple's Display face at this size.
                        Text(
                            text = if (effectiveRoute == Screens.Home.route) stringResource(R.string.app_name)
                                else titleRes?.let { stringResource(it) } ?: "",
                            style = AppTheme.typography.titleLarge.copy(
                                fontFamily = if (effectiveRoute == Screens.Home.route) ConvxLogoFontFamily else SfProFontFamily,
                                fontWeight = if (effectiveRoute == Screens.Home.route) FontWeight.Normal else FontWeight.Bold,
                                fontSize = 34.sp,
                                lineHeight = 41.sp,
                                letterSpacing = (-0.6).sp,
                            ),
                            color = topBarTitleColor,
                            maxLines = 1,
                            modifier = if (showRail) Modifier else Modifier.height(41.dp),
                        )
                    },
                    actions = {
                        if (effectiveRoute == Screens.Library.route) {
                            LibraryTopBarActions(navController)
                        } else {
                            GlassCircleButton(size = 44.dp, applyAdditiveChrome = false, forceFlat = true, onClick = onAccountClick) {
                                BadgedBox(badge = {}) {
                                    AccountAvatar(
                                        accountImageUrl, accountName,
                                        description = stringResource(R.string.account),
                                    )
                                }
                            }
                        }
                    },
                )
            }
        }
}

@Composable
private fun LibraryTopBarActions(navController: NavHostController) {
    var menuExpanded by remember {
        mutableStateOf(false) }
    var showCreatePlaylist by remember { mutableStateOf(false) }

    Box {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .iosGlass(shape = CircleShape, spec = IosGlassSpec.Control, interactive = true)
                .menuTrigger { menuExpanded = true },
        ) {
            IosGlyph(
                symbol = sf("ellipsis"),
                tint = AppTheme.colorScheme.onSurface,
                box = 22.dp,
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.new_playlist_ellipsis)) },
                leadingIcon = {
                    IosGlyph(sf("SFPlus"), AppTheme.colorScheme.onSurface, 19.dp, enterOnly = true)
                },
                onClick = {
                    menuExpanded = false
                    showCreatePlaylist = true
                },
            )
            IosMenuDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.history)) },
                leadingIcon = {
                    IosGlyph(sf("SFClock"), AppTheme.colorScheme.onSurface, 19.dp, enterOnly = true)
                },
                onClick = {
                    menuExpanded = false
                    navController.navigate("history")
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.stats)) },
                leadingIcon = {
                    IosGlyph(sf("SFChartBar"), AppTheme.colorScheme.onSurface, 19.dp, enterOnly = true)
                },
                onClick = {
                    menuExpanded = false
                    navController.navigate("stats")
                },
            )
        }
    }

    if (showCreatePlaylist) {
        CreatePlaylistDialog(onDismiss = { showCreatePlaylist = false })
    }

    Spacer(Modifier.width(8.dp))
}
