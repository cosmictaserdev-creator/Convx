package com.music.innertube

import kotlin.random.Random
import kotlin.time.Clock

internal object RetryPolicy {
    const val MAX_INLINE_WAIT_MS = 15_000L

    fun retryAfterMillis(value: String?, nowMillis: Long = Clock.System.now().toEpochMilliseconds()): Long? {
        val header = value?.trim() ?: return null
        header.toLongOrNull()?.let {
            return if (it < 0) null else it.coerceAtMost(Long.MAX_VALUE / 1000) * 1000
        }
        val tokens = header.split(' ')
        if (tokens.size != 6 || tokens[5] != "GMT") return null
        val month = Months.indexOf(tokens[2]) + 1
        val day = tokens[1].toIntOrNull() ?: return null
        val year = tokens[3].toIntOrNull() ?: return null
        val time = tokens[4].split(':').map { it.toIntOrNull() ?: return null }
        if (month == 0 || day !in 1..31 || time.size != 3 || time[0] !in 0..23 || time[1] !in 0..59 || time[2] !in 0..60) return null
        val targetMillis = daysFromCivil(year, month, day) * MillisPerDay +
            time[0] * 3_600_000L + time[1] * 60_000L + time[2] * 1_000L
        return (targetMillis - nowMillis).coerceAtLeast(0)
    }

    fun jitter(baseMillis: Long): Long = baseMillis / 2 + Random.nextLong(baseMillis / 2 + 1)

    private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
        var adjustedYear = year.toLong()
        if (month <= 2) adjustedYear--
        val era = adjustedYear / 400
        val yearOfEra = adjustedYear - era * 400
        val adjustedMonth = month + if (month > 2) -3 else 9
        val dayOfYear = (153L * adjustedMonth + 2) / 5 + day - 1
        val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
        return era * 146_097 + dayOfEra - 719_468
    }

    private const val MillisPerDay = 86_400_000L
    private val Months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
}

/** The server's cooldown is still active; callers should wait before requesting again. */
class InnerTubeRateLimitException(val retryAfterMillis: Long) :
    Exception("YouTube rate limit active; retry after $retryAfterMillis ms")
