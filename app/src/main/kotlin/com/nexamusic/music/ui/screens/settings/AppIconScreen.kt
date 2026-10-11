/**
 * NexaMusic Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.music.ui.screens.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nexamusic.music.LocalPlayerAwareWindowInsets
import com.nexamusic.music.R
import com.nexamusic.music.constants.AppIconKey
import com.nexamusic.music.ui.component.IconButton
import com.nexamusic.music.ui.utils.appTopBarWindowInsets
import com.nexamusic.music.ui.utils.backToMain
import com.nexamusic.music.utils.AppIcon
import com.nexamusic.music.utils.rememberPreference

/** Adaptive icons draw a 108dp canvas but launchers only reveal about 72dp of it. */
private const val ADAPTIVE_OVERSCAN = 108f / 72f

/**
 * Picker for the launcher icon. Each tile reproduces what the launcher will draw: the variant's
 * adaptive foreground composited over the shared tile colour, clipped to a squircle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppIconScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    AlertDialog(
        onDismissRequest = navController::navigateUp,
        title = { Text(stringResource(R.string.app_icon)) },
        text = { Text("Available Soon") },
        confirmButton = {
            TextButton(onClick = navController::navigateUp) {
                Text(stringResource(android.R.string.ok))
            }
        },
    )
}
