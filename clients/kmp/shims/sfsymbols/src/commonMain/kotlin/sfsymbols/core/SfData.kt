package com.composables.sfsymbols.core

import com.composables.sfsymbols.core.data.SF_ALIAS_CHUNKS
import com.composables.sfsymbols.core.data.SF_BLOCK_START
import com.composables.sfsymbols.core.data.SF_CATEGORY_ICONS
import com.composables.sfsymbols.core.data.SF_CATEGORY_KEYS
import com.composables.sfsymbols.core.data.SF_CATEGORY_LABELS
import com.composables.sfsymbols.core.data.SF_CATEGORY_TITLES
import com.composables.sfsymbols.core.data.SF_LAYER_COLORS
import com.composables.sfsymbols.core.data.SF_RELEASES
import com.composables.sfsymbols.core.data.SF_SYMBOL_CHUNKS
import com.composables.sfsymbols.core.data.SF_TAG_CHUNKS
import com.composables.sfsymbols.core.data.SF_VARIANT_CHUNKS
import com.composables.sfsymbols.core.data.sfBlobPiece
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/** Optical size variants Apple draws for every symbol (small / medium / large image scale). */
internal enum class SfScaleId { Small, Medium, Large }

/** One catalog line: where the symbol's geometry lives and what we know about it. */
internal class SfInfo(
    val name: String,
    val block: Int,
    val offset: Int,
    val categories: IntArray,
    val since: String,
    val flags: Int,
    val keywords: List<String>,
    val hierarchicalSince: String,
    val multicolorSince: String,
    /** `block:offset` of the small / large optical variant, or -1. */
    val smallBlock: Int, val smallOffset: Int,
    val largeBlock: Int, val largeOffset: Int,
) {
    val restricted get() = flags and 1 != 0
    val mirrorsInRtl get() = flags and 2 != 0
    val defaultsToHierarchical get() = flags and 4 != 0
    val supportsHierarchical get() = flags and 8 != 0
    val supportsMulticolor get() = flags and 16 != 0
    val inLegacyLibrary get() = flags and 32 != 0
}

internal class SfNode(
    val isGroup: Boolean,
    val canPulse: Boolean,
    val canRotate: Boolean,
    val clearBehind: Boolean,
    val animateTogether: Boolean,
    val variableDraw: Boolean,
    val autoVariable: Boolean,
    val monoErase: Boolean,
    val shapeErase: Boolean,
    /** -1 = not drawn in hierarchical mode, 0..2 = primary/secondary/tertiary. */
    val hierarchical: Int,
    /** Index into [SF_LAYER_COLORS] or -1. */
    val colorId: Int,
    val opacity: Float,
    /** Draw-on start point as a fraction of the symbol's bounds (y from top), NaN when the layer has no stroke. */
    val rootU: Float,
    val rootV: Float,
    /** Every draw-rail tie midpoint as (u, v) pairs, fractions of the symbol's bounds (y from top); first = start. */
    val ties: FloatArray,
    val contours: IntArray,
    /** Apple's semantic tags for the layer (used to match layers when one symbol replaces another). */
    val tags: IntArray,
    val children: List<SfNode>,
)

/** Raw outline of one optical size: 3 weight masters (wght 400 / 1 / 1000). */
internal class SfGeom(val ops: ByteArray, val reg: FloatArray, val lo: FloatArray, val hi: FloatArray)

/** Decoded symbol: geometry + layer tree + animation hints. */
internal class SfGlyph(
    val ops: ByteArray,
    /** x,y pairs, font units, y up. */
    val reg: FloatArray,
    val lo: FloatArray,
    val hi: FloatArray,
    val layersExact: Boolean,
    val backToFront: Boolean,
    val gapless: Boolean,
    val defaultVariableRendering: Int,
    val wiggle: Int,
    val wiggleAngle: Int,
    val rotation: Int,
    val anchors: FloatArray,
    val tree: List<SfNode>,
) {
    /** Same layer data over a different optical size's outline. */
    fun withGeometry(g: SfGeom) = SfGlyph(
        g.ops, g.reg, g.lo, g.hi, layersExact, backToFront, gapless, defaultVariableRendering,
        wiggle, wiggleAngle, rotation, anchors, tree,
    )
}

private class Reader(val b: ByteArray, var p: Int) {
    fun u8() = b[p++].toInt() and 0xff
    fun vi(): Int {
        var r = 0
        var s = 0
        while (true) {
            val x = b[p++].toInt() and 0xff
            r = r or ((x and 0x7f) shl s)
            if (x < 0x80) return r
            s += 7
        }
    }
    fun zz(): Int {
        val v = vi()
        return (v ushr 1) xor -(v and 1)
    }
}

