package com.composables.sfsymbols

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/** What happens visually when the `symbol` passed to [SfSymbolIcon] changes. */
public enum class SfSymbolTransition {
    /** Swap instantly. */
    None,

    /** Old symbol disappears, then the new one appears (Apple's down-up recipe). */
    Replace,

    /**
     * Like [Replace], but layers that carry the same Apple tag in both symbols (and the same outline structure)
     * morph into each other instead of fading.
     */
    MagicReplace,
}

/**
 * Draws an SF Symbol with weight, scale, rendering mode and (optionally) a symbol effect.
 *
 * @param size larger side of the Medium-scale symbol, in dp (the composable itself is a square of this size).
 * @param scale optical size variant; [SfScale.Small] / [SfScale.Large] draw smaller / larger than Medium.
 * @param palette colours for [SfRenderingMode.Palette], primary first. Missing levels reuse the last one.
 * @param gradient fills colours with Apple's automatic gradient (lighter at the top) instead of a flat colour.
 * @param variableValue 0..1 fill for symbols with variable layers (e.g. wifi bars, speaker waves); null = full.
 * @param mirrorInRtl flips symbols Apple marks as mirroring in right-to-left layouts.
 * @param effect animation to play, or null.
 * @param repeat how often it plays; null = the effect's natural behaviour (Breathe loops, others play once).
 * @param trigger discrete effects replay whenever this value changes.
 * @param active pauses/stops continuous effects when false; for state effects (Appear, Disappear,
 * Scale, DrawOn, DrawOff) it applies (true) or reverts (false) the effect.
 * @param transition how a changed [symbol] is animated.
 * @param replaceDirection which of Apple's replace recipes a transition plays.
 */
@Composable
public fun SfSymbolIcon(
    symbol: SfSymbol,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    weight: SfWeight = SfWeight.Regular,
    scale: SfScale = SfScale.Medium,
    mode: SfRenderingMode = SfRenderingMode.Monochrome,
    tint: Color = Color.Black,
    palette: List<Color> = emptyList(),
    gradient: Boolean = false,
    variableValue: Float? = null,
    mirrorInRtl: Boolean = true,
    effect: SfSymbolEffect? = null,
    repeat: SfEffectRepeat? = null,
    trigger: Any? = null,
    active: Boolean = true,
    transition: SfSymbolTransition = SfSymbolTransition.None,
    replaceDirection: SfReplaceDirection = SfReplaceDirection.DownUp,
    contentDescription: String? = null,
) {
    var current by remember { mutableStateOf(symbol) }
    var outgoing by remember { mutableStateOf<SfSymbol?>(null) }
    val swap = remember { mutableFloatStateOf(1f) }
    LaunchedEffect(symbol, transition, replaceDirection) {
        if (symbol == current) return@LaunchedEffect
        if (transition == SfSymbolTransition.None) {
            outgoing = null; current = symbol; swap.floatValue = 1f
        } else {
            outgoing = current; current = symbol; swap.floatValue = 0f
            swap.play(0f, 1f, replaceSeconds(replaceDirection))
            outgoing = null
        }
    }

    val time = remember { mutableFloatStateOf(-1f) }
    LaunchedEffect(effect, trigger, active, repeat, current) { runEffect(time, effect, current, repeat, active) }

    val geo = remember(current, weight, scale) { SfGeometry(current, weight, scale) }
    val outGeo = outgoing?.let { o -> remember(o, weight, scale) { SfGeometry(o, weight, scale) } }

    val sem = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription; role = Role.Image }
    } else Modifier
    Layout(
        modifier = modifier.then(sem).drawBehind {
            val mirror = mirrorInRtl && layoutDirection == LayoutDirection.Rtl && current.mirrorsInRtl
            val mirrored = mirror
            withTransform({ if (mirror) scale(-1f, 1f, Offset(this.size.width / 2f, 0f)) }) {
                val t = time.floatValue
                val sw = swap.floatValue
                val o = outgoing
                if (o != null && outGeo != null && sw < 1f) {
                    val fo = SfFx().also { replaceOut(sw * replaceSeconds(replaceDirection), replaceDirection, it) }
                    val fi = SfFx().also { replaceIn(sw * replaceSeconds(replaceDirection), replaceDirection, it) }
                    val match = if (transition == SfSymbolTransition.MagicReplace) matchLayers(outGeo, geo) else emptyMap()
                    if (match.isEmpty()) {
                        drawSymbol(outGeo, mode, tint, palette, gradient, null, -1f, null, alpha = fo.alpha, extraScale = fo.scale)
                        drawSymbol(geo, mode, tint, palette, gradient, effect, t, variableValue, alpha = fi.alpha, extraScale = fi.scale, mirrored = mirrored)
                    } else {
                        // matched layers morph over the whole transition; the rest fade out then in
                        val f = sw
                        val hideOld = match.values.toSet()
                        drawSymbol(
                            outGeo, mode, tint, palette, gradient, null, -1f, null, alpha = 1f, extraScale = 1f,
                            layerAlpha = { i -> if (i in hideOld) 0f else fo.alpha }, forceLayered = true,
                        )
                        val morph = HashMap<Int, Path>()
                        for ((newIdx, oldIdx) in match) morph[newIdx] = morphLayer(outGeo, oldIdx, geo, newIdx, f)
                        drawSymbol(
                            geo, mode, tint, palette, gradient, effect, t, variableValue, alpha = 1f, extraScale = 1f,
                            layerPath = { i -> morph[i] },
                            layerAlpha = { i -> if (i in morph) 1f else fi.alpha }, forceLayered = true,
                        )
                    }
                } else {
                    drawSymbol(geo, mode, tint, palette, gradient, effect, t, variableValue, alpha = 1f, extraScale = 1f, mirrored = mirrored)
                }
            }
        },
    ) { _, _ ->
        val px = size.roundToPx()
        layout(px, px) {}
    }
}

