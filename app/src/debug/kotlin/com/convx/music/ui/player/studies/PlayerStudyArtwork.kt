/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.studies

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

private val soot = Color(0xFF181A18)
private val silver = Color(0xFFE1DBC8)
private val lime = Color(0xFFE6FA8E)

@Composable
internal fun StudyCover(study: PlayerStudy, track: Int, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        scale(size.width / 100f, size.height / 100f, Offset.Zero) {
            cover(study, track)
        }
    }
}

private fun DrawScope.cover(study: PlayerStudy, track: Int) {
    when (study) {
        PlayerStudy.Atlas -> terrain(track)
        PlayerStudy.Arcade, PlayerStudy.Cutout, PlayerStudy.Reels -> prism(study, track)
        else -> landscape(study, track)
    }
}

private fun DrawScope.landscape(study: PlayerStudy, track: Int) {
    val dark = Color(0xFF111927)
    val hue = when (study) {
        PlayerStudy.Tachometer -> Color(0xFFFF6647)
        PlayerStudy.HiFi -> Color(0xFF557A6B)
        PlayerStudy.Pocket -> Color(0xFF339D84)
        else -> Color(0xFF6388E7)
    }
    val light = if (study == PlayerStudy.Pocket) Color(0xFFBDE3AC) else Color(0xFFF7DAAF)
    val sun = Offset(41f + track * 4, 39f - track % 3 * 3)
    drawRect(Brush.linearGradient(listOf(dark, hue, light), Offset(12f, 0f), Offset(85f, 100f)), size = Size(100f, 100f))
    drawCircle(Brush.radialGradient(listOf(light.copy(alpha = .45f), hue.copy(alpha = 0f)), sun, 42f), 42f, sun)
    drawCircle(light, 10.5f, sun)

    repeat(4) { layer ->
        val mountains = Path().apply {
            moveTo(0f, 100f)
            for (x in 0..100) {
                val ridge = sin(x * .054 + layer * 1.8 + track) * 8 +
                    sin(x * .132 + layer * 2.3) * 4 + cos(x * .62) * 1.2
                lineTo(x.toFloat(), 62f + layer * 8 + ridge.toFloat())
            }
            lineTo(100f, 100f)
            close()
        }
        drawPath(mountains, lerp(dark, hue, .62f - layer * .19f))
    }

    repeat(120) { index ->
        val x = ((index * 73 + 7) % 997) / 10f
        val y = 2f + ((index * 29) % 955) / 10f
        drawLine(light.copy(alpha = if (y < 48f) .18f else .10f), Offset(x, y), Offset(x + .65f, y), .16f)
    }
}

