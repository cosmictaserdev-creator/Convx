package com.convx.storage.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.*
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.convx.storage.data.SmbFileSource
import com.convx.storage.data.WebDavFileSource
import com.convx.storage.domain.*
import java.io.IOException

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class NetworkDataSourceFactory(
    private val downstream: DataSource.Factory,
    private val store: NetworkServerStore,
    private val webDav: WebDavFileSource,
    private val smb: SmbFileSource,
) : DataSource.Factory {
    override fun createDataSource(): DataSource = RoutingSource(downstream.createDataSource())

    private inner class RoutingSource(private val inner: DataSource) : DataSource {
        private val listeners = mutableListOf<TransferListener>()
        private var active: DataSource = inner
        private var logicalUri: Uri? = null
        override fun addTransferListener(listener: TransferListener) { listeners += listener; inner.addTransferListener(listener) }
        override fun open(dataSpec: DataSpec): Long {
            if (dataSpec.uri.scheme != NetworkMediaUri.Scheme) return inner.open(dataSpec)
            val location = NetworkMediaUri.parse(dataSpec.uri.toString()) ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
            val server = store.find(location.serverId) ?: throw StorageException(StorageError.NOT_FOUND)
            logicalUri = dataSpec.uri
            return when (server) {
                is NetworkServer.WebDav -> {
                    val factory = OkHttpDataSource.Factory(webDav.playbackClient)
                    WebDavFileSource.authorization(server)?.let { factory.setDefaultRequestProperties(mapOf("Authorization" to it)) }
                    active = factory.createDataSource().apply { listeners.forEach(::addTransferListener) }
                    active.open(dataSpec.buildUpon().setUri(WebDavFileSource.resolve(server, location.path).toString()).build())
                }
                is NetworkServer.Smb -> {
                    active = SmbDataSource(server, location.path, smb).apply { listeners.forEach(::addTransferListener) }
                    active.open(dataSpec)
                }
            }
        }
        override fun read(buffer: ByteArray, offset: Int, length: Int): Int = active.read(buffer, offset, length)
        override fun getUri(): Uri? = logicalUri ?: active.uri
        override fun getResponseHeaders(): Map<String, List<String>> = active.responseHeaders
        override fun close() { active.close(); active = inner; logicalUri = null }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private class SmbDataSource(private val server: NetworkServer.Smb, private val path: String, private val source: SmbFileSource) : BaseDataSource(true) {
    private var file: RemoteFile? = null
    private var window: ReadWindow? = null
    private var openedUri: Uri? = null
    private var started = false

    override fun open(dataSpec: DataSpec): Long {
        transferInitializing(dataSpec)
        val handle = source.open(server, path)
        try {
            if (dataSpec.position > handle.length) throw DataSourceException(PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE)
            window = ReadWindow(dataSpec.position, dataSpec.length, handle.length)
            file = handle
            openedUri = dataSpec.uri
            started = true
            transferStarted(dataSpec)
            return requireNotNull(window).remaining
        } catch (error: Exception) { handle.close(); throw IOException("Could not open remote file", error) }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        val readWindow = window ?: throw IOException("Data source is closed")
        val limit = readWindow.readLength(length)
        if (limit == 0) return C.RESULT_END_OF_INPUT
        val count = try { requireNotNull(file).read(readWindow.position, buffer, offset, limit) }
        catch (error: Exception) { throw IOException("Remote read failed", error) }
        if (count <= 0) return C.RESULT_END_OF_INPUT
        readWindow.advance(count)
        bytesTransferred(count)
        return count
    }
    override fun getUri(): Uri? = openedUri
    override fun close() {
        try { file?.close() } finally {
            file = null; window = null; openedUri = null
            if (started) { started = false; transferEnded() }
        }
    }
}
