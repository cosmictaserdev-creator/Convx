package com.convx.music.client.desktop

import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DesktopNetworkArtworkCacheTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun storesScaledArtworkAndReusesIt() = runBlocking {
        val source = BufferedImage(2048, 128, BufferedImage.TYPE_INT_RGB)
        val bytes = ByteArrayOutputStream().also { ImageIO.write(source, "png", it) }.toByteArray()
        val cache = DesktopNetworkArtworkCache(temporary.root.toPath().resolve("art"))
        val uri = assertNotNull(cache.put("album", bytes))
        val file = Path.of(java.net.URI(uri)).toFile()
        assertTrue(file.isFile)
        assertEquals(1024, ImageIO.read(file).width)
        assertEquals(uri, cache.put("album", bytes))
        assertNull(cache.put("invalid", byteArrayOf(1, 2, 3)))
    }
}
