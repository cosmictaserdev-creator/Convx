/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Multi-singer lyrics UI: deterministic per-singer colors and a Liquid Glass
 * "who is singing" badge rendered above the active line when the lead vocalist
 * changes (Apple Music-style duet support).
 */

package com.convxy.music.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convxy.music.lyrics.GROUP_AGENT_ID
import com.convxy.music.lyrics.LyricsEntry
import com.convxy.music.lyrics.SingerInfo
import com.convxy.music.lyrics.primaryAgentId
import com.convxy.music.ui.screens.settings.LyricsPosition

/**
 * Assigns a stable, distinguishable color to every lead vocalist of a song.
 *
 * Colors are iOS system accents, ordered by the singer's first appearance in
 * the lyrics, so they harmonize with the Apple Music aesthetic on any player
 * background. Shared-vocal (group) lines, background vocals, unknown ids and
 * songs with fewer than two lead voices intentionally receive no entry, letting
 * callers fall back to the global [Color] accent — which keeps single-singer
 * songs pixel-identical to the pre-multi-singer rendering.
 */
object SingerPalette {

    val LEAD_SINGER_COLORS = listOf(
        Color(0xFFFF375F), // systemPink
        Color(0xFF0A84FF), // systemBlue
        Color(0xFFFFD60A), // systemYellow
        Color(0xFFBF5AF2), // systemPurple
        Color(0xFF30D158), // systemGreen
        Color(0xFFFF9F0A), // systemOrange
        Color(0xFF64D2FF), // systemCyan
        Color(0xFF5E5CE6), // systemIndigo
    )

    /**
     * Returns `agentId -> color` for the lead vocalists of [entries].
     * Empty when the song does not switch between at least two lead voices.
     */
    fun assignColors(entries: List<LyricsEntry>): Map<String, Color> {
        val leadOrder = LinkedHashSet<String>()
        entries.forEach { entry ->
            val agent = entry.agent ?: return@forEach
            if (entry.isBackground) return@forEach
            leadOrder.add(primaryAgentId(agent))
        }
        if (leadOrder.size < 2) return emptyMap()

        val colors = HashMap<String, Color>(leadOrder.size)
        var index = 0
        leadOrder.forEach { agentId ->
            if (agentId != GROUP_AGENT_ID) {
                colors[agentId] = LEAD_SINGER_COLORS[index % LEAD_SINGER_COLORS.size]
                index++
            }
        }
        return colors
    }
}

/**
 * The singer attribution shown for a lyric line.
 *
 * @param id canonical agent id ("v1", "v2", "v1000", …).
 * @param name resolved display name, or null when unknown (callers may hide
 * the badge or substitute a localized label for shared vocals).
 * @param isGroup true when the line belongs to shared vocals.
 */
data class SingerDisplay(
    val id: String,
    val name: String?,
    val isGroup: Boolean,
)

/**
 * Resolves the singer attribution of every lead voice of a song in one pass.
 *
 * Name sources, in order of trust:
 *  1. the song's singer registry (Apple TTML `ttm:name` / `[singers:…]` header),
 *  2. vocal-dominance inference against the track's artist list,
 *  3. nothing (null name) — the UI then falls back to a localized label for
 *     shared vocals or hides the badge entirely for unknown voices.
 *
 * Apple's `v1`/`v2` voice ids are *not* artist indices, so a positional
 * fallback (`v2` -> second artist) mislabels duets whenever a featured artist
 * sings first or a solo-artist track carries two detected voices. Instead the
 * voices are matched to the credited artists by vocal dominance: the
 * first-billed artist typically sings the most lines, so track artists are
 * assigned to the unnamed lead voices in descending order of led lines. The
 * inference only runs when the shape of the data is believable (at least two
 * credited artists and no more lead voices than artists); otherwise the
 * voices keep their colors but show no name — a missing name is always better
 * than a wrong one.
 */
fun resolveSingerDisplays(
    entries: List<LyricsEntry>,
    singers: Map<String, SingerInfo>,
    trackArtists: List<String>,
): Map<String, SingerDisplay> {
    val leadCounts = LinkedHashMap<String, Int>()
    entries.forEach { entry ->
        if (entry.isBackground) return@forEach
        val agent = entry.agent ?: return@forEach
        val id = primaryAgentId(agent)
        leadCounts[id] = (leadCounts[id] ?: 0) + 1
    }
    if (leadCounts.size < 2) return emptyMap()

    val creditedArtists = trackArtists.map { it.trim() }.filter { it.isNotEmpty() }

    // 1. Keep registry names only when they look like a real person, then
    //    rewrite them to the matching track-artist spelling when we can.
    val registryNames = HashMap<String, String>()
    leadCounts.keys.forEach { id ->
        val raw = usableSingerName(singers[id]?.name) ?: return@forEach
        val matched = creditedArtists.firstOrNull { namesLikelySame(it, raw) }
        registryNames[id] = matched ?: raw
    }

    // 2. Remaining unnamed voices: match leftover artists by vocal dominance
    //    only when the shape is unambiguous (clear majority + 1:1 leftover
    //    count). A missing name is always better than swapping two singers.
    val unnamedLeads = leadCounts.keys.filter { it !in registryNames && it != GROUP_AGENT_ID }
    val claimed = registryNames.values.map { normalizePersonName(it) }.toSet()
    val availableArtists = creditedArtists.filter { normalizePersonName(it) !in claimed }
    val inferredNames = HashMap<String, String>()
    val ranked = unnamedLeads.sortedByDescending { leadCounts[it] ?: 0 }
    val top = leadCounts[ranked.firstOrNull()] ?: 0
    val second = leadCounts[ranked.getOrNull(1)] ?: 0
    val clearMajority = ranked.size == 1 || top >= second * 3 / 2 + 1
    val plausible = unnamedLeads.isNotEmpty() &&
        availableArtists.size >= unnamedLeads.size &&
        availableArtists.size >= 2 &&
        clearMajority
    if (plausible) {
        ranked.forEachIndexed { rank, id ->
            if (rank < availableArtists.size) inferredNames[id] = availableArtists[rank]
        }
    }

    return leadCounts.keys.associateWith { id ->
        val isGroup = id == GROUP_AGENT_ID || singers[id]?.isGroup == true
        SingerDisplay(id, registryNames[id] ?: inferredNames[id], isGroup)
    }
}

