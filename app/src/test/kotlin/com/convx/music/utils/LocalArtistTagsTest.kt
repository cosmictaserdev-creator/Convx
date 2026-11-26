package com.convx.music.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.ByteArrayOutputStream

class LocalArtistTagsTest {
    @Test
    fun everyArtistTagOfAFlacFileIsAnArtistInTagOrder() {
        val flac = flacWith("TITLE=Carnival", "ARTIST=Kanye West", "ARTIST=Ty Dolla \$ign", "ARTIST=¥\$")
        assertEquals(listOf("Kanye West", "Ty Dolla \$ign", "¥\$"), LocalArtistTags.read(flac.inputStream()))
    }

    @Test
    fun aSingleBandTagIsNeverSplit() {
        assertEquals(listOf("Simon & Garfunkel"), LocalArtistTags.read(flacWith("ARTIST=Simon & Garfunkel").inputStream()))
    }

    @Test
    fun musicBrainzArtistsWinOverTheJoinedDisplayArtist() {
        val flac = flacWith("ARTIST=Pawan Singh feat. Shilpi Raj", "ARTISTS=Pawan Singh", "ARTISTS=Shilpi Raj")
        assertEquals(listOf("Pawan Singh", "Shilpi Raj"), LocalArtistTags.read(flac.inputStream()))
    }

    @Test
    fun id3v24NulSeparatedArtistsAreSeparateArtists() {
        val body = byteArrayOf(3) + "A\u0000B".toByteArray()
        val frame = "TPE1".toByteArray() + syncSafe(body.size) + byteArrayOf(0, 0) + body
        val tag = "ID3".toByteArray() + byteArrayOf(4, 0, 0) + syncSafe(frame.size) + frame
        assertEquals(listOf("A", "B"), LocalArtistTags.read(tag.inputStream()))
    }

    @Test
    fun unknownFormatsLeaveMediaStoreInCharge() {
        assertNull(LocalArtistTags.read(ByteArray(32).inputStream()))
    }

    private fun flacWith(vararg fields: String): ByteArray {
        val comment = ByteArrayOutputStream().apply {
            writeLe(0)
            writeLe(fields.size)
            fields.forEach { field -> field.toByteArray().let { writeLe(it.size); write(it) } }
        }.toByteArray()
        return ByteArrayOutputStream().apply {
            write("fLaC".toByteArray())
            // STREAMINFO (34 bytes), then the last block: VORBIS_COMMENT.
            write(byteArrayOf(0, 0, 0, 34)); write(ByteArray(34))
            write(byteArrayOf(0x84.toByte(), (comment.size shr 16).toByte(), (comment.size shr 8).toByte(), comment.size.toByte()))
            write(comment)
        }.toByteArray()
    }

    private fun ByteArrayOutputStream.writeLe(value: Int) =
        write(byteArrayOf(value.toByte(), (value shr 8).toByte(), (value shr 16).toByte(), (value shr 24).toByte()))

    private fun syncSafe(value: Int) = byteArrayOf(
        (value shr 21 and 0x7F).toByte(), (value shr 14 and 0x7F).toByte(), (value shr 7 and 0x7F).toByte(), (value and 0x7F).toByte(),
    )
}