internal object SfData {
    private val infos: List<SfInfo> by lazy { parseCatalog() }
    private val byName: Map<String, SfInfo> by lazy { infos.associateBy { it.name } }
    private val aliases: Map<String, String> by lazy {
        val m = HashMap<String, String>()
        for (chunk in SF_ALIAS_CHUNKS) for (line in chunk.lineSequence()) {
            val i = line.indexOf('|')
            if (i > 0) m[line.substring(0, i)] = line.substring(i + 1)
        }
        m
    }
    private val legacy: Map<String, SfInfo> by lazy {
        val m = HashMap<String, SfInfo>()
        for (i in infos) if (i.inLegacyLibrary) m[legacyName(i.name)] = i
        m
    }

    /** base symbol name -> names of its variants (e.g. `heart` -> `heart.fill`). */
    val variants: Map<String, List<String>> by lazy {
        val m = HashMap<String, List<String>>()
        for (chunk in SF_VARIANT_CHUNKS) for (line in chunk.lineSequence()) {
            val i = line.indexOf('|')
            if (i > 0) m[line.substring(0, i)] = line.substring(i + 1).split(';').filter { it.isNotEmpty() }
        }
        m
    }
    val baseOf: Map<String, String> by lazy {
        val m = HashMap<String, String>()
        for ((b, vs) in variants) for (v in vs) m[v] = b
        m
    }

    /** release key ("2019", "2022.1" …) -> platform -> OS version. */
    val releases: Map<String, Map<String, String>> by lazy {
        val platforms = listOf("iOS", "macOS", "tvOS", "visionOS", "watchOS")
        SF_RELEASES.associate { line ->
            val f = line.split('|')
            f[0] to platforms.indices.associate { platforms[it] to f.getOrElse(it + 1) { "" } }.filterValues { it.isNotEmpty() }
        }
    }

    private val tagTable: List<String> by lazy { SF_TAG_CHUNKS.flatMap { it.split('\n') } }
    fun tag(i: Int): String = tagTable[i]

    fun all(): List<SfInfo> = infos
    fun categoryKeys(): List<String> = SF_CATEGORY_KEYS
    fun categoryLabels(): List<String> = SF_CATEGORY_LABELS
    fun categoryIcons(): List<String> = SF_CATEGORY_ICONS
    fun categoryTitles(): Map<String, List<String>> = SF_CATEGORY_TITLES
    fun colorName(id: Int): String? = SF_LAYER_COLORS.getOrNull(id)

    /** "heart.fill" -> "SFHeartFill": the property name used by the original per-icon API. */
    fun legacyName(name: String): String =
        "SF" + name.split('.').joinToString("") { p -> p.replaceFirstChar { it.uppercaseChar() } }

    fun find(name: String): SfInfo? =
        byName[name] ?: aliases[name]?.let { byName[it] } ?: legacy[name]

    private fun parseCatalog(): List<SfInfo> {
        val out = ArrayList<SfInfo>(8600)
        for (chunk in SF_SYMBOL_CHUNKS) for (line in chunk.lineSequence()) {
            if (line.isEmpty()) continue
            val f = line.split('|')
            val bo = f[1].split(':')
            fun at(s: String?): Pair<Int, Int> =
                if (s.isNullOrEmpty()) -1 to -1 else s.split(':').let { it[0].toInt() to it[1].toInt() }
            val sm = at(f.getOrNull(8))
            val lg = at(f.getOrNull(9))
            out += SfInfo(
                name = f[0],
                block = bo[0].toInt(),
                offset = bo[1].toInt(),
                categories = if (f[2].isEmpty()) IntArray(0) else f[2].split(',').map { it.toInt() }.toIntArray(),
                since = f[3],
                flags = f[4].toInt(),
                keywords = if (f[5].isEmpty()) emptyList() else f[5].split(';'),
                hierarchicalSince = f.getOrElse(6) { "" },
                multicolorSince = f.getOrElse(7) { "" },
                smallBlock = sm.first, smallOffset = sm.second,
                largeBlock = lg.first, largeOffset = lg.second,
            )
        }
        return out
    }

    // ---- block cache (UI-thread use; keep it simple)
    private val blocks = HashMap<Int, ByteArray>()
    private val order = ArrayList<Int>()

    @OptIn(ExperimentalEncodingApi::class)
    private fun block(i: Int): ByteArray {
        blocks[i]?.let { return it }
        val sb = StringBuilder()
        for (p in SF_BLOCK_START[i] until SF_BLOCK_START[i + 1]) sb.append(sfBlobPiece(p))
        val data = SfInflate.inflate(Base64.Default.decode(sb.toString()))
        if (order.size >= 6) blocks.remove(order.removeAt(0))
        blocks[i] = data
        order += i
        return data
    }

