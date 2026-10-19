package com.convx.music.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convx.music.R
import com.convx.music.ui.component.ScreenPage
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.viewmodels.SongInsightViewModel

@Composable
fun SongInsightScreen(navController: NavController, viewModel: SongInsightViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.refreshConfiguration()
        onPauseOrDispose { }
    }
    DisposableEffect(viewModel) { onDispose { viewModel.cancel() } }
    ScreenPage(
        title = stringResource(R.string.song_insight_title), onBack = { navController.navigateUp() },
        horizontalPadding = 20.dp, verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(state.title)
        Text(state.artist)
        Text(stringResource(R.string.song_insight_disclosure))
        if (!state.loaded || state.running) CircularProgressIndicator()
        if (state.failed) Text(stringResource(R.string.song_insight_error))
        state.summary?.let { summary -> SelectionContainer { Text(summary) } }
        if (state.running) {
            TextButton(onClick = viewModel::cancel) { Text(stringResource(android.R.string.cancel)) }
        } else if (state.loaded && state.title.isNotBlank() && state.configured) {
            Button(onClick = viewModel::generate, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(if (state.summary == null) R.string.song_insight_generate else R.string.song_insight_retry))
            }
        }
        if (!state.configured && state.loaded) Text(stringResource(R.string.song_insight_configure))
        TextButton(onClick = { navController.navigate("settings/ai") }) { Text(stringResource(R.string.settings)) }
    }
}
