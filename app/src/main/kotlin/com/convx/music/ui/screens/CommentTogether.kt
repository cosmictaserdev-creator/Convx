/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.ios.compat.AppTheme

import androidx.compose.animation.*
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.convx.music.LocalListenTogetherManager
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFBubbleLeftAndBubbleRight
import com.composables.sfsymbols.monochrome.SFChevronLeft
import com.composables.sfsymbols.monochrome.SFPaperplane
import com.composables.sfsymbols.monochrome.SFXmark
import com.convx.music.listentogether.ChatMessagePayload
import com.convx.music.listentogether.RepliedMessage
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import android.net.Uri
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentTogetherScreen(navController: NavController) {
    val manager = LocalListenTogetherManager.current ?: return
    val messages by manager.chatMessages.collectAsStateWithLifecycle()
    val userId by manager.userId.collectAsStateWithLifecycle()
    val roomState by manager.roomState.collectAsStateWithLifecycle()
    val windowInsets = LocalPlayerAwareWindowInsets.current
    
    var textInput by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<ChatMessagePayload?>(null) }
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    com.convx.music.ui.component.ScreenScaffold(
        title = stringResource(R.string.comments),
        titleAlpha = 1f,
        onBack = { navController.navigateUp() },
        actions = {
            roomState?.roomCode?.let { code ->
                Text(text = code, style = AppTheme.typography.labelMedium)
            }
        },
    ) { chrome ->
    Scaffold(
        modifier = Modifier.then(chrome.contentModifier()),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(16.dp)
            ) {
                AnimatedVisibility(
                    visible = replyingTo != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    replyingTo?.let { replyMsg ->
                        Surface(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxWidth(),
                            color = AppTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .height(IntrinsicSize.Min),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(AppTheme.colorScheme.primary)
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = replyMsg.username,
                                        style = AppTheme.typography.labelMedium,
                                        color = AppTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = replyMsg.message,
                                        style = AppTheme.typography.bodySmall,
                                        maxLines = 1,
                                        color = AppTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { replyingTo = null }) {
                                    SymbolIcon(symbol = sf("SFXmark"), contentDescription = null, size = 16.dp)
                                }
                            }
                        }
                    }
                }

                ChatInputArea(
                    text = textInput,
                    onTextChange = { textInput = it },
                    onSend = {
                        if (textInput.isNotBlank()) {
                            val replyData = replyingTo?.let { 
                                RepliedMessage(it.username, it.message)
                            }
                            manager.sendChatMessage(textInput, replyData)
                            textInput = ""
                            replyingTo = null
                            focusManager.clearFocus()
                        }
                    }
                )
            }
        },
        contentWindowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppTheme.colorScheme.surface)
        ) {
            if (messages.isEmpty()) {
                EmptyChatPlaceholder()
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = com.convx.music.ui.component.topBarHeight() + 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.timestamp.toString() + it.userId }
                    ) { message ->
                        MessageItem(
                            message = message,
                            isMe = message.userId == userId,
                            onReply = { replyingTo = it }
                        )
                    }
                }
            }
        }
    }
}
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun MessageItem(
    message: ChatMessagePayload, 
    isMe: Boolean,
    onReply: (ChatMessagePayload) -> Unit
) {
    val alignment = if (isMe) Alignment.End else Alignment.Start
    val bubbleColor = if (isMe) AppTheme.colorScheme.primaryContainer else AppTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) AppTheme.colorScheme.onPrimaryContainer else AppTheme.colorScheme.onSurfaceVariant
    val replyBgColor = if (isMe) AppTheme.colorScheme.primary.copy(alpha = 0.1f) else AppTheme.colorScheme.onSurface.copy(alpha = 0.05f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!isMe) {
            Text(
                text = message.username,
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
            )
        }
        
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            tonalElevation = 2.dp,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .combinedBounceClick(
                    onClick = {},
                    onLongClick = { onReply(message) }
                )
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                message.replyTo?.let { reply ->
                    Surface(
                        color = replyBgColor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(IntrinsicSize.Min)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(3.dp)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(AppTheme.colorScheme.primary)
                            )
                            Column(modifier = Modifier.padding(start = 10.dp)) {
                                Text(
                                    text = reply.username,
                                    style = AppTheme.typography.labelSmall,
                                    color = AppTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = formatMessageWithLinks(reply.message),
                                    style = AppTheme.typography.bodySmall,
                                    maxLines = 2,
                                    color = textColor.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatMessageWithLinks(message.message),
                    style = AppTheme.typography.bodyMedium,
                    color = textColor
                )
                
                Text(
                    text = formatTime(message.timestamp),
                    style = AppTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.6f),
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatInputArea(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text(stringResource(R.string.type_message)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onSend() }
                )
            )
            
            FloatingActionButton(
                onClick = onSend,
                shape = CircleShape,
                containerColor = AppTheme.colorScheme.primary,
                contentColor = AppTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
            ) {
                SymbolIcon(symbol = sf("SFPaperplane"), contentDescription = stringResource(R.string.send), size = 24.dp)
            }
        }
    }
}

@Composable
private fun EmptyChatPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AppTheme.colorScheme.primaryContainer,
                            AppTheme.colorScheme.secondaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            SymbolIcon(symbol = sf("SFBubbleLeftAndBubbleRight"), contentDescription = null, size = 56.dp, tint = AppTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No messages yet",
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colorScheme.onSurface
        )
        Text(
            text = "Start the conversation!",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun formatMessageWithLinks(text: String): AnnotatedString {
    val context = LocalContext.current
    val ytMusicRegex = Regex("(https?://music\\.youtube\\.com/[\\w\\-\\.\\?&=\\%/]*)")
    val matches = ytMusicRegex.findAll(text)
    
    if (matches.none()) return AnnotatedString(text)
    
    return buildAnnotatedString {
        var lastIdx = 0
        for (match in matches) {
            // Append text before the link
            append(text.substring(lastIdx, match.range.first))
            
            // Append the link with interaction listener
            val url = match.value
            val linkAnnotation = LinkAnnotation.Url(
                url = url,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = AppTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    )
                ),
                linkInteractionListener = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            `package` = context.packageName
                        }
                        context.startActivity(intent)
                        Toast.makeText(context, "Playing now", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }
            )
            
            withLink(linkAnnotation) {
                append(url)
            }
            
            lastIdx = match.range.last + 1
        }
        if (lastIdx < text.length) {
            append(text.substring(lastIdx))
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
