package com.convx.music.lyrics.bitchord

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun currentEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()

/** `yyyyMMdd` of [epochMillis] in UTC. */
internal fun utcDateStamp(epochMillis: Long): String {
    val days = epochMillis.floorDiv(86_400_000L)
    val shifted = days + 719_468
    val era = shifted.floorDiv(146_097)
    val dayOfEra = shifted - era * 146_097
    val yearOfEra = (dayOfEra - dayOfEra / 1_460 + dayOfEra / 36_524 - dayOfEra / 146_096) / 365
    val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val monthIndex = (5 * dayOfYear + 2) / 153
    val day = dayOfYear - (153 * monthIndex + 2) / 5 + 1
    val month = if (monthIndex < 10) monthIndex + 3 else monthIndex - 9
    val year = yearOfEra + era * 400 + if (month <= 2) 1 else 0
    return year.toString().padStart(4, '0') + month.toString().padStart(2, '0') + day.toString().padStart(2, '0')
}

internal fun hmacSha256(key: ByteArray, message: ByteArray): ByteArray {
    val block = 64
    val normalizedKey = (if (key.size > block) sha256(key) else key).copyOf(block)
    val inner = ByteArray(block) { (normalizedKey[it].toInt() xor 0x36).toByte() }
    val outer = ByteArray(block) { (normalizedKey[it].toInt() xor 0x5c).toByte() }
    return sha256(outer + sha256(inner + message))
}

internal fun sha256(message: ByteArray): ByteArray {
    val hash = intArrayOf(
        0x6a09e667, -0x4498517b, 0x3c6ef372, -0x5ab00ac6,
        0x510e527f, -0x64fa9774, 0x1f83d9ab, 0x5be0cd19,
    )
    val bitLength = message.size.toLong() * 8
    val paddedSize = ((message.size + 8) / 64 + 1) * 64
    val padded = message.copyOf(paddedSize)
    padded[message.size] = 0x80.toByte()
    for (i in 0 until 8) padded[paddedSize - 1 - i] = (bitLength ushr (8 * i)).toByte()

    val w = IntArray(64)
    for (chunk in 0 until paddedSize step 64) {
        for (i in 0 until 16) {
            val o = chunk + i * 4
            w[i] = (padded[o].toInt() and 0xff shl 24) or (padded[o + 1].toInt() and 0xff shl 16) or
                (padded[o + 2].toInt() and 0xff shl 8) or (padded[o + 3].toInt() and 0xff)
        }
        for (i in 16 until 64) {
            val s0 = w[i - 15].rotateRight(7) xor w[i - 15].rotateRight(18) xor (w[i - 15] ushr 3)
            val s1 = w[i - 2].rotateRight(17) xor w[i - 2].rotateRight(19) xor (w[i - 2] ushr 10)
            w[i] = w[i - 16] + s0 + w[i - 7] + s1
        }
        var a = hash[0]; var b = hash[1]; var c = hash[2]; var d = hash[3]
        var e = hash[4]; var f = hash[5]; var g = hash[6]; var h = hash[7]
        for (i in 0 until 64) {
            val s1 = e.rotateRight(6) xor e.rotateRight(11) xor e.rotateRight(25)
            val choice = (e and f) xor (e.inv() and g)
            val t1 = h + s1 + choice + ROUND_CONSTANTS[i] + w[i]
            val s0 = a.rotateRight(2) xor a.rotateRight(13) xor a.rotateRight(22)
            val majority = (a and b) xor (a and c) xor (b and c)
            val t2 = s0 + majority
            h = g; g = f; f = e; e = d + t1
            d = c; c = b; b = a; a = t1 + t2
        }
        hash[0] += a; hash[1] += b; hash[2] += c; hash[3] += d
        hash[4] += e; hash[5] += f; hash[6] += g; hash[7] += h
    }
    return ByteArray(32) { (hash[it / 4] ushr (24 - 8 * (it % 4))).toByte() }
}

private val ROUND_CONSTANTS = intArrayOf(
    0x428a2f98, 0x71374491, -0x4a3f0431, -0x164a245b, 0x3956c25b, 0x59f111f1, -0x6dc07d5c, -0x54e3a12b,
    -0x27f85568, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, -0x7f214e02, -0x6423f959, -0x3e640e8c,
    -0x1b64963f, -0x1041b87a, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    -0x67c1aeae, -0x57ce3993, -0x4ffcd838, -0x40a68039, -0x391ff40d, -0x2a586eb9, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, -0x7e3d36d2, -0x6d8dd37b,
    -0x5d40175f, -0x57e599b5, -0x3db47490, -0x3893ae5d, -0x2e6d17e7, -0x2966f9dc, -0xbf1ca7b, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, -0x7b3787ec, -0x7338fdf8, -0x6f410006, -0x5baf9315, -0x41065c09, -0x398e870e,
)
