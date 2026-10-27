/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convx.music.R
import com.convx.music.jam.JamManager
import com.convx.music.jam.JamSession
import com.convx.music.jam.PartyActivity
import com.convx.music.jam.PartyMember
import com.convx.music.jam.PartyPreview
import com.convx.music.jam.PartyStats
import com.convx.music.ui.component.AccountAvatar
import com.convx.music.ui.component.ScreenPage
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.foundation.IosMetrics
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.iosRowPress
import com.convx.music.ui.utils.backToMain
import com.convx.music.viewmodels.HomeViewModel
import com.convx.music.viewmodels.JamViewModel
import kotlinx.coroutines.delay

private const val CODE_LENGTH = 6
private const val MIN_PARTY_SIZE = 2
private const val MAX_PARTY_SIZE = 10
private const val ACTIVITY_SHOWN = 6

/** Listen together: one code, everyone hears the same song at the same moment. */
@Composable
fun JamScreen(navController: NavController, asTab: Boolean = false) {
    val viewModel: JamViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val session by viewModel.session.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val activity by viewModel.activity.collectAsStateWithLifecycle()
    val accountName by homeViewModel.accountName.collectAsStateWithLifecycle()
    val accountImageUrl by homeViewModel.accountImageUrl.collectAsStateWithLifecycle()
    val avatarUrl = accountImageUrl.takeIf { accountName.isNotBlank() && !accountName.equals("Guest", ignoreCase = true) }

    // Wake a sleeping server while the user types a name, and take a party link opened from outside.
    val pendingInvite by viewModel.invite.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.warm() }
    LaunchedEffect(pendingInvite) {
        val link = viewModel.consumeInvite() ?: return@LaunchedEffect
        link.server?.let { viewModel.serverUrl = it }
        if (viewModel.session.value == null) viewModel.join(link.code, viewModel.displayName, avatarUrl)
    }

    ScreenPage(
        title = stringResource(R.string.jam_title),
        onBack = if (asTab) null else ({ navController.navigateUp(); Unit }),
        onBackLongClick = if (asTab) null else navController::backToMain,
        pageColor = IosTheme.colors.systemGroupedBackground,
        horizontalPadding = 16.dp,
    ) {
        message?.let { Banner(it, onDismiss = viewModel::dismissMessage) }

        val current = session
        if (current == null) {
            StartParty(viewModel, busy, avatarUrl)
        } else {
            InParty(current, activity, viewModel)
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ---- building blocks ------------------------------------------------------------------------

@Composable
private fun Card(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(ContinuousRoundedRectangle(IosMetrics.GroupedCornerRadius))
            .background(IosTheme.colors.secondarySystemGroupedBackground),
        content = content,
    )
}

@Composable
private fun Section(title: String?, footer: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(top = 24.dp)) {
        if (title != null) Text(
            title,
            style = IosTheme.typography.footnote,
            fontWeight = FontWeight.SemiBold,
            color = IosTheme.colors.secondaryLabel,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
        )
        Card(content = content)
        if (footer != null) Text(
            footer,
            style = IosTheme.typography.footnote,
            color = IosTheme.colors.secondaryLabel,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp),
        )
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(Modifier.padding(start = 16.dp))
}

