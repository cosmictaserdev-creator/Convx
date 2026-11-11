package com.convx.music.playback

import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheKeyFactory
import com.convx.music.canvas.canvasCacheKey

/** HLS manifests and segments share the same song namespace as progressive videos. */
internal fun canvasSegmentKeyFactory(mediaId: String): CacheKeyFactory = CacheKeyFactory { spec ->
    spec.key ?: canvasCacheKey(mediaId, spec.uri.toString())
}

/** Downloads are read-only; ordinary playback writes to the bounded player cache. */
internal fun canvasCacheDataSourceFactory(
    downloads: Cache,
    playback: Cache,
    upstream: DataSource.Factory,
    keyFactory: CacheKeyFactory = CacheKeyFactory.DEFAULT,
    prefetch: Boolean = false,
): CacheDataSource.Factory = CacheDataSource.Factory()
    .setCache(downloads)
    .setCacheKeyFactory(keyFactory)
    .setUpstreamDataSourceFactory(
        CacheDataSource.Factory()
            .setCache(playback)
            .setCacheWriteDataSinkFactory(PlayerCacheBudgetRegistry.sinkFactory(playback, prefetch))
            .setCacheKeyFactory(keyFactory)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR),
    )
    .setCacheWriteDataSinkFactory(null)
    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
