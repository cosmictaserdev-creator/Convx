package com.convxy.music.comments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The whole decision layer of the feature, tested without a player, a network or Android.
 *
 * These are the cases the task singled out: ordering, active selection, near-identical timestamps,
 * and the "what does this position mean" question. Everything here is pure arithmetic over a sorted
 * list, so a failure points at a real behavioural bug rather than at a test harness.
 */
class CommentTimelineTest {

    private val duration = 200_000L // 3:20

    private fun comment(
        id: String,
        at: Long,
        text: String = "body of $id",
        source: String = "soundcloud",
        createdAt: Long? = null,
    ) = TimestampedComment(
        id = id,
        trackId = "track-1",
        authorName = "author-$id",
        text = text,
        timestampMs = at,
        createdAtEpochMs = createdAt,
        sourceName = source,
    )

    // ── normalize ──────────────────────────────────────────────────────────

    @Test
    fun `normalize sorts into playback order`() {
        val out = CommentTimeline.normalize(
            listOf(comment("c", 5_000), comment("a", 1_000), comment("b", 3_000)),
            duration,
        )
        assertEquals(listOf("a", "b", "c"), out.map { it.id })
    }

    @Test
    fun `normalize drops comments that cannot be shown or sought to`() {
        val out = CommentTimeline.normalize(
            listOf(
                comment("blank", 1_000, text = "   "),
                comment("negative", -1),
                comment("past-the-end", duration + 1),
                comment("at-the-end", duration),
                comment("fine", 2_000),
            ),
            duration,
        )
        assertEquals(listOf("fine", "at-the-end"), out.map { it.id })
    }

    @Test
    fun `normalize keeps out-of-range timestamps when the duration is unknown`() {
        // A provider that does not report a length must not cause every comment to be rejected.
        val out = CommentTimeline.normalize(listOf(comment("a", 9_999_999)), null)
        assertEquals(1, out.size)
    }

    @Test
    fun `normalize dedupes by source plus id, not by id alone`() {
        val out = CommentTimeline.normalize(
            listOf(
                comment("dup", 1_000, text = "first"),
                comment("dup", 2_000, text = "second"),
                comment("dup", 3_000, text = "other source", source = "other"),
            ),
            duration,
        )
        // Two ids behind one repository can legitimately collide across providers; the source prefix
        // is what keeps them distinct in both the dedupe set and Compose's item keys.
        assertEquals(listOf("first", "other source"), out.map { it.text })
    }

    @Test
    fun `normalize breaks timestamp ties deterministically`() {
        val byCreation = CommentTimeline.normalize(
            listOf(comment("late", 1_000, createdAt = 200), comment("early", 1_000, createdAt = 100)),
            duration,
        )
        assertEquals(listOf("early", "late"), byCreation.map { it.id })

        // No creation date at all: still one stable order, so a cached read and a fresh fetch of the
        // same track cannot present the comments in a different sequence.
        val byId = CommentTimeline.normalize(
            listOf(comment("z", 1_000), comment("a", 1_000)),
            duration,
        )
        assertEquals(listOf("a", "z"), byId.map { it.id })
    }

    @Test
    fun `normalize caps the list`() {
        val many = (0 until CommentTimeline.MAX_COMMENTS + 100).map { comment("c$it", it.toLong()) }
        assertEquals(CommentTimeline.MAX_COMMENTS, CommentTimeline.normalize(many, null).size)
    }

    // ── activeGroup ────────────────────────────────────────────────────────

    private val track = CommentTimeline.normalize(
        listOf(
            comment("a", 10_000),
            comment("b", 30_000),
            comment("b2", 30_200), // near-identical to b: one moment, not two
            comment("c", 90_000),
        ),
        duration,
    )

    @Test
    fun `nothing is active before the first comment`() {
        assertNull(CommentTimeline.activeGroup(track, 0))
        assertNull(CommentTimeline.activeGroup(track, 9_999))
    }

    @Test
    fun `a comment goes live exactly at its timestamp`() {
        val group = CommentTimeline.activeGroup(track, 10_000)
        assertNotNull(group)
        assertEquals(0, group!!.startIndex)
        assertEquals(0, group.endIndexInclusive)
        assertEquals(10_000L, group.timestampMs)
    }

    @Test
    fun `near-identical timestamps highlight as one group`() {
        val group = CommentTimeline.activeGroup(track, 30_000)!!
        assertEquals(1, group.startIndex)
        assertEquals(2, group.endIndexInclusive)
        assertEquals(2, group.size)
        // Still one group partway through the pair — the highlight must not flicker between them.
        assertEquals(group, CommentTimeline.activeGroup(track, 30_100))
        assertEquals(group, CommentTimeline.activeGroup(track, 30_200))
    }

