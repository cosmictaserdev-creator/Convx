package com.convx.music.eq.data

import java.io.InputStream

internal object EQProfileImport {
    private const val MAX_BYTES = 64 * 1024

    fun read(input: InputStream): ParametricEQ {
        val bytes = ByteArray(MAX_BYTES + 1)
        var count = 0
        while (count < bytes.size) {
            val read = input.read(bytes, count, bytes.size - count)
            if (read == -1) break
            if (read == 0) {
                val next = input.read()
                if (next == -1) break
                bytes[count++] = next.toByte()
            } else count += read
        }
        require(count <= MAX_BYTES) { "EQ file is too large" }
        val profile = ParametricEQParser.parseText(String(bytes, 0, count, Charsets.UTF_8).removePrefix("\uFEFF"))
        require(ParametricEQParser.validate(profile).isEmpty()) { "Invalid EQ profile" }
        return profile
    }
}
