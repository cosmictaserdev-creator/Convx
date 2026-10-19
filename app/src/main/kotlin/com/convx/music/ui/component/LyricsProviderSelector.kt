package com.convx.music.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.R
import com.convx.music.constants.LyricsProviderOrderKey
import com.convx.music.db.entities.LyricsEntity
import com.convx.music.lyrics.LyricsProviderRegistry
import com.convx.music.lyrics.LyricsProviderStatus
import com.convx.music.models.MediaMetadata
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.Text
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.LyricsProviderViewModel

@Composable
fun LyricsProviderSelector(
    metadata: MediaMetadata,
    lyrics: LyricsEntity?,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val current = lyrics
        ?.takeIf { it.id == metadata.id }
        ?.provider
        ?.takeUnless { it == "Unknown" }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(AppTheme.colorScheme.surfaceContainer.copy(alpha = 0.94f))
            .clickable(onClick = onOpenMenu)
            .heightIn(min = 44.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SymbolIcon(symbol = sf("quote.bubble"), contentDescription = null, size = 18.dp)
        Text(
            current?.let(LyricsProviderRegistry::getDisplayName)
                ?: stringResource(R.string.lyrics_choose_provider),
            style = AppTheme.typography.labelLarge,
        )
        SymbolIcon(
            symbol = sf("chevron.right"),
            contentDescription = stringResource(R.string.lyrics_choose_provider),
            size = 16.dp,
        )
    }
}

@Composable
fun LyricsProviderMenu(
    metadata: MediaMetadata,
    lyrics: LyricsEntity?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LyricsProviderViewModel = hiltViewModel(),
) {
    val statuses by viewModel.statuses.collectAsStateWithLifecycle()
    val syncLevels by viewModel.syncLevels.collectAsStateWithLifecycle()
    val requested by viewModel.requested.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val (order) = rememberPreference(LyricsProviderOrderKey, "")
    val providers = androidx.compose.runtime.remember(order) {
        (LyricsProviderRegistry.deserializeProviderOrder(order) + LyricsProviderRegistry.providerIds).distinct()
    }
    val current = lyrics
        ?.takeIf { it.id == metadata.id }
        ?.provider
        ?.let(LyricsProviderRegistry::getProviderForStoredName)
        ?.id

    BackHandler(onBack = onBack)
    DisposableEffect(metadata.id) {
        onDispose { viewModel.dismiss() }
    }
    LaunchedEffect(metadata, lyrics) {
        viewModel.bind(metadata, lyrics)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                SymbolIcon(symbol = sf("chevron.left"), contentDescription = null, size = 22.dp)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.lyrics_choose_provider),
                    style = AppTheme.typography.titleLarge,
                )
                Text(
                    metadata.title,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }

        if (error) {
            Text(
                stringResource(R.string.lyrics_provider_lookup_failed),
                color = AppTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp),
        ) {
            items(providers, key = { it }) { name ->
                val status = statuses[name] ?: LyricsProviderStatus.NOT_FETCHED
                val selected = name == current
                val unavailable = status in setOf(
                    LyricsProviderStatus.NOT_FOUND,
                    LyricsProviderStatus.DISABLED,
                    LyricsProviderStatus.UNSUPPORTED,
                )
                val isFetching = status == LyricsProviderStatus.FETCHING || requested == name
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !selected && !unavailable && !isFetching) {
                            viewModel.select(name, onBack)
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (isFetching) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        SymbolIcon(
                            symbol = sf(
                                when {
                                    selected || status == LyricsProviderStatus.FOUND -> "checkmark"
                                    unavailable -> "xmark"
                                    else -> "magnifyingglass"
                                },
                            ),
                            contentDescription = null,
                            size = 22.dp,
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            LyricsProviderRegistry.getDisplayName(name),
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                        Text(
                            stringResource(
                                when {
                                    selected -> R.string.lyrics_provider_current
                                    isFetching -> R.string.lyrics_provider_fetching
                                    status == LyricsProviderStatus.FOUND -> R.string.lyrics_provider_found
                                    status == LyricsProviderStatus.NOT_FOUND -> R.string.lyrics_provider_not_found
                                    status == LyricsProviderStatus.FAILED -> R.string.lyrics_provider_retry
                                    status == LyricsProviderStatus.DISABLED -> R.string.lyrics_provider_disabled
                                    status == LyricsProviderStatus.UNSUPPORTED -> R.string.lyrics_provider_unsupported
                                    else -> R.string.lyrics_provider_not_fetched
                                },
                            ),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant,
                        )
                        if (status == LyricsProviderStatus.FOUND) {
                            Text(syncLevels[name]?.label.orEmpty(), style = AppTheme.typography.labelSmall,
                                color = AppTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