@Composable
private fun Banner(text: String, onDismiss: () -> Unit) {
    Row(
        Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .clip(ContinuousRoundedRectangle(IosMetrics.GroupedCornerRadius))
            .background(IosTheme.colors.systemOrange.copy(alpha = 0.16f))
            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SymbolIcon(
            symbol = sf("exclamationmark.circle.fill"),
            contentDescription = null,
            size = 20.dp,
            tint = IosTheme.colors.systemOrange,
        )
        Spacer(Modifier.width(12.dp))
        Text(text, style = IosTheme.typography.subheadline, color = IosTheme.colors.label, modifier = Modifier.weight(1f))
        Box(
            Modifier.size(44.dp).clickable(role = Role.Button, onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            SymbolIcon(
                symbol = sf("xmark"),
                contentDescription = stringResource(R.string.jam_dismiss),
                size = 14.dp,
                tint = IosTheme.colors.secondaryLabel,
            )
        }
    }
}

@Composable
private fun TextRow(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Row(
        modifier.fillMaxWidth().defaultMinSize(minHeight = 52.dp).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label != null) {
            Text(label, style = IosTheme.typography.body, color = IosTheme.colors.label, modifier = Modifier.width(88.dp))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = IosTheme.typography.body.copy(color = IosTheme.colors.label),
            cursorBrush = SolidColor(IosTheme.tint),
            keyboardOptions = keyboardOptions,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = IosTheme.typography.body, color = IosTheme.colors.label)
            Text(subtitle, style = IosTheme.typography.footnote, color = IosTheme.colors.secondaryLabel)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun StepButton(symbol: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(IosTheme.colors.tertiarySystemFill)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        SymbolIcon(
            symbol = sf(symbol),
            contentDescription = description,
            size = 16.dp,
            tint = if (enabled) IosTheme.colors.label else IosTheme.colors.quaternaryLabel,
        )
    }
}

@Composable
private fun AvatarStack(members: List<com.convx.music.jam.PartyPreviewMember>) {
    Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
        members.take(4).forEach { AccountAvatar(imageUrl = it.avatarUrl, name = it.displayName, size = 32.dp) }
    }
}

// ---- not in a party -------------------------------------------------------------------------

