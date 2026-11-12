package com.convx.music.utils

import java.io.DataInputStream
import java.io.EOFException
import java.io.InputStream

/**
 * Reads every artist a file's own tags name, in order. MediaStore keeps just one of several ARTIST
 * tags (not reliably the first), so a collaboration showed under whichever artist it picked and
 * the others were lost. Several ARTIST tags, or a MusicBrainz `ARTISTS` tag, are several artists;
 * a single tag stays one artist, so "Simon & Garfunkel" is never split. Null when the file isn't
 * FLAC, Ogg (Opus/Vorbis) or ID3v2-tagged, or names no artist: MediaStore's value stands then.
 */
object LocalArtistTags {
    fun read(input: InputStream): List<String>? = runCatching {
        val stream = DataInputStream(input.buffered())
        stream.mark(16)
        val magic = ByteArray(4).also { stream.readFully(it) }
        stream.reset()
        val comments = when {
            magic.contentEquals("fLaC".toByteArray()) -> flacComments(stream)
            magic.contentEquals("OggS".toByteArray()) -> oggComments(stream)
            magic.copyOf(3).contentEquals("ID3".toByteArray()) -> return@runCatching id3Artists(stream)
            else -> null
        } ?: return@runCatching null
        artistsFrom(comments)
    }.getOrNull()?.takeIf { it.isNotEmpty() }

    /** `ARTISTS` (one value per artist) wins; otherwise every ARTIST field, in tag order. */
    internal fun artistsFrom(comments: List<Pair<String, String>>): List<String> {
        fun values(key: String) = comments.filter { it.first.equals(key, ignoreCase = true) }
            .map { it.second.trim() }.filter { it.isNotEmpty() }
        return (values("ARTISTS").ifEmpty { values("ARTIST") }).distinct()
    }

    private fun flacComments(stream: DataInputStream): List<Pair<String, String>>? {
        stream.skipFully(4)
        while (true) {
            val header = stream.readInt()
            val last = header ushr 31 == 1
            val type = (header ushr 24) and 0x7F
            val length = header and 0xFFFFFF
            if (type == VORBIS_COMMENT) return vorbisComments(ByteArray(length).also { stream.readFully(it) }, 0)
            stream.skipFully(length.toLong())
            if (last) return null
        }
    }

    /** Ogg: the comment header is the second packet, possibly split across pages. */
    private fun oggComments(stream: DataInputStream): List<Pair<String, String>>? {
        val packets = mutableListOf<ByteArray>()
        var current = java.io.ByteArrayOutputStream()
        while (packets.size < 2) {
            val capture = ByteArray(4).also { stream.readFully(it) }
            if (!capture.contentEquals("OggS".toByteArray())) return null
            stream.skipFully(22)
            val segments = stream.readUnsignedByte()
            val lacing = IntArray(segments) { stream.readUnsignedByte() }
            for (size in lacing) {
                current.write(ByteArray(size).also { stream.readFully(it) })
                if (size < 255) {
                    packets += current.toByteArray()
                    current = java.io.ByteArrayOutputStream()
                    if (packets.size == 2) break
                }
            }
            if (current.size() > MAX_COMMENT_BYTES) return null
        }
        val packet = packets[1]
        val offset = when {
            packet.startsWith("OpusTags") -> 8
            packet.size > 7 && packet[0] == 3.toByte() && packet.startsWith("vorbis", 1) -> 7
            else -> return null
        }
        return vorbisComments(packet, offset)
    }

    private fun vorbisComments(data: ByteArray, start: Int): List<Pair<String, String>> {
        var at = start
        fun int32(): Int = (data[at].toInt() and 0xFF or ((data[at + 1].toInt() and 0xFF) shl 8) or
            ((data[at + 2].toInt() and 0xFF) shl 16) or ((data[at + 3].toInt() and 0xFF) shl 24)).also { at += 4 }
        val vendorLength = int32()
        at += vendorLength
        val count = int32()
        return List(count.coerceAtMost(MAX_FIELDS)) {
            val length = int32()
            val field = String(data, at, length, Charsets.UTF_8).also { at += length }
            field.substringBefore('=') to field.substringAfter('=', "")
        }
    }

    /** ID3v2.3/2.4: TPE1 (values NUL-separated in 2.4) or a `TXXX:ARTISTS` frame. */
    private fun id3Artists(stream: DataInputStream): List<String>? {
        stream.skipFully(3)
        val major = stream.readUnsignedByte()
        stream.skipFully(1)
        val flags = stream.readUnsignedByte()
        if (major !in 3..4 || flags and 0x80 != 0) return null
        val tagSize = syncSafe(stream.readInt())
        val tag = ByteArray(tagSize.coerceAtMost(MAX_COMMENT_BYTES)).also { stream.readFully(it) }
        var at = 0
        var tpe1: List<String>? = null
        var artists: List<String>? = null
        while (at + 10 <= tag.size) {
            val id = String(tag, at, 4, Charsets.ISO_8859_1)
            if (id[0] == '\u0000') break
            val raw = (tag[at + 4].toInt() and 0xFF shl 24) or (tag[at + 5].toInt() and 0xFF shl 16) or
                (tag[at + 6].toInt() and 0xFF shl 8) or (tag[at + 7].toInt() and 0xFF)
            val size = if (major == 4) syncSafe(raw) else raw
            val body = at + 10
            if (size <= 0 || body + size > tag.size) break
            val values = id3Text(tag, body, size)
            when {
                id == "TPE1" -> tpe1 = values
                id == "TXXX" && values.firstOrNull().equals("ARTISTS", ignoreCase = true) -> artists = values.drop(1)
            }
            at = body + size
        }
        return (artists ?: tpe1)?.map(String::trim)?.filter(String::isNotEmpty)?.distinct()
    }

    private fun id3Text(tag: ByteArray, start: Int, size: Int): List<String> {
        val charset = when (tag[start].toInt()) {
            1 -> Charsets.UTF_16
            2 -> Charsets.UTF_16BE
            3 -> Charsets.UTF_8
            else -> Charsets.ISO_8859_1
        }
        return String(tag, start + 1, size - 1, charset).split('\u0000').filter { it.isNotEmpty() }
    }

    private fun syncSafe(value: Int) =
        (value and 0x7F) or ((value shr 8 and 0x7F) shl 7) or ((value shr 16 and 0x7F) shl 14) or ((value shr 24 and 0x7F) shl 21)

    private fun ByteArray.startsWith(prefix: String, offset: Int = 0) =
        size >= offset + prefix.length && prefix.indices.all { this[offset + it] == prefix[it].code.toByte() }

    private fun DataInputStream.skipFully(count: Long) {
        var left = count
        while (left > 0) {
            val skipped = skip(left)
            if (skipped <= 0) {
                if (read() < 0) throw EOFException()
                left--
            } else left -= skipped
        }
    }

    private const val VORBIS_COMMENT = 4
    private const val MAX_FIELDS = 512
    /** Comment blocks are small; an embedded picture can be megabytes and is skipped, not read. */
    private const val MAX_COMMENT_BYTES = 4 * 1024 * 1024
}
