package com.composables.sfsymbols.core

/**
 * Minimal raw-DEFLATE (RFC 1951) decoder for the packed symbol blocks. Pure common Kotlin so the
 * library needs no platform zip API on any target.
 */
internal object SfInflate {
    private val LEN_BASE = intArrayOf(3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258)
    private val LEN_EXTRA = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0)
    private val DIST_BASE = intArrayOf(1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577)
    private val DIST_EXTRA = intArrayOf(0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13)
    private val CL_ORDER = intArrayOf(16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15)

    private class Huffman(lengths: IntArray, n: Int) {
        val count = IntArray(16)
        val symbol = IntArray(n)

        init {
            for (i in 0 until n) count[lengths[i]]++
            val offs = IntArray(16)
            for (len in 1 until 15) offs[len + 1] = offs[len] + count[len]
            for (i in 0 until n) if (lengths[i] != 0) symbol[offs[lengths[i]]++] = i
        }
    }

    private val fixedLit: Huffman by lazy {
        val l = IntArray(288)
        for (i in 0 until 144) l[i] = 8
        for (i in 144 until 256) l[i] = 9
        for (i in 256 until 280) l[i] = 7
        for (i in 280 until 288) l[i] = 8
        Huffman(l, 288)
    }
    private val fixedDist: Huffman by lazy { Huffman(IntArray(30) { 5 }, 30) }

    private class State(val src: ByteArray) {
        var pos = 0
        var bitBuf = 0
        var bitCnt = 0
        var out = ByteArray(1 shl 17)
        var outLen = 0

        fun bits(n: Int): Int {
            var v = bitBuf
            while (bitCnt < n) {
                v = v or ((src[pos++].toInt() and 0xff) shl bitCnt)
                bitCnt += 8
            }
            bitBuf = v ushr n
            bitCnt -= n
            return v and ((1 shl n) - 1)
        }

        fun decode(h: Huffman): Int {
            var code = 0
            var first = 0
            var index = 0
            for (len in 1..15) {
                code = code or bits(1)
                val c = h.count[len]
                if (code - c < first) return h.symbol[index + (code - first)]
                index += c
                first += c
                first = first shl 1
                code = code shl 1
            }
            error("bad huffman code")
        }

        fun put(b: Int) {
            if (outLen == out.size) out = out.copyOf(out.size * 2)
            out[outLen++] = b.toByte()
        }

        fun codes(lit: Huffman, dist: Huffman) {
            while (true) {
                var sym = decode(lit)
                if (sym < 256) {
                    put(sym)
                } else if (sym == 256) {
                    return
                } else {
                    sym -= 257
                    val len = LEN_BASE[sym] + bits(LEN_EXTRA[sym])
                    val ds = decode(dist)
                    val d = DIST_BASE[ds] + bits(DIST_EXTRA[ds])
                    for (i in 0 until len) put(out[outLen - d].toInt())
                }
            }
        }

        fun dynamic() {
            val nlen = bits(5) + 257
            val ndist = bits(5) + 1
            val ncode = bits(4) + 4
            val lengths = IntArray(320)
            for (i in 0 until ncode) lengths[CL_ORDER[i]] = bits(3)
            val clh = Huffman(lengths.copyOf(19), 19)
            val ll = IntArray(nlen + ndist)
            var idx = 0
            while (idx < nlen + ndist) {
                val sym = decode(clh)
                if (sym < 16) {
                    ll[idx++] = sym
                } else {
                    var prev = 0
                    val rep: Int
                    when (sym) {
                        16 -> { prev = ll[idx - 1]; rep = 3 + bits(2) }
                        17 -> rep = 3 + bits(3)
                        else -> rep = 11 + bits(7)
                    }
                    repeat(rep) { ll[idx++] = prev }
                }
            }
            codes(Huffman(ll.copyOf(nlen), nlen), Huffman(ll.copyOfRange(nlen, nlen + ndist), ndist))
        }

        fun run(): ByteArray {
            do {
                val last = bits(1)
                when (bits(2)) {
                    0 -> {
                        bitBuf = 0; bitCnt = 0
                        val len = (src[pos].toInt() and 0xff) or ((src[pos + 1].toInt() and 0xff) shl 8)
                        pos += 4
                        repeat(len) { put(src[pos++].toInt()) }
                    }
                    1 -> codes(fixedLit, fixedDist)
                    2 -> dynamic()
                    else -> error("bad block type")
                }
            } while (last == 0)
            return out.copyOf(outLen)
        }
    }

    fun inflate(src: ByteArray): ByteArray = State(src).run()
}
