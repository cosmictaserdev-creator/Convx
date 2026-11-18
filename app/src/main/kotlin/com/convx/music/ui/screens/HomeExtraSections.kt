package com.convx.music.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.convx.music.R
import com.convx.music.db.entities.Artist
import com.convx.music.models.SimilarRecommendation
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.bleedStart
import com.convx.music.ui.utils.plusStart
import com.convx.music.ui.theme.AppPage
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.convx.music.ui.component.rememberPlaylistAppearance
import com.convx.music.ui.component.rememberHomeCardShape
import com.convx.music.ui.utils.rememberGridSpacing

internal fun LazyListScope.homeVideoSection(key: String, @StringRes stringResourceId: Int, deps: HomeSectionDeps, videos: List<SongItem>) {
    if (videos.isEmpty()) return
    item("${key}_title") { NavigationTitle(title = stringResource(stringResourceId)) }
    homeVideoCardRow("${key}_cards", deps, videos)
}

internal fun LazyListScope.homeVideoCardRow(key: String, deps: HomeSectionDeps, videos: List<SongItem>) {
    if (videos.isEmpty()) return
    item(key) {
        BoxWithConstraints(Modifier.fillMaxWidth().bleedStart(deps.sideInset)) {
            val cardWidth = (maxWidth * .62f).coerceAtMost(340.dp)
            LazyRow(
                contentPadding = PaddingValues(horizontal = AppPage.Gutter).plusStart(deps.sideInset),
                horizontalArrangement = Arrangement.spacedBy(rememberGridSpacing()),
            ) {
                items(videos, key = { it.id }) { video ->
                    var fallback by remember(video.id) { mutableStateOf(false) }
                    val cardShape = rememberHomeCardShape(12.dp)
                    HomePopMenu(
                        pop = HomePop.Yt(video),
                        deps = deps,
                        thumbnailUrl = video.thumbnail,
                        title = video.title,
                        subtitle = video.artists.joinToString(", ") { it.name },
                        onClick = { deps.openYtItem(video) },
                        modifier = Modifier.width(cardWidth),
                    ) {
                    Column(Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = if (fallback) video.thumbnail else "https://i.ytimg.com/vi/${video.id}/hq720.jpg",
                            onError = { fallback = true },
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(cardShape),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(video.title, style = IosTheme.typography.body, fontWeight = FontWeight.SemiBold,
                            color = IosTheme.colors.label, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(video.artists.joinToString(", ") { it.name }, style = IosTheme.typography.footnote,
                            color = IosTheme.colors.secondaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    }
                }
            }
        }
    }
}

internal fun LazyListScope.homeArtistListSection(deps: HomeSectionDeps, recommendations: List<SimilarRecommendation>) {
    val artists = recommendations.filter { it.title is Artist && it.items.isNotEmpty() }.distinctBy { it.title.id }
    if (artists.isEmpty()) return
    item("artist_list_title") { NavigationTitle(title = stringResource(R.string.home_artist_list)) }
    item("artist_list_cards") {
        LazyRow(
            modifier = Modifier.fillMaxWidth().bleedStart(deps.sideInset),
            contentPadding = PaddingValues(horizontal = AppPage.Gutter).plusStart(deps.sideInset),
            horizontalArrangement = Arrangement.spacedBy(rememberGridSpacing()),
        ) {
            items(artists, key = { it.title.id }) { recommendation ->
                val artist = recommendation.title as Artist
                val cardShape = rememberHomeCardShape(20.dp)
                val palette = rememberPlaylistAppearance(artist.thumbnailUrl ?: recommendation.items.firstOrNull()?.thumbnail)
                val source = palette.mesh.sourceAverage
                val tint = Color(source.red * .35f, source.green * .35f, source.blue * .35f)
                Column(Modifier.width(276.dp).height(344.dp).clip(cardShape)
                    .background(Brush.verticalGradient(listOf(tint, Color(0xFF101016), Color.Black)))
                    .clickable { deps.navController.navigate(artistRoute(artist.id, artist.thumbnailUrl)) }
                    .padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(artist.thumbnailUrl, null, Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)), contentScale = ContentScale.Crop)
                        Text(artist.title, modifier = Modifier.padding(start = 12.dp), color = Color.White,
                            style = IosTheme.typography.body, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                    Spacer(Modifier.height(12.dp))
                    recommendation.items.take(4).forEach { song ->
                        Row(Modifier.fillMaxWidth().height(64.dp).clickable { deps.openYtItem(song) }, verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(song.thumbnail, null, Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            Text(song.title, Modifier.padding(start = 10.dp), color = Color.White,
                                style = IosTheme.typography.footnote, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

internal fun LazyListScope.homeHitsSection(deps: HomeSectionDeps, hits: List<YTItem>) {
    if (hits.isEmpty()) return
    item("all_time_hits_title") { NavigationTitle(title = stringResource(R.string.home_all_time_hits)) }
    item("all_time_hits_cards") {
        LazyRow(
            modifier = Modifier.fillMaxWidth().bleedStart(deps.sideInset),
            contentPadding = PaddingValues(horizontal = AppPage.Gutter).plusStart(deps.sideInset),
            horizontalArrangement = Arrangement.spacedBy(rememberGridSpacing()),
        ) {
            items(hits, key = { it.id }) { hit ->
                val cardShape = rememberHomeCardShape(14.dp)
                Box(Modifier.size(156.dp).clip(cardShape).clickable { deps.openYtItem(hit) }) {
                    AsyncImage(hit.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .75f)))))
                    val label = when (hit) {
                        is SongItem -> hit.artists.joinToString(", ") { it.name }
                        is AlbumItem -> hit.artists?.joinToString(", ") { it.name }.orEmpty()
                        is ArtistItem -> hit.title
                        is PlaylistItem -> hit.author?.name.orEmpty()
                    }.ifBlank { hit.title }
                    Text(label, Modifier.align(Alignment.BottomStart).padding(10.dp), color = Color.White,
                        style = IosTheme.typography.footnote, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
