package com.convx.music.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.R
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.ui.menu.AddToPlaylistDialog
import com.convx.music.viewmodels.AppleMusicImportState
import com.convx.music.viewmodels.AppleMusicImportViewModel

@Composable
fun AppleMusicPlaylistImport(
    navController: NavController,
    viewModel: AppleMusicImportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val exportPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let(viewModel::loadExport)
    }
    IosSettingsGroup(items = listOf(IosSettingsItem(
        title = { Text(stringResource(R.string.import_from_apple_music)) },
        icon = painterResource(R.drawable.playlist_add),
        onClick = viewModel::open,
    )))
    if (state.visible) AppleMusicImportDialog(
        state,
        viewModel::updateUrl,
        viewModel::load,
        viewModel::chooseDestination,
        onCreatePlaylist = { viewModel.createPlaylist { id -> navController.navigate("local_playlist/$id") } },
        onDismiss = viewModel::dismiss,
        onImportFile = { exportPicker.launch(arrayOf("application/xml", "text/xml", "text/plain")) },
    )
    key(state.session) {
        AddToPlaylistDialog(
            isVisible = state.choosingDestination,
            allowSyncing = false,
            initialTextFieldValue = state.title,
            onGetSong = { viewModel.persistSongs() },
            onDismiss = viewModel::dismiss,
        )
    }
}

@Composable
private fun AppleMusicImportDialog(
    state: AppleMusicImportState,
    onUrlChange: (String) -> Unit,
    onLoad: () -> Unit,
    onChooseDestination: () -> Unit,
    onCreatePlaylist: () -> Unit,
    onDismiss: () -> Unit,
    onImportFile: () -> Unit,
) {
    DefaultDialog(
        title = { Text(stringResource(R.string.import_from_apple_music)) },
        onDismiss = onDismiss,
        buttons = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
            if (!state.busy) {
                if (state.ready) {
                    TextButton(onClick = onChooseDestination, enabled = state.songs.isNotEmpty() && !state.busy) { Text(stringResource(R.string.apple_music_choose_playlist)) }
                    TextButton(onClick = onCreatePlaylist, enabled = state.songs.isNotEmpty() && !state.busy) { Text(stringResource(R.string.create_playlist)) }
                } else {
                    TextButton(onClick = onLoad, enabled = state.url.isNotBlank()) { Text(stringResource(R.string.import_playlist)) }
                }
            }
        },
    ) {
        when {
            state.busy -> Text(
                when {
                    state.creatingPlaylist -> stringResource(R.string.apple_music_creating_playlist)
                    state.total == 0 -> stringResource(R.string.apple_music_loading)
                    else -> stringResource(R.string.apple_music_matching, state.completed, state.total)
                }
            )
            state.ready -> {
                Text(state.title)
                Text(stringResource(R.string.apple_music_match_summary, state.songs.size, state.skipped))
                state.error?.let { Text(stringResource(it)) }
            }
            else -> {
                Text(stringResource(R.string.apple_music_public_link_description))
                OutlinedTextField(value = state.url, onValueChange = onUrlChange, singleLine = true,
                    label = { Text(stringResource(R.string.apple_music_playlist_link)) })
                Text(stringResource(R.string.apple_music_private_export_description))
                TextButton(onClick = onImportFile) { Text(stringResource(R.string.apple_music_import_export)) }
                state.error?.let { Text(stringResource(it)) }
            }
        }
    }
}
