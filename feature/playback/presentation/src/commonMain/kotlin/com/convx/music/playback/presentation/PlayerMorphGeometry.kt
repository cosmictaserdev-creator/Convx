package com.convx.music.playback.presentation

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.lerp

const val PLAYER_MORPH_ARTWORK_VERTICAL_LINEAR_WEIGHT = 0.4f
const val PLAYER_MORPH_SCREEN_CORNER_EXPANSION_MILLIS = 140
const val PLAYER_MORPH_CORNER_EXPANSION_PROGRESS_BAND = 0.02f
const val PLAYER_MORPH_INPUT_MINI_END_PROGRESS = 0.2f
const val PLAYER_MORPH_LAYER_HANDOFF_PROGRESS = 0.2f
const val PLAYER_MORPH_MINI_FADE_END = 0.3f
const val PLAYER_MORPH_CONTENT_APPEAR_START = 0.1f
const val PLAYER_MORPH_CONTENT_APPEAR_END = 0.6f
const val PLAYER_MORPH_FLING_THRESHOLD_PX_PER_SECOND = 250f
private const val PLAYER_MORPH_FLING_DECELERATION_RATE = 0.998f

enum class PlayerMorphInputOwner { MINI, FULL, NONE }

data class PlayerMorphCornerRadii(
    val topStart: Float,
    val topEnd: Float,
    val bottomEnd: Float,
    val bottomStart: Float,
) {
    companion object {
        fun uniform(cornerRadius: Float) = PlayerMorphCornerRadii(
            topStart = cornerRadius,
            topEnd = cornerRadius,
            bottomEnd = cornerRadius,
            bottomStart = cornerRadius,
        )
    }
}

/** Platform-neutral geometry and handoff policy for the mini/full player transition. */
object PlayerMorphGeometry {
    fun sanitizeProgress(progress: Float): Float =
        if (progress.isNaN()) 0f else progress.coerceIn(0f, 1f)

    fun easeInCubic(value: Float): Float {
        val fraction = sanitizeProgress(value)
        return fraction * fraction * fraction
    }

    fun easeOutCubic(value: Float): Float {
        val inverse = 1f - sanitizeProgress(value)
        return 1f - inverse * inverse * inverse
    }

    fun containerRect(source: Rect, target: Rect, progress: Float): Rect {
        val fraction = sanitizeProgress(progress)
        return Rect(
            left = lerp(source.left, target.left, fraction),
            top = lerp(source.top, target.top, fraction),
            right = lerp(source.right, target.right, fraction),
            bottom = lerp(source.bottom, target.bottom, fraction),
        )
    }

    fun miniTitleShift(sourcePlayerBounds: Rect, animatedPlayerBounds: Rect): Float =
        sourcePlayerBounds.left - animatedPlayerBounds.left

    fun miniControlsShift(
        sourcePlayerBounds: Rect,
        animatedPlayerBounds: Rect,
        controlsBounds: Rect,
    ): Float {
        val sourceRightInset = sourcePlayerBounds.right - controlsBounds.right
        return animatedPlayerBounds.right - sourceRightInset - controlsBounds.right
    }

    fun artworkRect(source: Rect, target: Rect, progress: Float): Rect {
        val fraction = sanitizeProgress(progress)
        val centerX = lerp(source.center.x, target.center.x, easeOutCubic(fraction))
        val verticalFraction = lerp(
            easeInCubic(fraction),
            fraction,
            PLAYER_MORPH_ARTWORK_VERTICAL_LINEAR_WEIGHT,
        )
        val centerY = lerp(source.center.y, target.center.y, verticalFraction)
        val sourceWidth = source.width.coerceAtLeast(1f)
        val targetWidth = target.width.coerceAtLeast(sourceWidth)
        val scale = lerp(1f, targetWidth / sourceWidth, fraction)
        val width = sourceWidth * scale
        val height = source.height.coerceAtLeast(1f) * scale
        return Rect(
            left = centerX - width / 2f,
            top = centerY - height / 2f,
            right = centerX + width / 2f,
            bottom = centerY + height / 2f,
        )
    }

    fun artworkWindow(source: Rect, target: Rect, progress: Float): Rect {
        val fraction = sanitizeProgress(progress)
        val centerX = lerp(source.center.x, target.center.x, easeOutCubic(fraction))
        val centerY = lerp(
            source.center.y,
            target.center.y,
            lerp(
                easeInCubic(fraction),
                fraction,
                PLAYER_MORPH_ARTWORK_VERTICAL_LINEAR_WEIGHT,
            ),
        )
        val halfWidth = lerp(source.width, target.width, fraction).coerceAtLeast(1f) / 2f
        val halfHeight = lerp(source.height, target.height, fraction).coerceAtLeast(1f) / 2f
        return Rect(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY + halfHeight)
    }

