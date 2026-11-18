package com.composables.sfsymbols

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.unit.dp
import com.composables.sfsymbols.core.SfData
import com.composables.sfsymbols.core.SfGlyph
import com.composables.sfsymbols.core.SfInfo
import com.composables.sfsymbols.core.SfNode
import com.composables.sfsymbols.core.SfScaleId

/** The nine SF Symbols weights. [wght] is the value on the font's variable weight axis. */
public enum class SfWeight(internal val wght: Float) {
    Ultralight(28.93f), Thin(112.72f), Light(276.31f), Regular(400f), Medium(508f),
    Semibold(590.8f), Bold(700f), Heavy(858.4f), Black(1000f),
}

/**
 * Apple's image scale. Each symbol has three optically-adjusted outlines; [Small] and [Large] are drawn
 * smaller / larger than [Medium] relative to the same point size, as with `Image.Scale` in SwiftUI.
 */
public enum class SfScale(internal val id: SfScaleId) { Small(SfScaleId.Small), Medium(SfScaleId.Medium), Large(SfScaleId.Large) }

public enum class SfRenderingMode { Monochrome, Hierarchical, Palette, Multicolor }

/** One animatable layer of a symbol (a leaf of Apple's layer tree). */
public class SfLayer internal constructor(
    internal val contours: IntArray,
    /** -1 = not drawn in hierarchical mode, 0 primary, 1 secondary, 2 tertiary. */
    public val hierarchicalLevel: Int,
    internal val erases: Boolean,
    internal val colorName: String?,
    internal val opacity: Float,
    public val canPulse: Boolean,
    public val canRotate: Boolean,
    internal val autoVariable: Boolean,
    /** True when the layer can be partially drawn (variable draw). */
    internal val variableDraw: Boolean,
    internal val rootU: Float,
    internal val rootV: Float,
    /** Draw-rail tie midpoints as (u, v) pairs, fractions of the symbol's bounds (y from top). */
    internal val ties: FloatArray,
    /** Approximate centre-line length of the layer's strokes, in points at a 100 pt symbol. */
    internal val strokeLength: Float,
    /** Index of the animation group this layer belongs to; layers of one group animate together. */
    internal val group: Int,
    /** Apple's semantic tags for the layer (e.g. `"circle"`, `"checkmark"`). */
    public val tags: Set<String>,
)

public class SfCategory internal constructor(public val key: String, public val label: String, public val icon: String) {
    /**
     * Title in Apple's own translation for [languageTag] (`"de"`, `"pt-BR"`, `"zh_TW"` ...), falling back to the
     * language alone and then to the English [label].
     */
    public fun title(languageTag: String): String {
        val i = SfData.categoryKeys().indexOf(key)
        val titles = SfData.categoryTitles()
        val norm = languageTag.replace('-', '_')
        val lang = norm.substringBefore('_')
        val list = titles[norm] ?: titles[lang] ?: titles.entries.firstOrNull { it.key.substringBefore('_') == lang }?.value
        return list?.getOrNull(i)?.takeIf { it.isNotEmpty() } ?: label
    }
}

/**
 * An SF Symbol with real Apple name, weights, layers and animation hints. Geometry is decoded lazily.
 *
 * ```kotlin
 * val s = SfSymbol["heart.fill"]
 * SfSymbolIcon(s, size = 28.dp, effect = SfSymbolEffect.Bounce())
 * ```
 */
public class SfSymbol internal constructor(private val info: SfInfo) {
    public val name: String get() = info.name

    /** Property name from the original per-icon API, e.g. `SFHeartFill`. */
    public val legacyName: String get() = SfData.legacyName(info.name)
    public val keywords: List<String> get() = info.keywords
    public val categories: List<SfCategory> get() = info.categories.map { categoryList[it] }

    /** First OS-release year it shipped, as Apple lists it ("2019", "2022.1", ...). */
    public val since: String get() = info.since

    /** OS versions of [since], keyed by platform: `"iOS" -> "16.0"`, `"macOS" -> "13.0"`, ... */
    public val releases: Map<String, String> get() = SfData.releases[info.since].orEmpty()

    /** Release (year key) from which hierarchical rendering is available, or null. */
    public val hierarchicalSince: String? get() = info.hierarchicalSince.ifEmpty { null }

    /** Release (year key) from which multicolor rendering is available, or null. */
    public val multicolorSince: String? get() = info.multicolorSince.ifEmpty { null }
    public val isRestricted: Boolean get() = info.restricted
    public val mirrorsInRtl: Boolean get() = info.mirrorsInRtl
    public val supportsHierarchical: Boolean get() = info.supportsHierarchical
    public val supportsMulticolor: Boolean get() = info.supportsMulticolor

