package com.kyant.capsule.continuities

import androidx.compose.runtime.Immutable
import com.kyant.capsule.Continuity
import com.kyant.capsule.path.PathSegments
import com.kyant.capsule.path.buildPathSegments

@Immutable
data object G0Continuity : Continuity {

    override fun createRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments {
        return buildPathSegments {
            moveTo(0.0, topLeft)
            if (topLeft > 0.0) {
                lineTo(topLeft, 0.0)
            }
            lineTo(width - topRight, 0.0)
            if (topRight > 0.0) {
                lineTo(width, topRight)
            }
            lineTo(width, height - bottomRight)
            if (bottomRight > 0.0) {
                lineTo(width - bottomRight, height)
            }
            lineTo(bottomLeft, height)
            if (bottomLeft > 0.0) {
                lineTo(0.0, height - bottomLeft)
            }
            close()
        }
    }

    override fun lerp(stop: Continuity, fraction: Double): Continuity {
        return when (stop) {
            is G0Continuity,
            is G1Continuity,
            is G2Continuity -> if (fraction < 0.5) this else stop

            else -> stop.lerp(this, 1f - fraction)
        }
    }
}
