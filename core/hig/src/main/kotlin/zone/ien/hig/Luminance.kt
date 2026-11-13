package zone.ien.hig

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

fun ImageBitmap.scale(width: Int, height: Int): ImageBitmap = drawRegion(
    x = 0,
    y = 0,
    sourceWidth = this.width,
    sourceHeight = this.height,
    destinationWidth = width,
    destinationHeight = height,
)

fun ImageBitmap.crop(x: Int, y: Int, width: Int, height: Int): ImageBitmap {
    require(x >= 0 && y >= 0) {
        "x($x), y($y) should not be less than 0)"
    }

    val endX = (x + width).coerceAtMost(this.width)
    val endY = (y + height).coerceAtMost(this.height)
    val cropWidth = (endX - x).coerceAtLeast(0)
    val cropHeight = (endY - y).coerceAtLeast(0)

    return drawRegion(
        x = x,
        y = y,
        sourceWidth = cropWidth,
        sourceHeight = cropHeight,
        destinationWidth = cropWidth,
        destinationHeight = cropHeight,
    )
}

fun ImageBitmap.averageLuminance(
    cropX: Int = 0,
    cropY: Int = 0,
    cropWidth: Int = width,
    cropHeight: Int = height,
    sampleWidth: Int = 5,
    sampleHeight: Int = sampleWidth,
    defaultColor: Color,
): Double {
    require(sampleWidth > 0 && sampleHeight > 0) {
        "sampleWidth and sampleHeight must be positive"
    }

    val buffer = IntArray(sampleWidth * sampleHeight)
    drawRegion(
        x = cropX,
        y = cropY,
        sourceWidth = cropWidth.coerceIn(0..width),
        sourceHeight = cropHeight.coerceIn(0..height),
        destinationWidth = sampleWidth,
        destinationHeight = sampleHeight,
    ).readPixels(buffer)

    return buffer.sumOf { it.toLuminance(defaultColor) } / buffer.size
}

private fun ImageBitmap.drawRegion(
    x: Int,
    y: Int,
    sourceWidth: Int,
    sourceHeight: Int,
    destinationWidth: Int,
    destinationHeight: Int,
): ImageBitmap {
    require(sourceWidth > 0 && sourceHeight > 0) {
        "sourceWidth and sourceHeight must be positive"
    }
    require(destinationWidth > 0 && destinationHeight > 0) {
        "destinationWidth and destinationHeight must be positive"
    }

    val destination = ImageBitmap(destinationWidth, destinationHeight)
    Canvas(destination).drawImageRect(
        image = this,
        srcOffset = IntOffset(x, y),
        srcSize = IntSize(sourceWidth, sourceHeight),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(destinationWidth, destinationHeight),
        paint = Paint().apply { filterQuality = FilterQuality.None },
    )
    return destination
}

private fun Int.toLuminance(defaultColor: Color): Double {
    val color = this.takeIf { it != Color.Transparent.toArgb() } ?: defaultColor.toArgb()

    val r = ((color shr 16) and 0xFF) / 255f
    val g = ((color shr 8) and 0xFF) / 255f
    val b = (color and 0xFF) / 255f
    return 0.2126 * r + 0.7152 * g + 0.0722 * b
}

fun Int.toHexString(includeAlpha: Boolean = true): String {
    return if (includeAlpha) {
        "#${this.toUInt().toString(16).padStart(8, '0').uppercase()}"
    } else {
        "#${(this and 0xFFFFFF).toString(16).padStart(6, '0').uppercase()}"
    }
}
