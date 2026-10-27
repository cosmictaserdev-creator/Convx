package com.convx.music.client.desktop

import com.convx.music.canvas.ArtworkColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI
import javax.imageio.ImageIO

internal object DesktopArtworkColors {
    private val cache = linkedMapOf<String, Int>()

    suspend fun estimate(url: String?): Int? = withContext(Dispatchers.IO) {
        if (url == null || !url.startsWith("https://")) return@withContext null
        synchronized(cache) { cache[url] }?.let { return@withContext it }
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 5_000; connection.readTimeout = 5_000
        try {
            if (connection.responseCode != 200) return@withContext null
            val bytes = connection.inputStream.use { it.readNBytes(4 * 1024 * 1024 + 1) }
            ensureActive()
            if (bytes.size > 4 * 1024 * 1024) return@withContext null
            val image = ImageIO.read(bytes.inputStream()) ?: return@withContext null
            val color = ArtworkColors.estimateDirect(image.getRGB(0, 0, image.width, image.height, null, 0, image.width), image.width, image.height)
            synchronized(cache) { if (cache.size >= 100) cache.remove(cache.keys.first()); cache[url] = color }
            color
        } finally { connection.disconnect() }
    }
}
