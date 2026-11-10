/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.component.HeroSource
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.LocalAppBackdrop
import androidx.compose.ui.text.font.FontWeight
import com.convx.music.ui.component.GlassCircleButton
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.convx.music.ui.utils.bounceClick
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import com.convx.music.ui.component.rememberHeroTopBlur
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.chrisbanes.haze.hazeSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.convx.music.utils.rememberPreference
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.utils.youtubeBrowseRoute
import com.convx.music.ui.component.shimmer.ListItemPlaceHolder
import com.convx.music.ui.component.shimmer.ShimmerHost
import com.convx.music.ui.utils.backToMain
import com.convx.music.viewmodels.MoodAndGenresViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodAndGenresScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: MoodAndGenresViewModel = koinViewModel(),
) {
    val localConfiguration = LocalConfiguration.current
    val itemsPerRow = if (localConfiguration.orientation == ORIENTATION_LANDSCAPE) 3 else 2

    val moodAndGenresList by viewModel.moodAndGenres.collectAsStateWithLifecycle()

    val tint = Color.Black
    val onTint = AppleTokens.onColor(tint)

    // Unattached: safe ambient backdrop for anything inside the list (glass
    // sampling it early-returns instead of forming a RenderNode cycle).
    val heroBackdrop = rememberLayerBackdrop()

    // Attached to a Box wrapping the list, and handed only to the floating
    // chrome row — a *sibling* of that layer, never a descendant. Without this
    // the back button's liquidGlass had nothing to sample and rendered as a flat
    // translucent circle, which is why this screen looked like glass was off.
    val listBackdrop = rememberLayerBackdrop(
        onDraw = remember(tint) {
            val bg = tint
            { drawRect(bg); drawContent() }
        }
    )
    val backdropFreeze = rememberBackdropFreeze()
    val heroZoom = rememberHeroZoom()
    val lazyListState = rememberLazyListState()

    HeroBackground(
        tint = tint,
        heroSource = HeroSource.Default,
        bottomGradient = true,
        heroScale = heroZoom.scale,
        // Progressive top blur: the artwork dissolves under the status bar as
        // the list scrolls up — same treatment as the other hero screens.
        topBlurProgress = rememberHeroTopBlur(lazyListState),
        modifier = Modifier.fillMaxSize(),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val hazeState = remember { dev.chrisbanes.haze.HazeState() }
        Box(modifier = Modifier.fillMaxSize()) {
            // Capture through a plain Box; LazyColumn's recycled item layers do not flatten reliably.
            Box(modifier = Modifier
            .nestedScroll(backdropFreeze.connection)

            // Cache list content so sibling redraws do not re-record the backdrop.
            .graphicsLayer()
            .layerBackdrop(listBackdrop, frozen = backdropFreeze.frozen)
            .graphicsLayer()
            .hazeSource(hazeState)) {
            LazyColumn(
                state = lazyListState,
                overscrollEffect = heroZoom.listOverscroll(),
                modifier = Modifier.heroPullZoom(heroZoom),
                contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
            ) {
                item(key = "header") {
                    LargeScreenTitle(
                        title = stringResource(R.string.mood_and_genres),
                        color = onTint,
                    )
                }

                if (moodAndGenresList == null) {
                    item(key = "mood_and_genres_shimmer") {
                        ShimmerHost(
                            modifier = Modifier.animateItem()
                        ) {
                            repeat(8) {
                                ListItemPlaceHolder()
                            }
                        }
                    }
                }

                moodAndGenresList?.forEachIndexed { index, moodAndGenres ->
                    item(key = "mood_and_genres_section_$index") {
                        Column(
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 6.dp),
                        ) {
                            NavigationTitle(
                                title = moodAndGenres.title,
                            )
                            moodAndGenres.items.chunked(itemsPerRow).forEach { row ->
                                Row {
                                    row.forEach { item ->
                                        MoodAndGenresButton(
                                            title = item.title,
                                            stripeColor = item.stripeColor,
                                            onClick = {
                                                navController.navigate(youtubeBrowseRoute(item.endpoint.browseId, item.endpoint.params))
                                            },
                                            modifier =
                                            Modifier
                                                .weight(1f)
                                                .padding(6.dp),
                                        )
                                    }

                                    repeat(itemsPerRow - row.size) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    Spacer(Modifier.height(100.dp))
                }
            }
            }

            com.convx.music.ui.component.TopFadeBlur(
                hazeState = hazeState,
                pageColor = tint,
                modifier = Modifier.align(Alignment.TopCenter),
            )
            // Resolve `align` here because the provider lambda below is not a BoxScope.
            val chromeRowModifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .windowInsetsPadding(appTopBarWindowInsets())
                .padding(horizontal = 16.dp, vertical = 8.dp)

            CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
                Row(
                    modifier = chromeRowModifier,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlassCircleButton(
                        onClick = { navController.navigateUp() },
                        onLongClick = { navController.backToMain() },
                    ) {
                        SymbolIcon(symbol = sf("chevron.left"), contentDescription = null)
                    }

                    Spacer(Modifier.weight(1f))
                }
            }
        }
      }
    }
}

@Composable
fun MoodAndGenresButton(
    title: String,
    stripeColor: Long? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (stripeColor != null) {
        Color(stripeColor).copy(alpha = 0.25f)
    } else {
        AppTheme.colorScheme.surfaceContainer
    }
    
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier =
        modifier
            .height(MoodAndGenresButtonHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .bounceClick(onClick = onClick)
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = title,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

val MoodAndGenresButtonHeight = 48.dp
