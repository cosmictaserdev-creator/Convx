/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings.diy

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.PlayerIconsKey
import com.convx.music.constants.UseAppleMusicPlayerKey
import com.convx.music.constants.V2PlayerIconsKey
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.player.customize.DiyOrientation
import com.convx.music.ui.player.customize.DiyPlayerMockup
import com.convx.music.ui.player.customize.PlayerIconOverride
import com.convx.music.ui.player.customize.PlayerIconSet
import com.convx.music.ui.player.customize.PlayerIconSlot
import com.convx.music.ui.player.customize.PlayerIconStore
import com.convx.music.ui.player.customize.rememberPlayerIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.MediaImport
import com.convx.music.utils.dataStore
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import java.io.File
import com.convx.music.ui.screens.settings.SettingsPage

/**
 * Swaps the player's control glyphs for the user's own artwork.
 *
 * A live mockup sits under the list so a choice can be judged in place — a flower that looks
 * charming at 96dp in a settings row can be unreadable at 32dp on the transport bar, and the only
 * honest way to show that is to draw it there.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerIconsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val iconWriteMutex = remember { Mutex() }
    val (useAppleMusicPlayer, _) = rememberPreference(UseAppleMusicPlayerKey, defaultValue = true)
    val (json) = rememberPreference(PlayerIconsKey, defaultValue = "{}")
    val v1Set = remember(json) { PlayerIconSet.fromJson(json) }
    val (v2Json) = rememberPreference(V2PlayerIconsKey, defaultValue = "{}")
    val v2Set = remember(v2Json) { PlayerIconSet.fromJson(v2Json) }

    var pendingSlot by remember { mutableStateOf<PlayerIconSlot?>(null) }
    var choosingSlot by remember { mutableStateOf<PlayerIconSlot?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun persist(slot: PlayerIconSlot, update: (PlayerIconSet) -> PlayerIconSet) {
        val key = if (slot.isV2) V2PlayerIconsKey else PlayerIconsKey
        scope.launch {
            iconWriteMutex.withLock {
                var updated = PlayerIconSet.EMPTY
                context.dataStore.edit { preferences ->
                    val latest = PlayerIconSet.fromJson(preferences[key] ?: "{}")
                    updated = update(latest)
                    preferences[key] = updated.toJson()
                }
                // Merge against the latest set and prune while holding one lock. Concurrent
                // imports used to overwrite each other's slots, then prune the other import's
                // file using a stale snapshot.
                withContext(Dispatchers.IO) {
                    PlayerIconStore.pruneOrphans(context, updated, slot.isV2)
                }
            }
        }
    }

    val pickLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        val slot = pendingSlot
        pendingSlot = null
        if (uri == null || slot == null) return@rememberLauncherForActivityResult
        scope.launch {
            val kind = if (slot == PlayerIconSlot.SEEK_THUMB) {
                MediaImport.Kind.SEEK_THUMB
            } else {
                MediaImport.Kind.PLAYER_ICON
            }
            val destDir = if (slot.isV2) PlayerIconStore.v2Dir(context) else PlayerIconStore.dir(context)
            val result = withContext(Dispatchers.IO) {
                MediaImport.import(
                    context = context,
                    uri = uri,
                    kind = kind,
                    destDir = destDir,
                    baseName = slot.name.lowercase(),
                )
            }
            when (result) {
                is MediaImport.Result.Failed ->
                    errorMessage = context.getString(importErrorRes(result.error))

                is MediaImport.Result.Ok -> persist(slot) { latest ->
                    latest.with(
                        slot,
                        PlayerIconOverride(
                            fileName = result.file.name,
                            tint = latest.overrides[slot]?.tint ?: false,
                        ),
                    )
                }
            }
        }
    }

    choosingSlot?.let { slot ->
        val set = if (slot.isV2) v2Set else v1Set
        val choices = remember(set, slot, context) {
            set.overrides.mapNotNull { (sourceSlot, override) ->
                PlayerIconStore.fileFor(context, override, slot.isV2)
                    .takeIf(File::isFile)
                    ?.let { PlayerIconChoice(it, sourceSlot.labelRes) }
            }.distinctBy { it.file.name }
        }
        PlayerIconChoiceDialog(
            slot = slot,
            choices = choices,
            selectedFileName = set.overrides[slot]?.fileName,
            onDismiss = { choosingSlot = null },
            onChoose = { fileName ->
                choosingSlot = null
                scope.launch {
                    val copiedName = withContext(Dispatchers.IO) {
                        runCatching {
                            val source = PlayerIconStore.fileFor(
                                context,
                                PlayerIconOverride(fileName, tint = false),
                                slot.isV2,
                            )
                            val destination = File(
                                if (slot.isV2) PlayerIconStore.v2Dir(context) else PlayerIconStore.dir(context),
                                "${slot.name.lowercase()}.${source.extension}",
                            )
                            if (source.absoluteFile != destination.absoluteFile) {
                                source.copyTo(destination, overwrite = true)
                            }
                            destination.name
                        }.getOrNull()
                    }
                    if (copiedName == null) {
                        errorMessage = context.getString(R.string.media_import_io)
                    } else {
                        persist(slot) { latest ->
                            latest.with(
                                slot,
                                PlayerIconOverride(
                                    fileName = copiedName,
                                    tint = latest.overrides[slot]?.tint ?: false,
                                ),
                            )
                        }
                    }
                }
            },
            onBrowse = {
                choosingSlot = null
                pendingSlot = slot
                pickLauncher.launch(ACCEPTED_TYPES)
            },
        )
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text(stringResource(R.string.player_icons)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
        )
    }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.player_icons),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
            Text(
                text = stringResource(R.string.player_icons_desc),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            // Scrolls with the slot list rather than pinned to the bottom, so nothing in it is
            // cropped by a fixed-height footer — the point is still to watch it change live as a
            // slot is swapped, that just doesn't require it to stay on screen while scrolling.
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                DiyPlayerMockup(
                    orientation = DiyOrientation.PORTRAIT,
                    modifier = Modifier
                        .height(320.dp)
                        .aspectRatio(9f / 19.5f)
                        .clip(RoundedCornerShape(18.dp)),
                )
            }

            PlayerIconSlot.entries.filter { !it.isV2 }.forEach { slot ->
                SlotRow(
                    slot = slot,
                    override = v1Set.overrides[slot],
                    onChoose = { choosingSlot = slot },
                    onClear = { persist(slot) { it.with(slot, null) } },
                    onTintChange = { tint ->
                        persist(slot) { current ->
                            current.overrides[slot]?.let { current.with(slot, it.copy(tint = tint)) } ?: current
                        }
                    },
                )
            }

            if (useAppleMusicPlayer) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.v2_player_icons),
                    style = AppTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = stringResource(R.string.v2_player_icons_desc),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                PlayerIconSlot.entries.filter { it.isV2 }.forEach { slot ->
                    SlotRow(
                        slot = slot,
                        override = v2Set.overrides[slot],
                        onChoose = { choosingSlot = slot },
                        onClear = { persist(slot) { it.with(slot, null) } },
                        onTintChange = { tint ->
                            persist(slot) { current ->
                                current.overrides[slot]?.let { current.with(slot, it.copy(tint = tint)) } ?: current
                            }
                        },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        
    }
}

/**
 * Deliberately broad. A narrow filter hides files whose provider mislabels them — an ordinary PNG
 * reported as `application/octet-stream` simply would not appear in the picker. MediaImport does
 * the real gating after the pick, where it can look at the bytes.
 */
