/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.theme.colorengine

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.ui.graphics.Color
import com.convx.music.utils.ArtworkProcessing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Builds deterministic artwork palettes and light/dark color schemes.
 *
 * The pipeline samples and weights the image, clusters its colors, then assigns
 * semantic roles. Clustering uses a pixel-seeded random generator and stable
 * tie-breaking, so the same image always produces the same cached result.
 *
 * Scene detection is optional. Pass [VisionData] from a detector when available;
 * otherwise the pipeline uses geometric and spatial weighting.
 */
object ColorEngine {

    /** Bump when the pipeline's output changes, so stale cache entries are ignored. */
    private const val Version = "v26-kt1"

    // 60 analyses, matching the Swift NSCache's countLimit. Each entry is a few hundred
    // bytes of colours, not pixels, so this is sized for how many artworks a session
    // actually touches rather than for memory.
    private val memoryCache = LruCache<String, ColorEngineResult>(60)

    /** Disk tier, mirroring `ColorEngineCache.swift`. Null until [init] is called. */
    private var diskCache: ColorEngineDiskCache? = null

    /** Global debug flag, mirroring `JoiColorAnalysis.setDebugEnabled`. */
    private var isDebugEnabled = false

    /**
     * Wires the disk cache tier to [context]. Call once from [android.app.Application
     * .onCreate]; analysis and the memory cache work without it, so a missed call
     * degrades to memory-only caching rather than crashing.
     */
    fun init(context: Context) {
        diskCache = ColorEngineDiskCache(context.applicationContext, Version)
    }

    /** Mirrors `JoiColorAnalysis.setDebugEnabled` — logs a diagnostics report per call. */
    fun setDebugEnabled(enabled: Boolean) {
        isDebugEnabled = enabled
    }

    /**
     * Analyses [bitmap] and returns its schemes and palette, or null if the bitmap
     * cannot be sampled (recycled, zero-sized, or clustering found nothing).
     *
     * [cacheKey] should identify the *image* — an artwork URL is the natural choice.
     * Runs the pixel work on [Dispatchers.Default]; safe to call from a composable's
     * side effect.
     */
    suspend fun analyze(
        bitmap: Bitmap,
        cacheKey: String,
        style: ColorStyle = ColorStyle.BALANCED,
        visionMode: VisionMode = VisionMode.DISABLED,
        vision: VisionData? = null,
        debug: Boolean = false,
    ): ColorEngineResult? {
        val effectiveKey = "$cacheKey|$Version|vision=$visionMode|style=$style"
        val shouldDebug = debug || isDebugEnabled
        memoryCache.get(effectiveKey)?.let {
            if (shouldDebug) logCacheHit(cacheKey, visionMode, style)
            return it
        }
        return ArtworkProcessing.cached("palette|$effectiveKey", { memoryCache.get(effectiveKey) }) {
            withContext(ArtworkProcessing.dispatcher) {
                diskCache?.load(effectiveKey)?.let { fromDisk ->
                    memoryCache.put(effectiveKey, fromDisk)
                    if (shouldDebug) logCacheHit(cacheKey, visionMode, style)
                    return@withContext fromDisk
                }
                val diagnostics = if (shouldDebug) AnalysisDiagnostics() else null
                val result = runPipeline(bitmap, style, visionMode, vision, diagnostics) ?: return@withContext null
                memoryCache.put(effectiveKey, result)
                diskCache?.store(effectiveKey, result)
                if (diagnostics != null) logDiagnostics(cacheKey, diagnostics, result)
                result
            }
        }
    }

    /** Cached result for [cacheKey], if this image has already been analysed. */
    fun cached(
        cacheKey: String,
        style: ColorStyle = ColorStyle.BALANCED,
        visionMode: VisionMode = VisionMode.DISABLED,
    ): ColorEngineResult? {
        val effectiveKey = "$cacheKey|$Version|vision=$visionMode|style=$style"
        return memoryCache.get(effectiveKey) ?: diskCache?.load(effectiveKey)
            ?.also { memoryCache.put(effectiveKey, it) }
    }

    fun invalidateAll() {
        memoryCache.evictAll()
        diskCache?.invalidateAll()
    }