    @Test
    fun `a comment stays live for the window, then goes quiet`() {
        // b's own window would run to 50_000, but c is at 90_000 so the cap is what closes it.
        assertNotNull(CommentTimeline.activeGroup(track, 45_000))
        assertNotNull(CommentTimeline.activeGroup(track, 49_999))
        assertNull("nothing is live in a genuine gap", CommentTimeline.activeGroup(track, 50_000))
        assertNull(CommentTimeline.activeGroup(track, 89_999))
    }

    @Test
    fun `the window closes early at the next comment, so there is no dead time`() {
        val pair = CommentTimeline.normalize(
            listOf(comment("a", 10_000), comment("b", 25_000)),
            duration,
        )
        assertEquals(0, CommentTimeline.activeGroup(pair, 24_999)!!.startIndex)
        assertEquals(1, CommentTimeline.activeGroup(pair, 25_000)!!.startIndex)
    }

    @Test
    fun `the last comment is capped by the window rather than staying live forever`() {
        assertEquals(3, CommentTimeline.activeGroup(track, 90_000)!!.startIndex)
        assertNotNull(CommentTimeline.activeGroup(track, 109_999))
        assertNull(CommentTimeline.activeGroup(track, 110_000))
    }

    @Test
    fun `a negative position is never live`() {
        assertNull(CommentTimeline.activeGroup(track, -1))
    }

    @Test
    fun `empty input has no active group`() {
        assertNull(CommentTimeline.activeGroup(emptyList(), 5_000))
        assertNull(CommentTimeline.activeCommentId(emptyList(), 5_000))
    }

    @Test
    fun `activeCommentId names the head of the live group`() {
        assertEquals("a", CommentTimeline.activeCommentId(track, 12_000))
        assertEquals("b", CommentTimeline.activeCommentId(track, 30_100))
        assertNull(CommentTimeline.activeCommentId(track, 60_000))
    }

    // ── displayGroups ──────────────────────────────────────────────────────

    @Test
    fun `displayGroups partitions every comment exactly once`() {
        val groups = CommentTimeline.displayGroups(track)
        assertEquals(listOf(0, 1, 3), groups.map { it.startIndex })
        assertEquals(listOf(0, 2, 3), groups.map { it.endIndexInclusive })
        assertEquals(track.size, groups.sumOf { it.size })
    }

    @Test
    fun `the live group is always one of the displayed groups`() {
        // The invariant the UI depends on: the sheet highlights a *row*, and the row it highlights has
        // to be a row that exists. Both sides share groupAround(), so this holds for any position.
        val groups = CommentTimeline.displayGroups(track)
        for (position in 0L..duration step 250L) {
            val live = CommentTimeline.activeGroup(track, position) ?: continue
            assertTrue(
                "position $position produced $live, which is not in $groups",
                groups.any { it.startIndex == live.startIndex && it.endIndexInclusive == live.endIndexInclusive },
            )
        }
    }

    @Test
    fun `displayGroups of an empty list is empty`() {
        assertTrue(CommentTimeline.displayGroups(emptyList()).isEmpty())
    }

    // ── markers ────────────────────────────────────────────────────────────

    @Test
    fun `markers need a duration`() {
        assertTrue(CommentTimeline.markers(track, null).isEmpty())
        assertTrue(CommentTimeline.markers(track, 0).isEmpty())
        assertTrue(CommentTimeline.markers(emptyList(), duration).isEmpty())
    }

    @Test
    fun `markers cluster a dense run into one tick and report its size`() {
        // Default gap is 1.2% of 120_000 = 1440ms, so b and b2 (200ms apart) collapse and a and c
        // stand alone.
        val markers = CommentTimeline.markers(track, 120_000)
        assertEquals(3, markers.size)

        assertEquals(1, markers[0].count)
        assertEquals(10_000L, markers[0].timestampMs)

        val clustered = markers[1]
        assertEquals(2, clustered.count)
        assertEquals(1, clustered.firstIndex)
        assertEquals(2, clustered.lastIndexInclusive)
        // Sits at the mean of its cluster, not pinned to the earliest member.
        assertEquals(30_100L, clustered.timestampMs)
        assertEquals(30_100.0 / 120_000.0, clustered.fraction.toDouble(), 0.0001)

        assertEquals(1, markers[2].count)
        assertEquals(90_000.0 / 120_000.0, markers[2].fraction.toDouble(), 0.0001)
    }

