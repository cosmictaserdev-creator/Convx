package com.convx.music.client.desktop

import com.convx.storage.domain.ArtworkCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import javax.imageio.ImageIO

/** Stores bounded network artwork in the profile so saved library entries survive cache eviction. */
internal class DesktopNetworkArtworkCache(private val directory: Path) : ArtworkCache {
    override suspend fun put(key: String, bytes: ByteArray): String? = withContext(Dispatchers.IO) {
        if (bytes.isEmpty() || bytes.size > MAX_BYTES) return@withContext null
        val digest = MessageDigest.getInstance("SHA-256").digest(key.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        val destination = directory.resolve("$digest.png")
        if (Files.isRegularFile(destination) && Files.size(destination) > 0) return@withContext destination.toUri().toString()
        val image = decode(bytes) ?: return@withContext null
        Files.createDirectories(directory)
        val temporary = Files.createTempFile(directory, "cover-", ".tmp")
        try {
            if (!ImageIO.write(image, "png", temporary.toFile())) return@withContext null
            try {
                Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING)
            }
            destination.toUri().toString()
        } finally {
            image.flush()
            Files.deleteIfExists(temporary)
        }
    }

    private fun decode(bytes: ByteArray): BufferedImage? = try {
        ImageIO.createImageInputStream(ByteArrayInputStream(bytes)).use { input ->
            val readers = ImageIO.getImageReaders(input)
            if (!readers.hasNext()) return null
            val reader = readers.next()
            try {
                reader.input = input
                val width = reader.getWidth(0)
                val height = reader.getHeight(0)
                if (width <= 0 || height <= 0 || width > MAX_SOURCE_EDGE || height > MAX_SOURCE_EDGE) return null
                val sample = ((maxOf(width, height) + MAX_EDGE - 1) / MAX_EDGE).coerceAtLeast(1)
                reader.read(0, reader.defaultReadParam.apply { setSourceSubsampling(sample, sample, 0, 0) })
            } finally {
                reader.dispose()
            }
        }
    } catch (_: java.io.IOException) {
        null
    }

    private companion object {
        const val MAX_BYTES = 16 * 1024 * 1024
        const val MAX_EDGE = 1024
        const val MAX_SOURCE_EDGE = 32_768
    }
}