/** Role / placeholder labels Apple sometimes ships instead of a real name. */
private val GENERIC_SINGER_NAME = Regex(
    """^(v\d+|voice\s*\d*|singer\s*\d*|vocal(ist)?\s*\d*|male|female|background|bgv?|both|all|group|duet|chorus|choir|lead|harmony)$""",
    RegexOption.IGNORE_CASE,
)

internal fun usableSingerName(raw: String?): String? {
    val name = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    if (GENERIC_SINGER_NAME.matches(name)) return null
    return name
}

internal fun normalizePersonName(name: String): String =
    name.lowercase()
        .replace(Regex("""[^\p{L}\p{N}]+"""), " ")
        .trim()

internal fun namesLikelySame(a: String, b: String): Boolean {
    val left = normalizePersonName(a)
    val right = normalizePersonName(b)
    if (left.isEmpty() || right.isEmpty()) return false
    if (left == right) return true
    if (left.contains(right) || right.contains(left)) return true
    val leftTokens = left.split(' ').filter { it.length > 1 }.toSet()
    val rightTokens = right.split(' ').filter { it.length > 1 }.toSet()
    return leftTokens.isNotEmpty() && rightTokens.isNotEmpty() &&
        leftTokens.intersect(rightTokens).isNotEmpty()
}

/**
 * Liquid Glass pill identifying the current vocalist: a colored dot plus the
 * singer's name, tinted with the singer's palette color on a translucent
 * surface so it stays legible over every player background style.
 */
@Composable
fun SingerBadge(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .border(
                width = 0.5.dp,
                color = color.copy(alpha = 0.45f),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(50))
                .background(color),
        )
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
            color = color,
            maxLines = 1,
        )
    }
}

/**
 * Animated wrapper for [SingerBadge]: fades/expands in when the lead vocalist
 * becomes active and collapses away otherwise, matching the timing of the
 * existing line-change animations so singer transitions never flicker.
 */
@Composable
fun AnimatedSingerBadge(
    visible: Boolean,
    name: String?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible && name != null,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
            expandVertically(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 200)) +
            shrinkVertically(animationSpec = tween(durationMillis = 200)),
        modifier = modifier,
    ) {
        if (name != null) {
            SingerBadge(
                name = name,
                color = color,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
    }
}

/**
 * Horizontal alignment for a lyric line in Apple Music duet layout:
 * background vocals always centered, shared/group agents (v1000, composites)
 * centered, lead vocalists alternate sides (v1 left, v2 right, v3 left, …),
 * and lines without agent metadata honor the user's text-position preference.
 *
 * Shared by the standard, Vivi Music and Metro lyric renderers so alignment
 * behavior stays identical across styles.
 */
fun singerLineAlignment(
    agent: String?,
    isBackground: Boolean,
    lyricsTextPosition: LyricsPosition,
): Alignment.Horizontal =
    when {
        isBackground -> Alignment.CenterHorizontally
        agent != null -> when (val id = primaryAgentId(agent)) {
            GROUP_AGENT_ID -> Alignment.CenterHorizontally
            else -> when (id.removePrefix("v").toIntOrNull()?.rem(2)) {
                1 -> Alignment.Start
                0 -> Alignment.End
                else -> lyricsTextPosition.toAlignment()
            }
        }
        else -> lyricsTextPosition.toAlignment()
    }

/** [singerLineAlignment] for [TextAlign] (used by the text/FlowRow layers). */
fun singerTextAlign(
    agent: String?,
    isBackground: Boolean,
    lyricsTextPosition: LyricsPosition,
): TextAlign =
    when {
        isBackground -> TextAlign.Center
        agent != null -> when (val id = primaryAgentId(agent)) {
            GROUP_AGENT_ID -> TextAlign.Center
            else -> when (id.removePrefix("v").toIntOrNull()?.rem(2)) {
                1 -> TextAlign.Left
                0 -> TextAlign.Right
                else -> lyricsTextPosition.toTextAlign()
            }
        }
        else -> lyricsTextPosition.toTextAlign()
    }

private fun LyricsPosition.toAlignment(): Alignment.Horizontal = when (this) {
    LyricsPosition.LEFT -> Alignment.Start
    LyricsPosition.CENTER -> Alignment.CenterHorizontally
    LyricsPosition.RIGHT -> Alignment.End
}

private fun LyricsPosition.toTextAlign(): TextAlign = when (this) {
    LyricsPosition.LEFT -> TextAlign.Left
    LyricsPosition.CENTER -> TextAlign.Center
    LyricsPosition.RIGHT -> TextAlign.Right
}
