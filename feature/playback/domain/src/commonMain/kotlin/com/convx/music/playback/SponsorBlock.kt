package com.convx.music.playback

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray

enum class SponsorCategory(val apiName: String) {
    SPONSOR("sponsor"),
    INTRO("intro"),
    OUTRO("outro"),
    SELF_PROMO("selfpromo"),
    INTERACTION("interaction"),
    PREVIEW("preview"),
    MUSIC_OFFTOPIC("music_offtopic"),
    FILLER("filler"),
}

val DefaultSponsorCategories = setOf(SponsorCategory.SPONSOR, SponsorCategory.SELF_PROMO, SponsorCategory.MUSIC_OFFTOPIC)

data class SponsorSegment(val startMs: Long, val endMs: Long, val category: SponsorCategory)

/** Parses a hash-prefix `skipSegments` response, keeping only skippable segments of [videoId]. */
fun parseSponsorSegments(json: String, videoId: String): List<SponsorSegment> =
    decodeSponsorSegments(json, videoId).orEmpty()

/** Null means an invalid response, distinct from a valid response containing no segments. */
fun decodeSponsorSegments(json: String, videoId: String): List<SponsorSegment>? {
    val videos = try {
        Json.parseToJsonElement(json).jsonArray
    } catch (_: SerializationException) {
        return null
    } catch (_: IllegalArgumentException) {
        return null
    }
    if (videos.any { video ->
        video !is JsonObject || (video["videoID"] as? JsonPrimitive)?.isString != true || video["segments"] !is JsonArray
    }) return null
    return videos
        .mapNotNull { it as? JsonObject }
        .filter { it["videoID"].text() == videoId }
        .flatMap { (it["segments"] as? JsonArray).orEmpty() }
        .mapNotNull { (it as? JsonObject)?.toSegment() }
}

private fun JsonElement?.text(): String? = (this as? JsonPrimitive)?.contentOrNull

private fun JsonObject.toSegment(): SponsorSegment? {
    if ((this["actionType"].text() ?: "skip") != "skip") return null
    val category = SponsorCategory.entries.firstOrNull { it.apiName == this["category"].text() } ?: return null
    val times = (this["segment"] as? JsonArray)?.map { (it as? JsonPrimitive)?.doubleOrNull } ?: return null
    if (times.size != 2 || times.any { it == null || !it.isFinite() || it < 0 || it * 1000 >= Long.MAX_VALUE.toDouble() }) return null
    if (times[1]!! <= times[0]!!) return null
    return SponsorSegment((times[0]!! * 1000).toLong(), (times[1]!! * 1000).toLong(), category)
}

class SponsorBlockSkipper(segments: List<SponsorSegment>, enabled: Set<SponsorCategory>) {
    private val merged: List<LongRange> = merge(segments.filter { it.category in enabled })

    // The end guard stops a seek that lands just short of a segment's end from jumping again.
    fun skipTarget(positionMs: Long): Long? {
        val segment = merged.firstOrNull { positionMs >= it.first && positionMs < it.last - END_GUARD_MS }
        return segment?.last
    }

    companion object {
        const val END_GUARD_MS = 250L
        const val MERGE_GAP_MS = 250L

        private fun merge(segments: List<SponsorSegment>): List<LongRange> {
            val usable = segments
                .filter { it.startMs >= 0 && it.endMs > it.startMs }
                .sortedBy { it.startMs }
            val result = mutableListOf<LongRange>()
            for (segment in usable) {
                val last = result.lastOrNull()
                if (last != null && segment.startMs - last.last <= MERGE_GAP_MS) {
                    result[result.lastIndex] = last.first..maxOf(last.last, segment.endMs)
                } else {
                    result += segment.startMs..segment.endMs
                }
            }
            return result
        }
    }
}