@Composable
private fun StartParty(viewModel: JamViewModel, busy: Boolean, avatarUrl: String?) {
    var name by remember { mutableStateOf(viewModel.displayName) }
    var code by remember { mutableStateOf("") }
    var server by remember { mutableStateOf(viewModel.serverUrl) }
    var serverOpen by remember { mutableStateOf(false) }
    var consistentSource by remember { mutableStateOf(viewModel.consistentSource) }
    var preview by remember { mutableStateOf<PartyPreview?>(null) }
    var missing by remember { mutableStateOf(false) }
    val lastCode = remember { viewModel.lastCode }
    var lastPreview by remember { mutableStateOf<PartyPreview?>(null) }
    var stats by remember { mutableStateOf<PartyStats?>(null) }
    var statsChecked by remember { mutableStateOf(false) }

    LaunchedEffect(code) {
        preview = null
        missing = false
        if (code.length < CODE_LENGTH) return@LaunchedEffect
        delay(350)
        val found = viewModel.preview(code)
        preview = found
        missing = found == null
    }
    LaunchedEffect(server) {
        statsChecked = false
        delay(400)
        stats = viewModel.stats()
        statsChecked = true
    }
    LaunchedEffect(lastCode) {
        if (lastCode != null) lastPreview = viewModel.preview(lastCode)
    }

    Text(
        stringResource(R.string.jam_desc),
        style = IosTheme.typography.subheadline,
        color = IosTheme.colors.secondaryLabel,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )

    Section(title = null) {
        SwitchRow(
            stringResource(R.string.jam_consistent_source),
            stringResource(R.string.jam_consistent_source_desc),
            consistentSource,
        ) { enabled -> consistentSource = enabled; viewModel.consistentSource = enabled }
    }

    AnimatedVisibility(lastPreview != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
        val last = lastPreview
        if (last != null) Section(title = null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .iosRowPress(enabled = !busy, onClick = { viewModel.join(last.code, name, avatarUrl) })
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AvatarStack(last.members)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.jam_rejoin, last.code), style = IosTheme.typography.headline, color = IosTheme.colors.label)
                    Text(
                        stringResource(R.string.jam_rejoin_desc, last.hostName),
                        style = IosTheme.typography.footnote,
                        color = IosTheme.colors.secondaryLabel,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                SymbolIcon(symbol = sf("chevron.right"), contentDescription = null, size = 14.dp, tint = IosTheme.colors.tertiaryLabel)
            }
        }
    }

    Section(title = null) {
        TextRow(name, { name = it }, label = stringResource(R.string.jam_name_label))
    }

    Spacer(Modifier.height(16.dp))
    Button(
        onClick = { viewModel.create(name, avatarUrl) },
        enabled = !busy && name.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (busy) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        else {
            SymbolIcon(symbol = sf("plus"), contentDescription = null, size = 18.dp)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.jam_create))
        }
    }

    Section(title = stringResource(R.string.jam_join_section)) {
        Box(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            BasicTextField(
                value = code,
                onValueChange = { code = JamManager.codeFrom(it).take(12) },
                singleLine = true,
                textStyle = IosTheme.typography.title2.copy(
                    color = IosTheme.colors.label,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp,
                ),
                cursorBrush = SolidColor(IosTheme.tint),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                decorationBox = { field ->
                    if (code.isEmpty()) Text(
                        stringResource(R.string.jam_code),
                        style = IosTheme.typography.body,
                        color = IosTheme.colors.placeholderText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    field()
                },
            )
        }
        AnimatedVisibility(preview != null || missing, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Column {
                RowDivider()
                val found = preview
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (found != null) {
                        AvatarStack(found.members)
                        Spacer(Modifier.width(12.dp))
                    }
                    Text(
                        text = when {
                            found == null -> stringResource(R.string.jam_preview_missing)
                            found.isFull -> stringResource(R.string.jam_preview_full, found.hostName)
                            else -> stringResource(R.string.jam_preview_open, found.hostName, found.memberCount, found.maxMembers)
                        },
                        style = IosTheme.typography.footnote,
                        color = if (found == null || found.isFull) IosTheme.colors.systemRed else IosTheme.colors.secondaryLabel,
                    )
                }
            }
        }
        RowDivider()
        Box(Modifier.padding(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.join(code, name, avatarUrl) },
                enabled = !busy && code.length >= CODE_LENGTH && name.isNotBlank() && preview?.isFull != true,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.jam_join)) }
        }
    }

    val live = stats
    Section(
        title = stringResource(R.string.jam_server_section),
        footer = when {
            live != null -> stringResource(R.string.jam_stats, live.parties, live.members)
            statsChecked -> stringResource(R.string.jam_server_down)
            else -> stringResource(R.string.jam_server_footer)
        },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .iosRowPress(onClick = { serverOpen = !serverOpen })
                .defaultMinSize(minHeight = 52.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                server.removePrefix("https://").removePrefix("http://"),
                style = IosTheme.typography.body,
                color = IosTheme.colors.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            SymbolIcon(symbol = sf("chevron.right"), contentDescription = null, size = 14.dp, tint = IosTheme.colors.tertiaryLabel)
        }
        AnimatedVisibility(serverOpen, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Column {
                RowDivider()
                TextRow(
                    value = server,
                    onValueChange = { server = it; viewModel.serverUrl = it },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
                )
                RowDivider()
                Text(
                    stringResource(R.string.jam_server_reset),
                    style = IosTheme.typography.body,
                    color = IosTheme.tint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .iosRowPress(onClick = { viewModel.serverUrl = ""; server = viewModel.serverUrl })
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                )
            }
        }
    }
}

// ---- in a party -----------------------------------------------------------------------------