private val ACCEPTED_TYPES = arrayOf("image/*", "image/svg+xml")

@Composable
private fun SlotRow(
    slot: PlayerIconSlot,
    override: PlayerIconOverride?,
    onChoose: () -> Unit,
    onClear: () -> Unit,
    onTintChange: (Boolean) -> Unit,
) {
    val icon = rememberPlayerIcon(slot)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onChoose)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (icon.isCustom && !icon.tint) {
                Image(
                    painter = icon.painter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp),
                )
            } else {
                Icon(
                    painter = icon.painter,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(stringResource(slot.labelRes), style = AppTheme.typography.bodyLarge)
            Text(
                text = stringResource(
                    if (override == null) R.string.player_icon_default else R.string.player_icon_active,
                ),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (override != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.player_icon_tint),
                    style = AppTheme.typography.labelSmall,
                )
                Switch(checked = override.tint, onCheckedChange = onTintChange)
            }
            IconButton(onClick = onClear, onLongClick = {}) {
                SymbolIcon(symbol = sf("trash"), contentDescription = null, tint = AppTheme.colorScheme.error)
            }
        }
    }
}

private data class PlayerIconChoice(val file: File, val labelRes: Int)

@Composable
private fun PlayerIconChoiceDialog(
    slot: PlayerIconSlot,
    choices: List<PlayerIconChoice>,
    selectedFileName: String?,
    onDismiss: () -> Unit,
    onChoose: (String) -> Unit,
    onBrowse: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.player_icon_choose_title, stringResource(slot.labelRes))) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(R.string.player_icon_choose_description),
                    style = AppTheme.typography.bodySmall,
                )
                if (choices.isEmpty()) {
                    Text(
                        stringResource(R.string.player_icon_no_saved),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(60.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(choices, key = { it.file.name }) { choice ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val selected = choice.file.name == selectedFileName
                                val painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(choice.file)
                                        .memoryCacheKey("player_icon_${choice.file.path}_${choice.file.lastModified()}")
                                        .diskCacheKey("player_icon_${choice.file.path}_${choice.file.lastModified()}")
                                        .build(),
                                )
                                Box(
                                    Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppTheme.colorScheme.surfaceVariant)
                                        .border(
                                            width = if (selected) 2.dp else 1.dp,
                                            color = if (selected) AppTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp),
                                        )
                                        .clickable { onChoose(choice.file.name) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        painter = painter,
                                        contentDescription = stringResource(choice.labelRes),
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(36.dp),
                                    )
                                }
                                Text(
                                    stringResource(choice.labelRes),
                                    style = AppTheme.typography.labelSmall,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
                TextButton(onClick = onBrowse, modifier = Modifier.align(Alignment.End)) {
                    Text(stringResource(R.string.player_icon_browse))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
    )
}
