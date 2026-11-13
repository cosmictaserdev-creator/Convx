package com.convx.music.lyrics.bitchord

import kotlin.test.Test
import kotlin.test.assertEquals

class SigningTest {
    private fun ByteArray.hex() = joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }

    @Test
    fun sha256MatchesKnownDigests() {
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", sha256(ByteArray(0)).hex())
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", sha256("abc".encodeToByteArray()).hex())
        val twoBlocks = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".encodeToByteArray()
        assertEquals("248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1", sha256(twoBlocks).hex())
    }

    @Test
    fun hmacSha256MatchesRfc4231() {
        val key = ByteArray(20) { 0x0b }
        assertEquals(
            "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7",
            hmacSha256(key, "Hi There".encodeToByteArray()).hex(),
        )
        assertEquals(
            "5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843",
            hmacSha256("Jefe".encodeToByteArray(), "what do ya want for nothing?".encodeToByteArray()).hex(),
        )
        val longKey = ByteArray(131) { 0xaa.toByte() }
        assertEquals(
            "60e431591ee0b67f0d8a26aacbf5b77f8e0bc6213728c5140546040f0ee37f54",
            hmacSha256(longKey, "Test Using Larger Than Block-Size Key - Hash Key First".encodeToByteArray()).hex(),
        )
    }

    @Test
    fun utcDateStampHandlesEpochLeapDaysAndDayBoundaries() {
        assertEquals("19700101", utcDateStamp(0))
        assertEquals("20240229", utcDateStamp(1_709_164_800_000))
        assertEquals("20241231", utcDateStamp(1_735_689_599_999))
        assertEquals("20250101", utcDateStamp(1_735_689_600_000))
        assertEquals("19691231", utcDateStamp(-1))
    }
}