    /** Symbols that are variants of this one (`heart` -> `heart.fill`). */
    public val variants: List<SfSymbol> get() = SfData.variants[info.name].orEmpty().mapNotNull { named(it) }

    /** The symbol this one is a variant of (`heart.fill` -> `heart`), or null. */
    public val base: SfSymbol? get() = SfData.baseOf[info.name]?.let { named(it) }

    /**
     * Apple's variant for a language/layout (`"ar"`, `"he"`, `"ja"`, `"hi"`, `"th"`, ...; `rtl` for right-to-left
     * layouts), e.g. a mirrored or script-specific glyph. Returns this symbol when Apple has none.
     */
    public fun localized(languageTag: String): SfSymbol {
        val lang = languageTag.replace('_', '-').substringBefore('-').lowercase()
        val rtl = lang in RTL_LANGUAGES
        return named("$name.$lang") ?: (if (rtl) named("$name.rtl") else null) ?: this
    }

    /** True when this symbol is itself a language/layout variant (`*.ar`, `*.rtl` ...). */
    public val isLocaleVariant: Boolean
        get() = name.substringAfterLast('.').let { it == "rtl" || it == "ltr" || it in LOCALE_SUFFIXES }

    internal val glyph: SfGlyph by lazy { SfData.glyph(info) }

    private val scaleGlyphs = HashMap<SfScaleId, SfGlyph>()

    /** Outline for an optical scale; Medium (or a scale Apple doesn't draw) falls back to [glyph]. */
    internal fun glyphAt(scale: SfScale): SfGlyph =
        if (scale == SfScale.Medium) glyph
        else scaleGlyphs.getOrPut(scale.id) { SfData.scaleGlyph(info, scale.id) ?: glyph }

    /** True when layers come straight from Apple's layer data (false: one merged layer). */
    public val hasExactLayers: Boolean get() = glyph.layersExact
    internal val backToFront: Boolean get() = glyph.backToFront

    public val layers: List<SfLayer> by lazy { flatten(glyph.tree) }

    /** Number of animation groups: layers sharing a group move together, others are staggered. */
    internal val groupCount: Int by lazy { (layers.maxOfOrNull { it.group } ?: 0) + 1 }

    /** Erase layers (knock-outs) need layered drawing even in plain monochrome. */
    internal val hasErasers: Boolean by lazy { layers.any { it.erases } }

    private fun flatten(tree: List<SfNode>): List<SfLayer> {
        val lengths = contourLengths()
        val out = ArrayList<SfLayer>()
        var group = 0
        fun walk(n: SfNode, g: Int) {
            if (n.children.isEmpty()) {
                out += SfLayer(
                    n.contours, n.hierarchical, n.monoErase, SfData.colorName(n.colorId), n.opacity,
                    n.canPulse, n.canRotate, n.autoVariable || n.variableDraw, n.variableDraw, n.rootU, n.rootV, n.ties,
                    n.contours.sumOf { c -> lengths.getOrElse(c) { 0f }.toDouble() }.toFloat() / 2f / 2048f * 100f, g,
                    n.tags.map { SfData.tag(it) }.toSet(),
                )
            } else {
                val gg = if (n.animateTogether) group++ else -1
                for (c in n.children) walk(c, if (gg >= 0) gg else group++)
            }
        }
        for (n in tree) walk(n, group++)
        return out
    }

    private class Contours(val ops: List<List<Byte>>, val start: IntArray)

    private val contourInfo: Contours by lazy {
        val ops = ArrayList<List<Byte>>()
        val start = ArrayList<Int>()
        var cur = ArrayList<Byte>()
        var k = 0
        for (op in glyph.ops) {
            if (cur.isEmpty()) start += k
            cur += op
            when (op.toInt()) { 0, 1 -> k += 2; 2 -> k += 6 }
            if (op.toInt() == 3) { ops += cur; cur = ArrayList() }
        }
        Contours(ops, start.toIntArray())
    }

    /** Path-op sequence (0 move, 1 line, 2 cubic, 3 close) of contour [c]. */
    internal fun contourOps(c: Int): List<Byte> = contourInfo.ops.getOrElse(c) { emptyList() }

    /** Index of contour [c]'s first coordinate in [points]. */
    internal fun contourStart(c: Int): Int = contourInfo.start.getOrElse(c) { 0 }

