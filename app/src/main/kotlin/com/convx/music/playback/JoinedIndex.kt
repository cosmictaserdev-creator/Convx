/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

/**
 * Seek indexes for a download stitched together from HLS/DASH segments. Without one, a seek in a
 * remuxed FLAC is a binary search over the whole file, and fragmented MP4 is unseekable past its
 * first segment, so every scrub decoded its way forward from the start.
 */
internal object JoinedIndex {
    /** One media segment as it will be written: its bytes plus where it starts and how long it runs. */
    class Segment(val size: Long, val startTime: Long, val duration: Long)

    /** Timescale of the first track, from moov/trak/mdia/mdhd. */
    fun timescale(init: ByteArray): Long? {
        val mdhd = path(init, "moov", "trak", "mdia", "mdhd") ?: return null
        val version = init[mdhd.first].toInt()
        return u32(init, mdhd.first + if (version == 1) 20 else 12)
    }

    /** moov/trak/tkhd track_ID of the first track. */
    fun trackId(init: ByteArray): Long? {
        val tkhd = path(init, "moov", "trak", "tkhd") ?: return null
        return u32(init, tkhd.first + if (init[tkhd.first].toInt() == 1) 20 else 12)
    }

    /** The trex default sample duration, used when a fragment names none of its own. */
    fun defaultDuration(init: ByteArray): Long? = path(init, "moov", "mvex", "trex")?.let { u32(init, it.first + 12) }

    /** Start time (tfdt) and total sample duration of a media segment, in the track timescale. */
    fun timing(segment: ByteArray, trexDuration: Long?): Pair<Long, Long>? {
        val moof = mp4Boxes(segment).firstOrNull { it.first == "moof" } ?: return null
        val traf = mp4Boxes(segment, moof.second, moof.third).firstOrNull { it.first == "traf" } ?: return null
        var start: Long? = null
        var defaultDuration = trexDuration ?: 0L
        var duration = 0L
        for ((type, from, to) in mp4Boxes(segment, traf.second, traf.third)) {
            when (type) {
                "tfdt" -> start = if (segment[from].toInt() == 1) u64(segment, from + 4) else u32(segment, from + 4)
                "tfhd" -> {
                    val flags = flags(segment, from)
                    var at = from + 8
                    if (flags and 0x01 != 0) at += 8
                    if (flags and 0x02 != 0) at += 4
                    if (flags and 0x08 != 0 && at + 4 <= to) defaultDuration = u32(segment, at)
                }
                "trun" -> {
                    val flags = flags(segment, from)
                    val count = u32(segment, from + 4).toInt()
                    var at = from + 8
                    if (flags and 0x01 != 0) at += 4
                    if (flags and 0x04 != 0) at += 4
                    if (flags and 0x100 == 0) {
                        duration += count * defaultDuration
                    } else {
                        val stride = 4 * listOf(0x100, 0x200, 0x400, 0x800).count { flags and it != 0 }
                        for (i in 0 until count) {
                            if (at + 4 > to) break
                            duration += u32(segment, at)
                            at += stride
                        }
                    }
                }
            }
        }
        return start?.let { it to duration }
    }

    /**
     * A version 1 `sidx` that indexes every [segments] entry back to back, to be written right
     * before the first one: Media3 reads only one, so it must cover the whole download.
     */
    fun sidx(trackId: Long, timescale: Long, segments: List<Segment>): ByteArray {
        val size = 12 + 4 + 4 + 8 + 8 + 2 + 2 + segments.size * 12
        val out = java.nio.ByteBuffer.allocate(size)
        out.putInt(size).put("sidx".toByteArray(Charsets.ISO_8859_1))
        out.putInt(1 shl 24) // version 1, flags 0
        out.putInt(trackId.toInt()).putInt(timescale.toInt())
        out.putLong(segments.firstOrNull()?.startTime ?: 0L).putLong(0L)
        out.putShort(0).putShort(segments.size.toShort())
        for (segment in segments) {
            out.putInt(segment.size.toInt() and 0x7fffffff) // reference_type 0: media
            out.putInt(segment.duration.toInt())
            out.putInt(1 shl 31) // starts_with_SAP, SAP type 0
        }
        return out.array()
    }