    private fun runPipeline(
        bitmap: Bitmap,
        style: ColorStyle,
        visionMode: VisionMode,
        vision: VisionData?,
        diagnostics: AnalysisDiagnostics?,
    ): ColorEngineResult? {
        if (bitmap.isRecycled || bitmap.width <= 0 || bitmap.height <= 0) {
            diagnostics?.warnings?.add("Bitmap unusable: recycled or zero-sized.")
            return null
        }
        // A HARDWARE bitmap lives in graphics memory and throws on getPixels.
        // Copy to a software bitmap so callers don't need allowHardware(false).
        val workBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false) ?: run {
                diagnostics?.warnings?.add("HARDWARE bitmap copy failed.")
                return null
            }
        } else {
            bitmap
        }
        val startNanos = System.nanoTime()

        val assessment = assessDetail(workBitmap, visionMode) ?: run {
            diagnostics?.warnings?.add("Detail assessment failed (16px probe unreadable).")
            return null
        }
        diagnostics?.detailAssessment = assessment
        diagnostics?.requestedClusters = assessment.requestedClusterCount
        val (sampleWidth, sampleHeight) =
            sampleDimensions(workBitmap.width, workBitmap.height, assessment.thumbnailSize)
        diagnostics?.sampleWidth = sampleWidth
        diagnostics?.sampleHeight = sampleHeight
        val pixels = sampleBitmap(workBitmap, assessment.thumbnailSize) ?: run {
            diagnostics?.warnings?.add("Sampling failed at thumbnail size ${assessment.thumbnailSize}.")
            return null
        }
        val plane = makePixelPlane(pixels, sampleWidth, sampleHeight) ?: run {
            diagnostics?.warnings?.add("Pixel plane construction failed.")
            return null
        }
        diagnostics?.pixelCount = plane.count

        val weightMap = makeWeightMap(plane, vision, visionMode)
        val toneStatistics = analyzeTone(plane)
        diagnostics?.toneStatistics = toneStatistics
        val averages = calculateAverages(plane, weightMap.background)

        val kMeans = deterministicKMeans(plane, weightMap, assessment.requestedClusterCount)
        if (kMeans.clusters.isEmpty()) {
            diagnostics?.warnings?.add("K-means produced no clusters.")
            return null
        }
        diagnostics?.actualClusters = kMeans.clusters.size
        diagnostics?.kMeansIterations = kMeans.iterations
        diagnostics?.quantizedSampleCount = kMeans.quantizedSampleCount
        diagnostics?.fittingSampleCount = kMeans.fittingSampleCount
        diagnostics?.identityClusterCount = kMeans.identityClusters.size

        val roles = selectRoles(kMeans.clusters, kMeans.identityClusters)
        diagnostics?.backgroundRole = roles.background
        diagnostics?.foregroundRole = roles.foreground
        diagnostics?.accentRole = roles.accent
        if (roles.accent == null && roles.identityClusters.none { hasIdentityEvidence(it) }) {
            diagnostics?.warnings?.add("Neutral artwork: synthetic hue forcing disabled.")
        }
        if (toneStatistics.confidence < 0.22f) {
            diagnostics?.warnings?.add("Source tone is near the dark/light decision boundary.")
        }
        if (roles.foreground == null) {
            diagnostics?.warnings?.add("No independent foreground role passed the evidence gate.")
        }

        val styleProfile = StyleProfile(style)
        val backgrounds =
            makeBackgroundPair(kMeans.clusters, roles, toneStatistics, styleProfile)
        if (backgrounds.source.c < 12) {
            diagnostics?.warnings?.add(
                "Background desaturated to neutral: no cluster cleared the chroma/identity floor.",
            )
        }
        val meshSources =
            makeMeshSourceColors(kMeans.clusters, roles, toneStatistics, styleProfile)

        val darkScheme = makeScheme(
            roles = roles,
            background = backgrounds.dark,
            meshColors = makeSchemeMeshColors(meshSources, backgrounds.dark, isDark = true),
            isDark = true,
            style = styleProfile,
        )
        val lightScheme = makeScheme(
            roles = roles,
            background = backgrounds.light,
            meshColors = makeSchemeMeshColors(meshSources, backgrounds.light, isDark = false),
            isDark = false,
            style = styleProfile,
        )

        val palette = makePalette(
            roles = roles,
            clusters = presentationClusters(kMeans.clusters, roles.identityClusters),
            backgroundSource = backgrounds.source,
            averages = averages,
        )

        return ColorEngineResult(
            dark = darkScheme,
            light = lightScheme,
            palette = palette,
            style = style,
            sourceImageTone = toneStatistics.tone,
            sourceToneConfidence = toneStatistics.confidence,
            processingTimeMs = (System.nanoTime() - startNanos) / 1_000_000.0,
        )
    }

    private fun logCacheHit(cacheKey: String, visionMode: VisionMode, style: ColorStyle) {
        val diagnostics = AnalysisDiagnostics().apply {
            cacheHit = true
            this.visionMode = visionMode
            this.style = style
        }
        logDiagnosticsReport(cacheKey, diagnostics, result = null, totalMs = 0.0)
    }

    private fun logDiagnostics(cacheKey: String, diagnostics: AnalysisDiagnostics, result: ColorEngineResult) {
        logDiagnosticsReport(cacheKey, diagnostics, result, result.processingTimeMs)
    }
}

/** One appearance's worth of tokens, ready to hand to Compose. */
data class ColorEngineScheme(
    val background: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val tertiaryText: Color,
    val quaternaryText: Color,
    val accent: Color,
    val surface: Color,
    val border: Color,
    val shadow: Color,
    val tint: Color,
    val destructive: Color,
    val backgroundGradientTop: Color,
    val backgroundGradientBottom: Color,
    val textGradientTop: Color,
    val textGradientBottom: Color,
    val meshColors: List<Color>,
)

/** The raw artwork colours behind the schemes. */
data class ColorEnginePalette(
    val dominant: Color,
    val secondary: Color,
    val accent: Color,
    val dominantHex: String,
    val secondaryHex: String,
    val accentHex: String,
    val averageColor: LabColor,
    val weightedAverageColor: LabColor,
    val harmonies: Map<ColorHarmony, List<LChColor>>,
    val clusters: List<LabColor>,
)

data class ColorEngineResult(
    val dark: ColorEngineScheme,
    val light: ColorEngineScheme,
    val palette: ColorEnginePalette,
    val style: ColorStyle,
    val sourceImageTone: ImageTone,
    val sourceToneConfidence: Float,
    val processingTimeMs: Double,
) {
    /** The scheme matching the artwork's own tone — a dark cover keeps its dark scheme. */
    val native: ColorEngineScheme get() = if (sourceImageTone == ImageTone.DARK) dark else light

    /** The scheme for a given appearance. */
    fun forDarkTheme(isDark: Boolean): ColorEngineScheme = if (isDark) dark else light
}
