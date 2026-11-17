package com.convx.music.history

import kotlin.test.Test
import kotlin.test.assertEquals

class HistoryGroupingCommonTest {
    @Test
    fun groupsLocalDatesIntoTodayYesterdayWeeksAndMonthsInDisplayOrder() {
        val groups = groupHistoryRecords(
            records = listOf(
                record(1, "month", 2025, 12, 31),
                record(2, "last-week", 2026, 1, 12),
                record(3, "this-week", 2026, 1, 20),
                record(4, "yesterday", 2026, 1, 18),
                record(5, "today", 2026, 1, 19),
            ),
            today = HistoryDate(2026, 1, 19),
        )

        assertEquals(
            listOf(
                HistorySection.Today,
                HistorySection.Yesterday,
                HistorySection.ThisWeek,
                HistorySection.LastWeek,
                HistorySection.Month(2025, 12),
            ),
            groups.map { it.section },
        )
    }

    @Test
    fun deduplicatesSongsWithinEachSectionAndHandlesLeapDay() {
        val groups = groupHistoryRecords(
            records = listOf(
                record(1, "same-song", 2024, 2, 29),
                record(2, "same-song", 2024, 2, 28),
                record(3, "other-song", 2024, 2, 28),
            ),
            today = HistoryDate(2024, 3, 1),
        )

        assertEquals(listOf(HistorySection.Yesterday, HistorySection.ThisWeek), groups.map { it.section })
        assertEquals(listOf(1L), groups.first().records.map { it.eventId })
        assertEquals(listOf(2L, 3L), groups.last().records.map { it.eventId })
    }

    @Test
    fun validatesDateComponents() {
        val invalidDate = runCatching { HistoryDate(2025, 2, 29) }

        assertEquals(true, invalidDate.isFailure)
    }

    private fun record(id: Long, songId: String, year: Int, month: Int, day: Int) =
        HistoryRecord(id, songId, HistoryDate(year, month, day), "event-$id")
}
