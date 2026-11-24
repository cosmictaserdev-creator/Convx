/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import androidx.compose.runtime.Immutable
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.pages.HistoryPage
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.HistorySource
import com.convx.music.constants.InnerTubeCookieKey
import com.music.innertube.utils.parseCookieString
import com.convx.music.history.HistoryLocalDataSource
import com.convx.music.utils.dataStore
import com.convx.music.utils.reportException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.convx.music.db.entities.EventWithSong
import com.convx.music.history.HistoryDate
import com.convx.music.history.HistoryRecord
import com.convx.music.history.HistorySection
import com.convx.music.history.groupHistoryRecords

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    private val context: Context,
    private val historyDataSource: HistoryLocalDataSource,
) : ViewModel() {
    var historySource = MutableStateFlow(HistorySource.LOCAL)

    private val today = LocalDate.now()

    val historyPage = MutableStateFlow<HistoryPage?>(null)

    val searchQuery = MutableStateFlow("")
    private val _events = MutableStateFlow<Map<DateAgo, List<EventWithSong>>>(emptyMap())
    val filteredEvents = _events.asStateFlow()
    private val _flatEvents = MutableStateFlow<List<EventWithSong>>(emptyList())
    val filteredFlatEvents = _flatEvents.asStateFlow()
    private var pageOffset = 0
    private var reachedEnd = false
    private var hideVideoSongs = false
    private var localLoadJob: kotlinx.coroutines.Job? = null

    private fun groupEvents(events: List<EventWithSong>): Map<DateAgo, List<EventWithSong>> {
        val grouped = groupHistoryRecords(
            records = events.map { item ->
                val date = item.event.timestamp.toLocalDate()
                HistoryRecord(
                    eventId = item.event.id,
                    songId = item.song.id,
                    date = HistoryDate(date.year, date.monthValue, date.dayOfMonth),
                    value = item,
                )
            },
            today = HistoryDate(today.year, today.monthValue, today.dayOfMonth),
        )

        return grouped.associateTo(linkedMapOf()) { group ->
            val section = when (val key = group.section) {
                HistorySection.Today -> DateAgo.Today
                HistorySection.Yesterday -> DateAgo.Yesterday
                HistorySection.ThisWeek -> DateAgo.ThisWeek
                HistorySection.LastWeek -> DateAgo.LastWeek
                is HistorySection.Month -> DateAgo.Other(LocalDate.of(key.year, key.month, 1))
            }
            section to group.records.map { it.value }
        }
    }

    fun loadMoreEvents() {
        if (reachedEnd || localLoadJob?.isActive == true) return
        localLoadJob = viewModelScope.launch {
            val page = historyDataSource.loadEvents(PAGE_SIZE, pageOffset, hideVideoSongs, searchQuery.value)
            pageOffset += page.size
            reachedEnd = page.size < PAGE_SIZE
            if (page.isNotEmpty()) {
                val combined = (_flatEvents.value + page).distinctBy { it.event.id }
                _flatEvents.value = combined
                _events.value = groupEvents(combined)
            }
        }
    }

    private fun resetLocalHistory(hideVideos: Boolean, query: String) {
        localLoadJob?.cancel()
        hideVideoSongs = hideVideos
        pageOffset = 0
        reachedEnd = false
        _flatEvents.value = emptyList()
        _events.value = emptyMap()
        localLoadJob = viewModelScope.launch {
            val page = historyDataSource.loadEvents(PAGE_SIZE, pageOffset, hideVideoSongs, query)
            pageOffset = page.size
            reachedEnd = page.size < PAGE_SIZE
            _flatEvents.value = page
            _events.value = groupEvents(page)
        }
    }

    val filteredRemoteContent = combine(historyPage, searchQuery) { page, queryText ->
        if (queryText.isEmpty()) {
            page?.sections
        } else {
            page?.sections?.map { section ->
                section.copy(
                    songs = section.songs.filter { song ->
                        song.title.contains(queryText, ignoreCase = true) ||
                                song.artists.any { it.name.contains(queryText, ignoreCase = true) }
                    }
                )
            }?.filter { it.songs.isNotEmpty() }
        }
    }.flowOn(Dispatchers.Default)
     .stateIn(viewModelScope, SharingStarted.Eagerly, null)


    init {
        viewModelScope.launch {
            val hideVideos = context.dataStore.data
                .map { (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false) }
                .distinctUntilChanged()
            combine(hideVideos, searchQuery) { hide, query -> hide to query }
                .distinctUntilChanged()
                .collect { (hide, query) -> resetLocalHistory(hide, query) }
        }
        fetchRemoteHistory()
        // Auto-clear remote history when user logs out
        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.data
                .map { it[InnerTubeCookieKey] ?: "" }
                .distinctUntilChanged()
                .collect { cookie ->
                    if ("SAPISID" !in parseCookieString(cookie)) {
                        historyPage.value = null
                        historySource.value = HistorySource.LOCAL
                    }
                }
        }
    }

    fun fetchRemoteHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            YouTube.musicHistory().onSuccess {
                historyPage.value = it
            }.onFailure {
                reportException(it)
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 50
    }
}

@Immutable
sealed class DateAgo {
    @Immutable
    data object Today : DateAgo()

    @Immutable
    data object Yesterday : DateAgo()

    @Immutable
    data object ThisWeek : DateAgo()

    @Immutable
    data object LastWeek : DateAgo()

    @Immutable
    class Other(
        val date: LocalDate,
    ) : DateAgo() {
        override fun equals(other: Any?): Boolean {
            if (other is Other) return date == other.date
            return super.equals(other)
        }

        override fun hashCode(): Int = date.hashCode()
    }
}
