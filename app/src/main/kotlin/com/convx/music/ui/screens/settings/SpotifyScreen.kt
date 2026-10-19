package com.convx.music.ui.screens.settings

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.ios.compat.AppTheme

import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.LinearProgressIndicator
import com.convx.music.ui.ios.compat.ModalBottomSheet
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.rememberIosModalSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.music.spotify.SpotifyAuth
import com.music.spotify.SpotifyHomeItem
import com.music.spotify.SpotifyHomeShelf
import com.convx.music.R
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.viewmodels.SpotifyImportViewModel
import com.convx.music.viewmodels.SpotifyImportUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: SpotifyImportViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showSpotifyLogin by remember { mutableStateOf(false) }
    var showPlaylistsSheet by remember { mutableStateOf(false) }
    val importProgress by viewModel.importProgress.collectAsStateWithLifecycle()

    val refreshEnabled = state.isAuthenticated && !state.isLoading
    val rotationAngle by if (state.isLoading) {
        val transition = rememberInfiniteTransition(label = "rotation")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotationAngle"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.spotify),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 24.dp,
    ) {
        IosSettingsGroup(
            title = stringResource(R.string.spotify_account),
            items = listOf(
                if (state.isAuthenticated) {
                    IosSettingsItem(
                        leadingContent = if (!state.accountAvatarUrl.isNullOrBlank()) {
                            {
                                AsyncImage(
                                    model = state.accountAvatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else null,
                        icon = if (state.accountAvatarUrl.isNullOrBlank()) painterResource(R.drawable.spotify) else null,
                        title = {
                            Text(
                                text = if (state.accountName.isNotBlank()) state.accountName
                                else stringResource(R.string.spotify_account),
                                color = AppTheme.colorScheme.primary,
                                style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        trailingContent = {
                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = AppTheme.colorScheme.surfaceContainer,
                                    contentColor = AppTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(stringResource(R.string.action_logout))
                            }
                        },
                        onClick = {}
                    )
                } else {
                    IosSettingsItem(
                        title = { Text(stringResource(R.string.spotify_connect)) },
                        description = { Text(if (state.isLoading) "Connecting to Spotify…" else stringResource(R.string.spotify_not_connected)) },
                        icon = painterResource(R.drawable.spotify),
                        enabled = !state.isLoading,
                        onClick = { showSpotifyLogin = true }
                    )
                }
            )
        )

        if (state.isAuthenticated) {
            Spacer(modifier = Modifier.height(24.dp))
            SpotifyHomeFeedSection(
                state = state,
                onRefresh = {
                    viewModel.loadSources()
                    viewModel.loadHomeFeed()
                },
                onImportPlaylist = viewModel::importHomePlaylist,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val totalPlaylists = state.playlists.size + if (state.likedSongsCount > 0) 1 else 0
        IosSettingsGroup(
            title = stringResource(R.string.playlists),
            items = listOf(
                IosSettingsItem(
                    title = { Text(stringResource(R.string.spotify_select_sources)) },
                    description = {
                        Text(
                            if (state.isAuthenticated) {
                                if (totalPlaylists > 0) stringResource(R.string.spotify_available_count, totalPlaylists)
                                else stringResource(R.string.spotify_no_sources)
                            } else {
                                stringResource(R.string.spotify_not_connected)
                            }
                        )
                    },
                    icon = painterResource(R.drawable.bookmark_star_library),
                    enabled = state.isAuthenticated && totalPlaylists > 0 && !state.isLoading,
                    onClick = { showPlaylistsSheet = true }
                ),
                IosSettingsItem(
                    title = { Text(stringResource(R.string.spotify_refresh)) },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    AppTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            SymbolIcon(symbol = sf("arrow.triangle.2.circlepath"), contentDescription = null, size = 24.dp, modifier = Modifier
                                    
                                    .graphicsLayer {
                                        rotationZ = rotationAngle
                                    }, tint = if (!refreshEnabled) {
                                    AppTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                } else {
                                    AppTheme.colorScheme.primary.copy(alpha = 0.9f)
                                })
                        }
                    },
                    enabled = refreshEnabled,
                    onClick = {
                        viewModel.loadSources()
                        viewModel.loadHomeFeed()
                    }
                )
            )
        )

        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.Top
        ) {
            SymbolIcon(symbol = sf("info.circle"), contentDescription = null, modifier = Modifier.padding(end = 8.dp), tint = AppTheme.colorScheme.onSurfaceVariant)
            Text(
                text = stringResource(R.string.spotify_import_desc),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    
    }

    if (showSpotifyLogin) {
        SpotifyLoginSheet(
            onDismiss = { showSpotifyLogin = false },
            onCookiesCaptured = { spDc, spKey ->
                showSpotifyLogin = false
                viewModel.connectWithCookies(spDc, spKey)
            }
        )
    }

    importProgress?.let { progress ->
        val isFinished = progress.isFinished || progress.percent >= 1f
        DefaultDialog(
            onDismiss = {
                if (isFinished) {
                    viewModel.dismissImportProgress()
                } else {
                    viewModel.cancelImport()
                }
            },
            title = {
                Text(
                    text = if (isFinished) {
                        stringResource(R.string.spotify_import_complete)
                    } else {
                        stringResource(R.string.spotify_import_in_progress)
                    }
                )
            },
            buttons = {
                if (isFinished) {
                    Button(
                        onClick = { viewModel.dismissImportProgress() },
                        shape = CircleShape
                    ) {
                        Text(stringResource(android.R.string.ok))
                    }
                } else {
                    TextButton(onClick = { viewModel.cancelImport() }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.playlists) + ": " + progress.playlistName,
                    style = AppTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isFinished) {
                        stringResource(R.string.spotify_imported_songs_count, progress.currentSongIndex, progress.totalSongs)
                    } else {
                        stringResource(R.string.spotify_importing_songs_count, progress.currentSongIndex, progress.totalSongs)
                    },
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = { progress.percent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = AppTheme.colorScheme.primary,
                    trackColor = AppTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
    state.errorMessage?.let { error ->
        DefaultDialog(
            onDismiss = { viewModel.dismissError() },
            title = { Text("Error") },
            buttons = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        ) {
            Text(
                text = error,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showPlaylistsSheet) {
        SpotifyPlaylistBottomSheet(
            onDismiss = { showPlaylistsSheet = false },
            viewModel = viewModel
        )
    }
}

@Composable
private fun SpotifyHomeFeedSection(
    state: SpotifyImportUiState,
    onRefresh: () -> Unit,
    onImportPlaylist: (SpotifyHomeItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.spotify_home_title),
                style = AppTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colorScheme.onSurface,
            )
            TextButton(onClick = onRefresh, enabled = !state.isHomeFeedLoading) {
                Text(stringResource(R.string.spotify_refresh))
            }
        }

        when {
            state.isHomeFeedLoading && state.homeShelves.isEmpty() -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            state.homeFeedErrorMessage != null && state.homeShelves.isEmpty() -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.spotify_home_error),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = onRefresh) {
                        Text(stringResource(R.string.spotify_home_retry))
                    }
                }
            }
            state.homeShelves.isEmpty() -> {
                Text(
                    text = stringResource(R.string.spotify_home_empty),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> {
                state.homeShelves.forEach { shelf ->
                    SpotifyHomeShelfRow(shelf = shelf, onImportPlaylist = onImportPlaylist)
                }
            }
        }
    }
}

@Composable
private fun SpotifyHomeShelfRow(
    shelf: SpotifyHomeShelf,
    onImportPlaylist: (SpotifyHomeItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = shelf.title,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colorScheme.onSurface,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(shelf.items, key = SpotifyHomeItem::uri) { item ->
                Surface(
                    modifier = Modifier.width(148.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = AppTheme.colorScheme.surfaceContainerLow,
                    onClick = { onImportPlaylist(item) },
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AsyncImage(
                            model = item.artworkUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppTheme.colorScheme.primary.copy(alpha = 0.08f)),
                            contentScale = ContentScale.Crop,
                            error = painterResource(R.drawable.spotify),
                            placeholder = painterResource(R.drawable.spotify),
                        )
                        Text(
                            text = item.title,
                            style = AppTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = AppTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            minLines = 2,
                        )
                        Text(
                            text = stringResource(R.string.spotify_home_import_hint),
                            style = AppTheme.typography.labelSmall,
                            color = AppTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpotifyLoginSheet(
    onDismiss: () -> Unit,
    onCookiesCaptured: (spDc: String, spKey: String) -> Unit,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var captured by remember { mutableStateOf(false) }
    var pageError by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.loadUrl("about:blank")
            webView?.destroy()
            webView = null
        }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = AppTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.spotify_login_title),
                style = AppTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.spotify_waiting_for_login),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { if (webView?.canGoBack() == true) webView?.goBack() else onDismiss() }) { Text("Back") }
                TextButton(onClick = onDismiss) { Text("Close") }
            }
            if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            pageError?.let { message ->
                Text(message, color = AppTheme.colorScheme.error)
                TextButton(onClick = { pageError = null; webView?.reload() }) { Text("Retry") }
            }
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(AppTheme.shapes.large),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        // Spotify rejects embedded WebViews; drop the WebView markers from the UA.
                        settings.userAgentString = SpotifyAuth.browserUserAgent(settings.userAgentString)
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.REQUESTED_WITH_HEADER_ALLOW_LIST)) {
                            WebSettingsCompat.setRequestedWithHeaderOriginAllowList(settings, emptySet())
                        }
                        webViewClient = object : WebViewClient() {
                            private fun captureCookies(url: String?): Boolean {
                                if (captured) return true
                                val uri = android.net.Uri.parse(url.orEmpty())
                                if (uri.scheme != "https" || uri.host != "open.spotify.com") return false
                                cookieManager.flush()
                                val domainsToCheck = listOfNotNull(
                                    url,
                                    "https://open.spotify.com",
                                    "https://accounts.spotify.com",
                                    "https://spotify.com",
                                    ".spotify.com"
                                )
                                for (domain in domainsToCheck) {
                                    val cookiesStr = cookieManager.getCookie(domain) ?: continue
                                    val cookies = cookiesStr.split(";").associate {
                                        val parts = it.split("=")
                                        val key = parts.firstOrNull()?.trim().orEmpty()
                                        val valStr = parts.drop(1).joinToString("=").trim()
                                        key to valStr
                                    }
                                    val spDc = cookies["sp_dc"].orEmpty()
                                    if (spDc.isNotBlank()) {
                                        captured = true
                                        onCookiesCaptured(spDc, cookies["sp_key"].orEmpty())
                                        return true
                                    }
                                }
                                return false
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest,
                            ): Boolean = request.url.scheme != "https"

                            override fun onPageStarted(
                                view: WebView,
                                url: String?,
                                favicon: android.graphics.Bitmap?,
                            ) {
                                loading = true
                                pageError = null
                                captureCookies(url)
                            }

                            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: android.webkit.WebResourceError) {
                                if (request.isForMainFrame) {
                                    loading = false
                                    pageError = "Spotify's sign-in page could not load. Check your connection and retry."
                                }
                            }

                            override fun onPageFinished(view: WebView, url: String?) {
                                loading = false
                                captureCookies(url)
                            }
                        }
                        webChromeClient = android.webkit.WebChromeClient()
                        webView = this
                        // CookieManager is shared by every WebView in the app. Clearing it here
                        // races its asynchronous callback against loadUrl(), and also removes the
                        // user's Discord session. Keep the existing browser session and let the
                        // normal page callbacks detect Spotify's authenticated cookie.
                        loadUrl(SpotifyAuth.LOGIN_URL)
                    }
                },
                update = { view ->
                    webView = view
                },
            )
        }
    }
    }
}
