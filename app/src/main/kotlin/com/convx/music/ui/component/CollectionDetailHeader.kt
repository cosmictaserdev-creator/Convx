package com.convx.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.media3.exoplayer.offline.Download
import coil3.compose.AsyncImage
import com.convx.music.R
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.screens.library.apple.SfStyle
import com.convx.music.ui.utils.SharedArtworkSeedImage
import com.convx.music.ui.utils.sharedArtworkTarget
import com.convx.music.ui.utils.sharedTitleTarget

@Composable
fun CollectionDetailHeader(
    title: String,
    artwork: String?,
    metadata: String,
    foreground: Color,
    playing: Boolean,
    enabled: Boolean,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    description: String? = null,
    descriptionRuns: List<LinkSegment>? = null,
    rightSymbol: String = "ellipsis",
    onRightAction: (() -> Unit)? = null,
    onDownload: (() -> Unit)? = null,
    downloadState: Int = Download.STATE_STOPPED,
    onEditCover: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    searchValue: TextFieldValue? = null,
    onSearchValueChange: ((TextFieldValue) -> Unit)? = null,
    onSearchImeAction: (() -> Unit)? = null,
    searchFocusRequester: FocusRequester? = null,
    titleContent: (@Composable () -> Unit)? = null,
    artworkOverlay: (@Composable BoxScope.() -> Unit)? = null,
) {
    CompositionLocalProvider(LocalContentColor provides foreground) {
        BoxWithConstraints(modifier.fillMaxWidth()) {
            val artworkSize = minOf(maxWidth * .68f, 300.dp)
            var artworkFailed by remember(artwork) { mutableStateOf(false) }
            Column(
                Modifier.fillMaxWidth().padding(top = 26.dp, bottom = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (onSearch != null) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 18.dp).height(40.dp)
                            .clip(RoundedCornerShape(20.dp)).background(foreground.copy(alpha = .12f))
                            .clickable(role = Role.Button) {
                                if (searchValue == null) onSearch() else searchFocusRequester?.requestFocus()
                            }
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SymbolIcon(sf("magnifyingglass"), null, size = 18.dp, tint = foreground.copy(alpha = .48f))
                        Spacer(Modifier.width(8.dp))
                        if (searchValue == null || onSearchValueChange == null) {
                            Text(stringResource(R.string.search), style = SfStyle(17.sp, 22.sp), color = foreground.copy(alpha = .48f))
                        } else {
                            BasicTextField(
                                value = searchValue,
                                onValueChange = onSearchValueChange,
                                singleLine = true,
                                textStyle = SfStyle(17.sp, 22.sp).copy(color = foreground),
                                cursorBrush = SolidColor(foreground),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { onSearchImeAction?.invoke() }),
                                modifier = Modifier.weight(1f).then(
                                    searchFocusRequester?.let { Modifier.focusRequester(it) } ?: Modifier,
                                ),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (searchValue.text.isEmpty()) {
                                            Text(stringResource(R.string.search), style = SfStyle(17.sp, 22.sp), color = foreground.copy(alpha = .48f))
                                        }
                                        innerTextField()
                                    }
                                },
                            )
                        }
                    }
                    Spacer(Modifier.height(26.dp))
                }
                Box(
                    Modifier.size(artworkSize).shadow(18.dp, RoundedCornerShape(12.dp), clip = false,
                        ambientColor = Color.Black.copy(alpha = .18f), spotColor = Color.Black.copy(alpha = .18f)),
                ) {
                    Box(
                        Modifier.sharedArtworkTarget().fillMaxSize().clip(RoundedCornerShape(12.dp))
                            .background(foreground.copy(alpha = .12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        val seeded = SharedArtworkSeedImage(Modifier.fillMaxSize(), ContentScale.Crop)
                        if (artwork.isNullOrBlank() || artworkFailed) {
                            SymbolIcon(sf("music.note.list"), null, tint = foreground.copy(alpha = .5f), size = 72.dp)
                        } else if (!seeded) {
                            // Keep the tapped tile's decoded image as the hero for the
                            // whole screen lifetime. Starting another request after the
                            // morph causes a visible swap and defeats the seeded handoff.
                            AsyncImage(artwork, null, onError = { if (!seeded) artworkFailed = true },
                                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                        artworkOverlay?.invoke(this)
                    }
                    if (onEditCover != null) {
                        ContrastCircleButton(foreground, onEditCover,
                            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp), size = 36.dp) {
                            SymbolIcon(sf("square.and.pencil"), stringResource(R.string.edit_playlist_cover), size = 18.dp)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                if (titleContent != null) titleContent() else Text(
                    title, style = SfStyle(22.sp, 27.sp, FontWeight.Bold), color = foreground,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
                    modifier = Modifier.sharedTitleTarget().padding(horizontal = 24.dp),
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(subtitle, style = SfStyle(20.sp, 24.sp), color = foreground,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
                        modifier = Modifier.then(if (titleContent != null) Modifier.sharedTitleTarget() else Modifier).padding(horizontal = 24.dp))
                }
                if (metadata.isNotBlank()) {
                    Spacer(Modifier.height(7.dp))
                    Text(metadata, style = SfStyle(15.sp, 19.sp, FontWeight.Medium),
                        color = foreground.copy(alpha = .48f), textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp))
                }
                Row(Modifier.padding(top = 17.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ContrastCircleButton(foreground, onShuffle, size = 54.dp, enabled = enabled) {
                        SymbolIcon(sf("shuffle"), stringResource(R.string.shuffle), size = 26.dp)
                    }
                    Row(
                        Modifier.width(140.dp).height(50.dp).clip(RoundedCornerShape(25.dp))
                            .background(foreground.copy(alpha = if (enabled) .82f else .4f))
                            .clickable(enabled = enabled, role = Role.Button, onClick = onPlay),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        val inverse = if (foreground == Color.White) Color.Black else Color.White
                        SymbolIcon(sf(if (playing) "pause.fill" else "play.fill"), null, tint = inverse, size = 19.dp)
                        Spacer(Modifier.width(7.dp))
                        Text(stringResource(if (playing) R.string.pause else R.string.play),
                            style = SfStyle(19.sp, 23.sp, FontWeight.Bold), color = inverse)
                    }
                    if (onDownload != null) {
                        // Filled once everything is saved, so "done" reads differently from "not yet".
                        val downloaded = downloadState == Download.STATE_COMPLETED
                        val inProgress = downloadState == Download.STATE_QUEUED ||
                            downloadState == Download.STATE_DOWNLOADING
                        ContrastCircleButton(foreground, onDownload, size = 54.dp, filled = downloaded, enabled = enabled) {
                            if (inProgress) {
                                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                SymbolIcon(
                                    sf("arrow.down.circle"),
                                    stringResource(if (downloaded) R.string.remove_download else R.string.action_download),
                                    size = 26.dp,
                                )
                            }
                        }
                    }
                    if (onRightAction != null) {
                        ContrastCircleButton(foreground, onRightAction, size = 54.dp) {
                            SymbolIcon(sf(rightSymbol), stringResource(R.string.more_options), size = 26.dp)
                        }
                    }
                }
                if (!description.isNullOrBlank()) {
                    Spacer(Modifier.height(20.dp))
                    CompositionLocalProvider(LocalContentColor provides foreground.copy(alpha = .48f)) {
                        ExpandableText(description, runs = descriptionRuns, collapsedMaxLines = 2,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
                    }
                }
            }
        }
    }
}
