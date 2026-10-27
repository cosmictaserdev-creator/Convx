package com.convx.storage.data

import androidx.media3.datasource.DataSource
import com.convx.storage.domain.*
import com.convx.storage.playback.NetworkDataSourceFactory
import kotlinx.coroutines.Dispatchers

class NetworkMusicRepository(
    private val store: NetworkServerStore,
    private val webDav: WebDavFileSource = WebDavFileSource(),
    private val smb: SmbFileSource = SmbFileSource(),
) : NetworkLibraryRepository by RemoteNetworkLibraryRepository(store, webDav, smb, Dispatchers.IO) {
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun dataSourceFactory(downstream: DataSource.Factory): DataSource.Factory = NetworkDataSourceFactory(downstream, store, webDav, smb)

}
