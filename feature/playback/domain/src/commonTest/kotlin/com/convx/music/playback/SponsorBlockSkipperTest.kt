package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SponsorBlockSkipperTest {
    @Test fun extremeAndInvalidSegmentTimesAreIgnored() {
        val json = """[{"videoID":"v1","segments":[
            {"category":"sponsor","segment":[0,1e300]},
            {"category":"sponsor","segment":["NaN",10]},
            {"category":"sponsor","segment":[-1,10]},
            {"category":"sponsor","segment":[5,2]}
        ]}]"""
        assertEquals(emptyList(), parseSponsorSegments(json, "v1"))
        assertNull(decodeSponsorSegments("not JSON", "v1"))
        assertNull(decodeSponsorSegments("[1,2,3]", "v1"))
    }

    @Test fun overlappingRangesNearTheLongLimitMergeWithoutOverflow() {
        val skipper = SponsorBlockSkipper(listOf(
            seg(Long.MAX_VALUE - 1_000, Long.MAX_VALUE - 100),
            seg(Long.MAX_VALUE - 200, Long.MAX_VALUE),
        ), all)
        assertEquals(Long.MAX_VALUE, skipper.skipTarget(Long.MAX_VALUE - 500))
    }
    private val all = SponsorCategory.entries.toSet()

    private fun seg(start: Long, end: Long, category: SponsorCategory = SponsorCategory.INTRO) =
        SponsorSegment(start, end, category)

    @Test
    fun noSegmentsNeverSkip() {
        assertNull(SponsorBlockSkipper(emptyList(), all).skipTarget(5_000))
    }

    @Test
    fun insideASegmentSkipsToItsEnd() {
        val skipper = SponsorBlockSkipper(listOf(seg(10_000, 20_000)), all)
        assertEquals(20_000L, skipper.skipTarget(10_000))
        assertEquals(20_000L, skipper.skipTarget(15_000))
    }

    @Test
    fun outsideASegmentDoesNotSkip() {
        val skipper = SponsorBlockSkipper(listOf(seg(10_000, 20_000)), all)
        assertNull(skipper.skipTarget(9_999))
        assertNull(skipper.skipTarget(20_000))
        assertNull(skipper.skipTarget(30_000))
    }

    @Test
    fun aSegmentStartingAtZeroSkipsFromTheFirstFrame() {
        assertEquals(8_000L, SponsorBlockSkipper(listOf(seg(0, 8_000)), all).skipTarget(0))
    }

    @Test
    fun landingJustBeforeTheEndDoesNotJumpAgain() {
        val skipper = SponsorBlockSkipper(listOf(seg(10_000, 20_000)), all)
        assertNull(skipper.skipTarget(20_000 - SponsorBlockSkipper.END_GUARD_MS))
        assertEquals(20_000L, skipper.skipTarget(20_000 - SponsorBlockSkipper.END_GUARD_MS - 1))
    }

    @Test
    fun overlappingSegmentsClearInOneJump() {
        val skipper = SponsorBlockSkipper(listOf(seg(10_000, 20_000), seg(15_000, 30_000)), all)
        assertEquals(30_000L, skipper.skipTarget(11_000))
    }

    @Test
    fun nearlyTouchingSegmentsMergeButDistantOnesDoNot() {
        val near = SponsorBlockSkipper(listOf(seg(1_000, 5_000), seg(5_200, 9_000)), all)
        assertEquals(9_000L, near.skipTarget(2_000))
        val far = SponsorBlockSkipper(listOf(seg(1_000, 5_000), seg(8_000, 9_000)), all)
        assertEquals(5_000L, far.skipTarget(2_000))
        assertEquals(9_000L, far.skipTarget(8_000))
    }

    @Test
    fun unsortedInputIsHandled() {
        val skipper = SponsorBlockSkipper(listOf(seg(30_000, 40_000), seg(1_000, 5_000)), all)
        assertEquals(5_000L, skipper.skipTarget(1_000))
        assertEquals(40_000L, skipper.skipTarget(35_000))
    }

    @Test
    fun disabledCategoriesAreIgnored() {
        val segments = listOf(seg(1_000, 5_000, SponsorCategory.SPONSOR), seg(6_000, 9_000, SponsorCategory.INTRO))
        val skipper = SponsorBlockSkipper(segments, setOf(SponsorCategory.INTRO))
        assertNull(skipper.skipTarget(2_000))
        assertEquals(9_000L, skipper.skipTarget(7_000))
    }

    @Test
    fun emptyReversedAndNegativeSegmentsAreDropped() {
        val skipper = SponsorBlockSkipper(listOf(seg(5_000, 5_000), seg(9_000, 4_000), seg(-100, 3_000)), all)
        assertNull(skipper.skipTarget(5_000))
        assertNull(skipper.skipTarget(6_000))
        assertNull(skipper.skipTarget(1_000))
    }

    private fun response(videoId: String, vararg segments: String) =
        """[{"videoID":"$videoId","hash":"abc","segments":[${segments.joinToString(",")}]}]"""

    @Test
    fun parsesSkipSegmentsOfTheRequestedVideo() {
        val json = response("v1", """{"category":"sponsor","actionType":"skip","segment":[12.5,45.25]}""")
        assertEquals(listOf(seg(12_500, 45_250, SponsorCategory.SPONSOR)), parseSponsorSegments(json, "v1"))
    }

    @Test
    fun ignoresOtherVideosSharingTheHashPrefix() {
        assertEquals(
            emptyList(),
            parseSponsorSegments(response("other", """{"category":"sponsor","segment":[1,2]}"""), "v1"),
        )
    }

    @Test
    fun aMissingActionTypeMeansSkip() {
        val parsed = parseSponsorSegments(response("v1", """{"category":"intro","segment":[0,5]}"""), "v1")
        assertEquals(listOf(seg(0, 5_000, SponsorCategory.INTRO)), parsed)
    }

    @Test
    fun nonSkipActionsUnknownCategoriesAndBadSegmentsAreDropped() {
        val json = response(
            "v1",
            """{"category":"sponsor","actionType":"mute","segment":[1,2]}""",
            """{"category":"poi_highlight","actionType":"skip","segment":[1,2]}""",
            """{"category":"sponsor","actionType":"skip","segment":[1]}""",
            """{"category":"sponsor","actionType":"skip","segment":["a","b"]}""",
            """{"category":"sponsor","actionType":"skip"}""",
        )
        assertEquals(emptyList(), parseSponsorSegments(json, "v1"))
    }

    @Test
    fun malformedOrUnexpectedJsonGivesNoSegments() {
        assertEquals(emptyList(), parseSponsorSegments("", "v1"))
        assertEquals(emptyList(), parseSponsorSegments("not json", "v1"))
        assertEquals(emptyList(), parseSponsorSegments("""{"videoID":"v1"}""", "v1"))
        assertEquals(emptyList(), parseSponsorSegments("[1,2,3]", "v1"))
    }

    @Test
    fun apiNamesMatchTheSponsorBlockApi() {
        assertEquals("selfpromo", SponsorCategory.SELF_PROMO.apiName)
        assertEquals("music_offtopic", SponsorCategory.MUSIC_OFFTOPIC.apiName)
    }
}