private fun DrawScope.prism(study: PlayerStudy, track: Int) {
    val dark = Color(0xFF1C1328)
    val mid = when (study) {
        PlayerStudy.Cutout -> Color(0xFFB12F32)
        PlayerStudy.Reels -> Color(0xFF445843)
        else -> Color(0xFF6333C1)
    }
    val light = if (study == PlayerStudy.Arcade) lime else Color(0xFFFFD4A7)
    drawRect(Brush.radialGradient(listOf(mid, dark), Offset(45f, 58f), 68f), size = Size(100f, 100f))
    repeat(13) { index ->
        val radius = 41f - index * 2.6f
        val mesh = Path().apply {
            repeat(7) { side ->
                val angle = side * 2 * PI / 7 + index * .14 + track * .13
                val x = 50f + (radius * cos(angle)).toFloat()
                val y = 52f + (radius * sin(angle)).toFloat()
                if (side == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(mesh, Brush.linearGradient(listOf(light, mid, dark, light), Offset(67f - index * 2, 9f), Offset(45f, 95f - index * 3)))
        drawPath(mesh, light.copy(alpha = .34f), style = Stroke(.36f))
    }
    drawLine(light.copy(alpha = .5f), Offset(12f, 86f), Offset(32f, 86f), .45f)
    drawLine(light.copy(alpha = .5f), Offset(12f, 88f), Offset(20f, 88f), .45f)
}

private val elevationColors = listOf(
    Color(0xFF182652), Color(0xFF314C9C), Color(0xFF4B70CA), Color(0xFF8AA7E4),
    Color(0xFFE1BBD6), Color(0xFFDB79B9), Color(0xFFBB4B93), Color(0xFFEDFA8A),
)

private fun DrawScope.terrain(track: Int) {
    val grid = 64
    val step = 100f / grid
    repeat(grid) { row ->
        repeat(grid) { column ->
            val x = abs((column - 31.5) / 32)
            val y = (row - 32.0) / 32
            val altitude = 1.24 - x * 1.28 - y.pow(2) * .94 +
                .19 * sin(x * 17 + cos(y * 14 + track * .41) * 2.1) +
                .12 * sin(y * 23 + x * 8) + .07 * cos(x * 43 + y * 34)
            val band = (altitude * 7).toInt().coerceIn(elevationColors.indices)
            drawRect(elevationColors[band], Offset(column * step, row * step), Size(step + .11f, step + .11f))
        }
    }
}

@Composable
internal fun StudyGauge(study: PlayerStudy, index: Int, progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = min(size.width, size.height) / 100f
        val halo = study == PlayerStudy.Halo
        translate((size.width - unit * 100f) * if (halo) .5f else .78f, (size.height - unit * 100f) / 2f) {
            scale(unit, unit, Offset.Zero) {
                val center = Offset(50f, 50f)
                val plate = if (halo) Color(0xFF101B46) else Color(0xFF281C19)
                val signal = if (halo) Color(0xFFE4EAFF) else Color(0xFFFF754A)
                drawCircle(Color.Black.copy(alpha = .18f), 49.5f, center + Offset(1.4f, 1.4f))
                drawCircle(Brush.radialGradient(listOf(lerp(plate, signal, .18f), plate), center, 50f), 49f, center)
                drawCircle(signal.copy(alpha = .30f), 48f, center, style = Stroke(.35f))
                for (ring in listOf(20f, 28f, 35f, 39f)) {
                    drawCircle(signal.copy(alpha = .21f), ring, center, style = Stroke(.26f))
                }
                val start = if (halo) -90f else 144f
                val sweep = if (halo) 360f else 260f
                for (tick in 0..60) {
                    val angle = (start + tick / 60f * sweep) * PI / 180
                    val marker = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                    val major = tick % 5 == 0
                    drawLine(signal.copy(alpha = if (major) .72f else .32f),
                        center + marker * if (major) 40.8f else 43.7f, center + marker * 46.1f,
                        if (major) .64f else .31f)
                }
                drawArc(signal.copy(alpha = .12f), start, sweep, false, Offset(3f, 3f), Size(94f, 94f), style = Stroke(2.4f))
                drawArc(signal, start, progress * sweep, false, Offset(3f, 3f), Size(94f, 94f),
                    style = Stroke(if (halo) 3.0f else 1.55f, cap = StrokeCap.Round))
                val angle = (start + progress * sweep) * PI / 180
                val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                drawCircle(signal, if (halo) 1.6f else 2.0f, center + direction * 46.9f)
                if (!halo) drawLine(signal, center + direction * 22f, center + direction * 38.5f, 1.25f, StrokeCap.Round)
                circleCover(study, index, center, if (halo) 17.5f else 19f)
                drawCircle(signal.copy(alpha = .55f), if (halo) 18.5f else 20f, center, style = Stroke(.35f))
            }
        }
    }
}

@Composable
internal fun ReelsArtwork(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = min(size.width / 100f, size.height / 55f)
        translate((size.width - unit * 100f) / 2f, (size.height - unit * 55f) / 2f) {
            scale(unit, unit, Offset.Zero) {
                val body = Color(0xFF26332A)
                val face = Color(0xFF4B5C45)
                val film = Color(0xFFA0B389)
                drawLine(body, Offset(23f, 38f), Offset(50f, 52f), 1.1f)
                drawLine(body, Offset(50f, 52f), Offset(77f, 38f), 1.1f)
                drawCircle(body, 2.5f, Offset(50f, 52f))
                listOf(Offset(26f, 23f), Offset(74f, 23f)).forEachIndexed { index, center ->
                    drawCircle(film, 22.6f, center)
                    drawCircle(body, 21.7f, center)
                    val roll = if (index == 0) 17f - progress * 7f else 10f + progress * 7f
                    drawCircle(face, roll, center)
                    for (ring in 6..roll.toInt() step 2) {
                        drawCircle(film.copy(alpha = .34f), ring.toFloat(), center, style = Stroke(.27f))
                    }
                    for (spoke in 0..2) {
                        val angle = spoke * PI * 2 / 3 + progress * 22 + index * .9
                        val vector = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                        drawLine(film, center + vector * 5f, center + vector * 18f, 2.6f, StrokeCap.Round)
                    }
                    drawCircle(film, 4.7f, center)
                    drawCircle(body, 2.2f, center)
                }
            }
        }
    }
}

@Composable
internal fun TurntableArtwork(track: Int, progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = min(size.width / 100f, size.height / 83f)
        translate((size.width - unit * 100f) / 2f, (size.height - unit * 83f) / 2f) {
            scale(unit, unit, Offset.Zero) {
                val center = Offset(43f, 42f)
                val brushed = Color(0xFF7D847B)
                drawCircle(Color.Black.copy(alpha = .13f), 36f, center + Offset(.8f, 1.5f))
                drawCircle(silver, 35.4f, center)
                drawCircle(soot, 34f, center)
                drawCircle(Brush.sweepGradient(listOf(soot, Color(0xFF414643), soot, Color(0xFF3B3D38), soot), center), 33.6f, center)
                for (i in 13..33) {
                    drawCircle(silver.copy(alpha = if (i % 4 == 0) .18f else .09f), i.toFloat(), center, style = Stroke(.26f))
                }
                circleCover(PlayerStudy.HiFi, track, center, 12.5f)
                drawCircle(silver, 1.35f, center)
                drawCircle(brushed, 5.3f, Offset(88f, 12f))
                drawCircle(silver, 4.1f, Offset(88f, 12f))
                val tip = Offset(77f - progress * 15f, 64f - progress * 5f)
                val arm = Path().apply {
                    moveTo(88f, 12f)
                    cubicTo(88f, 28f, 91f, 49f, tip.x, tip.y)
                }
                drawPath(arm, brushed, style = Stroke(2.5f, cap = StrokeCap.Round))
                drawPath(arm, silver, style = Stroke(1.25f, cap = StrokeCap.Round))
                drawLine(brushed, tip + Offset(1.6f, -2.2f), tip + Offset(-2.2f, 2.8f), 4.8f, StrokeCap.Round)
                drawLine(silver, tip + Offset(1.3f, -2.1f), tip + Offset(-2.3f, 2.7f), 3.1f, StrokeCap.Round)
            }
        }
    }
}

private fun DrawScope.circleCover(study: PlayerStudy, track: Int, center: Offset, radius: Float) {
    val clip = Path().apply { addOval(androidx.compose.ui.geometry.Rect(center, radius)) }
    clipPath(clip) {
        translate(center.x - radius, center.y - radius) {
            scale(radius / 50f, radius / 50f, Offset.Zero) {
                cover(study, track)
            }
        }
    }
}

@Composable
internal fun Waveform(progress: Float, played: Color, unplayed: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val spacing = size.width / 74
        for (i in 0..73) {
            val fraction = i / 73f
            val amplitude = (.16 + abs(sin(i * 2.47) * cos(i * .109)) * .81).toFloat()
            val x = spacing * (i + .5f)
            val height = size.height * amplitude / 2
            drawLine(if (fraction <= progress) played else unplayed,
                Offset(x, center.y - height), Offset(x, center.y + height), spacing * .57f, StrokeCap.Round)
        }
    }
}
