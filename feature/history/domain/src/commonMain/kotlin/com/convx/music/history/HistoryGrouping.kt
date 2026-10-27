package com.convx.music.history

/** A local calendar date, without tying history policy to a platform date library. */
data class HistoryDate(
    val year: Int,
    val month: Int,
    val day: Int,
) {
    init {
        require(year >= 1) { "year must be positive" }
        require(month in 1..12) { "month must be between 1 and 12" }
        require(day in 1..daysInMonth(year, month)) { "day is outside the month" }
    }

    internal fun epochDay(): Long {
        val adjustedYear = year - if (month <= 2) 1 else 0
        val era = adjustedYear / 400
        val yearOfEra = adjustedYear - era * 400
        val adjustedMonth = month + if (month > 2) -3 else 9
        val dayOfYear = (153 * adjustedMonth + 2) / 5 + day - 1
        val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
        return era.toLong() * 146_097 + dayOfEra - 719_468
    }

    private companion object {
        fun daysInMonth(year: Int, month: Int): Int = when (month) {
            2 -> if (isLeapYear(year)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }

        fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }
}

sealed interface HistorySection {
    data object Today : HistorySection
    data object Yesterday : HistorySection
    data object ThisWeek : HistorySection
    data object LastWeek : HistorySection
    data class Month(val year: Int, val month: Int) : HistorySection
}

/** A portable wrapper around a client-specific history row. */
data class HistoryRecord<T>(
    val eventId: Long,
    val songId: String,
    val date: HistoryDate,
    val value: T,
)

data class HistoryGroup<T>(
    val section: HistorySection,
    val records: List<HistoryRecord<T>>,
)

/** Groups by local calendar date and orders sections the same way as Android history. */
fun <T> groupHistoryRecords(
    records: List<HistoryRecord<T>>,
    today: HistoryDate,
): List<HistoryGroup<T>> {
    val todayEpochDay = today.epochDay()
    val thisMondayEpochDay = todayEpochDay - mondayOffset(todayEpochDay)
    val lastMondayEpochDay = thisMondayEpochDay - 7
    val grouped = linkedMapOf<HistorySection, MutableList<HistoryRecord<T>>>()

    records.forEach { record ->
        val recordEpochDay = record.date.epochDay()
        val daysAgo = todayEpochDay - recordEpochDay
        val section = when {
            daysAgo == 0L -> HistorySection.Today
            daysAgo == 1L -> HistorySection.Yesterday
            recordEpochDay >= thisMondayEpochDay -> HistorySection.ThisWeek
            recordEpochDay >= lastMondayEpochDay -> HistorySection.LastWeek
            else -> HistorySection.Month(record.date.year, record.date.month)
        }
        grouped.getOrPut(section) { mutableListOf() }.add(record)
    }

    return grouped.map { (section, entries) ->
        HistoryGroup(section, entries.distinctBy { it.songId })
    }.sortedBy { group ->
        when (val section = group.section) {
            HistorySection.Today -> 0L
            HistorySection.Yesterday -> 1L
            HistorySection.ThisWeek -> 2L
            HistorySection.LastWeek -> 3L
            is HistorySection.Month -> todayEpochDay - HistoryDate(section.year, section.month, 1).epochDay()
        }
    }
}

private fun mondayOffset(epochDay: Long): Long {
    val weekdayFromThursday = (epochDay + 3) % 7
    return (weekdayFromThursday + 7) % 7
}
