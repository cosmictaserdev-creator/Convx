package com.convx.music.client.desktop

import com.convx.storage.data.NetworkMediaHttpServer
import com.convx.storage.domain.NetworkMediaUri
import com.convx.storage.domain.NetworkTagReader
import com.convx.storage.domain.NetworkTags
import com.convx.storage.domain.RemoteFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.FFmpegFrameGrabber

/** Lets FFmpeg seek a remote file through the existing loopback range bridge. */
internal class DesktopNetworkTagReader : NetworkTagReader {
    override suspend fun read(file: RemoteFile, size: Long): NetworkTags? = withContext(Dispatchers.IO) {
        val uri = NetworkMediaUri.create("metadata", "track")
        val borrowed = object : RemoteFile {
            override val length: Long get() = file.length.takeIf { it >= 0 } ?: size
            override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int =
                synchronized(file) { file.read(position, buffer, offset, length) }
            override fun close() = Unit
        }
        NetworkMediaHttpServer { borrowed }.use { server ->
            val source = server.createMediaSource(uri, "application/octet-stream")
            try {
                FFmpegFrameGrabber(source).use { grabber ->
                    grabber.setOption("rw_timeout", "15000000")
                    grabber.start()
                    val tags = grabber.metadata.entries.associate { it.key.lowercase() to it.value.trim() }
                    fun text(vararg keys: String) = keys.firstNotNullOfOrNull { tags[it]?.takeIf(String::isNotBlank) }
                    fun number(vararg keys: String) = text(*keys)?.substringBefore('/')?.trim()?.toIntOrNull()
                    val picture = grabber.formatContext?.let { context ->
                        (0 until context.nb_streams()).firstNotNullOfOrNull { index ->
                            val packet = context.streams(index).attached_pic()
                            val count = packet.size()
                            if (count <= 0 || count > MAX_PICTURE_BYTES || packet.data() == null) null
                            else ByteArray(count).also { packet.data().get(it) }
                        }
                    }
                    NetworkTags(
                        title = text("title"), artist = text("artist"), albumArtist = text("album_artist", "albumartist"),
                        album = text("album"), trackNumber = number("track"), discNumber = number("disc"),
                        year = text("date", "year")?.take(4)?.toIntOrNull(),
                        durationSeconds = (grabber.lengthInTime / 1_000_000).takeIf { it > 0 && it <= Int.MAX_VALUE }?.toInt(),
                        picture = picture,
                    )
                }
            } catch (_: org.bytedeco.javacv.FrameGrabber.Exception) {
                null
            }
        }
    }

    private companion object { const val MAX_PICTURE_BYTES = 16 * 1024 * 1024 }
}
