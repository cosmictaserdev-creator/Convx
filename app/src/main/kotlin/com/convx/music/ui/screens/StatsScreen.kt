/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.component.sf
import dev.chrisbanes.haze.hazeSource
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.component.rememberHeroSource
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.isGlassAllowed
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.music.innertube.models.WatchEndpoint
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.StatPeriod
import com.convx.music.extensions.toMediaItem
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.queues.ListQueue
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.ChoiceChipsRow
import com.convx.music.ui.component.FrostedTopBar
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.rememberTitleCollapseProgressState
import com.convx.music.ui.component.LocalAlbumsGrid
import com.convx.music.ui.component.LocalArtistsGrid
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.LocalSongsGrid
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.menu.AlbumMenu
import com.convx.music.ui.menu.ArtistMenu
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.utils.backToMain
import com.convx.music.ui.utils.bleedStart
import com.convx.music.ui.utils.plusStart
import com.convx.music.utils.joinByBullet
import com.convx.music.utils.makeTimeString
import com.convx.music.viewmodels.StatsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsViewModel = koinViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val indexChips by viewModel.indexChips.collectAsStateWithLifecycle()
    val mostPlayedSongs by viewModel.mostPlayedSongs.collectAsStateWithLifecycle()
    val mostPlayedSongsStats by viewModel.mostPlayedSongsStats.collectAsStateWithLifecycle()
    val mostPlayedArtists by viewModel.mostPlayedArtists.collectAsStateWithLifecycle()
    val mostPlayedAlbums by viewModel.mostPlayedAlbums.collectAsStateWithLifecycle()
    val firstEvent by viewModel.firstEvent.collectAsStateWithLifecycle()
    val currentDate = LocalDateTime.now()

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val statsTitleProgress = rememberTitleCollapseProgressState(lazyListState)
    val selectedOption by viewModel.selectedOption.collectAsStateWithLifecycle()

    val weeklyDates =
        if (currentDate != null && firstEvent != null) {
            generateSequence(currentDate) { it.minusWeeks(1) }
                .takeWhile { it.isAfter(firstEvent?.event?.timestamp?.minusWeeks(1)) }
                .mapIndexed { index, date ->
                    val endDate = date.plusWeeks(1).minusDays(1).coerceAtMost(currentDate)
                    val formatter = DateTimeFormatter.ofPattern("dd MMM")

                    val startDateFormatted = formatter.format(date)
                    val endDateFormatted = formatter.format(endDate)

                    val startMonth = date.month
                    val endMonth = endDate.month
                    val startYear = date.year
                    val endYear = endDate.year

                    val text =
                        when {
                            startYear != currentDate.year -> "$startDateFormatted, $startYear - $endDateFormatted, $endYear"
                            startMonth != endMonth -> "$startDateFormatted - $endDateFormatted"
                            else -> "${date.dayOfMonth} - $endDateFormatted"
                        }
                    Pair(index, text)
                }.toList()
        } else {
            emptyList()
        }

    val monthlyDates =
        if (currentDate != null && firstEvent != null) {
            generateSequence(
                currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1)
            ) { it.minusMonths(1) }
                .takeWhile {
                    it.isAfter(
                        firstEvent
                            ?.event
                            ?.timestamp
                            ?.withDayOfMonth(1),
                    )
                }.mapIndexed { index, date ->
                    val formatter = DateTimeFormatter.ofPattern("MMM")
                    val formattedDate = formatter.format(date)
                    val text =
                        if (date.year != currentDate.year) {
                            "$formattedDate ${date.year}"
                        } else {
                            formattedDate
                        }
                    Pair(index, text)
                }.toList()
        } else {
            emptyList()
        }

    val yearlyDates =
        if (currentDate != null && firstEvent != null) {
            generateSequence(
                currentDate
                    .plusYears(1)
                    .withDayOfYear(1)
                    .minusDays(1),
            ) { it.minusYears(1) }
                .takeWhile {
                    it.isAfter(
                        firstEvent
                            ?.event
                            ?.timestamp,
                    )
                }.mapIndexed { index, date ->
                    Pair(index, "${date.year}")
                }.toList()
        } else {
            emptyList()
        }

    val tint = AppleTokens.BgElevated
    val onTint = AppleTokens.onColor(tint)
    val heroSource = rememberHeroSource(staticArt = null)
    val heroBackdrop = rememberLayerBackdrop()

    val heroZoom = rememberHeroZoom()

    val sideInset = LocalPlayerAwareWindowInsets.current
        .asPaddingValues()
        .calculateStartPadding(LocalLayoutDirection.current)

    HeroBackground(
        tint = tint,
        heroSource = heroSource,
        heroScale = heroZoom.scale,
        modifier = Modifier.fillMaxSize(),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val chromeShape = ContinuousRoundedRectangle(percent = 50)
        val hazeState = androidx.compose.runtime.remember { dev.chrisbanes.haze.HazeState() }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                overscrollEffect = heroZoom.listOverscroll(),
                modifier = Modifier.heroPullZoom(heroZoom).hazeSource(hazeState),
                state = lazyListState,
                contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
            ) {
                item(key = "header") {
                    LargeScreenTitle(
                        title = stringResource(R.string.stats),
                        color = onTint,
                    )
                }

                item(key = "choice_chips") {
                    ChoiceChipsRow(
                        chips =
                        when (selectedOption) {
                            OptionStats.WEEKS -> weeklyDates
                            OptionStats.MONTHS -> monthlyDates
                            OptionStats.YEARS -> yearlyDates
                            OptionStats.CONTINUOUS -> {
                                listOf(
                                    StatPeriod.WEEK_1.ordinal to pluralStringResource(
                                        R.plurals.n_week,
                                        1,
                                        1
                                    ),
                                    StatPeriod.MONTH_1.ordinal to pluralStringResource(
                                        R.plurals.n_month,
                                        1,
                                        1
                                    ),
                                    StatPeriod.MONTH_3.ordinal to pluralStringResource(
                                        R.plurals.n_month,
                                        3,
                                        3
                                    ),
                                    StatPeriod.MONTH_6.ordinal to pluralStringResource(
                                        R.plurals.n_month,
                                        6,
                                        6
                                    ),
                                    StatPeriod.YEAR_1.ordinal to pluralStringResource(
                                        R.plurals.n_year,
                                        1,
                                        1
                                    ),
                                    StatPeriod.ALL.ordinal to stringResource(R.string.filter_all),
                                )
                            }
                        },
                        options =
                        listOf(
                            OptionStats.CONTINUOUS to stringResource(id = R.string.continuous),
                            OptionStats.WEEKS to stringResource(R.string.weeks),
                            OptionStats.MONTHS to stringResource(R.string.months),
                            OptionStats.YEARS to stringResource(R.string.years),
                        ),
                        selectedOption = selectedOption,
                        onSelectionChange = {
                            viewModel.selectedOption.value = it
                            viewModel.indexChips.value = 0
                        },
                        currentValue = indexChips,
                        onValueUpdate = { viewModel.indexChips.value = it },
                    )
                }

                if (mostPlayedSongs.isNotEmpty()) {
                    item(key = "mostPlayedSongs") {
                        NavigationTitle(
                            title = "${mostPlayedSongsStats.size} ${stringResource(id = R.string.songs)}",
                            modifier = Modifier,
                        )
                    }

                    item(key = "mostPlayedSongsList") {
                        LazyRow(
                            contentPadding = WindowInsets.systemBars
                                .only(WindowInsetsSides.Horizontal)
                                .asPaddingValues().plusStart(sideInset),
                            modifier = Modifier.bleedStart(sideInset),
                        ) {
                            itemsIndexed(
                                items = mostPlayedSongsStats,
                                key = { _, song -> song.id },
                            ) { index, song ->
                                LocalSongsGrid(
                                    title = "${index + 1}. ${song.title}",
                                    subtitle =
                                    joinByBullet(
                                        pluralStringResource(
                                            R.plurals.n_time,
                                            song.songCountListened,
                                            song.songCountListened,
                                        ),
                                        makeTimeString(song.timeListened),
                                    ),
                                    thumbnailUrl = song.thumbnailUrl,
                                    isActive = song.id == mediaMetadata?.id,
                                    isPlaying = isPlaying,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                if (song.id == mediaMetadata?.id) {
                                                    playerConnection.togglePlayPause()
                                                } else {
                                                    playerConnection.playQueue(
                                                        YouTubeQueue(
                                                            endpoint = WatchEndpoint(song.id),
                                                            preloadItem = mostPlayedSongs[index].toMediaMetadata(),
                                                        ),
                                                    )
                                                }
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    SongMenu(
                                                        originalSong = mostPlayedSongs[index],
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
)
                                )
                            }
                        }
                    }
                }

                if (mostPlayedArtists.isNotEmpty()) {
                    item(key = "mostPlayedArtistsTitle") {
                        NavigationTitle(
                            title = "${mostPlayedArtists.size} ${stringResource(id = R.string.artists)}",
                            modifier = Modifier,
                        )
                    }

                    item(key = "mostPlayedArtistsList") {
                        LazyRow(
                            contentPadding = WindowInsets.systemBars
                                .only(WindowInsetsSides.Horizontal)
                                .asPaddingValues().plusStart(sideInset),
                            modifier = Modifier.bleedStart(sideInset),
                        ) {
                            itemsIndexed(
                                items = mostPlayedArtists,
                                key = { _, artist -> artist.id },
                            ) { index, artist ->
                                LocalArtistsGrid(
                                    title = "${index + 1}. ${artist.artist.name}",
                                    subtitle =
                                    joinByBullet(
                                        pluralStringResource(
                                            R.plurals.n_time,
                                            artist.songCount,
                                            artist.songCount
                                        ),
                                        makeTimeString(artist.timeListened?.toLong()),
                                    ),
                                    thumbnailUrl = artist.artist.thumbnailUrl,
                                    modifier =
                                    Modifier
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(artistRoute(artist.id, artist.artist.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    ArtistMenu(
                                                        originalArtist = artist,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
)
                                )
                            }
                        }
                    }
                }

                if (mostPlayedAlbums.isNotEmpty()) {
                    item(key = "mostPlayedAlbumsTitle") {
                        NavigationTitle(
                            title = "${mostPlayedAlbums.size} ${stringResource(id = R.string.albums)}",
                            modifier = Modifier,
                        )
                    }

                    item(key = "mostPlayedAlbumsList") {
                        LazyRow(
                            contentPadding = WindowInsets.systemBars
                                .only(WindowInsetsSides.Horizontal)
                                .asPaddingValues().plusStart(sideInset),
                            modifier = Modifier.bleedStart(sideInset),
                        ) {
                            itemsIndexed(
                                items = mostPlayedAlbums,
                                key = { _, album -> album.id },
                            ) { index, album ->
                                val playCount = album.songCountListened ?: 0
                                LocalAlbumsGrid(
                                    title = "${index + 1}. ${album.album.title}",
                                    subtitle =
                                    joinByBullet(
                                        pluralStringResource(
                                            R.plurals.n_time,
                                            playCount,
                                            playCount,
                                        ),
                                        makeTimeString(album.timeListened),
                                    ),
                                    thumbnailUrl = album.album.thumbnailUrl,
                                    sharedArtworkId = album.id,
                                    isActive = album.id == mediaMetadata?.album?.id,
                                    isPlaying = isPlaying,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(albumRoute(album.id, album.album.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    AlbumMenu(
                                                        originalAlbum = album,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
)
                                )
                            }
                        }
                    }
                }
            }

            if (mostPlayedSongs.isNotEmpty()) {
                HideOnScrollFAB(
                    visible = true,
                    lazyListState = lazyListState,
                    icon = R.drawable.shuffle,
                    onClick = {
                        playerConnection.playQueue(
                            ListQueue(
                                title = context.getString(R.string.most_played_songs),
                                items = mostPlayedSongs.map { it.toMediaMetadata().toMediaItem() }.shuffled()
                            )
                        )
                    }
                )
            }

            com.convx.music.ui.component.TopFadeBlur(
                hazeState = hazeState,
                pageColor = tint,
                modifier = Modifier.align(Alignment.TopCenter),
            )

            FrostedTopBar(
                title = stringResource(R.string.stats),
                titleAlpha = 0f,
                titleAlphaProvider = { statsTitleProgress.value },
                titleColor = onTint,
                modifier = Modifier.align(Alignment.TopCenter),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(appTopBarWindowInsets())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlassCircleButton(
                    onClick = navController::navigateUp,
                    onLongClick = navController::backToMain,
                ) {
                    SymbolIcon(symbol = sf("arrow.left"), contentDescription = null)
                }

                Spacer(Modifier.weight(1f))

                GlassCircleButton(
                    onClick = { navController.navigate("wrapped") }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sparks),
                        contentDescription = null,
                    )
                }
            }
        }
      }
    }
}

enum class OptionStats { WEEKS, MONTHS, YEARS, CONTINUOUS }
