package com.nexamusic.app.vivimusic.changelog



import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nexamusic.app.BuildConfig
import com.nexamusic.app.LocalPlayerAwareWindowInsets
import com.nexamusic.app.R
import com.nexamusic.app.vivimusic.updater.extractUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChangelogScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    versionTag: String = "v${BuildConfig.VERSION_NAME}"
) {
    val context = LocalContext.current
    var changelogSections by remember { mutableStateOf<List<ChangelogSection>>(emptyList()) }
    var updateImage by remember { mutableStateOf<String?>(null) }
    var updateDescription by remember { mutableStateOf<String?>(null) }
    var updateWarning by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var showingCached by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var currentVersionTag by remember { mutableStateOf(versionTag) }
    var availableReleases by remember { mutableStateOf<List<ReleaseMetadata>>(
        listOf(ReleaseMetadata(versionTag, versionTag, context.getString(R.string.current), null))
    ) }
    var isFetchingOldReleases by remember { mutableStateOf(false) }

    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = isLoading || isFetchingOldReleases

    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(pullToRefreshState.distanceFraction).coerceIn(0f, 1f)
    }

    fun fetchChangelog(tag: String) {
        isLoading = true
        hasError = false
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val cachedData = loadChangelogFromCache(context, tag)
                if (cachedData != null) {
                    withContext(Dispatchers.Main) {
                        changelogSections = cachedData.sections
                        updateImage = cachedData.image
                        updateDescription = cachedData.description
                        updateWarning = cachedData.warning
                        isLoading = false
                        showingCached = true
                    }
                } else {
                    val changelogUrl = URL("https://github.com/nkosanamahungela-dev/NexaMusic-/releases/download/$tag/changelog.json")
                    val connection = changelogUrl.openConnection() as HttpURLConnection
                    connection.setRequestProperty("User-Agent", "ViviMusic-Changelog-App")
                    connection.setRequestProperty("Accept", "application/json")
                    
                    if (connection.responseCode == 200) {
                        val changelogJson = connection.inputStream.bufferedReader().use { it.readText() }
                        val changelogData = JSONObject(changelogJson)
                        
                        val desc = changelogData.optString("description", null)
                        val imageUrl = changelogData.optString("image", null)
                        val warning = changelogData.optString("warning", null)
                        val changelogArray = changelogData.optJSONArray("changelog")
                        
                        val sections = mutableListOf<ChangelogSection>()
                        if (changelogArray != null) {
                            for (i in 0 until changelogArray.length()) {
                                val sectionObj = changelogArray.optJSONObject(i)
                                if (sectionObj != null) {
                                    val title = sectionObj.optString("title", "")
                                    val itemsArray = sectionObj.optJSONArray("items")
                                    val items = mutableListOf<String>()
                                    if (itemsArray != null) {
                                        for (j in 0 until itemsArray.length()) {
                                            items.add(itemsArray.getString(j))
                                        }
                                    }
                                    if (title.isNotBlank() || items.isNotEmpty()) {
                                        sections.add(ChangelogSection(title, items))
                                    }
                                } else {
                                    // Fallback: This is the old format (Array of Strings)
                                    val item = changelogArray.optString(i, "")
                                    if (item.isNotBlank()) {
                                        if (sections.isEmpty() || sections[0].title.isNotBlank()) {
                                            sections.add(0, ChangelogSection("", mutableListOf()))
                                        }
                                        (sections[0].items as MutableList<String>).add(item)
                                    }
                                }
                            }
                        }
                        
                        saveChangelogToCache(context, tag, sections, imageUrl, desc, warning)
                        withContext(Dispatchers.Main) {
                            changelogSections = sections
                            updateImage = imageUrl.takeIf { !it.isNullOrBlank() }
                            updateDescription = desc.takeIf { !it.isNullOrBlank() }
                            updateWarning = warning.takeIf { !it.isNullOrBlank() }
                            isLoading = false
                            hasError = false
                            showingCached = false
                        }
                    } else {
                        Timber.tag("ChangelogScreen").e("HTTP Error ${connection.responseCode} for $tag")
                        withContext(Dispatchers.Main) { hasError = true; isLoading = false }
                    }
