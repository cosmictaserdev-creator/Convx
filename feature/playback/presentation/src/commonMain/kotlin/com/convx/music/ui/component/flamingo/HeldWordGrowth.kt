package com.convx.music.ui.component.flamingo

/**
 * BitChord's held-note growth. Only a word carried long enough earns it; each letter then swells,
 * lifts and leans outward in turn, so the word opens up as the singer holds it rather than the
 * whole line twitching on every syllable. Ordinary words don't swell at all.
 */
class HeldWordGrowth(private val startMs: Float, private val endMs: Float, chars: Int) {
    private val scalePeak = FloatArray(chars)
    private val shiftPeak = FloatArray(chars)
    private val risePeak = FloatArray(chars)

    init {
        val held = (endMs - startMs).coerceAtLeast(1f)
        // Cubed, so a word held for one second barely moves and one held for three does fully.
        val earned = ((held - RAMP_MIN_MS) / (RAMP_MAX_MS - RAMP_MIN_MS)).coerceIn(0f, 1f).let { it * it * it }
        val decay = decayRate(chars, held)
        val base = if (chars <= 3) BASE_SHORT else BASE_LONG
        val liftPace = (held / LIFT_PACE_MS).coerceIn(LIFT_FLOOR, 1f)
        for (i in 0 until chars) {
            val place = if (chars > 1) i.toFloat() / (chars - 1) else 0f
            val scale = 1f + base + earned * (1f - place * decay) * SCALE_RANGE
            scalePeak[i] = scale * SCALE_TRIM
            // Rise follows the swell, so the two read as one movement.
            risePeak[i] = ((scale - 1f) / SCALE_CEILING) * liftPace
            val centre = (i + 0.5f) / chars
            shiftPeak[i] = (centre - 0.5f) * 2f * (scale - 1f) * SHIFT_EM * SCALE_TRIM
        }
    }

    /** Fills [into] with letter [charIndex]'s swell, lean (ems) and lift (in word rises) at [positionMs]. */
    fun sample(charIndex: Int, positionMs: Float, into: CharGrowth) {
        if (charIndex !in scalePeak.indices || !positionMs.isFinite()) { into.set(1f, 0f, 0f); return }
        val span = (endMs - startMs).coerceAtLeast(1f)
        val phase = ((positionMs - startMs - charIndex * span * STAGGER) / (span * SPAN)).coerceIn(0f, 1f)
        val peak = scalePeak[charIndex]
        when {
            phase < IN -> {
                val t = smooth(phase / IN)
                into.set(1f + (peak - 1f) * t, shiftPeak[charIndex] * t, risePeak[charIndex] * t)
            }
            phase < HOLD -> into.set(peak, shiftPeak[charIndex], risePeak[charIndex])
            phase < OUT -> {
                val t = smooth((phase - HOLD) / (OUT - HOLD))
                into.set(peak + (1f - peak) * t, shiftPeak[charIndex] * (1f - t), risePeak[charIndex] + (REST - risePeak[charIndex]) * t)
            }
            else -> into.set(1f, 0f, REST)
        }
    }

    companion object {
        /** Whether [text], held for [heldMs], has earned the letter-by-letter treatment. */
        fun canGrow(text: String, heldMs: Float): Boolean {
            if (!heldMs.isFinite() || heldMs <= 0f || text.any { it.isSurrogate() }) return false
            val length = text.length
            if (length == 0 || length > MAX_CHARS) return false
            if ('-' in text || text.any { it.isBlockScript() || it.isJoinedScript() }) return false
            return when {
                length == 1 -> heldMs >= MIN_SOLO_MS
                length <= 3 -> heldMs >= MIN_SHORT_MS + (length - 2) * SHORT_STEP_MS
                length == 4 -> heldMs >= MIN_FOUR_MS
                else -> heldMs >= MIN_LONG_MS && heldMs >= length * MS_PER_CHAR
            }
        }

        private fun decayRate(length: Int, heldMs: Float): Float {
            val long = length > DECAY_LONG_CHARS
            val quick = heldMs < DECAY_QUICK_MS
            if (!long && !quick) return 0f
            var strength = 0f
            if (long) strength += minOf((length - DECAY_LONG_CHARS) / 5f, 1f) * DECAY_LONG
            if (quick) {
                val short = maxOf(0f, 1f - (heldMs - DECAY_QUICK_FLOOR_MS) / 400f)
                strength += short * if (length > 3) DECAY_QUICK else DECAY_QUICK_TINY
            }
            return minOf(strength, DECAY_MAX)
        }

        private fun smooth(f: Float) = f * f * (3f - 2f * f)
        private fun Char.isBlockScript() = this in '一'..'鿿' || this in '぀'..'ゟ' || this in '゠'..'ヿ' || this in '가'..'힯'
        private fun Char.isJoinedScript() = this in '֐'..'ࣿ'

        private const val MAX_CHARS = 7
        private const val MIN_SOLO_MS = 1_100f
        private const val MIN_SHORT_MS = 1_360f
        private const val SHORT_STEP_MS = 140f
        private const val MIN_FOUR_MS = 1_050f
        private const val MIN_LONG_MS = 900f
        private const val MS_PER_CHAR = 200f
        private const val DECAY_LONG_CHARS = 5
        private const val DECAY_QUICK_MS = 1_200f
        private const val DECAY_QUICK_FLOOR_MS = 800f
        private const val DECAY_LONG = 0.4f
        private const val DECAY_QUICK = 0.3f
        private const val DECAY_QUICK_TINY = 0.1f
        private const val DECAY_MAX = 0.7f
        private const val STAGGER = 0.09f
        private const val SPAN = 1.5f
        private const val IN = 0.25f
        private const val HOLD = 0.30f
        private const val OUT = 0.75f
        private const val REST = 1f
        private const val RAMP_MIN_MS = 400f
        private const val RAMP_MAX_MS = 3_000f
        private const val BASE_SHORT = 0.05f
        private const val BASE_LONG = 0.04f
        private const val SCALE_RANGE = 0.08f
        private const val SCALE_CEILING = 0.1f
        private const val SCALE_TRIM = 0.98f
        private const val SHIFT_EM = 25f / 34f
        private const val LIFT_PACE_MS = 2_000f
        private const val LIFT_FLOOR = 0.3f
    }
}

/** One letter's state, reused across frames to avoid allocating per draw. */
class CharGrowth {
    var scale = 1f
    var shift = 0f
    var rise = 0f

    fun set(scale: Float, shift: Float, rise: Float) {
        this.scale = scale
        this.shift = shift
        this.rise = rise
    }
}
