/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens.settings.diy

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
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
import com.nexamusic.app.R
import com.nexamusic.app.constants.PlayerIconsKey
import com.nexamusic.app.constants.UseAppleMusicPlayerKey
import com.nexamusic.app.constants.V2PlayerIconsKey
import com.nexamusic.app.ui.component.IconButton
import com.nexamusic.app.ui.player.customize.DiyOrientation
import com.nexamusic.app.ui.player.customize.DiyPlayerMockup
import com.nexamusic.app.ui.player.customize.PlayerIconOverride
import com.nexamusic.app.ui.player.customize.PlayerIconSet
import com.nexamusic.app.ui.player.customize.PlayerIconSlot
import com.nexamusic.app.ui.player.customize.PlayerIconStore
import com.nexamusic.app.ui.player.customize.rememberPlayerIcon
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.backToMain
import com.nexamusic.app.utils.MediaImport
import com.nexamusic.app.utils.dataStore
import com.nexamusic.app.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val (useAppleMusicPlayer, _) = rememberPreference(UseAppleMusicPlayerKey, defaultValue = false)
    val (json) = rememberPreference(PlayerIconsKey, defaultValue = "{}")
    val v1Set = remember(json) { PlayerIconSet.fromJson(json) }
    val (v2Json) = rememberPreference(V2PlayerIconsKey, defaultValue = "{}")
    val v2Set = remember(v2Json) { PlayerIconSet.fromJson(v2Json) }

    var pendingSlot by remember { mutableStateOf<PlayerIconSlot?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun persist(slot: PlayerIconSlot, set: PlayerIconSet) {
        val key = if (slot.isV2) V2PlayerIconsKey else PlayerIconsKey
        scope.launch {
            context.dataStore.edit { it[key] = set.toJson() }
            // Prune the directory this slot actually lives in. Pruning the V1 directory
            // against a V2 set (or the reverse) deletes every glyph the user picked for
            // the other player, since the two use different file names.
            withContext(Dispatchers.IO) {
                PlayerIconStore.pruneOrphans(context, set, slot.isV2)
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
            val currentSet = if (slot.isV2) v2Set else v1Set
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

                is MediaImport.Result.Ok -> persist(
                    slot,
                    currentSet.with(
                        slot,
                        PlayerIconOverride(
                            fileName = result.file.name,
                            tint = currentSet.overrides[slot]?.tint ?: false,
                        ),
                    ),
                )
            }
        }
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

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = appTopBarWindowInsets(),
                title = { Text(stringResource(R.string.player_icons)) },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.player_icons_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    onPick = { pendingSlot = slot; pickLauncher.launch(ACCEPTED_TYPES) },
                    onClear = { persist(slot, v1Set.with(slot, null)) },
                    onTintChange = { tint ->
                        val current = v1Set.overrides[slot] ?: return@SlotRow
                        persist(slot, v1Set.with(slot, current.copy(tint = tint)))
                    },
                )
            }

            if (useAppleMusicPlayer) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.v2_player_icons),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = stringResource(R.string.v2_player_icons_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                PlayerIconSlot.entries.filter { it.isV2 }.forEach { slot ->
                    SlotRow(
                        slot = slot,
                        override = v2Set.overrides[slot],
                        onPick = { pendingSlot = slot; pickLauncher.launch(ACCEPTED_TYPES) },
                        onClear = { persist(slot, v2Set.with(slot, null)) },
                        onTintChange = { tint ->
                            val current = v2Set.overrides[slot] ?: return@SlotRow
                            persist(slot, v2Set.with(slot, current.copy(tint = tint)))
                        },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
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
    onPick: () -> Unit,
    onClear: () -> Unit,
    onTintChange: (Boolean) -> Unit,
) {
    val icon = rememberPlayerIcon(slot)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onPick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(stringResource(slot.labelRes), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(
                    if (override == null) R.string.player_icon_default else R.string.player_icon_custom,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (override != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.player_icon_tint),
                    style = MaterialTheme.typography.labelSmall,
                )
                Switch(checked = override.tint, onCheckedChange = onTintChange)
            }
            IconButton(onClick = onClear, onLongClick = {}) {
                Icon(
                    painterResource(R.drawable.delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