    @Test
    fun `marker fractions stay inside the track`() {
        val markers = CommentTimeline.markers(track, 91_000)
        markers.forEach { assertTrue("${it.fraction} out of range", it.fraction in 0f..1f) }
    }

    @Test
    fun `a comment-dense track cannot exceed the marker cap`() {
        // 2000 comments 10ms apart: the naive one-marker-per-comment rendering would paint a solid
        // bar over the seek bar. The cap and the widening re-cluster are what stop that.
        val dense = (0 until 2_000).map { comment("c$it", it * 10L) }
        val markers = CommentTimeline.markers(dense, 20_000)
        assertTrue("got ${markers.size}", markers.size <= CommentTimeline.MAX_MARKERS)
        assertTrue(markers.isNotEmpty())
        assertEquals(dense.size, markers.sumOf { it.count })
    }

    @Test
    fun `markerForGroup finds the tick the live comment belongs to`() {
        val markers = CommentTimeline.markers(track, 120_000)
        // One tick per marker, walked in playhead order: a stands alone, b+b2 are one clustered tick,
        // c stands alone. The clustered case is the one worth pinning down - the live comment at
        // 30_050 is `b` (index 1), which belongs to the tick covering indices 1..2, not to `a`'s.
        assertEquals(markers[0], CommentTimeline.markerForGroup(markers, CommentTimeline.activeGroup(track, 10_000)!!))
        assertEquals(markers[1], CommentTimeline.markerForGroup(markers, CommentTimeline.activeGroup(track, 30_050)!!))
        assertEquals(markers[2], CommentTimeline.markerForGroup(markers, CommentTimeline.activeGroup(track, 90_000)!!))
        assertNull(CommentTimeline.markerForGroup(emptyList(), CommentTimeline.activeGroup(track, 30_050)!!))
    }

    // ── seeking and formatting ─────────────────────────────────────────────

    @Test
    fun `the seek target is the comment timestamp, floored at zero`() {
        assertEquals(30_200L, CommentTimeline.seekTargetMs(comment("b2", 30_200)))
        assertEquals(0L, CommentTimeline.seekTargetMs(comment("weird", -500)))
    }

    @Test
    fun `sanitizeTimestampMs rejects what must not reach seekTo`() {
        assertNull(CommentTimeline.sanitizeTimestampMs(null, duration))
        assertNull(CommentTimeline.sanitizeTimestampMs(-1, duration))
        assertNull(CommentTimeline.sanitizeTimestampMs(duration + 1, duration))
        assertEquals(duration, CommentTimeline.sanitizeTimestampMs(duration, duration))
        assertEquals(5L, CommentTimeline.sanitizeTimestampMs(5L, null))
        assertEquals(5L, CommentTimeline.sanitizeTimestampMs(5L, 0L))
    }

    @Test
    fun `timestamps format as m ss`() {
        assertEquals("0:00", CommentTimeline.formatTimestamp(0))
        assertEquals("0:09", CommentTimeline.formatTimestamp(9_999))
        assertEquals("0:18", CommentTimeline.formatTimestamp(18_000))
        assertEquals("1:42", CommentTimeline.formatTimestamp(102_000))
        assertEquals("3:20", CommentTimeline.formatTimestamp(duration))
        // Negative input is coerced rather than formatted as the empty string makeTimeString returns.
        assertEquals("0:00", CommentTimeline.formatTimestamp(-1))
    }

    // ── the track ref ──────────────────────────────────────────────────────

    @Test
    fun `the cache key includes the duration so a re-tagged file is not served stale comments`() {
        val a = CommentTrackRef.of("id", "Title", listOf("Artist"), 200)
        val b = CommentTrackRef.of("id", "Title", listOf("Artist"), 201)
        assertEquals("id|200", a.cacheKey)
        assertTrue(a.cacheKey != b.cacheKey)
    }

    @Test
    fun `a track ref trims and drops blank artists`() {
        val ref = CommentTrackRef.of("id", "Title", listOf("  Artist  ", "", "   "), 200)
        assertEquals(listOf("Artist"), ref.artistNames)
        assertTrue(ref.hasUsableTitle)
        assertTrue(!CommentTrackRef.of("id", "  ", emptyList(), 200).hasUsableTitle)
    }

    @Test
    fun `an external track id is kept only when it carries something`() {
        assertEquals("abc", CommentTrackRef.of("id", "T", emptyList(), 1, externalTrackId = " abc ").externalTrackId)
        assertNull(CommentTrackRef.of("id", "T", emptyList(), 1, externalTrackId = "  ").externalTrackId)
        assertNull(CommentTrackRef.of("id", "T", emptyList(), 1).externalTrackId)
    }
}
