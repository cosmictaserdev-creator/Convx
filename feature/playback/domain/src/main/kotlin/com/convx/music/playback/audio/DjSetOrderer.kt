/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.audio

import com.convx.music.playback.dj.BeatGrid
import com.convx.music.playback.dj.MusicalKey
import kotlin.math.abs

object DjSetOrderer {
    private val NoAnalysis = BeatGrid.NONE to MusicalKey.NONE

    fun order(candidateIds: List<String>, analyses: Map<String, Pair<BeatGrid, MusicalKey>>): List<String> {
        if (candidateIds.size <= 2) return candidateIds

        val remaining = candidateIds.toMutableList()
        val ordered = ArrayList<String>(candidateIds.size)
        ordered += remaining.removeAt(0)

        while (remaining.isNotEmpty()) {
            val (lastGrid, lastKey) = analyses[ordered.last()] ?: NoAnalysis
            var bestIndex = 0
            var bestScore = Double.NEGATIVE_INFINITY
            for (index in remaining.indices) {
                val (grid, key) = analyses[remaining[index]] ?: NoAnalysis
                val plan = DjMixPlanner.plan(lastGrid, grid, lastKey, key)
                val bpmDelta = if (lastGrid.bpm > 0f && grid.bpm > 0f) abs(lastGrid.bpm - grid.bpm) else 1000f
                val score = -plan.tier.ordinal * 10_000.0 - bpmDelta
                if (score > bestScore) {
                    bestScore = score
                    bestIndex = index
                }
            }
            ordered += remaining.removeAt(bestIndex)
        }
        return ordered
    }
}
