package com.convx.music.playback

import kotlinx.serialization.Serializable

/** Decisions behind "new releases", free of where artist pages and release dates come from. */
object ReleaseRadarRules {
    @Serializable
    enum class Kind(val label: String) { ALBUM("Album"), SINGLE("Single"), EP("EP") }

    data class KindAndYear(val kind: Kind, val year: Int)

    private val YEAR = Regex("""\b(?:19|20)\d{2}\b""")
    private val BRACKETED = Regex("""\s*[(\[][^)\]]*[)\]]""")
    private val SUFFIX_DASH = Regex("""\s+-\s+(single|ep)$""")
    private val SUFFIX_TAG = Regex("""\s+(ep|single)$""")

    /** "Single • 2026" is a 2026 single; "2024" alone is a 2024 album; "EP · 2025" is a 2025 EP. Year 0 when none is given. */
    fun kindAndYear(subtitle: String): KindAndYear {
        val kind = when (subtitle.split('•', '·').firstOrNull()?.trim()?.lowercase()) {
            "single" -> Kind.SINGLE
            "ep" -> Kind.EP
            else -> Kind.ALBUM
        }
        val year = YEAR.findAll(subtitle).lastOrNull()?.value?.toInt() ?: 0
        return KindAndYear(kind, year)
    }

    /** The kind a section of an artist page lists, from its title: "Singles & EPs" reads as singles, other languages as albums. */
    fun kindOfSection(title: String): Kind {
        val words = title.lowercase().split(Regex("""[^\p{L}\p{N}]+""")).filter(String::isNotEmpty)
        return when {
            "single" in words || "singles" in words -> Kind.SINGLE
            "ep" in words || "eps" in words -> Kind.EP
            else -> Kind.ALBUM
        }
    }

    /**
     * A title to match releases by: lower case, without bracketed parts, an "EP" or "Single" tag, or punctuation.
     * A title that is nothing but a bracketed part keeps its letters so it does not match every other one.
     */
    fun normalize(title: String): String {
        val lower = title.lowercase()
        val stripped = lower.replace(BRACKETED, "").replace(SUFFIX_DASH, "").replace(SUFFIX_TAG, "")
        return stripped.filter(Char::isLetterOrDigit).ifEmpty { lower.filter(Char::isLetterOrDigit) }
    }

    /**
     * Whether a release is new: out within the last [days] days (or announced up to a week ahead), judged by its
     * release date when known and otherwise by the day it was first seen. All values are days since 1970-01-01.
     */
    fun isRecent(releaseEpochDay: Long?, firstSeenEpochDay: Long, todayEpochDay: Long, days: Int): Boolean {
        if (days < 0) return false
        val age = todayEpochDay - (releaseEpochDay ?: firstSeenEpochDay)
        return age in -ANNOUNCED_AHEAD_DAYS..days.toLong()
    }

    /** Days since 1970-01-01 for an ISO `yyyy-MM-dd` date, or null when it is not a real calendar date. */
    fun epochDayOf(isoDate: String): Long? {
        val match = Regex("""(\d{4})-(\d{2})-(\d{2})""").matchEntire(isoDate.trim()) ?: return null
        val (year, month, day) = match.destructured.toList().map(String::toInt)
        if (month !in 1..12 || day !in 1..daysInMonth(year, month)) return null
        val shiftedYear = if (month <= 2) year - 1 else year
        val era = if (shiftedYear >= 0) shiftedYear / 400 else (shiftedYear - 399) / 400
        val yearOfEra = shiftedYear - era * 400
        val dayOfYear = (153 * (month + if (month > 2) -3 else 9) + 2) / 5 + day - 1
        val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
        return era * 146_097L + dayOfEra - 719_468L
    }

    /** The calendar year containing [epochDay] (days since 1970-01-01). */
    fun yearOf(epochDay: Long): Int {
        val shifted = epochDay + 719_468L
        val era = if (shifted >= 0) shifted / 146_097L else (shifted - 146_096L) / 146_097L
        val dayOfEra = shifted - era * 146_097L
        val yearOfEra = (dayOfEra - dayOfEra / 1_460 + dayOfEra / 36_524 - dayOfEra / 146_096) / 365
        val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
        val monthPart = (5 * dayOfYear + 2) / 153
        val year = yearOfEra + era * 400
        return (if (monthPart >= 10) year + 1 else year).toInt()
    }

    private fun daysInMonth(year: Int, month: Int): Int = when (month) {
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }

    private const val ANNOUNCED_AHEAD_DAYS = 7L
}
