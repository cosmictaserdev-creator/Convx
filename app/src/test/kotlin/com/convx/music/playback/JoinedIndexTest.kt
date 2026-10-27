package com.convx.music.playback

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer

class JoinedIndexTest {
    private fun box(type: String, vararg children: ByteArray): ByteArray {
        val payload = children.fold(ByteArray(0)) { acc, b -> acc + b }
        return ByteBuffer.allocate(8 + payload.size).putInt(8 + payload.size).put(type.toByteArray()).put(payload).array()
    }

    private fun full(type: String, version: Int, flags: Int, body: ByteArray) =
        box(type, ByteBuffer.allocate(4).putInt((version shl 24) or flags).array() + body)

    private fun ints(vararg values: Int) = ByteBuffer.allocate(4 * values.size).apply { values.forEach { putInt(it) } }.array()

    private val init = box(
        "moov",
        box(
            "trak",
            full("tkhd", 0, 3, ints(0, 0, 7)),
            box("mdia", full("mdhd", 0, 0, ints(0, 0, 48_000, 0))),
        ),
        box("mvex", full("trex", 0, 0, ints(7, 1, 1_024, 0, 0))),
    )

    private fun segment(start: Long, durations: IntArray?, count: Int = 3, payload: Int = 100): ByteArray {
        val trun = if (durations != null) full("trun", 0, 0x100, ints(durations.size, *durations))
        else full("trun", 0, 0, ints(count))
        val tfdt = full("tfdt", 1, 0, ByteBuffer.allocate(8).putLong(start).array())
        return box("styp") + full("sidx", 1, 0, ByteArray(28)) +
            box("moof", box("traf", full("tfhd", 0, 0, ints(7)), tfdt, trun)) + box("mdat", ByteArray(payload))
    }

    @Test
    fun `init boxes give timescale, track and default duration`() {
        assertEquals(48_000L, JoinedIndex.timescale(init))
        assertEquals(7L, JoinedIndex.trackId(init))
        assertEquals(1_024L, JoinedIndex.defaultDuration(init))
    }

    @Test
    fun `segment timing reads tfdt and sums trun durations or the default`() {
        assertEquals(96_000L to 3_000L, JoinedIndex.timing(segment(96_000, intArrayOf(1_000, 1_000, 1_000)), 1_024))
        assertEquals(0L to 3 * 1_024L, JoinedIndex.timing(segment(0, null, count = 3), 1_024))
    }

    @Test
    fun `per-segment sidx boxes are dropped and everything else kept`() {
        val raw = segment(0, null)
        val stripped = JoinedIndex.withoutSidx(raw)
        assertFalse(mp4Boxes(stripped).any { it.first == "sidx" })
        assertEquals(listOf("styp", "moof", "mdat"), mp4Boxes(stripped).map { it.first }.toList())
        assertEquals(raw.size - (8 + 4 + 28), stripped.size)
    }

    @Test
    fun `sidx indexes every segment back to back`() {
        val segments = listOf(JoinedIndex.Segment(500, 1_000, 48_000), JoinedIndex.Segment(700, 49_000, 47_000))
        val sidx = ByteBuffer.wrap(JoinedIndex.sidx(7, 48_000, segments))
        assertEquals(sidx.capacity(), sidx.getInt())
        assertEquals("sidx", String(ByteArray(4).also { sidx.get(it) }))
        assertEquals(1, sidx.getInt() ushr 24)
        assertEquals(7, sidx.getInt())
        assertEquals(48_000, sidx.getInt())
        assertEquals(1_000L, sidx.getLong())
        assertEquals(0L, sidx.getLong())
        sidx.getShort()
        assertEquals(2, sidx.getShort().toInt())
        assertEquals(500, sidx.getInt())
        assertEquals(48_000, sidx.getInt())
        assertTrue(sidx.getInt() < 0) // starts_with_SAP
        assertEquals(700, sidx.getInt())
        assertEquals(47_000, sidx.getInt())
    }

    @Test
    fun `flac header gains a seek table and its total sample count`() {
        // STREAMINFO as the only (last) block: 44.1 kHz, stereo, 16-bit, total samples unknown.
        val info = ByteArray(34)
        info[10] = 0x0A; info[11] = 0xC4.toByte(); info[12] = 0x42; info[13] = 0xF0.toByte()
        val header = "fLaC".toByteArray() + byteArrayOf(0x80.toByte(), 0, 0, 34) + info
        val segments = listOf(
            JoinedIndex.Segment(1_000, 0, 44_100),
            JoinedIndex.Segment(2_000, 44_100, 44_100),
            JoinedIndex.Segment(3_000, 88_200, 22_050),
        )
        val out = JoinedIndex.flacWithSeekTable(header, 44_100, segments)
        assertArrayEquals("fLaC".toByteArray(), out.copyOfRange(0, 4))
        assertEquals(0, out[4].toInt() and 0x80) // STREAMINFO is no longer last
        val total = ((out[8 + 13].toLong() and 0x0f) shl 32) or (ByteBuffer.wrap(out, 8 + 14, 4).int.toLong() and 0xffffffffL)
        assertEquals(110_250L, total)
        val table = ByteBuffer.wrap(out, 42, out.size - 42)
        val typeByte = table.get().toInt()
        assertEquals(0x83, typeByte and 0xff) // SEEKTABLE, last block
        val length = ((table.get().toInt() and 0xff) shl 16) or ((table.get().toInt() and 0xff) shl 8) or (table.get().toInt() and 0xff)
        assertEquals(3 * 18, length)
        val expected = listOf(0L to 0L, 44_100L to 1_000L, 88_200L to 3_000L)
        for ((sample, offset) in expected) {
            assertEquals(sample, table.getLong())
            assertEquals(offset, table.getLong())
            table.getShort()
        }
        assertEquals(42 + 4 + 54, out.size)
    }

    @Test
    fun `a flac header that is too short is left alone`() {
        val header = "fLaC".toByteArray() + ByteArray(10)
        assertArrayEquals(header, JoinedIndex.flacWithSeekTable(header, 44_100, listOf(JoinedIndex.Segment(1, 0, 1))))
    }
}
