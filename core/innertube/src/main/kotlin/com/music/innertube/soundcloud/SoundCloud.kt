package com.music.innertube.soundcloud

import com.music.innertube.NewPipeExtractor
import com.music.innertube.models.Artist
import com.music.innertube.models.SongItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.kiosk.KioskInfo
import org.schabi.newpipe.extractor.search.SearchInfo
import org.schabi.newpipe.extractor.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory
import org.schabi.newpipe.extractor.stream.ContentAvailability
import org.schabi.newpipe.extractor.stream.DeliveryMethod
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem

data class SoundCloudStream(
    val url: String,
    val isHls: Boolean,
    val mimeType: String?,
    val bitrateKbps: Int?,
    val durationSeconds: Long,
)

// Uses the web API through NewPipeExtractor; client-id rotation can break it, so failure is normal.
object SoundCloud {
    suspend fun search(query: String): Result<List<SongItem>> = request {
        val service = ServiceList.SoundCloud
        val extractor = service.getSearchExtractor(query, listOf(SoundcloudSearchQueryHandlerFactory.TRACKS), "")
        SearchInfo.getInfo(extractor).relatedItems.filterIsInstance<StreamInfoItem>().mapNotNull { it.toSongItem() }
    }

    suspend fun charts(kioskId: String? = null): Result<List<SongItem>> = request {
        val kiosks = ServiceList.SoundCloud.kioskList
        val extractor = if (kioskId == null) kiosks.defaultKioskExtractor else kiosks.getExtractorById(kioskId, null)
        KioskInfo.getInfo(extractor).relatedItems.mapNotNull { it.toSongItem() }
    }

    suspend fun resolveStream(id: String): Result<SoundCloudStream> = request {
        val url = requireNotNull(SoundCloudIds.toUrl(id)) { "Invalid SoundCloud track id" }
        val info = StreamInfo.getInfo(ServiceList.SoundCloud, url)
        require(info.contentAvailability !in unavailable) { "SoundCloud track is not freely available" }
        val streams = info.audioStreams.filter { it.isUrl && it.content.startsWith("https://") }
        val progressive = streams.filter { it.deliveryMethod == DeliveryMethod.PROGRESSIVE_HTTP }
        val hls = streams.filter { it.deliveryMethod == DeliveryMethod.HLS }
        val stream = (progressive.ifEmpty { hls }).maxByOrNull { it.averageBitrate }
            ?: error("No playable SoundCloud audio stream")
        SoundCloudStream(
            url = stream.content,
            isHls = stream.deliveryMethod == DeliveryMethod.HLS,
            mimeType = stream.format?.mimeType,
            bitrateKbps = stream.averageBitrate.takeIf { it > 0 },
            durationSeconds = info.duration,
        )
    }

    private suspend fun <T> request(block: () -> T): Result<T> = withContext(Dispatchers.IO) {
        runCatching {
            NewPipeExtractor.init()
            block()
        }.onFailure { if (it is CancellationException) throw it }
    }
}

private val unavailable = setOf(ContentAvailability.PAID, ContentAvailability.MEMBERSHIP, ContentAvailability.UPCOMING)

internal fun StreamInfoItem.toSongItem(): SongItem? {
    if (contentAvailability in unavailable) return null
    val trackId = SoundCloudIds.toId(url) ?: return null
    return SongItem(
        id = trackId,
        title = name,
        artists = listOf(Artist(name = uploaderName.orEmpty(), id = null)),
        duration = duration.takeIf { it in 0..Int.MAX_VALUE.toLong() }?.toInt(),
        thumbnail = thumbnails.maxByOrNull { it.width.toLong() * it.height }?.url.orEmpty(),
    )
}