// ---------------------------------------------------------------------------------------------

private class SfGeometry(val symbol: SfSymbol, val weight: SfWeight, val scale: SfScale) {
    val contours: List<Path> = symbol.contourPaths(weight, scale)
    val whole: Path = Path().also { p -> contours.forEach { p.addPath(it) } }
    val layerPaths: List<Path> = symbol.layers.map { l ->
        Path().also { p -> l.contours.forEach { c -> if (c < contours.size) p.addPath(contours[c]) } }
    }
    val layerBounds: List<Rect> = layerPaths.map { it.getBounds() }
    val bounds: Rect = symbol.bounds(weight, scale)

    private val drawFields = HashMap<Int, SfDrawField?>()

    /** Reveal regions for drawing layer [i] on/off, or null when it has no draw rail. */
    fun drawField(i: Int): SfDrawField? {
        if (!drawFields.containsKey(i)) {
            drawFields[i] = symbol.layers[i].ties.takeIf { it.isNotEmpty() }?.let { buildDrawField(layerPaths[i], bounds, it) }
        }
        return drawFields[i]
    }

    /** Medium-scale bounds: the size reference, so Small/Large draw smaller/larger than Medium. */
    val fit: Rect = if (scale == SfScale.Medium) bounds else symbol.bounds(weight, SfScale.Medium)
}

/** Linear playback of a float from [from] to [to] over |to-from| seconds of wall time. */
private suspend fun MutableFloatState.play(from: Float, to: Float, seconds: Float) {
    floatValue = from
    if (from == to) return
    val start = withFrameNanos { it }
    while (true) {
        val el = (withFrameNanos { it } - start) / 1_000_000_000f
        if (el >= seconds) { floatValue = to; return }
        floatValue = from + (to - from) * (el / seconds)
    }
}

private suspend fun runEffect(
    time: MutableFloatState, effect: SfSymbolEffect?, symbol: SfSymbol, repeat: SfEffectRepeat?, active: Boolean,
) {
    if (effect == null) { time.floatValue = -1f; return }
    val dur = effectDuration(effect, symbol)
    if (effect.isHeld()) {
        val from = max(0f, time.floatValue)
        val to = if (active) dur else 0f
        time.play(from, to, abs(to - from).coerceAtLeast(0.001f))
        return
    }
    if (!active) { time.floatValue = -1f; return }
    when (val r = repeat ?: effect.defaultRepeat()) {
        SfEffectRepeat.Continuous -> while (true) time.play(0f, dur, dur)
        is SfEffectRepeat.Times -> repeat(max(1, r.count)) { time.play(0f, dur, dur) }
        SfEffectRepeat.Once -> time.play(0f, dur, dur)
    }
    time.floatValue = -1f
}

private fun layerColor(layer: SfLayer, mode: SfRenderingMode, tint: Color, palette: List<Color>): Color? {
    if (layer.colorName == "hidden") return null
    return when (mode) {
        SfRenderingMode.Monochrome -> if (layer.erases) null else tint
        SfRenderingMode.Hierarchical -> {
            val lvl = layer.hierarchicalLevel
            if (lvl < 0) null else tint.copy(alpha = tint.alpha * HIER_ALPHA[min(lvl, 2)])
        }
        SfRenderingMode.Palette -> {
            val lvl = layer.hierarchicalLevel
            if (lvl < 0) null
            else palette.getOrNull(lvl) ?: palette.lastOrNull() ?: tint.copy(alpha = tint.alpha * HIER_ALPHA[min(lvl, 2)])
        }
        SfRenderingMode.Multicolor -> {
            val c = sfNamedColor(layer.colorName, tint)
            c?.copy(alpha = c.alpha * layer.opacity)
        }
    }
}

