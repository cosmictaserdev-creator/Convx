/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens

import com.convxy.music.ui.utils.appTopBarWindowInsets
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.component.LargeScreenTitle
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.utils.rememberHeroZoom
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.ui.component.HeroSource
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.LocalAppBackdrop
import androidx.compose.ui.text.font.FontWeight
import com.convxy.music.ui.component.GlassCircleButton
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.convxy.music.ui.utils.bounceClick
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.convxy.music.utils.rememberPreference
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.component.shimmer.ListItemPlaceHolder
import com.convxy.music.ui.component.shimmer.ShimmerHost
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.viewmodels.MoodAndGenresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodAndGenresScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: MoodAndGenresViewModel = hiltViewModel(),
) {
    val localConfiguration = LocalConfiguration.current
    val itemsPerRow = if (localConfiguration.orientation == ORIENTATION_LANDSCAPE) 3 else 2

    val moodAndGenresList by viewModel.moodAndGenres.collectAsState()

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

    HeroBackground(
        tint = tint,
        heroSource = HeroSource.Default,
        bottomGradient = true,
        heroScale = heroZoom.scale,
        modifier = Modifier.fillMaxSize(),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Capture from a plain Box wrapping the LazyColumn, not the
            // LazyColumn itself: it promotes its items to their own RenderNodes
            // for recycling, which a capture attached directly to it doesn't
            // reliably flatten.
            Box(modifier = Modifier
            .nestedScroll(backdropFreeze.connection)

            // OUTER layer, and it must come BEFORE layerBackdrop: the layer has to
            // enclose the backdrop node, or that node's draw re-runs whenever anything
            // else in the window redraws. The mini player, the playing indicator and
            // the position poll are all siblings that tick on their own schedule, and
            // each tick was re-recording this entire list. MainActivity pairs an outer
            // and inner layer for exactly this; the screen-local backdrops were left
            // with only the inner half.
            .graphicsLayer()
            .layerBackdrop(listBackdrop, frozen = backdropFreeze.frozen)
            // Content becomes ONE cached RenderNode, so the backdrop's
            // layer.record { drawContent() } records a single drawRenderNode
            // instead of re-issuing every op in the list.
            .graphicsLayer()) {
            LazyColumn(
                // No bounce here: the top pull drives the hero zoom instead.
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
                                                navController.navigate("youtube_browse/${item.endpoint.browseId}?params=${item.endpoint.params}")
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

            // Top bar logic. `align` is resolved out here, in BoxScope, because
            // the provider lambda below is not a BoxScope.
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
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = null,
                        )
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
        MaterialTheme.colorScheme.surfaceContainer
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

val MoodAndGenresButtonHeight = 48.dp
