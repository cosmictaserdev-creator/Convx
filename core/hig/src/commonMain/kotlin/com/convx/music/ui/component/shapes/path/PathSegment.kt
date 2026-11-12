/*
 * Vendored from Kyant0/Capsule
 * https://github.com/Kyant0/Capsule — Copyright 2025 Kyant0, Apache License 2.0
 * capsule/src/main/java/com/kyant/capsule/path/PathSegment.kt
 *
 * Vendored so the shape ships as source with this app. Package renamed
 * accordingly (com.kyant.capsule -> com.convx.music.ui.component.shapes).
 */
package com.convx.music.ui.component.shapes.path

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Rect
import com.convx.music.ui.component.shapes.core.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Immutable
sealed interface PathSegment {

    val from: Point
    val to: Point

    fun drawTo(path: Path)

    data class Line(
        override val from: Point,
        override val to: Point
    ) : PathSegment {

        override fun drawTo(path: Path) {
            path.lineTo(to.x.toFloat(), to.y.toFloat())
        }
    }

    data class Arc(
        val center: Point,
        val radius: Double,
        val startAngle: Double,
        val sweepAngle: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(
                center.x + cos(startAngle) * radius,
                center.y + sin(startAngle) * radius
            )

        override val to: Point
            get() = Point(
                center.x + cos(startAngle + sweepAngle) * radius,
                center.y + sin(startAngle + sweepAngle) * radius
            )

        override fun drawTo(path: Path) {
            path.arcTo(
                Rect(
                    (center.x - radius).toFloat(),
                    (center.y - radius).toFloat(),
                    (center.x + radius).toFloat(),
                    (center.y + radius).toFloat()
                ),
                (startAngle * (180.0 / PI)).toFloat(),
                (sweepAngle * (180.0 / PI)).toFloat(),
                forceMoveTo = false
            )
        }
    }

    data class Circle(
        val center: Point,
        val radius: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(center.x + radius, center.y)

        override val to: Point
            get() = from

        override fun drawTo(path: Path) {
            val bounds = Rect(
                (center.x - radius).toFloat(),
                (center.y - radius).toFloat(),
                (center.x + radius).toFloat(),
                (center.y + radius).toFloat()
            )
            path.arcTo(bounds, 0f, 180f, forceMoveTo = true)
            path.arcTo(bounds, 180f, 180f, forceMoveTo = false)
            path.close()
        }
    }

    data class Cubic(
        val p0: Point,
        val p1: Point,
        val p2: Point,
        val p3: Point
    ) : PathSegment {

        override val from: Point
            get() = p0

        override val to: Point
            get() = p3

        override fun drawTo(path: Path) {
            path.cubicTo(
                p1.x.toFloat(), p1.y.toFloat(),
                p2.x.toFloat(), p2.y.toFloat(),
                p3.x.toFloat(), p3.y.toFloat()
            )
        }
    }
}