/** Font units per em: Apple's offsets are in em. */
private const val EM = 2048f

private val HIER_ALPHA = floatArrayOf(1f, 0.5f, 0.25f)

private fun hex(v: Long) = Color((0xFF000000L or v).toInt())

internal fun sfNamedColor(name: String?, tint: Color): Color? = when (name) {
    null, "tintColor", "labelColor", "controlAccentColor", "_gradient.highlegibility" -> tint
    "erase", "hidden" -> null
    "white" -> Color.White
    "black" -> Color.Black
    "secondaryLabelColor" -> tint.copy(alpha = tint.alpha * 0.5f)
    "tertiaryLabelColor" -> tint.copy(alpha = tint.alpha * 0.3f)
    "systemRedColor" -> hex(0xFF3B30)
    "systemOrangeColor" -> hex(0xFF9500)
    "systemYellowColor" -> hex(0xFFCC00)
    "systemGreenColor" -> hex(0x34C759)
    "systemMintColor" -> hex(0x00C7BE)
    "systemTealColor" -> hex(0x30B0C7)
    "systemCyanColor" -> hex(0x32ADE6)
    "systemBlueColor" -> hex(0x007AFF)
    "systemIndigoColor" -> hex(0x5856D6)
    "systemPurpleColor" -> hex(0xAF52DE)
    "systemPinkColor" -> hex(0xFF2D55)
    "systemBrownColor" -> hex(0xA2845E)
    "systemGrayColor" -> hex(0x8E8E93)
    else -> if (name.startsWith("#") && name.length == 7) hex(name.substring(1).toLong(16)) else tint
}

/** Fills [path] with [color], or with Apple's automatic top-to-bottom gradient of it. */
private fun DrawScope.fillPath(path: Path, color: Color, gradient: Boolean, top: Float, bottom: Float) {
    if (!gradient || color.alpha <= 0f) { drawPath(path, color); return }
    val (a, b) = sfAutomaticGradient(color)
    drawPath(path, Brush.verticalGradient(listOf(a, b), startY = top, endY = bottom))
}

private fun DrawScope.drawSymbol(
    geo: SfGeometry, mode: SfRenderingMode, tint: Color, palette: List<Color>, gradient: Boolean,
    effect: SfSymbolEffect?, time: Float, variableValue: Float?, alpha: Float, extraScale: Float,
    layerPath: (Int) -> Path? = { null },
    layerAlpha: (Int) -> Float = { 1f },
    forceLayered: Boolean = false,
    mirrored: Boolean = false,
) {
    val b = geo.bounds
    val maxDim = max(geo.fit.width, geo.fit.height)
    if (maxDim <= 0f) return
    val s = min(size.width, size.height) / maxDim * extraScale
    val ox = (size.width - b.width * s) / 2f - b.left * s
    val oy = (size.height - b.height * s) / 2f - b.top * s
    val playing = effect != null && (time >= 0f || effect.isHeld())
    val sym = geo.symbol
    val layered = mode != SfRenderingMode.Monochrome || playing || alpha < 1f || sym.hasErasers ||
        variableValue != null || forceLayered
    withTransform({ translate(ox, oy); scale(s, s, Offset.Zero) }) {
        if (!layered) {
            fillPath(geo.whole, tint, gradient, b.top, b.bottom)
            return@withTransform
        }
        val layers = sym.layers
        val pad = max(b.width, b.height)
        drawContext.canvas.saveLayer(Rect(b.left - pad, b.top - pad, b.right + pad, b.bottom + pad), Paint().apply { this.alpha = alpha })
        val fx = SfFx()
        val tt = max(0f, time)
        val variable = variableValue?.let { sfVariableLevels(sym, it) }
        for (i in layers.indices) {
            fx.reset()
            if (playing) applyEffect(effect, tt, i, sym, fx, rtl = layoutDirection == LayoutDirection.Rtl && !mirrored)
            if (variable != null) {
                fx.alpha *= variable.alpha[i]
                fx.draw = min(fx.draw, variable.draw[i])
            }
            fx.alpha *= layerAlpha(i)
            if (fx.alpha <= 0.001f) continue
            val px = if (fx.pivotX.isNaN()) b.center.x else b.left + fx.pivotX * b.width
            val py = if (fx.pivotY.isNaN()) b.center.y else b.top + fx.pivotY * b.height
            val pivot = Offset(px, py)
            withTransform({
                translate(fx.dx * EM, fx.dy * EM)
                if (fx.rot != 0f) rotate(fx.rot, pivot)
                if (fx.scale != 1f) scale(fx.scale, fx.scale, pivot)
            }) {
                val layer = layers[i]
                val path = layerPath(i) ?: geo.layerPaths[i]
                if (layer.erases) drawPath(path, Color.Black, blendMode = BlendMode.Clear)
                val c = layerColor(layer, mode, tint, palette)
                if (c != null) {
                    val col = c.copy(alpha = c.alpha * fx.alpha)
                    if (fx.draw < 0.999f) {
                        val field = if (layer.rootU.isNaN()) null else geo.drawField(i)
                        if (field != null) {
                            // the stroke is revealed by distance travelled along it from its start rail tie
                            val k = min(DRAW_LEVELS, (fx.draw * DRAW_LEVELS).toInt())
                            if (k > 0) clipPath(field.levels[k - 1]) { fillPath(path, col, gradient, b.top, b.bottom) }
                        } else {
                            fillPath(path, col.copy(alpha = col.alpha * fx.draw), gradient, b.top, b.bottom)
                        }
                    } else fillPath(path, col, gradient, b.top, b.bottom)
                }
            }
        }
        drawContext.canvas.restore()
    }
}

