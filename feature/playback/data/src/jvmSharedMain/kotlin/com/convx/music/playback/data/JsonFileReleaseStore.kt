package com.convx.music.playback.data

import com.convx.music.playback.ReleaseRadarSnapshot
import com.convx.music.playback.ReleaseStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

/** Keeps Release Radar's snapshot in one JSON file. */
class JsonFileReleaseStore(file: File) : ReleaseStore {
    private val storage = AtomicTextFile(file)
    private val lock = Mutex()
    private val current = MutableStateFlow<ReleaseRadarSnapshot?>(null)

    override suspend fun read(): ReleaseRadarSnapshot = lock.withLock { load() }

    override suspend fun save(snapshot: ReleaseRadarSnapshot) = lock.withLock {
        if (snapshot == current.value) return@withLock
        storage.write(snapshot.toJson())
        current.value = snapshot
    }

    /** The snapshot now and after every save. */
    fun observe(): Flow<ReleaseRadarSnapshot> = flow {
        read()
        emitAll(current.filterNotNull())
    }.distinctUntilChanged()

    private suspend fun load(): ReleaseRadarSnapshot =
        current.value ?: ReleaseRadarSnapshot.parse(storage.read()).also { current.value = it }
}
