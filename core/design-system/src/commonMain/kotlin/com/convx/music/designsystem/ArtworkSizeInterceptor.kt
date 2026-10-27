package com.convx.music.designsystem

import coil3.intercept.Interceptor
import coil3.request.ErrorResult
import coil3.request.ImageResult
import coil3.size.Dimension

/** Requests CDN artwork for the measured pixel size, with smaller originals as fallbacks. */
class ArtworkSizeInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val original = chain.request.data as? String ?: return chain.proceed()
        val pixels = maxOf(
            (chain.size.width as? Dimension.Pixels)?.px ?: 0,
            (chain.size.height as? Dimension.Pixels)?.px ?: 0,
        )
        val candidates = artworkUrlCandidates(original, pixels)
        for ((index, url) in candidates.withIndex()) {
            val result = chain.withRequest(chain.request.newBuilder().data(url).build()).proceed()
            if (result !is ErrorResult || index == candidates.lastIndex) return result
        }
        return chain.proceed()
    }
}

fun artworkUrlCandidates(url: String, pixels: Int): List<String> {
    if (pixels <= 0) return listOf(url)
    val host = artworkHost.find(url)?.groupValues?.get(1)?.lowercase() ?: return listOf(url)
    val path = url.substringBefore('?')
    val query = url.removePrefix(path)
    if (host == "i.ytimg.com" || host == "img.youtube.com") {
        val filename = videoThumbnail.find(path) ?: return listOf(url)
        val first = when {
            pixels > 480 -> 0
            pixels > 360 -> 1
            pixels > 180 -> 2
            else -> 3
        }
        val extension = filename.groupValues[2]
        return (videoQualities.drop(first).map { quality ->
            path.replaceRange(filename.range, "$quality.$extension") + query
        } + url).distinct()
    }
    if (host.endsWith(".googleusercontent.com") || host == "yt3.ggpht.com") {
        val size = artworkSizes.firstOrNull { it >= pixels } ?: artworkSizes.last()
        val resized = when {
            googleDimensions.containsMatchIn(path) -> path.replace(googleDimensions, "=w$size-h$size")
            googleSquare.containsMatchIn(path) -> path.replace(googleSquare, "=s$size")
            '=' !in path.substringAfterLast('/') -> "$path=w$size-h$size-l90-rj"
            else -> path
        }
        return listOf(resized + query, url).distinct()
    }
    return listOf(url)
}

private val artworkHost = Regex("^https?://([^/:]+)(?:[:/]|$)", RegexOption.IGNORE_CASE)
private val videoThumbnail = Regex("(maxresdefault|sddefault|hqdefault|mqdefault|default)\\.(jpg|webp)$")
private val googleDimensions = Regex("=w\\d+-h\\d+")
private val googleSquare = Regex("=s\\d+")
private val artworkSizes = listOf(96, 192, 384, 768, 1200)
private val videoQualities = listOf("maxresdefault", "sddefault", "hqdefault", "mqdefault", "default")
