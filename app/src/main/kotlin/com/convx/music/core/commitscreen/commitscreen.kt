package com.convx.music.core.commitscreen

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import android.content.Intent
import android.net.Uri
import timber.log.Timber
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Scaffold
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFChevronLeft
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class CommitData(
    val sha: String,
    val message: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val authorLogin: String?,
    val date: String,
    val htmlUrl: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CommitScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val context = LocalContext.current
    var commits by remember { mutableStateOf<List<CommitData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pullToRefreshState = rememberPullToRefreshState()

    val scaleFraction = {
        if (isLoading) 1f
        else LinearOutSlowInEasing.transform(pullToRefreshState.distanceFraction).coerceIn(0f, 1f)
    }

    fun fetchCommits() {
        isLoading = true
        hasError = false
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/cosmictaserdev-creator/Convx/commits?branch=main&per_page=50")
                val json = url.openStream().bufferedReader().use { it.readText() }
                val array = JSONArray(json)
                val outputFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

                val list = mutableListOf<CommitData>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val sha = obj.getString("sha")
                    val htmlUrl = obj.getString("html_url")

                    val commitObj = obj.getJSONObject("commit")
                    val fullMessage = commitObj.getString("message")
                    // Take only the first line (subject) of the commit message
                    val message = fullMessage.lines().firstOrNull { it.isNotBlank() } ?: fullMessage

                    val authorObj = commitObj.getJSONObject("author")
                    val authorName = authorObj.optString("name", "Unknown")
                    val rawDate = authorObj.optString("date", "")
                    val formattedDate = try {
                        ZonedDateTime.parse(rawDate).format(outputFormatter)
                    } catch (e: Exception) { rawDate }

                    // GitHub user info (may be null for non-GitHub accounts)
                    val authorLogin = if (!obj.isNull("author")) {
                        obj.getJSONObject("author").optString("login", null)
                    } else null
                    val authorAvatarUrl = if (!obj.isNull("author")) {
                        obj.getJSONObject("author").optString("avatar_url", null)
                    } else null

                    list.add(CommitData(sha, message, authorName, authorAvatarUrl, authorLogin, formattedDate, htmlUrl))
                }

                withContext(Dispatchers.Main) {
                    commits = list
                    isLoading = false
                    hasError = false
                }
            } catch (e: Exception) {
                Timber.tag("CommitScreen").e("Error fetching commits: ${e.message}")
                withContext(Dispatchers.Main) {
                    hasError = true
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchCommits()
    }

    com.convx.music.ui.component.ScreenScaffold(
        title = stringResource(R.string.commits_title),
        titleAlpha = 1f,
        onBack = navController::navigateUp,
    ) { chrome ->
    Scaffold(
        modifier = Modifier.then(chrome.contentModifier()).pullToRefresh(
            state = pullToRefreshState,
            isRefreshing = isLoading,
            onRefresh = { fetchCommits() }
        ),

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = com.convx.music.ui.component.TopBarContentHeight)
                .background(AppTheme.colorScheme.background)
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom))
        ) {
            when {
                hasError && !isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            SymbolIcon(symbol = sf("exclamationmark.circle.fill"), contentDescription = null, size = 48.dp, tint = AppTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.error_loading_commits),
                                color = AppTheme.colorScheme.error
                            )
                        }
                    }
                }

                commits.isNotEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        commits.forEachIndexed { index, commit ->
                            CommitItem(
                                commit = commit,
                                onClick = {
                                    ContextCompat.startActivity(
                                        context,
                                        Intent(Intent.ACTION_VIEW, Uri.parse(commit.htmlUrl)),
                                        null
                                    )
                                }
                            )
                            if (index < commits.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 72.dp),
                                    color = AppTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // Pull-to-refresh indicator
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        scaleX = scaleFraction()
                        scaleY = scaleFraction()
                    }
            ) {
                PullToRefreshDefaults.LoadingIndicator(state = pullToRefreshState, isRefreshing = isLoading)
            }
        }
    }
    }
}

