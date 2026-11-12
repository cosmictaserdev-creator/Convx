package com.convx.music.playback.data

import com.convx.music.playback.SpeedDialPin
import com.convx.music.playback.SpeedDialSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

/** The pins on Home's Speed Dial, kept in one JSON file. */
class JsonFileSpeedDial(file: File) {
    private val storage = AtomicTextFile(file)
    private val lock = Mutex()
    private val current = MutableStateFlow<SpeedDialSnapshot?>(null)

    suspend fun read(): SpeedDialSnapshot = lock.withLock { load() }

    suspend fun toggle(pin: SpeedDialPin) = lock.withLock {
        val next = load().toggled(pin)
        storage.write(next.toJson())
        current.value = next
    }

    fun observe(): Flow<SpeedDialSnapshot> = flow {
        read()
        emitAll(current.filterNotNull())
    }.distinctUntilChanged()

    private suspend fun load(): SpeedDialSnapshot =
        current.value ?: SpeedDialSnapshot.parse(storage.read()).also { current.value = it }
}