// ---- MagicReplace: match layers by Apple tag + identical outline structure, then morph their points

/** newLayerIndex -> oldLayerIndex for layers that share a tag and have the same outline structure. */
private fun matchLayers(old: SfGeometry, new: SfGeometry): Map<Int, Int> {
    val oldL = old.symbol.layers
    val newL = new.symbol.layers
    val used = HashSet<Int>()
    val out = LinkedHashMap<Int, Int>()
    for (j in newL.indices) {
        if (newL[j].erases) continue
        for (i in oldL.indices) {
            if (i in used || oldL[i].erases) continue
            if (oldL[i].tags.intersect(newL[j].tags).isEmpty()) continue
            if (layerSignature(old.symbol, i) != layerSignature(new.symbol, j)) continue
            used += i; out[j] = i
            break
        }
    }
    return out
}

private fun layerSignature(sym: SfSymbol, layer: Int): List<List<Byte>> =
    sym.layers[layer].contours.map { c -> sym.contourOps(c) }

/** The new layer's outline with every point moved a fraction [f] of the way from the old layer's outline. */
private fun morphLayer(old: SfGeometry, oldLayer: Int, new: SfGeometry, newLayer: Int, f: Float): Path {
    val po = old.symbol.points(old.weight, old.scale)
    val pn = new.symbol.points(new.weight, new.scale)
    val path = Path()
    val oc = old.symbol.layers[oldLayer].contours
    val nc = new.symbol.layers[newLayer].contours
    val bo = old.bounds
    val bn = new.bounds
    val so = 1f
    for (k in nc.indices) {
        val ops = new.symbol.contourOps(nc[k])
        var io = old.symbol.contourStart(oc[k])
        var inn = new.symbol.contourStart(nc[k])
        // bring the old outline into the new symbol's frame (same centre, scaled by the size ratio)
        val ratio = max(new.fit.width, new.fit.height).let { n -> max(old.fit.width, old.fit.height) / n }
        fun mapX(x: Float) = (x - bo.center.x) / ratio * so + bn.center.x
        fun mapY(y: Float) = (y - bo.center.y) / ratio * so + bn.center.y
        fun lx(a: Float, b: Float) = a + (b - a) * f
        for (op in ops) {
            when (op.toInt()) {
                0 -> { path.moveTo(lx(mapX(po[io]), pn[inn]), lx(mapY(po[io + 1]), pn[inn + 1])); io += 2; inn += 2 }
                1 -> { path.lineTo(lx(mapX(po[io]), pn[inn]), lx(mapY(po[io + 1]), pn[inn + 1])); io += 2; inn += 2 }
                2 -> {
                    path.cubicTo(
                        lx(mapX(po[io]), pn[inn]), lx(mapY(po[io + 1]), pn[inn + 1]),
                        lx(mapX(po[io + 2]), pn[inn + 2]), lx(mapY(po[io + 3]), pn[inn + 3]),
                        lx(mapX(po[io + 4]), pn[inn + 4]), lx(mapY(po[io + 5]), pn[inn + 5]),
                    )
                    io += 6; inn += 6
                }
                else -> path.close()
            }
        }
    }
    return path
}
