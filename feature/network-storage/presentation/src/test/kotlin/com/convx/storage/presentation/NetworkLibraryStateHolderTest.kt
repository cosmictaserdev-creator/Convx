@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.convx.storage.presentation

import com.convx.storage.domain.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class NetworkLibraryStateHolderTest {
    @Test fun lateResultsFromAnOldFolderCannotReplaceTheNewSelection() = runTest {
        val oldResponse = CompletableDeferred<Unit>()
        val repository = object : TestNetworkLibrary() {
            override suspend fun browse(id: String, folder: String): List<NetworkEntry> {
                if (folder == "old") withContext(NonCancellable) { oldResponse.await() }
                return listOf(NetworkEntry("$folder/song.mp3", "$folder.mp3", false))
            }
        }
        val holder = NetworkLibraryStateHolder(repository, this)
        try {
            holder.load(); runCurrent()
            holder.browse("nas", "old"); runCurrent()
            holder.browse("nas", "new"); runCurrent()
            oldResponse.complete(Unit); runCurrent()
            assertEquals("new", holder.state.value.folder)
            assertEquals("new/song.mp3", NetworkMediaUri.parse(holder.state.value.tracks.single().id)?.path)
            assertFalse(holder.state.value.busy)
        } finally { oldResponse.complete(Unit); holder.close() }
    }

    @Test fun deletingTheCurrentSourceClearsItsTracksAndSelection() = runTest {
        val repository = TestNetworkLibrary()
        val holder = NetworkLibraryStateHolder(repository, this)
        try {
            holder.load(); runCurrent()
            holder.browse("nas"); runCurrent()
            holder.remove("nas"); runCurrent()
            assertNull(holder.state.value.selectedId)
            assertTrue(holder.state.value.tracks.isEmpty())
            assertTrue(holder.state.value.sources.isEmpty())
        } finally { holder.close() }
    }
}
