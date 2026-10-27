package com.convx.storage.domain

import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/** Library traversal and metadata rules are identical regardless of protocol or host. */
class NetworkLibraryScanner(private val listFolder: suspend (String) -> List<NetworkEntry>) {
    suspend fun scan(serverId: String, maxTracks: Int = 10_000): NetworkLibrary {
        require(maxTracks in 1..100_000)
        val files = LinkedHashMap<String, NetworkEntry>()
        val images = LinkedHashMap<String, NetworkEntry>()
        val visited = HashSet<String>()
        val queued = hashSetOf("")
        val folders = ArrayDeque<String>().apply { add("") }
        var truncated = false
        while (folders.isNotEmpty()) {
            coroutineContext.ensureActive()
            val folder = folders.removeFirst()
            if (!visited.add(folder)) continue
            if (visited.size > 10_000) { truncated = true; break }
            for (entry in listFolder(folder)) {
                coroutineContext.ensureActive()
                when {
                    entry.directory -> {
                        val child = RemotePath.normalize(entry.path)
                        if (queued.add(child)) folders.add(child)
                    }
                    NetworkMedia.isAudio(entry.name) && entry.path !in files -> {
                        if (files.size >= maxTracks) { truncated = true; break }
                        files[entry.path] = entry
                    }
                    NetworkMedia.isImage(entry.name) -> if (images.size < 10_000) images[entry.path] = entry
                }
            }
            if (truncated) break
        }
        val byFolder = images.values.groupBy { RemotePath.parent(it.path) }
        return NetworkLibrary(files.values.map {
            NetworkMedia.track(serverId, it, byFolder[RemotePath.parent(it.path)].orEmpty())
        }, truncated)
    }
}