    /** Outline length of each contour at Regular weight, in font units. */
    private fun contourLengths(): FloatArray {
        val g = glyph
        val p = points(SfWeight.Regular.wght, SfScale.Medium)
        val out = ArrayList<Float>()
        var k = 0
        var cx = 0f; var cy = 0f
        var acc = 0f
        for (op in g.ops) {
            when (op.toInt()) {
                0 -> { cx = p[k]; cy = p[k + 1]; k += 2 }
                1 -> { acc += kotlin.math.hypot(p[k] - cx, p[k + 1] - cy); cx = p[k]; cy = p[k + 1]; k += 2 }
                2 -> {
                    var px = cx; var py = cy
                    for (i in 1..8) {
                        val t = i / 8f; val u = 1 - t
                        val x = u * u * u * cx + 3 * u * u * t * p[k] + 3 * u * t * t * p[k + 2] + t * t * t * p[k + 4]
                        val y = u * u * u * cy + 3 * u * u * t * p[k + 1] + 3 * u * t * t * p[k + 3] + t * t * t * p[k + 5]
                        acc += kotlin.math.hypot(x - px, y - py); px = x; py = y
                    }
                    cx = p[k + 4]; cy = p[k + 5]; k += 6
                }
                else -> { out += acc; acc = 0f }
            }
        }
        return out.toFloatArray()
    }

    internal fun points(weight: SfWeight, scale: SfScale = SfScale.Medium): FloatArray = points(weight.wght, scale)

    /** Interpolated point list, y flipped to screen orientation (y down). */
    internal fun points(wght: Float, scale: SfScale = SfScale.Medium): FloatArray {
        val g = glyphAt(scale)
        val n: Float
        val src: FloatArray
        if (wght >= 400f) { n = (wght - 400f) / 600f; src = g.hi } else { n = (400f - wght) / 399f; src = g.lo }
        val out = FloatArray(g.reg.size)
        for (i in out.indices step 2) {
            out[i] = g.reg[i] + n * (src[i] - g.reg[i])
            out[i + 1] = -(g.reg[i + 1] + n * (src[i + 1] - g.reg[i + 1]))
        }
        return out
    }

    /** One [Path] per contour (screen orientation, font units). */
    internal fun contourPaths(weight: SfWeight, scale: SfScale = SfScale.Medium): List<Path> {
        val g = glyphAt(scale)
        val pts = points(weight, scale)
        val paths = ArrayList<Path>()
        var cur = Path()
        var k = 0
        for (op in g.ops) {
            when (op.toInt()) {
                0 -> { cur.moveTo(pts[k], pts[k + 1]); k += 2 }
                1 -> { cur.lineTo(pts[k], pts[k + 1]); k += 2 }
                2 -> { cur.cubicTo(pts[k], pts[k + 1], pts[k + 2], pts[k + 3], pts[k + 4], pts[k + 5]); k += 6 }
                else -> { cur.close(); paths += cur; cur = Path() }
            }
        }
        return paths
    }

    /**
     * Tight bounds of the *visible* outline at [weight] (true curve extrema, not control points), y down.
     * Contours that only exist to knock out other layers are ignored, so ink stays centred.
     */
    public fun bounds(weight: SfWeight = SfWeight.Regular, scale: SfScale = SfScale.Medium): Rect =
        boundsCache.getOrPut(Triple(this, weight, scale)) {
            val g = glyphAt(scale)
            val p = points(weight, scale)
            val drawn = HashSet<Int>()
            for (l in layers) if (!l.erases) for (c in l.contours) drawn += c
            val all = drawn.isEmpty()
            var l = Float.MAX_VALUE; var t = Float.MAX_VALUE; var r = -Float.MAX_VALUE; var b = -Float.MAX_VALUE
            var contour = 0
            var skip = !all && contour !in drawn
            fun add(x: Float, y: Float) {
                if (skip) return
                if (x < l) l = x; if (x > r) r = x
                if (y < t) t = y; if (y > b) b = y
            }
            // extrema of a cubic on one axis: roots of the derivative in (0,1)
            fun cubicAxis(p0: Float, p1: Float, p2: Float, p3: Float, out: (Float) -> Unit) {
                val a = -p0 + 3 * p1 - 3 * p2 + p3
                val bb = 2 * (p0 - 2 * p1 + p2)
                val c = p1 - p0
                fun at(t: Float): Float { val u = 1 - t; return u * u * u * p0 + 3 * u * u * t * p1 + 3 * u * t * t * p2 + t * t * t * p3 }
                if (kotlin.math.abs(a) < 1e-6f) {
                    if (kotlin.math.abs(bb) > 1e-9f) { val tt = -c / bb; if (tt > 0f && tt < 1f) out(at(tt)) }
                } else {
                    val disc = bb * bb - 4 * a * c
                    if (disc >= 0f) {
                        val sq = kotlin.math.sqrt(disc)
                        for (tt in floatArrayOf((-bb + sq) / (2 * a), (-bb - sq) / (2 * a))) if (tt > 0f && tt < 1f) out(at(tt))
                    }
                }
            }
            var k = 0
            var cx = 0f; var cy = 0f
            for (op in g.ops) {
                when (op.toInt()) {
                    0, 1 -> { cx = p[k]; cy = p[k + 1]; add(cx, cy); k += 2 }
                    2 -> {
                        val x1 = p[k]; val y1 = p[k + 1]; val x2 = p[k + 2]; val y2 = p[k + 3]; val x3 = p[k + 4]; val y3 = p[k + 5]
                        add(x3, y3)
                        cubicAxis(cx, x1, x2, x3) { add(it, cy) }
                        cubicAxis(cy, y1, y2, y3) { add(cx, it) }
                        cx = x3; cy = y3; k += 6
                    }
                    else -> { contour++; skip = !all && contour !in drawn }
                }
            }
            Rect(l, t, r, b)
        }