    private val glyphs = HashMap<SfInfo, SfGlyph>()
    private val scaled = HashMap<Pair<SfInfo, SfScaleId>, SfGlyph?>()

    fun glyph(info: SfInfo): SfGlyph = glyphs.getOrPut(info) { readGlyph(Reader(block(info.block), info.offset)) }

    /** The small/large optical variant of [info], or null when Apple draws none (use medium). */
    fun scaleGlyph(info: SfInfo, scale: SfScaleId): SfGlyph? = scaled.getOrPut(info to scale) {
        val (b, o) = when (scale) {
            SfScaleId.Small -> info.smallBlock to info.smallOffset
            SfScaleId.Large -> info.largeBlock to info.largeOffset
            SfScaleId.Medium -> -1 to -1
        }
        if (b < 0) null else glyph(info).let { m -> m.withGeometry(readScaleGeom(Reader(block(b), o), m)) }
    }

    /** Optical-scale outline: per master, an affine map of the medium outline plus exact integer residuals. */
    private fun readScaleGeom(r: Reader, m: SfGlyph): SfGeom {
        val n = m.reg.size / 2
        fun master(med: FloatArray): FloatArray {
            val out = FloatArray(n * 2)
            for (ax in 0..1) {
                val sq = r.zz()
                val tq = r.zz()
                for (i in 0 until n) {
                    val k = i * 2 + ax
                    out[k] = (((med[k].toInt() * sq + 2048) shr 12) + tq + r.zz()).toFloat()
                }
            }
            return out
        }
        val reg = master(m.reg)
        val lo = master(m.lo)
        val hi = master(m.hi)
        return SfGeom(m.ops, reg, lo, hi)
    }

    private fun readGeom(r: Reader): SfGeom {
        val nOps = r.vi()
        val ops = ByteArray(nOps) { r.u8().toByte() }
        var n = 0
        for (o in ops) n += when (o.toInt()) { 0, 1 -> 1; 2 -> 3; else -> 0 }
        val reg = FloatArray(n * 2)
        var px = 0
        var py = 0
        for (i in 0 until n) {
            px += r.zz(); py += r.zz()
            reg[i * 2] = px.toFloat(); reg[i * 2 + 1] = py.toFloat()
        }
        val lo = FloatArray(n * 2)
        for (i in 0 until n * 2) lo[i] = reg[i] + r.zz()
        val hi = FloatArray(n * 2)
        for (i in 0 until n * 2) hi[i] = reg[i] + r.zz()
        return SfGeom(ops, reg, lo, hi)
    }

    private fun readGlyph(r: Reader): SfGlyph {
        val g = readGeom(r)
        val flags = r.u8()
        val wiggle = r.u8()
        val angle = if (wiggle == 9) r.vi() else 0
        val rotation = r.u8()
        val na = r.vi()
        val anchors = FloatArray(na * 2)
        for (i in 0 until na * 2) anchors[i] = r.zz() / 4096f
        val roots = List(r.vi()) { readNode(r) }
        return SfGlyph(
            g.ops, g.reg, g.lo, g.hi,
            layersExact = flags and 1 != 0,
            backToFront = flags and 2 != 0,
            gapless = flags and 4 != 0,
            defaultVariableRendering = (flags shr 3) and 3,
            wiggle = wiggle, wiggleAngle = angle, rotation = rotation, anchors = anchors, tree = roots,
        )
    }

    private fun readNode(r: Reader): SfNode {
        val tf = r.u8()
        val mf = r.u8()
        val cid = r.u8()
        val op = r.u8()
        val nTies = r.vi()
        val ties = FloatArray(nTies * 2) { r.zz() / 1024f }
        val ru = if (nTies > 0) ties[0] else Float.NaN
        val rv = if (nTies > 0) ties[1] else Float.NaN
        val contours = IntArray(r.vi()) { r.vi() }
        val tags = IntArray(r.vi()) { r.vi() }
        val children = List(r.vi()) { readNode(r) }
        return SfNode(
            isGroup = tf and 3 == 1,
            canPulse = tf and 4 != 0, canRotate = tf and 8 != 0, clearBehind = tf and 16 != 0,
            animateTogether = tf and 32 != 0, variableDraw = tf and 64 != 0, autoVariable = tf and 128 != 0,
            monoErase = mf and 1 != 0, shapeErase = mf and 2 != 0, hierarchical = ((mf shr 2) and 3) - 1,
            colorId = if (cid == 255) -1 else cid, opacity = op / 100f,
            contours = contours, tags = tags, rootU = ru, rootV = rv, ties = ties, children = children,
        )
    }
}