    /** The segment without its own `sidx` boxes, which would each replace the one covering the file. */
    fun withoutSidx(segment: ByteArray): ByteArray {
        val boxes = mp4Boxes(segment).toList()
        if (boxes.none { it.first == "sidx" }) return segment
        val out = java.io.ByteArrayOutputStream(segment.size)
        var at = 0
        for ((type, from, to) in boxes) {
            val start = from - headerSize(segment, at)
            if (type != "sidx") out.write(segment, start, to - start)
            at = to
        }
        if (at < segment.size) out.write(segment, at, segment.size - at)
        return out.toByteArray()
    }

    /**
     * [header] (`fLaC` + metadata blocks) with a SEEKTABLE after STREAMINFO pointing at every
     * segment, and STREAMINFO's total sample count filled in when the init segment left it zero.
     * [segments] sizes are FLAC frame bytes; times are in [timescale].
     */
    fun flacWithSeekTable(header: ByteArray, timescale: Long, segments: List<Segment>): ByteArray {
        if (header.size < 4 + 4 + 34 || segments.isEmpty()) return header
        val streamInfoEnd = 4 + 4 + 34
        val streamInfo = header.copyOfRange(4, streamInfoEnd)
        val wasLast = streamInfo[0].toInt() and 0x80 != 0
        streamInfo[0] = (streamInfo[0].toInt() and 0x7f).toByte()
        val rate = ((streamInfo[4 + 10].toLong() and 0xff) shl 12) or
            ((streamInfo[4 + 11].toLong() and 0xff) shl 4) or ((streamInfo[4 + 12].toLong() and 0xf0) shr 4)
        if (rate <= 0 || timescale <= 0) return header
        val origin = segments.first().startTime
        fun samples(time: Long) = (time - origin) * rate / timescale
        val last = segments.last()
        val total = samples(last.startTime + last.duration)
        val existingTotal = ((streamInfo[4 + 13].toLong() and 0x0f) shl 32) or u32(streamInfo, 4 + 14)
        if (existingTotal == 0L && total in 1 until (1L shl 36)) {
            streamInfo[4 + 13] = ((streamInfo[4 + 13].toInt() and 0xf0) or (total shr 32).toInt()).toByte()
            for (i in 0..3) streamInfo[4 + 14 + i] = (total shr (24 - 8 * i)).toByte()
        }
        val points = java.nio.ByteBuffer.allocate(4 + segments.size * 18)
        val length = segments.size * 18
        points.put(((if (wasLast) 0x80 else 0) or 3).toByte())
        points.put((length shr 16).toByte()).put((length shr 8).toByte()).put(length.toByte())
        var offset = 0L
        for (segment in segments) {
            points.putLong(samples(segment.startTime)).putLong(offset).putShort(0)
            offset += segment.size
        }
        return "fLaC".toByteArray() + streamInfo + points.array() + header.copyOfRange(streamInfoEnd, header.size)
    }

    private fun path(b: ByteArray, vararg names: String): Pair<Int, Int>? {
        var from = 0
        var to = b.size
        for (name in names) {
            val box = mp4Boxes(b, from, to).firstOrNull { it.first == name } ?: return null
            from = box.second
            to = box.third
        }
        return from to to
    }

    private fun headerSize(b: ByteArray, at: Int) = if (u32(b, at) == 1L) 16 else 8

    private fun flags(b: ByteArray, at: Int) =
        ((b[at + 1].toInt() and 0xff) shl 16) or ((b[at + 2].toInt() and 0xff) shl 8) or (b[at + 3].toInt() and 0xff)

    private fun u32(b: ByteArray, o: Int): Long =
        ((b[o].toLong() and 0xff) shl 24) or ((b[o + 1].toLong() and 0xff) shl 16) or
            ((b[o + 2].toLong() and 0xff) shl 8) or (b[o + 3].toLong() and 0xff)

    private fun u64(b: ByteArray, o: Int): Long = (u32(b, o) shl 32) or u32(b, o + 4)
}
