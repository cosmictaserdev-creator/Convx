/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.recognition

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.utils.openSearchResults

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.FilledTonalButton
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.Scaffold
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFArrowClockwise
import com.composables.sfsymbols.monochrome.SFClock
import com.composables.sfsymbols.monochrome.SFExclamationmarkTriangle
import com.composables.sfsymbols.monochrome.SFMicrophone
import com.composables.sfsymbols.monochrome.SFMusicNote
import com.composables.sfsymbols.monochrome.SFPlayFill
import com.composables.sfsymbols.monochrome.SFXmark
import com.convx.music.LocalDatabase
import com.convx.music.R
import com.convx.music.db.entities.RecognitionHistory
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.ui.utils.backToMain
import com.music.shazamkit.models.RecognitionResult
import com.music.shazamkit.models.RecognitionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import com.convx.music.ui.component.ScreenScaffold
import com.convx.music.ui.component.topBarContentPadding
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognitionScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val coroutineScope = rememberCoroutineScope()
    var recognitionJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    fun recognize() {
        if (recognitionJob?.isActive == true) return
        recognitionJob = coroutineScope.launch {
            com.convx.music.recognition.MusicRecognitionService.recognize(context)
        }
    }

    fun cancelRecognition() {
        recognitionJob?.cancel()
        com.convx.music.recognition.MusicRecognitionService.reset()
    }
    
    LaunchedEffect(Unit) {
        com.convx.music.recognition.MusicRecognitionService.reset()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            recognitionJob?.cancel()
            com.convx.music.recognition.MusicRecognitionService.reset()
        }
    }
    
    val recognitionStatus by com.convx.music.recognition.MusicRecognitionService.recognitionStatus.collectAsStateWithLifecycle()
    
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
                == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            recognize()
        }
    }
    
    fun startRecognition() {
        if (hasPermission) {
            recognize()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    
    fun resetToReady() {
        cancelRecognition()
    }

    fun saveToHistory(result: RecognitionResult) {
        coroutineScope.launch(Dispatchers.IO) {
            database.query {
                insert(
                    RecognitionHistory(
                        trackId = result.trackId,
                        title = result.title,
                        artist = result.artist,
                        album = result.album,
                        coverArtUrl = result.coverArtUrl,
                        coverArtHqUrl = result.coverArtHqUrl,
                        genre = result.genre,
                        releaseDate = result.releaseDate,
                        label = result.label,
                        shazamUrl = result.shazamUrl,
                        appleMusicUrl = result.appleMusicUrl,
                        spotifyUrl = result.spotifyUrl,
                        isrc = result.isrc,
                        youtubeVideoId = result.youtubeVideoId,
                        recognizedAt = LocalDateTime.now()
                    )
                )
            }
        }
    }
    
    ScreenScaffold(
        title = stringResource(R.string.recognize_music),
        // A single centred listening view, nothing scrolls under the bar.
        titleAlpha = 1f,
        onBack = { navController.navigateUp() },
        onBackLongClick = { navController.backToMain() },
        actions = {
            IconButton(onClick = { navController.navigate("recognition_history") }) {
                SymbolIcon(symbol = sf("SFClock"), contentDescription = stringResource(R.string.recognition_history))
            }
        },
    ) { chrome ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topBarContentPadding())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = recognitionStatus,
                transitionSpec = {
                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                },
                label = "recognition_content"
            ) { status ->
                when (status) {
                    is RecognitionStatus.Ready -> {
                        ReadyState(onStartRecognition = ::startRecognition)
                    }
                    is RecognitionStatus.Listening -> {
                        ListeningState(
                            onCancel = ::cancelRecognition
                        )
                    }
                    is RecognitionStatus.Processing -> {
                        ProcessingState()
                    }
                    is RecognitionStatus.Success -> {
                        SuccessState(
                            result = status.result,
                            onPlayOnApp = { result ->
                                // Search for the track on YouTube Music
                                val searchQuery = "${result.title} ${result.artist}"
                                navController.openSearchResults(searchQuery)
                            },
                            onTryAgain = {
                                startRecognition()
                            },
                            onClose = ::resetToReady,
                            onSaveToHistory = ::saveToHistory
                        )
                    }
                    is RecognitionStatus.NoMatch -> {
                        NoMatchState(
                            message = status.message,
                            onTryAgain = {
                                startRecognition()
                            }
                        )
                    }
                    is RecognitionStatus.Error -> {
                        ErrorState(
                            message = status.message,
                            onTryAgain = {
                                startRecognition()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadyState(
    onStartRecognition: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AppTheme.colorScheme.primary.copy(alpha = 0.3f),
                            AppTheme.colorScheme.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .bounceClick { onStartRecognition() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
SymbolIcon(symbol = sf("SFMicrophone"), contentDescription = null, size = 64.dp, tint = AppTheme.colorScheme.onPrimary)
            }
        }
        
        Text(
            text = stringResource(R.string.tap_to_recognize),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ListeningState(
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Container large enough for scaled animation (200dp * 1.2 = 240dp)
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer pulsing ring
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.primary.copy(alpha = 0.2f))
            )

            // Inner pulsing ring
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer { scaleX = scale * 0.9f; scaleY = scale * 0.9f }
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
            
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.primary)
                    .bounceClick { onCancel() },
                contentAlignment = Alignment.Center
            ) {
SymbolIcon(symbol = sf("SFMicrophone"), contentDescription = null, size = 64.dp, tint = AppTheme.colorScheme.onPrimary)
            }
        }
        
        Text(
            text = stringResource(R.string.listening),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.primary
        )
        
        OutlinedButton(onClick = onCancel) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Composable
private fun ProcessingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "rotate")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing)
            ),
            label = "rotation"
        )
        
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .border(
                        width = 4.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                AppTheme.colorScheme.primary,
                                AppTheme.colorScheme.primary.copy(alpha = 0.5f),
                                Color.Transparent,
                                AppTheme.colorScheme.primary.copy(alpha = 0.5f),
                                AppTheme.colorScheme.primary
                            )
                        ),
                        shape = CircleShape
                    )
            )
            
            SymbolIcon(symbol = sf("SFMusicNote"), contentDescription = null, size = 48.dp, tint = AppTheme.colorScheme.primary)
        }
        
        Text(
            text = stringResource(R.string.processing),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SuccessState(
    result: RecognitionResult,
    onPlayOnApp: (RecognitionResult) -> Unit,
    onTryAgain: () -> Unit,
    onClose: () -> Unit,
    onSaveToHistory: (RecognitionResult) -> Unit
) {
    LaunchedEffect(result) {
        onSaveToHistory(result)
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .size(180.dp)
                .aspectRatio(1f),
            shape = RoundedCornerShape(com.convx.music.constants.ThumbnailCornerRadius),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AsyncImage(
                model = result.coverArtHqUrl ?: result.coverArtUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = result.title,
            style = AppTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = result.artist,
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        result.album?.let { album ->
            Text(
                text = album,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Action buttons - stacked vertically
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onPlayOnApp(result) },
                modifier = Modifier.fillMaxWidth()
            ) {
                SymbolIcon(symbol = sf("SFPlayFill"), contentDescription = null, size = 18.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.play_on_app))
            }
            
            FilledTonalButton(
                onClick = onTryAgain,
                modifier = Modifier.fillMaxWidth()
            ) {
                SymbolIcon(symbol = sf("SFMicrophone"), contentDescription = null, size = 18.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.re_listen))
            }
            
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                SymbolIcon(symbol = sf("SFXmark"), contentDescription = null, size = 18.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.close))
            }
        }
    }
}

@Composable
private fun NoMatchState(
    message: String,
    onTryAgain: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AppTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            SymbolIcon(symbol = sf("SFXmark"), contentDescription = null, size = 48.dp, tint = AppTheme.colorScheme.onErrorContainer)
        }
        
        Text(
            text = stringResource(R.string.no_match_found),
            style = AppTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = message,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Button(onClick = onTryAgain) {
            SymbolIcon(symbol = sf("SFArrowClockwise"), contentDescription = null, size = 18.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.try_again))
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onTryAgain: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AppTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            SymbolIcon(symbol = sf("SFExclamationmarkTriangle"), contentDescription = null, size = 48.dp, tint = AppTheme.colorScheme.onErrorContainer)
        }
        
        Text(
            text = stringResource(R.string.recognition_error),
            style = AppTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = message,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Button(onClick = onTryAgain) {
            SymbolIcon(symbol = sf("SFArrowClockwise"), contentDescription = null, size = 18.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.try_again))
        }
    }
}
