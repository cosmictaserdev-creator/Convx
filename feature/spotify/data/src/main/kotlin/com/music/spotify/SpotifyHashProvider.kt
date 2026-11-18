package com.music.spotify

import java.util.concurrent.ConcurrentHashMap
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object SpotifyHashProvider {

    enum class HashSource { HARDCODED, CACHED, REMOTE }

    data class GqlHashEntry(
        val hash: String,
        val previousHash: String? = null,
        val source: HashSource = HashSource.HARDCODED,
    )

    private val hashes = ConcurrentHashMap<String, GqlHashEntry>()
    private val refreshMutex = Mutex()
    private var refreshedAt = 0L

    suspend fun refreshFromWebPlayer() = refreshMutex.withLock {
        if (System.currentTimeMillis() - refreshedAt < 60_000L) return@withLock
        withContext(Dispatchers.IO) {
            fun get(url: String): String {
                val connection = URL(url).openConnection() as HttpURLConnection
                try {
                    connection.connectTimeout = 10_000
                    connection.readTimeout = 15_000
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 Chrome/140.0.0.0 Safari/537.36")
                    return connection.inputStream.bufferedReader().use { it.readText() }
                } finally { connection.disconnect() }
            }
            val page = get("https://open.spotify.com/")
            val bundle = Regex("""https://open\.spotifycdn\.com/cdn/build/web-player/web-player\.[a-f0-9]+\.js""")
                .find(page)?.value ?: return@withContext
            val script = get(bundle)
            val discovered = hashes.keys.mapNotNull { operation ->
                val value = Regex("""["']${Regex.escape(operation)}["']\s*,\s*["']query["']\s*,\s*["']([a-f0-9]{64})["']""")
                    .find(script)?.groupValues?.get(1) ?: return@mapNotNull null
                operation to RemoteHashEntry(value, hashes[operation]?.hash)
            }.toMap()
            updateHashes(discovered, HashSource.REMOTE)
            refreshedAt = System.currentTimeMillis()
        }
    }

    init {
        loadHardcodedDefaults()
    }

    private fun loadHardcodedDefaults() {
        val defaults = mapOf(
            "profileAttributes" to "08ffb4730af3746e04a8301396f20875dbbce10c75243803091a9274eacc8ac0",
            "libraryV3" to "390c78e5b951029bad359785e69b07b536a509c581cbcd0aded5e5067f187455",
            "fetchPlaylist" to "21ea11a6ac75a5f3e01fd365d29c18e0a4a8b7dc09f89bbb5d73910190c6acaf",
            "fetchLibraryTracks" to "087278b20b743578a6262c2b0b4bcd20d879c503cc359a2285baf083ef944240",
            "home" to "76243c78b0e20ecdbe41b794dec8cbe73f75e585b0a7201b8d2e84578412847a",
            "searchDesktop" to "4801118d4a100f756e833d33984436a3899cff359c532f8fd3aaf174b60b3b49"
        )
        defaults.forEach { (op, hash) ->
            hashes[op] = GqlHashEntry(hash = hash, source = HashSource.HARDCODED)
        }
    }

    fun getHash(operationName: String): String =
        hashes[operationName]?.hash
            ?: error("No hash registered for GQL operation: $operationName")

    fun getPreviousHash(operationName: String): String? =
        hashes[operationName]?.previousHash

    fun updateHashes(
        remoteHashes: Map<String, RemoteHashEntry>,
        source: HashSource,
    ): UpdateResult {
        var updated = 0
        var unchanged = 0
        remoteHashes.forEach { (op, remote) ->
            val current = hashes[op]
            if (current != null) {
                val hashChanged = current.hash != remote.hash
                if (hashChanged) updated++ else unchanged++
                hashes[op] = GqlHashEntry(
                    hash = remote.hash,
                    previousHash = remote.previousHash,
                    source = source,
                )
            }
        }
        return UpdateResult(updated = updated, unchanged = unchanged)
    }

    data class UpdateResult(val updated: Int, val unchanged: Int)

    fun getAll(): Map<String, GqlHashEntry> = hashes.toMap()

    data class RemoteHashEntry(
        val hash: String,
        val previousHash: String? = null,
    )
}