    /**
     * Static monochrome [ImageVector] for APIs that need one (e.g. Material `Icon`). Use
     * [SfSymbolIcon] for layers, rendering modes and animation.
     */
    public fun toImageVector(weight: SfWeight = SfWeight.Regular, scale: SfScale = SfScale.Medium): ImageVector {
        val g = glyphAt(scale)
        val pts = points(weight, scale)
        val b = bounds(weight, scale)
        val nodes = ArrayList<PathNode>()
        var k = 0
        for (op in g.ops) {
            when (op.toInt()) {
                0 -> { nodes += PathNode.MoveTo(pts[k] - b.left, pts[k + 1] - b.top); k += 2 }
                1 -> { nodes += PathNode.LineTo(pts[k] - b.left, pts[k + 1] - b.top); k += 2 }
                2 -> {
                    nodes += PathNode.CurveTo(
                        pts[k] - b.left, pts[k + 1] - b.top, pts[k + 2] - b.left, pts[k + 3] - b.top,
                        pts[k + 4] - b.left, pts[k + 5] - b.top,
                    ); k += 6
                }
                else -> nodes += PathNode.Close
            }
        }
        val scaleFactor = 24f / maxOf(b.width, b.height)
        return ImageVector.Builder(
            name = name, defaultWidth = (b.width * scaleFactor).dp, defaultHeight = (b.height * scaleFactor).dp,
            viewportWidth = b.width, viewportHeight = b.height,
        ).addPath(
            pathData = nodes, fill = SolidColor(Color.Black),
            // knock-out layers can't be 'cleared' in a vector; even-odd carves them out of the shapes beneath
            pathFillType = if (hasErasers) PathFillType.EvenOdd else PathFillType.NonZero,
        ).build()
    }

    override fun toString(): String = "SfSymbol($name)"
    override fun equals(other: Any?): Boolean = other is SfSymbol && other.info === info
    override fun hashCode(): Int = info.hashCode()

    public companion object {
        private val RTL_LANGUAGES = setOf("ar", "he", "fa", "ur", "ps", "sd", "yi", "dv", "ku")
        private val LOCALE_SUFFIXES = setOf(
            "ar", "he", "hi", "ja", "ko", "th", "zh", "ru", "el", "bn", "gu", "kn", "ml", "mr", "my", "or", "pa", "ta", "te",
            "ur", "ka", "km", "lo", "si", "ne", "sat", "ps", "sd",
        )
        private val boundsCache = HashMap<Triple<SfSymbol, SfWeight, SfScale>, Rect>()
        private val symbols: Map<SfInfo, SfSymbol> by lazy { SfData.all().associateWith { SfSymbol(it) } }
        private val categoryList: List<SfCategory> by lazy {
            val k = SfData.categoryKeys(); val l = SfData.categoryLabels(); val i = SfData.categoryIcons()
            k.indices.map { SfCategory(k[it], l[it], i[it]) }
        }

        /** Every symbol (8,500+), in Apple's order. */
        public val all: List<SfSymbol> by lazy { SfData.all().map { symbols.getValue(it) } }
        public val allCategories: List<SfCategory> get() = categoryList

        /** OS versions Apple lists for each release key (`"2022"` -> iOS 16.0, macOS 13.0, ...). */
        public val releaseVersions: Map<String, Map<String, String>> get() = SfData.releases

        /**
         * Looks up by Apple name (`"heart.fill"`), Apple alias, or the original per-icon API name
         * (`"SFHeartFill"`). Returns null when unknown.
         */
        public fun named(name: String): SfSymbol? = SfData.find(name)?.let { symbols.getValue(it) }

        /** Throwing variant of [named]. */
        public operator fun get(name: String): SfSymbol = named(name) ?: error("Unknown SF Symbol: $name")

        /** Case-insensitive search over names and Apple's search keywords (including its enhanced keyword set). */
        public fun search(query: String, category: SfCategory? = null): List<SfSymbol> {
            val q = query.trim().lowercase()
            val ci = category?.let { c -> categoryList.indexOf(c) }
            return all.filter { s ->
                (ci == null || ci in s.info.categories) &&
                    (q.isEmpty() || s.name.contains(q) || s.info.keywords.any { it.lowercase().contains(q) })
            }
        }
    }
}
