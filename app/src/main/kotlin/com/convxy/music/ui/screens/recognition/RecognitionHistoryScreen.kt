/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.recognition

import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.background
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Box
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Row
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.only
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.height
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.size
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.width
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.items
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.text.KeyboardOptions
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Card
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.CardDefaults
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.IconButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Scaffold
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TextButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TextField
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TextFieldDefaults
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.collectAsState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.getValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateOf
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.remember
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.rememberCoroutineScope
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.rememberSaveable
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.setValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Alignment
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.draw.clip
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.graphics.Color
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.layout.ContentScale
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.stringResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.font.FontWeight
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.input.ImeAction
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.input.TextFieldValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.style.TextOverflow
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import com.convxy.music.ui.utils.appTopBarWindowInsets
import coil3.compose.AsyncImage
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.LocalDatabase
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.R
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.ThumbnailCornerRadius
import com.convxy.music.constants.ThumbnailRoundedShape
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.db.entities.RecognitionHistory
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.DefaultDialog
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.Dispatchers
import com.convxy.music.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.launch
import com.convxy.music.ui.utils.appTopBarWindowInsets
import java.time.LocalDate
import com.convxy.music.ui.utils.appTopBarWindowInsets
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RecognitionHistoryScreen(
    navController: NavController
) {
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val coroutineScope = rememberCoroutineScope()

    val historyItems by database.recognitionHistory().collectAsState(initial = emptyList())
    var showClearDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<RecognitionHistory?>(null) }

    // Search state
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    // Filtered list - client-side, matches title or artist
    val filteredItems = remember(historyItems, query) {
        if (query.text.isEmpty()) historyItems
        else historyItems.filter { item ->
            item.title.contains(query.text, ignoreCase = true) ||
                item.artist.contains(query.text, ignoreCase = true)
        }
    }

    // Group by date label: Today / Yesterday / This Week / Month Year
    val groupedItems = remember(filteredItems) {
        val today = LocalDate.now()
        filteredItems.groupBy { item ->
            val date = item.recognizedAt.toLocalDate()
            when {
                date == today                -> "Today"
                date == today.minusDays(1)  -> "Yesterday"
                date >= today.minusDays(7)  -> "This Week"
                else -> item.recognizedAt.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
            }
        }
    }

    if (showClearDialog) {
        DefaultDialog(
            onDismiss = { showClearDialog = false },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = null
                )
            },
            title = { Text(stringResource(R.string.clear_recognition_history)) },
            buttons = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
                TextButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            database.query {
                                clearRecognitionHistory()
                            }
                        }
                        showClearDialog = false
                    }
                ) {
                    Text(stringResource(R.string.clear))
                }
            }
        ) {
            Text(
                text = stringResource(R.string.clear_recognition_history_confirm),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    itemToDelete?.let { item ->
        DefaultDialog(
            onDismiss = { itemToDelete = null },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = null
                )
            },
            title = { Text(stringResource(R.string.delete)) },
            buttons = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
                TextButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            database.query {
                                deleteRecognitionHistoryById(item.id)
                            }
                        }
                        itemToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            }
        ) {
            Text(
                text = stringResource(R.string.delete_playlist_confirm, item.title),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
            windowInsets = appTopBarWindowInsets(),
                title = { Text(stringResource(R.string.recognition_history)) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        onLongClick = { navController.backToMain() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (historyItems.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                painter = painterResource(R.drawable.clear_all),
                                contentDescription = stringResource(R.string.clear_recognition_history)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Static search bar ──────────────────────────────────
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.search),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (query.text.isNotEmpty()) {
                        IconButton(onClick = { query = TextFieldValue() }) {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor  = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // ── List / empty state ─────────────────────────────────
            when {
                historyItems.isEmpty() -> {
                    // No history at all
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.history),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No recognition history",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                filteredItems.isEmpty() -> {
                    // History exists but query matches nothing
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No results for \"${query.text}\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = LocalPlayerAwareWindowInsets.current
                            .only(WindowInsetsSides.Bottom)
                            .asPaddingValues()
                    ) {
                        if (query.text.isEmpty()) {
                            // No active search → show grouped with sticky date headers
                            groupedItems.forEach { (label, groupItems) ->
                                stickyHeader(key = "header_$label") {
                                    NavigationTitle(
                                        title = label,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surface)
                                    )
                                }
                                items(
                                    items = groupItems,
                                    key = { it.id }
                                ) { item ->
                                    RecognitionHistoryItem(
                                        item = item,
                                        onClick = {
                                            val searchQuery = "${item.title} ${item.artist}"
                                            navController.navigate("search/${java.net.URLEncoder.encode(searchQuery, "UTF-8")}")
                                        },
                                        onDelete = {
                                            itemToDelete = item
                                        }
                                    )
                                }
                            }
                        } else {
                            // Active search → flat list, no date headers
                            items(
                                items = filteredItems,
                                key = { it.id }
                            ) { item ->
                                RecognitionHistoryItem(
                                    item = item,
                                    onClick = {
                                        val searchQuery = "${item.title} ${item.artist}"
                                        navController.navigate("search/${java.net.URLEncoder.encode(searchQuery, "UTF-8")}")
                                    },
                                    onDelete = {
                                        itemToDelete = item
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecognitionHistoryItem(
    item: RecognitionHistory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .bounceClick { onClick() },
        shape = ThumbnailRoundedShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art
            AsyncImage(
                model = item.coverArtUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(ThumbnailRoundedShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Track info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.recognizedAt.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Delete action
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = stringResource(R.string.delete_from_history),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
