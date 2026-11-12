package com.convx.music.playback.data

import com.convx.music.playback.HistoryPlay
import com.convx.music.playback.PlayHistorySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

/** Counted plays kept in one JSON file; feeds Daily Mixes and Release Radar's most played artists. */
class JsonFilePlayHistory(file: File) {
    private val storage = AtomicTextFile(file)
    private val lock = Mutex()
    private val current = MutableStateFlow<PlayHistorySnapshot?>(null)

    suspend fun read(): PlayHistorySnapshot = lock.withLock { load() }

    suspend fun record(play: HistoryPlay) = lock.withLock {
        val next = load().with(play)
        storage.write(next.toJson())
        current.value = next
    }

    fun observe(): Flow<PlayHistorySnapshot> = flow {
        read()
        emitAll(current.filterNotNull())
    }.distinctUntilChanged()

    private suspend fun load(): PlayHistorySnapshot =
        current.value ?: PlayHistorySnapshot.parse(storage.read()).also { current.value = it }
}
