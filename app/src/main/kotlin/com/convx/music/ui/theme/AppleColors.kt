package com.convx.music.ui.theme

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import com.convx.music.canvas.ArtworkColors

object AppleColors {
    const val WHITE = ArtworkColors.WHITE
    const val CONTROL_FILL_ALPHA = ArtworkColors.CONTROL_FILL_ALPHA
    fun red(c: Int) = ArtworkColors.red(c)
    fun green(c: Int) = ArtworkColors.green(c)
    fun blue(c: Int) = ArtworkColors.blue(c)
    fun rgb(r: Int, g: Int, b: Int) = ArtworkColors.rgb(r, g, b)
    fun color(rgb: Int) = Color(rgb or 0xFF000000.toInt())
    fun luminance(c: Int) = ArtworkColors.luminance(c)
    fun pageBackground(artworkBg: Int) = ArtworkColors.pageBackground(artworkBg)
    fun mix(from: Int, to: Int, t: Double) = ArtworkColors.mix(from, to, t)
    fun playFill(keyColor: Int?) = ArtworkColors.playFill(keyColor)
    fun playGlyph(pageBg: Int) = ArtworkColors.playGlyph(pageBg)
    fun cardFillAlpha(pageBg: Int) = ArtworkColors.cardFillAlpha(pageBg)
    fun accuracy(x: Int, y: Int) = ArtworkColors.accuracy(x, y)
    fun downscale(pixels: IntArray, width: Int, height: Int, size: Int) = ArtworkColors.downscale(pixels, width, height, size)
    fun estimateArtworkBackground(pixels: IntArray, width: Int, height: Int) = ArtworkColors.estimateArtworkBackground(pixels, width, height)
    fun estimateDirect(pixels: IntArray, width: Int, height: Int, grid: Int = 64) = ArtworkColors.estimateDirect(pixels, width, height, grid)
    fun estimateArtworkBackground(bitmap: Bitmap): Int {
        val width = bitmap.width; val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return estimateDirect(pixels, width, height)
    }
}