@Composable
private fun InParty(session: JamSession, activity: List<PartyActivity>, viewModel: JamViewModel) {
    PartyHeader(session, viewModel)

    if (session.localPlayback == com.convx.music.jam.JamLocalPlayback.UNAVAILABLE) {
        Text(stringResource(R.string.jam_audio_unavailable), color = IosTheme.colors.secondaryLabel)
        OutlinedButton(onClick = viewModel::retryAudio) { Text(stringResource(R.string.jam_retry_audio)) }
    }

    if (!session.canControl) {
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(ContinuousRoundedRectangle(IosMetrics.GroupedCornerRadius))
                .background(IosTheme.colors.systemOrange.copy(alpha = 0.16f))
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            SymbolIcon(symbol = sf("exclamationmark.circle.fill"), contentDescription = null, size = 22.dp, tint = IosTheme.colors.systemOrange)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(stringResource(R.string.jam_locked_title), style = IosTheme.typography.headline, color = IosTheme.colors.label)
                Text(stringResource(R.string.jam_view_only), style = IosTheme.typography.footnote, color = IosTheme.colors.secondaryLabel)
            }
        }
    }

    NowPlaying(session)
    Members(session, viewModel)

    if (session.you.isHost) {
        Section(title = stringResource(R.string.jam_settings_section)) {
            SwitchRow(
                stringResource(R.string.jam_host_only),
                stringResource(R.string.jam_host_only_desc),
                session.hostOnlyControl,
                viewModel::setHostOnlyControl,
            )
            RowDivider()
            SwitchRow(
                stringResource(R.string.jam_autoplay),
                stringResource(R.string.jam_autoplay_desc),
                session.playback.autoplayEnabled,
                viewModel::setAutoplay,
            )
            RowDivider()
            Row(
                Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(stringResource(R.string.jam_size), style = IosTheme.typography.body, color = IosTheme.colors.label)
                    Text(stringResource(R.string.jam_size_desc), style = IosTheme.typography.footnote, color = IosTheme.colors.secondaryLabel)
                }
                StepButton(
                    "minus", stringResource(R.string.jam_size_less),
                    enabled = session.maxMembers > maxOf(MIN_PARTY_SIZE, session.members.size),
                ) { viewModel.setMaxMembers(session.maxMembers - 1) }
                Text(
                    session.maxMembers.toString(),
                    style = IosTheme.typography.headline,
                    color = IosTheme.colors.label,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(36.dp),
                )
                StepButton(
                    "plus", stringResource(R.string.jam_size_more),
                    enabled = session.maxMembers < MAX_PARTY_SIZE,
                ) { viewModel.setMaxMembers(session.maxMembers + 1) }
            }
        }
    }

    if (activity.isNotEmpty()) {
        Section(title = stringResource(R.string.jam_activity_section)) {
            activity.take(ACTIVITY_SHOWN).forEachIndexed { index, entry ->
                if (index > 0) RowDivider()
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(entry.by, style = IosTheme.typography.subheadline, fontWeight = FontWeight.SemiBold, color = IosTheme.colors.label, maxLines = 1)
                    Text(entry.detail, style = IosTheme.typography.footnote, color = IosTheme.colors.secondaryLabel)
                }
            }
        }
    }

    Section(title = null, footer = stringResource(R.string.jam_leave_desc)) {
        Text(
            stringResource(R.string.jam_leave),
            style = IosTheme.typography.body,
            color = IosTheme.colors.systemRed,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .iosRowPress(onClick = { viewModel.leave() })
                .padding(vertical = 15.dp),
        )
    }
}

