/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.utils.plusStart
import com.convx.music.ui.utils.bleedStart
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFMusicNote
import com.composables.sfsymbols.monochrome.SFXmark
import com.convx.music.R
import com.convx.music.models.MediaMetadata
import com.convx.music.ui.component.ArtworkBlurMesh
import com.convx.music.ui.component.LocalItemPrefs
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.YouTubeGridItem
import com.convx.music.ui.component.flipAndMultiply
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.screens.library.apple.Cover
import com.convx.music.ui.theme.LocalPageMetrics
import com.convx.music.ui.utils.rememberGridSpacing
import com.convx.music.ui.utils.sharedArtworkSource
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem

/** Adds a titled horizontal rail; Top Picks can use the larger hero layout. */
internal fun LazyListScope.figmaRail(
    deps: HomeSectionDeps,
    mediaMetadata: State<MediaMetadata?>,
    isPlaying: State<Boolean>,
    key: String,
    title: String,
    route: String,
    items: List<YTItem>,
    hero: Boolean = false,
) {
    if (items.isEmpty()) return
    if (!hero && items.isSongShelf()) {
        item(key = "${key}_title", contentType = "section_title") {
            NavigationTitle(
                title = title,
                modifier = Modifier.animateItem(),
                onClick = { deps.navController.navigate(route) },
            )
        }
        shelfList(key = "${key}_list", deps = deps, entries = items.distinctBy { it.id }.map { it.toShelfEntry(deps) })
        return
    }
    if (!hero) {
        item(key = "${key}_title", contentType = "section_title") {
            NavigationTitle(
                title = title,
                modifier = Modifier.animateItem(),
                onClick = { deps.navController.navigate(route) },
            )
        }
    }
    item(key = "${key}_list", contentType = if (hero) "hero_rail" else "rail") {
        // Compensate for tile padding so covers keep their intended size, gap, and edge inset.
        val gs = rememberGridSpacing()
        val prefs = LocalItemPrefs.current
        val railState = rememberLazyListState()
        Column(Modifier.animateItem()) {
        if (hero) {
            // Keep the hero title close to the cards without regular section spacing.
            CompositionLocalProvider(
                LocalPageMetrics provides LocalPageMetrics.current.copy(gutter = 16.dp, headerTop = 0.dp, headerBottom = 4.dp),
            ) {
                NavigationTitle(
                    title = title,
                    onClick = { deps.navController.navigate(route) },
                )
            }
        }
        // Add the tile's horizontal padding to keep the cover itself 154.dp wide.
        CompositionLocalProvider(LocalItemPrefs provides prefs.copy(gridCardHeightOverrideDp = (154.dp + gs).value.toInt())) {
            LazyRow(
                state = railState,
                contentPadding = PaddingValues(
                    horizontal = if (hero) 16.dp else (20.dp - gs / 2).coerceAtLeast(0.dp),
                ).plusStart(deps.sideInset),
                horizontalArrangement = Arrangement.spacedBy(if (hero) 10.dp else (14.dp - gs).coerceAtLeast(0.dp)),
                modifier = Modifier.fillMaxWidth().bleedStart(deps.sideInset),
            ) {
                items(items, key = { it.id }, contentType = { if (hero) "figma_hero" else "figma_card" }) { item ->
                    HomePopMenu(
                        pop = HomePop.Yt(item),
                        deps = deps,
                        thumbnailUrl = item.thumbnail,
                        title = item.title,
                        subtitle = item.shelfSubtitle(),
                        onClick = { deps.openYtItem(item) },
                        modifier = Modifier.animateItem(),
                    ) {
                        if (hero) {
                            FigmaTopPickCard(item = item)
                        } else {
                            YouTubeGridItem(
                                item = item,
                                isActive = item.id == mediaMetadata.value?.id,
                                isPlaying = isPlaying.value && item.id == mediaMetadata.value?.id,
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

/**
 * Apple Music Home's "Transfer music" card: 16dp from the edges, 20dp continuous
 * corners on secondarySystemBackground, red glyph + 15sp body, a 24dp grey close
 * disc top-right and a full-width tinted "Get Started" capsule underneath.
 */
@Composable
internal fun HomeSignInCard(onGetStarted: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val colors = IosTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(ContinuousRoundedRectangle(20.dp))
            .background(colors.secondarySystemBackground)
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            SymbolIcon(symbol = sf("SFMusicNote"), contentDescription = null, size = 44.dp, modifier = Modifier.padding(top = 8.dp).padding(6.dp), tint = colors.systemRed)
            Text(
                text = stringResource(R.string.home_sign_in_card_body),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                letterSpacing = (-0.23).sp,
                fontWeight = FontWeight.Medium,
                color = colors.label,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, top = 8.dp, end = 8.dp),
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(colors.tertiarySystemFill)
                    .clickable(onClick = onDismiss),
            ) {
                SymbolIcon(symbol = sf("SFXmark"), contentDescription = stringResource(R.string.dismiss), size = 10.dp, tint = colors.secondaryLabel)
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(end = 4.dp)
                .fillMaxWidth()
                .height(44.dp)
                .clip(ContinuousRoundedRectangle(12.dp))
                .background(colors.accent.copy(alpha = 0.12f))
                .clickable(onClick = onGetStarted),
        ) {
            Text(
                text = stringResource(R.string.get_started),
                fontSize = 15.sp,
                letterSpacing = (-0.23).sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.accent,
            )
        }
    }
}

/**
 * Top Picks card with artwork reflected in a frosted footer and centered overlay text.
 */
@Composable
internal fun FigmaTopPickCard(
    item: YTItem,
    modifier: Modifier = Modifier,
) {
    FigmaTopPickCard(
        thumbnail = item.thumbnail,
        title = item.title,
        label = when (item) {
            is SongItem -> item.artists.firstOrNull()?.name
            is AlbumItem -> item.artists?.firstOrNull()?.name
            is PlaylistItem -> item.author?.name
            is ArtistItem -> null
        },
        detail = when (item) {
            is AlbumItem -> item.year?.toString()
            is SongItem -> item.album?.name
            else -> null
        },
        // A song opens its album, so it claims the album's id for the cover flight.
        artworkKey = if (item is SongItem) item.album?.id else item.id,
        modifier = modifier,
    )
}

@Composable
internal fun FigmaTopPickCard(
    thumbnail: String?,
    title: String,
    label: String?,
    detail: String?,
    artworkKey: String?,
    modifier: Modifier = Modifier,
) {
    val shape = ContinuousRoundedRectangle(20.dp)
    val context = LocalContext.current
    val request = remember(thumbnail) {
        ImageRequest.Builder(context).data(thumbnail).crossfade(false).build()
    }
    Column(modifier = modifier.width(242.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier
                .height(323.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.16f),
                    spotColor = Color.Black.copy(alpha = 0.16f),
                )
                .clip(shape),
        ) {
            AsyncImage(
                model = request,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .sharedArtworkSource(artworkKey, thumbnail)
                    .size(242.dp),
            )
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(81.dp),
            ) {
                ArtworkBlurMesh(
                    model = thumbnail,
                    modifier = Modifier.matchParentSize(),
                    finish = { bitmap -> flipAndMultiply(bitmap, android.graphics.Color.rgb(0xC7, 0xC7, 0xC7)) },
                    finishKey = "topPickFrost",
                )
                Column(
                    modifier = Modifier.matchParentSize().padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        letterSpacing = (-0.23).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    if (label != null) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            letterSpacing = (-0.08).sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (detail != null) {
                        Text(
                            text = detail,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            letterSpacing = (-0.08).sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
