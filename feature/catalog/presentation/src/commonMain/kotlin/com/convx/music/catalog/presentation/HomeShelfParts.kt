package com.convx.music.catalog.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

/** Song rows per column in a Home song shelf. */
const val ShelfListRows = 4
val ShelfListArtSize = 52.dp
val ShelfListPeek = 28.dp

/** Square artwork edge of a compact shelf card. */
val ShelfCardWidth = 154.dp
val ShelfCardInfoHeight = 46.dp
val ShelfCardHeight = ShelfCardWidth + ShelfCardInfoHeight

private val ArtRim = Color.Black.copy(alpha = 0.1f)

/** Rows [ShelfListRows] deep in columns that snap sideways; the next column peeks in. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ShelfListColumns(
    items: List<T>,
    key: (T) -> Any,
    gutter: Dp,
    sideInset: Dp,
    spacing: Dp,
    modifier: Modifier = Modifier,
    rowModifier: Modifier = Modifier,
    maxColumnWidth: Dp = Dp.Infinity,
    row: @Composable (T) -> Unit,
) {
    val columns = items.chunked(ShelfListRows)
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columnWidth = minOf(maxWidth - gutter - ShelfListPeek - sideInset, maxColumnWidth)
        val state = rememberLazyListState()
        LazyRow(
            state = state,
            flingBehavior = rememberSnapFlingBehavior(state, SnapPosition.Start),
            contentPadding = PaddingValues(start = gutter + sideInset, end = gutter),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = rowModifier.fillMaxWidth(),
        ) {
            items(columns, key = { key(it.first()) }, contentType = { "shelf_list_column" }) { column ->
                Column(Modifier.width(columnWidth)) { column.forEach { row(it) } }
            }
        }
    }
}

/** One song row: small artwork, title over subtitle. Platform extras (shared-element hooks) arrive as modifiers. */
@Composable
fun ShelfListRowBody(
    thumbnailUrl: String?,
    title: String,
    subtitle: String,
    titleColor: Color,
    subtitleColor: Color,
    modifier: Modifier = Modifier,
    artModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    onArtSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(vertical = 5.dp),
    ) {
        val shape = RoundedCornerShape(8.dp)
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = onArtSuccess,
            modifier = artModifier.size(ShelfListArtSize).clip(shape).border(0.25.dp, ArtRim, shape),
        )
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            BasicText(
                title,
                style = TextStyle(titleColor, 15.sp, lineHeight = 20.sp, letterSpacing = (-0.23).sp, fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = titleModifier,
            )
            if (subtitle.isNotEmpty()) BasicText(
                subtitle,
                style = TextStyle(subtitleColor, 13.sp, lineHeight = 18.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Compact shelf card: square artwork, a title and a subtitle. */
@Composable
fun ShelfCardBody(
    thumbnailUrl: String?,
    title: String,
    subtitle: String,
    titleColor: Color,
    subtitleColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
    artModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    onArtSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
) {
    Column(modifier.width(ShelfCardWidth)) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = onArtSuccess,
            modifier = artModifier.size(ShelfCardWidth).clip(shape).border(0.25.dp, ArtRim, shape),
        )
        Spacer(Modifier.height(4.dp))
        BasicText(
            title,
            style = TextStyle(titleColor, 15.sp, lineHeight = 20.sp, letterSpacing = (-0.23).sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = titleModifier,
        )
        BasicText(
            subtitle,
            style = TextStyle(subtitleColor, 15.sp, lineHeight = 20.sp, letterSpacing = (-0.23).sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