@Composable
private fun PartyHeader(session: JamSession, viewModel: JamViewModel) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(1_500)
            copied = false
        }
    }
    val dot by animateColorAsState(
        if (session.connected) IosTheme.colors.systemGreen else IosTheme.colors.systemOrange,
        label = "partyStatus",
    )

    Spacer(Modifier.height(12.dp))
    Card {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(dot, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(when {
                        !session.connected -> R.string.jam_reconnecting
                        session.controlPending -> R.string.jam_control_pending
                        session.localPlayback == com.convx.music.jam.JamLocalPlayback.PREPARING -> R.string.jam_preparing_audio
                        session.localPlayback == com.convx.music.jam.JamLocalPlayback.SCHEDULED -> R.string.jam_waiting_start
                        session.localPlayback == com.convx.music.jam.JamLocalPlayback.RECOVERING -> R.string.jam_recovering_audio
                        session.localPlayback == com.convx.music.jam.JamLocalPlayback.UNAVAILABLE -> R.string.jam_audio_unavailable
                        session.localPlayback == com.convx.music.jam.JamLocalPlayback.LOCALLY_PAUSED -> R.string.jam_locally_paused
                        else -> R.string.jam_connected
                    }),
                    style = IosTheme.typography.footnote,
                    color = IosTheme.colors.secondaryLabel,
                )
            }
            Text(
                session.code,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp,
                color = IosTheme.colors.label,
                modifier = Modifier.padding(top = 12.dp, bottom = 18.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {
                    clipboard.setText(AnnotatedString(session.code))
                    copied = true
                }) {
                    SymbolIcon(
                        symbol = sf(if (copied) "checkmark" else "doc.on.doc"),
                        contentDescription = null,
                        size = 16.dp,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(if (copied) R.string.jam_copied else R.string.jam_copy))
                }
                Button(onClick = {
                    val text = context.getString(R.string.jam_share_text, session.code, viewModel.inviteLink(session.code))
                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text),
                            null,
                        ),
                    )
                }) {
                    SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null, size = 16.dp)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.jam_share))
                }
            }
        }
    }
}

@Composable
private fun NowPlaying(session: JamSession) {
    val track = session.playback.track
    Section(title = stringResource(R.string.jam_now_playing)) {
        if (track == null) {
            Text(
                stringResource(R.string.jam_nothing_playing),
                style = IosTheme.typography.footnote,
                color = IosTheme.colors.secondaryLabel,
                modifier = Modifier.padding(16.dp),
            )
        } else {
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(60.dp).clip(ContinuousRoundedRectangle(12.dp)).background(IosTheme.colors.tertiarySystemFill),
                ) {
                    AsyncImage(
                        model = track.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(track.title, style = IosTheme.typography.headline, color = IosTheme.colors.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(track.artist, style = IosTheme.typography.subheadline, color = IosTheme.colors.secondaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    session.playback.startedByName?.takeIf { it.isNotBlank() }?.let {
                        Text(stringResource(R.string.jam_started_by, it), style = IosTheme.typography.caption, color = IosTheme.colors.tertiaryLabel, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun Members(session: JamSession, viewModel: JamViewModel) {
    var confirming by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(confirming) {
        if (confirming != null) {
            delay(3_000)
            confirming = null
        }
    }
    Section(title = stringResource(R.string.jam_members, session.members.size, session.maxMembers)) {
        session.members.forEachIndexed { index, member ->
            if (index > 0) HorizontalDivider(Modifier.padding(start = 68.dp))
            MemberRow(
                member = member,
                isYou = member.memberId == session.you.memberId,
                canRemove = session.you.isHost && member.memberId != session.you.memberId,
                confirming = confirming == member.memberId,
                onRemove = {
                    if (confirming == member.memberId) {
                        confirming = null
                        viewModel.kick(member.memberId)
                    } else {
                        confirming = member.memberId
                    }
                },
            )
        }
    }
}

@Composable
private fun MemberRow(member: PartyMember, isYou: Boolean, canRemove: Boolean, confirming: Boolean, onRemove: () -> Unit) {
    val status = listOfNotNull(
        stringResource(R.string.jam_host).takeIf { member.isHost },
        stringResource(if (member.connected) R.string.jam_member_listening else R.string.jam_member_away),
    ).joinToString(" · ")
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccountAvatar(imageUrl = member.avatarUrl, name = member.displayName, size = 40.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                member.displayName + if (isYou) " " + stringResource(R.string.jam_you) else "",
                style = IosTheme.typography.body,
                color = IosTheme.colors.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(status, style = IosTheme.typography.footnote, color = IosTheme.colors.secondaryLabel, maxLines = 1)
        }
        if (canRemove) {
            Text(
                stringResource(if (confirming) R.string.jam_remove_confirm else R.string.jam_remove),
                style = IosTheme.typography.subheadline,
                color = IosTheme.colors.systemRed,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onRemove)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            )
        }
    }
}
