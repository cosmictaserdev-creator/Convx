/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.convx.music.MainActivity
import com.convx.music.R
import com.convx.music.db.MusicDatabase
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.LyricsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConvxWidgetManager(
    private val context: Context,
    private val database: MusicDatabase
) {
    private val imageLoader by lazy {
        ImageLoader.Builder(context)
            .crossfade(false)
            .build()
    }

    private var cachedArtworkUri: String? = null
    private var cachedAlbumArt: Bitmap? = null
    private var cachedCircularAlbumArt: Bitmap? = null
    private val configuredBackgrounds = object : android.util.LruCache<String, Bitmap>(8 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.allocationByteCount
    }
    private val updateMutex = Mutex()

    suspend fun updateWidgets(
        title: String,
        artist: String,
        artworkUri: String?,
        isPlaying: Boolean,
        isLiked: Boolean,
        duration: Long = 0,
        currentPosition: Long = 0,
        mediaId: String? = null,
    ) = withContext(Dispatchers.IO) {
        updateMutex.withLock {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val musicWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MusicWidgetReceiver::class.java),
            )
            val nowPlayingWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, NowPlayingWidgetReceiver::class.java),
            )
            val lyricsWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, LyricsWidgetReceiver::class.java))
            val turntableWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, TurntableWidgetReceiver::class.java))
            if (musicWidgetIds.isEmpty() && nowPlayingWidgetIds.isEmpty() && lyricsWidgetIds.isEmpty() && turntableWidgetIds.isEmpty()) {
                cachedArtworkUri = null
                cachedAlbumArt = null
                cachedCircularAlbumArt = null
                return@withLock
            }

            val albumArt: Bitmap?
            val circularAlbumArt: Bitmap?

            if (artworkUri != null && artworkUri == cachedArtworkUri && cachedAlbumArt != null) {
                albumArt = cachedAlbumArt
                circularAlbumArt = cachedCircularAlbumArt
            } else {
                albumArt = artworkUri?.let { loadAlbumArt(it, 300) }
                circularAlbumArt = albumArt?.let { getCircularBitmap(it) }
                cachedArtworkUri = artworkUri
                cachedAlbumArt = albumArt
                cachedCircularAlbumArt = circularAlbumArt
            }

            musicWidgetIds.forEach { widgetId ->
                val options = appWidgetManager.getAppWidgetOptions(widgetId)
                val views = createRemoteViewsForSize(
                    options,
                    title,
                    artist,
                    albumArt,
                    isPlaying,
                    isLiked,
                    duration,
                    currentPosition
                )
                appWidgetManager.updateAppWidget(widgetId, views)
            }

            nowPlayingWidgetIds.forEach { widgetId ->
                val options = appWidgetManager.getAppWidgetOptions(widgetId)
                appWidgetManager.updateAppWidget(
                    widgetId,
                    createNowPlayingRemoteViews(
                        options,
                        WidgetConfig.load(context, widgetId),
                        title,
                        artist,
                        albumArt,
                        isPlaying,
                        duration,
                        currentPosition,
                    ),
                )
            }

            if (lyricsWidgetIds.isNotEmpty()) {
                val lines = loadLyricLines(mediaId)
                lyricsWidgetIds.forEach { widgetId ->
                    appWidgetManager.updateAppWidget(
                        widgetId,
                        createLyricsRemoteViews(
                            appWidgetManager.getAppWidgetOptions(widgetId),
                            WidgetConfig.load(context, widgetId),
                            title,
                            artist,
                            albumArt,
                            isPlaying,
                            lines,
                            currentPosition,
                        ),
                    )
                }
            }

            if (turntableWidgetIds.isNotEmpty()) {
                val turntableViews = createTurntableRemoteViews(
                    circularAlbumArt,
                    isPlaying,
                    isLiked
                )
                turntableWidgetIds.forEach { widgetId ->
                    appWidgetManager.updateAppWidget(widgetId, turntableViews)
                }
            }
        }
    }

    private fun createRemoteViewsForSize(
        options: Bundle,
        title: String,
        artist: String,
        albumArt: Bitmap?,
        isPlaying: Boolean,
        isLiked: Boolean,
        duration: Long,
        currentPosition: Long
    ): RemoteViews {
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val card = cardSizeFor(options)

        return when {
            minWidth < 180 && minHeight < 100 -> {
                createCompactSquareRemoteViews(card, albumArt, isPlaying)
            }
            minWidth >= 180 && minHeight < 100 -> {
                createCompactWideRemoteViews(card, title, artist, albumArt, isPlaying, isLiked)
            }
            else -> {
                createRemoteViews(
                    card, title, artist, albumArt, isPlaying, isLiked, duration, currentPosition,
                )
            }
        }
    }

    /**
     * The card these widgets paint. They have no per-instance config screen — only
     * the Now Playing and lyrics widgets do — so they always take the album tint.
     */
    private val defaultCardConfig = WidgetConfig()

    private fun createRemoteViews(
        card: CardSize,
        title: String,
        artist: String,
        albumArt: Bitmap?,
        isPlaying: Boolean,
        isLiked: Boolean,
        duration: Long = 0,
        currentPosition: Long = 0
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_music_player)
        val ink = onCardColorFor(defaultCardConfig, albumArt)

        views.setImageViewBitmap(
            R.id.widget_player_bg,
            createCardBackground(defaultCardConfig, albumArt, card),
        )

        views.setTextViewText(R.id.widget_song_title, title)
        views.setTextViewText(R.id.widget_artist_name, artist)

        if (albumArt != null) {
            val roundedAlbumArt = getRoundedCornerBitmap(albumArt)
            views.setImageViewBitmap(R.id.widget_album_art, roundedAlbumArt)
        } else {
            views.setImageViewBitmap(R.id.widget_album_art, getRoundedDefaultIcon())
        }

        val playPauseIcon = if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
        views.setImageViewResource(R.id.widget_play_pause, playPauseIcon)
        views.setTextViewText(
            R.id.widget_play_pause_text,
            context.getString(if (isPlaying) R.string.widget_pause else R.string.widget_play),
        )

        val likeIcon = if (isLiked) R.drawable.ic_widget_heart_nav else R.drawable.ic_widget_heart_outline_nav
        views.setImageViewResource(R.id.widget_like_button, likeIcon)

        if (duration > 0) {
            val level = ((currentPosition.toDouble() / duration.toDouble()) * 10000).toInt()
            views.setInt(R.id.widget_progress_fill, "setImageLevel", level)
        } else {
            views.setInt(R.id.widget_progress_fill, "setImageLevel", 0)
        }

        applyCardInk(
            views,
            ink,
            texts = listOf(
                R.id.widget_song_title,
                R.id.widget_artist_name,
                R.id.widget_play_pause_text,
            ),
            icons = listOf(
                R.id.widget_play_pause,
                R.id.widget_like_button,
                R.id.widget_progress_track,
                R.id.widget_progress_fill,
            ),
            pillId = R.id.widget_play_pause_container,
        )

        views.setOnClickPendingIntent(R.id.widget_player_bg, getOpenAppIntent())
        views.setOnClickPendingIntent(R.id.widget_album_art, getOpenAppIntent())
        views.setOnClickPendingIntent(R.id.widget_play_pause_container, getPlayPauseIntent())
        views.setOnClickPendingIntent(R.id.widget_like_button, getLikeIntent())

        return views
    }

    /**
     * Paints text, icons and the play pill in the card's ink colour.
     *
     * Shared by every widget family so text and icons contrast with either card theme.
     */
    private fun applyCardInk(
        views: RemoteViews,
        ink: Int,
        texts: List<Int>,
        icons: List<Int>,
        pillId: Int? = null,
    ) {
        texts.forEach { views.setTextColor(it, ink) }
        icons.forEach { tint(views, it, ink) }
        pillId?.let {
            views.setInt(
                it,
                "setBackgroundResource",
                if (ink == Color.WHITE) R.drawable.widget_pill_scrim_light
                else R.drawable.widget_pill_scrim_dark,
            )
        }
    }

    private suspend fun loadAlbumArt(artworkUri: String, size: Int = 200): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(artworkUri)
                    .size(size, size)
                    .allowHardware(false)
                    .crossfade(300)
                    .build()
                val result = imageLoader.execute(request)
                result.image?.toBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Square-cropped artwork with the app's continuous corners.
     *
     * Use a bitmap-relative radius because decoded artwork is scaled to each widget size.
     */
    private fun getRoundedCornerBitmap(
        bitmap: Bitmap,
        cornerFraction: Float = ART_CORNER_FRACTION,
    ): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val xOffset = (bitmap.width - size) / 2
        val yOffset = (bitmap.height - size) / 2
        val squareBitmap = Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)

        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            shader = BitmapShader(squareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        drawContinuousRoundedRect(canvas, size.toFloat(), size.toFloat(), size * cornerFraction, paint)

        if (squareBitmap != bitmap) {
            squareBitmap.recycle()
        }
        
        return output
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        
        val xOffset = (bitmap.width - size) / 2
        val yOffset = (bitmap.height - size) / 2
        val squareBitmap = Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
        
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            shader = BitmapShader(squareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        
        if (squareBitmap != bitmap) {
            squareBitmap.recycle()
        }
        return output
    }

    private fun createCompactSquareRemoteViews(
        card: CardSize,
        albumArt: Bitmap?,
        isPlaying: Boolean
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_compact_square)

        views.setImageViewBitmap(
            R.id.widget_compact_bg,
            createCardBackground(defaultCardConfig, albumArt, card),
        )

        if (albumArt != null) {
            val roundedAlbumArt = getRoundedCornerBitmap(albumArt)
            views.setImageViewBitmap(R.id.widget_compact_album_art, roundedAlbumArt)
        } else {
            views.setImageViewBitmap(R.id.widget_compact_album_art, getRoundedDefaultIcon())
        }

        val playPauseIcon = if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
        views.setImageViewResource(R.id.widget_compact_play_pause, playPauseIcon)

        // The pill sits on the artwork, not the card, so it always takes the
        // white-on-dark treatment regardless of the card's own ink.
        applyCardInk(
            views,
            Color.WHITE,
            texts = emptyList(),
            icons = listOf(R.id.widget_compact_play_pause),
            pillId = R.id.widget_compact_play_container,
        )

        views.setOnClickPendingIntent(R.id.widget_compact_bg, getOpenAppIntent())
        views.setOnClickPendingIntent(R.id.widget_compact_album_art, getOpenAppIntent())
        views.setOnClickPendingIntent(R.id.widget_compact_play_container, getPlayPauseIntent())

        return views
    }

    private fun createCompactWideRemoteViews(
        card: CardSize,
        title: String,
        artist: String,
        albumArt: Bitmap?,
        isPlaying: Boolean,
        isLiked: Boolean
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_compact_wide)
        val ink = onCardColorFor(defaultCardConfig, albumArt)

        views.setImageViewBitmap(
            R.id.widget_wide_bg,
            createCardBackground(defaultCardConfig, albumArt, card),
        )

        views.setTextViewText(R.id.widget_wide_song_title, title)
        views.setTextViewText(R.id.widget_wide_artist_name, artist)

        if (albumArt != null) {
            val roundedAlbumArt = getRoundedCornerBitmap(albumArt)
            views.setImageViewBitmap(R.id.widget_wide_album_art, roundedAlbumArt)
        } else {
            views.setImageViewBitmap(R.id.widget_wide_album_art, getRoundedDefaultIcon())
        }

        val playPauseIcon = if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
        views.setImageViewResource(R.id.widget_wide_play_pause, playPauseIcon)

        val likeIcon = if (isLiked) R.drawable.ic_widget_heart_nav else R.drawable.ic_widget_heart_outline_nav
        views.setImageViewResource(R.id.widget_wide_like_button, likeIcon)

        applyCardInk(
            views,
            ink,
            texts = listOf(R.id.widget_wide_song_title, R.id.widget_wide_artist_name),
            icons = listOf(R.id.widget_wide_play_pause, R.id.widget_wide_like_button),
        )

        views.setOnClickPendingIntent(R.id.widget_wide_bg, getOpenAppIntent())
        views.setOnClickPendingIntent(R.id.widget_wide_album_art, getOpenAppIntent())
        views.setOnClickPendingIntent(R.id.widget_wide_play_pause, getPlayPauseIntent())
        views.setOnClickPendingIntent(R.id.widget_wide_like_button, getLikeIntent())

        return views
    }

    private fun createTurntableRemoteViews(
        circularAlbumArt: Bitmap?,
        isPlaying: Boolean,
        isLiked: Boolean
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_turntable)

        if (circularAlbumArt != null) {
            views.setImageViewBitmap(R.id.widget_turntable_album_art, circularAlbumArt)
        } else {
            views.setImageViewBitmap(R.id.widget_turntable_album_art, getCircularDefaultIcon())
        }

        val playPauseIcon = if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
        views.setImageViewResource(R.id.widget_turntable_play_pause, playPauseIcon)

        views.setOnClickPendingIntent(R.id.widget_turntable_album_art, getOpenAppIntent())
        views.setOnClickPendingIntent(R.id.widget_turntable_play_container, getTurntablePlayPauseIntent())
        views.setOnClickPendingIntent(R.id.widget_turntable_prev_button, getTurntablePreviousIntent())
        views.setOnClickPendingIntent(R.id.widget_turntable_next_button, getTurntableNextIntent())

        return views
    }
    
    private fun getCircularDefaultIcon(): Bitmap {
        val drawable = context.packageManager.getApplicationIcon(context.packageName)
        val size = 300
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        return getCircularBitmap(bitmap)
    }

    private fun getRoundedDefaultIcon(
        cornerFraction: Float = ART_CORNER_FRACTION,
    ): Bitmap {
        val drawable = context.packageManager.getApplicationIcon(context.packageName)
        val size = 300
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        return getRoundedCornerBitmap(bitmap, cornerFraction)
    }

    /** Card colour for a widget instance, honouring its configured background mode. */
    private fun backgroundColorFor(config: WidgetConfig, albumArt: Bitmap?): Int =
        when (config.background) {
            WidgetBackground.ALBUM_TINT -> tintColorFor(albumArt)
            WidgetBackground.DARK -> Color.parseColor("#141414")
            WidgetBackground.LIGHT -> Color.parseColor("#F2EFED")
            // IMAGE draws the user's picture instead; this only shows through if
            // the image is missing or unreadable.
            WidgetBackground.IMAGE -> tintColorFor(albumArt)
        }

    /**
     * The user's chosen background image, centre-cropped into the card and rounded
     * to its corners. Returns null if unset or unreadable — a revoked uri after a
     * reboot or a deleted file must fall back to the colour card rather than render
     * blank.
     */
    private fun loadConfiguredBackground(config: WidgetConfig, card: CardSize): Bitmap? {
        if (config.background != WidgetBackground.IMAGE) return null
        val uri = config.imageUri ?: return null
        val cacheKey = "$uri:${card.width}:${card.height}:${card.cornerPx}"
        configuredBackgrounds.get(cacheKey)?.let { return it }
        val source = runCatching {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(Uri.parse(uri)).use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            var sampleSize = 1
            while (maxOf(options.outWidth, options.outHeight) / sampleSize > 2048) sampleSize *= 2
            options.inSampleSize = sampleSize
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            context.contentResolver.openInputStream(Uri.parse(uri)).use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
        }.getOrNull() ?: return null

        val output = Bitmap.createBitmap(card.width, card.height, Bitmap.Config.ARGB_8888)
        // Center-crop to preserve the image's aspect ratio.
        val scale = maxOf(
            card.width.toFloat() / source.width,
            card.height.toFloat() / source.height,
        )
        val matrix = android.graphics.Matrix().apply {
            setScale(scale, scale)
            postTranslate(
                (card.width - source.width * scale) / 2f,
                (card.height - source.height * scale) / 2f,
            )
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
            shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                .apply { setLocalMatrix(matrix) }
        }
        drawContinuousRoundedRect(
            Canvas(output),
            card.width.toFloat(),
            card.height.toFloat(),
            card.cornerPx,
            paint,
        )
        source.recycle()
        configuredBackgrounds.put(cacheKey, output)
        return output
    }

    private fun tintColorFor(albumArt: Bitmap?): Int {
        val fallback = Color.parseColor("#B03A3A")
        val bitmap = albumArt ?: return fallback
        val swatch = Palette.from(bitmap).clearFilters().generate().let { palette ->
            palette.vibrantSwatch ?: palette.dominantSwatch ?: palette.mutedSwatch
        } ?: return fallback

        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(swatch.rgb, hsl)
        // Keep the artwork's hue while limiting saturation and lightness for contrast.
        hsl[1] = hsl[1].coerceIn(0.35f, 0.85f)
        hsl[2] = hsl[2].coerceIn(0.32f, 0.52f)
        return ColorUtils.HSLToColor(hsl)
    }

    /** Draws the app's continuous rounded shape, with a round-rect fallback. */
    private fun drawContinuousRoundedRect(
        canvas: Canvas,
        width: Float,
        height: Float,
        cornerRadius: Float,
        paint: Paint,
    ) {
        val outline = ContinuousRoundedRectangle(cornerRadius).createOutline(
            size = Size(width, height),
            layoutDirection = LayoutDirection.Ltr,
            density = Density(1f),
        )
        val path = (outline as? Outline.Generic)?.path?.asAndroidPath()
        if (path != null) {
            canvas.drawPath(path, paint)
        } else {
            canvas.drawRoundRect(RectF(0f, 0f, width, height), cornerRadius, cornerRadius, paint)
        }
    }

    /** Pixel dimensions and corner radius used to render a widget card without stretching. */
    private data class CardSize(val width: Int, val height: Int, val cornerPx: Float)

    /** Card dimensions and launcher corner radius for one widget instance. */
    private fun cardSizeFor(options: Bundle): CardSize {
        val density = context.resources.displayMetrics.density
        val portrait = context.resources.configuration.orientation ==
            Configuration.ORIENTATION_PORTRAIT
        // Use min/max dimensions based on orientation to get the current widget size.
        val widthDp = options.getInt(
            if (portrait) AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
            else AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
        )
        val heightDp = options.getInt(
            if (portrait) AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
            else AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT
        )
        // Use the default tile size until the launcher reports dimensions.
        val width = ((if (widthDp > 0) widthDp else 250) * density).toInt().coerceIn(1, 1600)
        val height = ((if (heightDp > 0) heightDp else 110) * density).toInt().coerceIn(1, 1600)

        val radiusDp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching {
                context.resources.getDimension(android.R.dimen.system_app_widget_background_radius) /
                    density
            }.getOrDefault(CARD_CORNER_DP)
        } else {
            CARD_CORNER_DP
        }
        return CardSize(width, height, radiusDp.coerceIn(8f, 28f) * density)
    }

    /** Returns light and dark gradient colors based on the widget background. */
    private fun gradientColorsFor(config: WidgetConfig, albumArt: Bitmap?): Pair<Int, Int> {
        val base = backgroundColorFor(config, albumArt)
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(base, hsl)

        val top = ColorUtils.HSLToColor(
            floatArrayOf(hsl[0], hsl[1], (hsl[2] + 0.10f).coerceAtMost(0.62f)),
        )
        val bottom = ColorUtils.HSLToColor(
            floatArrayOf(hsl[0], hsl[1], (hsl[2] - 0.10f).coerceAtLeast(0.16f)),
        )
        return top to bottom
    }

    /** Draws the gradient card background and its top highlight. */
    private fun createCardBackground(
        config: WidgetConfig,
        albumArt: Bitmap?,
        card: CardSize,
    ): Bitmap {
        val (top, bottom) = gradientColorsFor(config, albumArt)
        val width = card.width
        val height = card.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, width * 0.35f, height.toFloat(),
                top, bottom, Shader.TileMode.CLAMP,
            )
        }
        drawContinuousRoundedRect(canvas, width.toFloat(), height.toFloat(), card.cornerPx, fill)

        // Specular rim: brightest at the top edge, gone by ~a third down.
        val rim = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (height * 0.012f).coerceAtLeast(1f)
            shader = LinearGradient(
                0f, 0f, 0f, height * 0.34f,
                Color.argb(120, 255, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP,
            )
        }
        drawContinuousRoundedRect(canvas, width.toFloat(), height.toFloat(), card.cornerPx, rim)
        return bitmap
    }

    /** Selects a readable foreground color from the card background. */
    private fun onCardColorFor(config: WidgetConfig, albumArt: Bitmap?): Int {
        // Use white for photos, whose luminance cannot be known before decoding.
        if (config.background == WidgetBackground.IMAGE) return Color.WHITE
        val luminance = ColorUtils.calculateLuminance(backgroundColorFor(config, albumArt))
        return if (luminance > 0.5) Color.parseColor("#12100F") else Color.WHITE
    }

    /** Layout variants selected from the widget's available size. */
    private enum class NowPlayingForm(val minWidthDp: Float, val minHeightDp: Float) {
        ROW(180f, 40f),
        SQUARE(110f, 110f),
        WIDE(250f, 110f),
    }

    /** Builds size-specific layouts; supported launchers choose the best fit while resizing. */
    private fun createNowPlayingRemoteViews(
        options: Bundle,
        config: WidgetConfig,
        title: String,
        artist: String,
        albumArt: Bitmap?,
        isPlaying: Boolean,
        duration: Long,
        currentPosition: Long,
    ): RemoteViews {
        val card = cardSizeFor(options)
        val build = { form: NowPlayingForm ->
            buildNowPlayingForm(
                form, card, config, title, artist, albumArt, isPlaying, duration, currentPosition,
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return RemoteViews(
                NowPlayingForm.entries.associate { form ->
                    SizeF(form.minWidthDp, form.minHeightDp) to build(form)
                }
            )
        }

        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val form = when {
            minHeight < 100 -> NowPlayingForm.ROW
            minWidth < 180 -> NowPlayingForm.SQUARE
            else -> NowPlayingForm.WIDE
        }
        return build(form)
    }

    private fun buildNowPlayingForm(
        form: NowPlayingForm,
        card: CardSize,
        config: WidgetConfig,
        title: String,
        artist: String,
        albumArt: Bitmap?,
        isPlaying: Boolean,
        duration: Long,
        currentPosition: Long,
    ): RemoteViews {
        val layout = when (form) {
            NowPlayingForm.ROW -> R.layout.widget_now_playing_row
            NowPlayingForm.SQUARE -> R.layout.widget_now_playing_square
            NowPlayingForm.WIDE -> R.layout.widget_now_playing_wide
        }
        val views = RemoteViews(context.packageName, layout)

        val bgId = when (form) {
            NowPlayingForm.ROW -> R.id.widget_np_row_bg
            NowPlayingForm.SQUARE -> R.id.widget_np_sq_bg
            NowPlayingForm.WIDE -> R.id.widget_np_wide_bg
        }
        val artId = when (form) {
            NowPlayingForm.ROW -> R.id.widget_np_row_album_art
            NowPlayingForm.SQUARE -> R.id.widget_np_sq_album_art
            NowPlayingForm.WIDE -> R.id.widget_np_wide_album_art
        }
        val titleId = when (form) {
            NowPlayingForm.ROW -> R.id.widget_np_row_title
            NowPlayingForm.SQUARE -> R.id.widget_np_sq_title
            NowPlayingForm.WIDE -> R.id.widget_np_wide_title
        }
        val artistId = when (form) {
            NowPlayingForm.ROW -> R.id.widget_np_row_artist
            NowPlayingForm.SQUARE -> R.id.widget_np_sq_artist
            NowPlayingForm.WIDE -> R.id.widget_np_wide_artist
        }
        val iconId = when (form) {
            NowPlayingForm.ROW -> R.id.widget_np_row_play_pause
            NowPlayingForm.SQUARE -> R.id.widget_np_sq_play_pause
            NowPlayingForm.WIDE -> R.id.widget_np_wide_play_pause
        }

        views.setImageViewBitmap(
            bgId,
            loadConfiguredBackground(config, card)
                ?: createCardBackground(config, albumArt, card),
        )

        if (config.showArtwork) {
            views.setViewVisibility(artId, android.view.View.VISIBLE)
            views.setImageViewBitmap(
                artId,
                albumArt?.let { getRoundedCornerBitmap(it) } ?: getRoundedDefaultIcon(),
            )
            sizeArtwork(views, artId, form, card)
        } else {
            views.setViewVisibility(artId, android.view.View.GONE)
        }

        views.setTextViewText(titleId, title)
        views.setTextViewText(artistId, artist)

        val ink = onCardColorFor(config, albumArt)
        val density = context.resources.displayMetrics.density
        fun px(dp: Float) = (dp * density).toInt()
        views.setImageViewBitmap(
            iconId,
            SfWidgetIcons.render(if (isPlaying) SfWidgetIcons.Pause else SfWidgetIcons.Play, px(32f), ink),
        )
        val labelId = if (form == NowPlayingForm.WIDE) R.id.widget_np_wide_label else null
        listOfNotNull(titleId, artistId, labelId).forEach { views.setTextColor(it, ink) }
        val glyphId = when (form) {
            NowPlayingForm.ROW -> null
            NowPlayingForm.SQUARE -> R.id.widget_np_sq_glyph
            NowPlayingForm.WIDE -> R.id.widget_np_wide_glyph
        }
        val outputId = when (form) {
            NowPlayingForm.ROW -> null
            NowPlayingForm.SQUARE -> R.id.widget_np_sq_output
            NowPlayingForm.WIDE -> R.id.widget_np_wide_output
        }
        glyphId?.let { views.setImageViewBitmap(it, SfWidgetIcons.render(SfWidgetIcons.AirPlay, px(72f), ink)) }
        outputId?.let { views.setImageViewBitmap(it, SfWidgetIcons.render(outputIcon(), px(80f), ink)) }
        sizeGlyphs(views, card, listOfNotNull(glyphId, outputId))

        // Only the wide form has room for a progress bar.
        val progressFillId = when (form) {
            NowPlayingForm.ROW, NowPlayingForm.SQUARE -> null
            NowPlayingForm.WIDE -> R.id.widget_np_wide_progress_fill
        }
        progressFillId?.let { fillId ->
            // Level drawables run 0..10000.
            val level =
                if (duration > 0) {
                    ((currentPosition.toDouble() / duration) * 10000).toInt().coerceIn(0, 10000)
                } else {
                    0
                }
            views.setInt(fillId, "setImageLevel", level)
            tint(views, fillId, ink)
            tint(views, R.id.widget_np_wide_progress_track, ink)
        }

        if (form == NowPlayingForm.ROW) {
            views.setOnClickPendingIntent(iconId, getPlayPauseIntent())
            val transportVisibility =
                if (config.showPrevNext) android.view.View.VISIBLE else android.view.View.GONE
            views.setViewVisibility(R.id.widget_np_row_next, transportVisibility)
            views.setViewVisibility(R.id.widget_np_row_prev, transportVisibility)
            views.setOnClickPendingIntent(R.id.widget_np_row_next, getNextIntent())
            views.setOnClickPendingIntent(R.id.widget_np_row_prev, getPreviousIntent())
            views.setImageViewBitmap(R.id.widget_np_row_next, SfWidgetIcons.render(SfWidgetIcons.Next, px(48f), ink))
            views.setImageViewBitmap(R.id.widget_np_row_prev, SfWidgetIcons.render(SfWidgetIcons.Previous, px(48f), ink))
        } else {
            val pillId =
                if (form == NowPlayingForm.WIDE) R.id.widget_np_wide_pill
                else R.id.widget_np_sq_pill
            val pillTextId =
                if (form == NowPlayingForm.WIDE) R.id.widget_np_wide_pill_text
                else R.id.widget_np_sq_pill_text
            views.setTextViewText(
                pillTextId,
                context.getString(if (isPlaying) R.string.widget_pause else R.string.widget_play),
            )
            views.setTextColor(pillTextId, ink)
            // A white scrim vanishes on a light card and a black one vanishes on a
            // dark card, so the pill follows whichever ink the card called for.
            views.setInt(
                pillId,
                "setBackgroundResource",
                if (ink == Color.WHITE) R.drawable.widget_pill_scrim_light
                else R.drawable.widget_pill_scrim_dark,
            )
            views.setOnClickPendingIntent(pillId, getPlayPauseIntent())
        }

        views.setOnClickPendingIntent(bgId, getOpenAppIntent())
        return views
    }

    /** Airpods for Bluetooth, headphones for a wire, the speaker otherwise: where the sound is going. */
    private fun outputIcon(): ImageVector {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        val types = audio.getDevices(android.media.AudioManager.GET_DEVICES_OUTPUTS).map { it.type }
        val bleHeadset = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && 26 in types
        return when {
            bleHeadset || android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP in types -> SfWidgetIcons.AirPods
            types.any {
                it == android.media.AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    it == android.media.AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                    it == android.media.AudioDeviceInfo.TYPE_USB_HEADSET
            } -> SfWidgetIcons.Headphones
            else -> SfWidgetIcons.Speaker
        }
    }

    /** Grows the corner glyphs with the tile; RemoteViews can only resize a view from API 31. */
    private fun sizeGlyphs(views: RemoteViews, card: CardSize, ids: List<Int>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val density = context.resources.displayMetrics.density
        val glyphDp = (minOf(card.width, card.height) / density * 0.24f).coerceIn(28f, 48f)
        ids.forEach {
            views.setViewLayoutWidth(it, glyphDp, TypedValue.COMPLEX_UNIT_DIP)
            views.setViewLayoutHeight(it, glyphDp, TypedValue.COMPLEX_UNIT_DIP)
        }
    }

    /** Tints a RemoteViews ImageView; there is no setImageTintList before API 31. */
    private fun tint(views: RemoteViews, viewId: Int, color: Int) {
        views.setInt(viewId, "setColorFilter", color)
    }

    /**
     * Scales the artwork to the tile it is actually being drawn in.
     *
     * The layouts carry a fixed dp size, which is right at the smallest size each
     * form covers and far too small once the tile is dragged bigger — the reference
     * widget's art is a fraction of the card, not a constant. RemoteViews can only
     * resize a view from API 31, so older devices keep the layout's value.
     */
    private fun sizeArtwork(
        views: RemoteViews,
        artId: Int,
        form: NowPlayingForm,
        card: CardSize,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val density = context.resources.displayMetrics.density
        val widthDp = card.width / density
        val heightDp = card.height / density
        val artDp = when (form) {
            // Fills the row's height inside its padding.
            NowPlayingForm.ROW -> (heightDp - 2 * ROW_PADDING_DP).coerceIn(36f, 72f)
            // Reference square: art is a little under half the tile.
            NowPlayingForm.SQUARE -> (minOf(widthDp, heightDp) * 0.46f).coerceIn(56f, 130f)
            // Reference wide: art is the full card height inside its padding.
            NowPlayingForm.WIDE -> (heightDp - 2 * CARD_PADDING_DP).coerceIn(72f, 190f)
        }
        views.setViewLayoutWidth(artId, artDp, TypedValue.COMPLEX_UNIT_DIP)
        views.setViewLayoutHeight(artId, artDp, TypedValue.COMPLEX_UNIT_DIP)
    }

    /**
     * Synced lyric lines for the current song, or empty when there are none or
     * they're unsynced. Unsynced lyrics have no timestamps, so there is no
     * "current line" to show and the widget says so rather than showing line 1.
     */
    private suspend fun loadLyricLines(mediaId: String?): List<LyricsEntry> {
        if (mediaId == null) return emptyList()
        val raw = runCatching { database.lyrics(mediaId).first()?.lyrics }.getOrNull()
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching { LyricsUtils.parseLyrics(raw) }.getOrDefault(emptyList())
    }

    /**
     * Lyrics widget: a three-line window around the current line.
     *
     * RemoteViews has no animation, so this cannot reproduce the app's Apple-style
     * glide — it steps as the line changes, keeping the same visual language
     * (active line solid, neighbours faded) that ConvxLyrics uses.
     */
    private fun createLyricsRemoteViews(
        options: Bundle,
        config: WidgetConfig,
        title: String,
        artist: String,
        albumArt: Bitmap?,
        isPlaying: Boolean,
        lines: List<LyricsEntry>,
        currentPosition: Long,
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_lyrics)
        val card = cardSizeFor(options)

        views.setImageViewBitmap(
            R.id.widget_lyr_bg,
            loadConfiguredBackground(config, card)
                ?: createCardBackground(config, albumArt, card),
        )

        views.setTextViewText(R.id.widget_lyr_title, title)
        views.setTextViewText(R.id.widget_lyr_artist, artist)

        if (lines.isEmpty()) {
            views.setTextViewText(R.id.widget_lyr_prev, "")
            views.setTextViewText(
                R.id.widget_lyr_current,
                context.getString(R.string.widget_no_lyrics),
            )
            views.setTextViewText(R.id.widget_lyr_next, "")
        } else {
            val index = LyricsUtils.findCurrentLineIndex(lines, currentPosition)
                .coerceIn(0, lines.lastIndex)
            views.setTextViewText(R.id.widget_lyr_prev, lines.getOrNull(index - 1)?.text ?: "")
            views.setTextViewText(R.id.widget_lyr_current, lines[index].text)
            views.setTextViewText(R.id.widget_lyr_next, lines.getOrNull(index + 1)?.text ?: "")
        }

        views.setImageViewResource(
            R.id.widget_lyr_play_pause,
            if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play,
        )

        val transportVisibility =
            if (config.showPrevNext) android.view.View.VISIBLE else android.view.View.GONE
        views.setViewVisibility(R.id.widget_lyr_skip_prev, transportVisibility)
        views.setViewVisibility(R.id.widget_lyr_skip_next, transportVisibility)

        views.setOnClickPendingIntent(R.id.widget_lyr_play_pause, getPlayPauseIntent())
        views.setOnClickPendingIntent(R.id.widget_lyr_skip_next, getNextIntent())
        views.setOnClickPendingIntent(R.id.widget_lyr_skip_prev, getPreviousIntent())
        views.setOnClickPendingIntent(R.id.widget_lyr_bg, getOpenAppIntent())

        val ink = onCardColorFor(config, albumArt)
        listOf(
            R.id.widget_lyr_title,
            R.id.widget_lyr_artist,
            R.id.widget_lyr_prev,
            R.id.widget_lyr_current,
            R.id.widget_lyr_next,
        ).forEach { views.setTextColor(it, ink) }
        listOf(
            R.id.widget_lyr_play_pause,
            R.id.widget_lyr_skip_next,
            R.id.widget_lyr_skip_prev,
            R.id.widget_lyr_glyph,
        ).forEach { tint(views, it, ink) }
        return views
    }

    private fun getOpenAppIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getPlayPauseIntent(): PendingIntent {
        val intent = Intent(context, MusicWidgetReceiver::class.java).apply {
            action = MusicWidgetReceiver.ACTION_PLAY_PAUSE
        }
        return PendingIntent.getBroadcast(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getNextIntent(): PendingIntent = broadcastToMusicWidget(
        MusicWidgetReceiver.ACTION_NEXT,
        requestCode = 5,
    )

    private fun getPreviousIntent(): PendingIntent = broadcastToMusicWidget(
        MusicWidgetReceiver.ACTION_PREVIOUS,
        requestCode = 6,
    )

    private fun broadcastToMusicWidget(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, MusicWidgetReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun getLikeIntent(): PendingIntent {
        val intent = Intent(context, MusicWidgetReceiver::class.java).apply {
            action = MusicWidgetReceiver.ACTION_LIKE
        }
        return PendingIntent.getBroadcast(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getTurntablePlayPauseIntent(): PendingIntent {
        val intent = Intent(context, TurntableWidgetReceiver::class.java).apply {
            action = TurntableWidgetReceiver.ACTION_TURNTABLE_PLAY_PAUSE
        }
        return PendingIntent.getBroadcast(
            context,
            3,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getTurntableNextIntent(): PendingIntent {
        val intent = Intent(context, TurntableWidgetReceiver::class.java).apply {
            action = TurntableWidgetReceiver.ACTION_TURNTABLE_NEXT
        }
        return PendingIntent.getBroadcast(
            context,
            4,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getTurntablePreviousIntent(): PendingIntent {
        val intent = Intent(context, TurntableWidgetReceiver::class.java).apply {
            action = TurntableWidgetReceiver.ACTION_TURNTABLE_PREVIOUS
        }
        return PendingIntent.getBroadcast(
            context,
            5,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        /**
         * Artwork corner as a share of the art's own side. Measured off the
         * reference widget, where the art keeps the same curve at every tile size.
         */
        const val ART_CORNER_FRACTION = 0.13f

        /** Card inset, shared by every layout so the forms read as one family. */
        const val CARD_PADDING_DP = 14f

        /** The row form is short, so it gets a tighter inset than the tall cards. */
        const val ROW_PADDING_DP = 8f

        /** Card corner used before API 31, which has no launcher radius to read. */
        const val CARD_CORNER_DP = 16f
    }
}