    fun cornerRadii(
        source: PlayerMorphCornerRadii,
        target: PlayerMorphCornerRadii,
        progress: Float,
    ): PlayerMorphCornerRadii {
        val fraction = sanitizeProgress(progress)
        fun interpolate(sourceRadius: Float, targetRadius: Float): Float =
            if (fraction < 1f) lerp(sourceRadius, targetRadius, fraction) else 0f
        return PlayerMorphCornerRadii(
            topStart = interpolate(source.topStart, target.topStart),
            topEnd = interpolate(source.topEnd, target.topEnd),
            bottomEnd = interpolate(source.bottomEnd, target.bottomEnd),
            bottomStart = interpolate(source.bottomStart, target.bottomStart),
        )
    }

    fun cornerRadius(
        collapsedCornerRadius: Float,
        expandedCornerRadius: Float,
        progress: Float,
        screenCornerExpansionProgress: Float,
    ): Float {
        val fraction = sanitizeProgress(progress)
        val collapsed = collapsedCornerRadius.takeIf(Float::isFinite) ?: 0f
        val expanded = expandedCornerRadius.takeIf(Float::isFinite) ?: 0f
        val transitionRadius = lerp(collapsed, expanded, fraction)
        val settled = (
            (fraction - (1f - PLAYER_MORPH_CORNER_EXPANSION_PROGRESS_BAND)) /
                PLAYER_MORPH_CORNER_EXPANSION_PROGRESS_BAND
            ).coerceIn(0f, 1f)
        val expansion = sanitizeProgress(screenCornerExpansionProgress) * settled
        return lerp(transitionRadius, 0f, expansion)
    }

    fun miniContentAlpha(progress: Float): Float =
        1f - easeOutCubic((sanitizeProgress(progress) / PLAYER_MORPH_MINI_FADE_END).coerceIn(0f, 1f))

    fun pageAlpha(progress: Float): Float = easeInCubic(
        (
            (sanitizeProgress(progress) - PLAYER_MORPH_CONTENT_APPEAR_START) /
                (PLAYER_MORPH_CONTENT_APPEAR_END - PLAYER_MORPH_CONTENT_APPEAR_START)
            ).coerceIn(0f, 1f),
    )

    fun backgroundAlpha(progress: Float): Float =
        easeInCubic((sanitizeProgress(progress) / PLAYER_MORPH_LAYER_HANDOFF_PROGRESS).coerceIn(0f, 1f))

    /** Matches the player's two-anchor sheet release rule using the same momentum projection. */
    fun shouldExpandAfterRelease(
        progress: Float,
        openingVelocityPxPerSecond: Float,
        travelPx: Float,
    ): Boolean {
        if (openingVelocityPxPerSecond > PLAYER_MORPH_FLING_THRESHOLD_PX_PER_SECOND) return true
        if (openingVelocityPxPerSecond < -PLAYER_MORPH_FLING_THRESHOLD_PX_PER_SECOND) return false
        val travel = travelPx.coerceAtLeast(1f)
        val momentum = if (openingVelocityPxPerSecond == 0f) {
            0f
        } else {
            (openingVelocityPxPerSecond / 1000f) * PLAYER_MORPH_FLING_DECELERATION_RATE /
                (1f - PLAYER_MORPH_FLING_DECELERATION_RATE)
        }
        val projected = sanitizeProgress(progress) * travel + momentum
        return projected > travel / 2f ||
            (projected == travel / 2f && openingVelocityPxPerSecond > 0f)
    }

    fun inputOwner(
        targetOpen: Boolean,
        isDragging: Boolean,
        dragStartedFromMiniPlayer: Boolean,
        progress: Float,
        sharedLayersReady: Boolean,
    ): PlayerMorphInputOwner {
        val safeProgress = sanitizeProgress(progress)
        return when {
            isDragging -> if (dragStartedFromMiniPlayer) PlayerMorphInputOwner.MINI else PlayerMorphInputOwner.FULL
            !targetOpen && safeProgress <= PLAYER_MORPH_INPUT_MINI_END_PROGRESS -> PlayerMorphInputOwner.MINI
            sharedLayersReady || targetOpen && safeProgress >= 1f -> PlayerMorphInputOwner.FULL
            else -> PlayerMorphInputOwner.NONE
        }
    }
}
