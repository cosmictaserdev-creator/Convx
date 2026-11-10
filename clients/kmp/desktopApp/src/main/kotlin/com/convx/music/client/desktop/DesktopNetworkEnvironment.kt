package com.convx.music.client.desktop

import com.convx.music.playback.AudioStreamResolver
import com.convx.storage.data.*
import com.convx.storage.domain.*
import com.convx.storage.presentation.NetworkAudioStreamResolver
import com.convx.storage.presentation.NetworkPlaybackEndpoint
import kotlinx.coroutines.Dispatchers
import java.nio.file.Path

/** Owns source credentials, connections and local decoder endpoints for one profile. */
internal class DesktopNetworkEnvironment(profile: Path) : AutoCloseable {
    private val secrets = DesktopSecureSessionStore("network-sources:${profile.toAbsolutePath().normalize()}")
    private val store = StoredNetworkServerStore(object : NetworkSnapshotStore {
        override suspend fun read() = secrets.read()
        override suspend fun write(snapshot: String) = secrets.write(snapshot)
    })
    val repository = RemoteNetworkLibraryRepository(store, WebDavFileSource(), SmbFileSource(), Dispatchers.IO)
    private val media = NetworkMediaHttpServer(repository)
    fun resolver(fallback: AudioStreamResolver): AudioStreamResolver = NetworkAudioStreamResolver(repository,
        NetworkPlaybackEndpoint { uri, mime -> media.createMediaSource(uri, mime) }, fallback)
    override fun close() {
        try { media.close() } finally { repository.close() }
    }
}
