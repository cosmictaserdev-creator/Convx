package com.convxy.music.comments.youtube

import com.music.innertube.models.NavigationEndpoint
import com.music.innertube.models.Run
import com.music.innertube.models.Runs
import com.music.innertube.models.Thumbnail
import com.music.innertube.models.Thumbnails
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.comment.CommentRenderer
import com.music.innertube.models.comment.CommentThreadRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The timestamp recovery rules, which are the whole of this source's value and the part most likely to
 * be wrong.
 *
 * YouTube has no timed-comment field, so every millisecond here is inferred from either a seek link
 * YouTube built or a time a viewer typed. Both inferences can be wrong in ways that are invisible on
 * screen — a comment pinned to the wrong moment looks exactly like one pinned to the right moment — so
 * the cases below are mostly about what must NOT be read as a time.
 */
class YouTubeCommentParserTest {

    private fun run(text: String, startSeconds: Int? = null) = Run(
        text = text,
        navigationEndpoint = startSeconds?.let {
            NavigationEndpoint(watchEndpoint = WatchEndpoint(startTimeSeconds = it))
        },
    )

    private fun renderer(
        id: String = "c1",
        body: List<Run> = listOf(run("great song")),
        author: String? = "alice",
        votes: String? = null,
        replies: Int? = null,
        avatar: String? = null,
        channel: String? = null,
    ) = CommentRenderer(
        authorText = author?.let { Runs(listOf(run(it))) },
        authorThumbnail = avatar?.let { Thumbnails(listOf(Thumbnail(it, 48, 48))) },
        contentText = Runs(body),
        authorEndpoint = channel?.let {
            NavigationEndpoint(
                browseEndpoint = com.music.innertube.models.BrowseEndpoint(browseId = it),
            )
        },
        commentId = id,
        voteCount = votes?.let { Runs(listOf(run(it))) },
        replyCount = replies,
    )

    private fun thread(vararg renderers: CommentRenderer) = renderers.map {
        CommentThreadRenderer(comment = CommentThreadRenderer.Comment(commentRenderer = it))
    }

    // ── reading a time out of text ─────────────────────────────────────────

    @Test
    fun `a bare mm ss is read as minutes and seconds`() {
        assertEquals(83_000L, YouTubeCommentParser.timestampFromText("1:23"))
        assertEquals(125_000L, YouTubeCommentParser.timestampFromText("2:05"))
        assertEquals(45_000L, YouTubeCommentParser.timestampFromText("0:45"))
    }

    @Test
    fun `a time in the middle of a sentence is still found`() {
        assertEquals(45_000L, YouTubeCommentParser.timestampFromText("the drop at 0:45 is insane"))
        assertEquals(83_000L, YouTubeCommentParser.timestampFromText("[1:23] give it up"))
        assertEquals(83_000L, YouTubeCommentParser.timestampFromText("try (1:23) instead"))
    }

    @Test
    fun `hours are read as hours`() {
        assertEquals(3_723_000L, YouTubeCommentParser.timestampFromText("1:02:03"))
        assertEquals(3_723_000L, YouTubeCommentParser.timestampFromText("[1:02:03] still going"))
        // Must not be read as its 2:03 tail.
        assertTrue(YouTubeCommentParser.timestampFromText("1:02:03")!! > 60_000L)
    }

    @Test
    fun `the first time in a comment is the one it is about`() {
        // A viewer can name several moments. This feature pins one comment to one position, and the
        // opening one is what the comment leads with.
        assertEquals(83_000L, YouTubeCommentParser.timestampFromText("1:23 and 3:45 both hit"))
    }

    @Test
    fun `a single-digit seconds field is a ratio, not a time`() {
        // "2:1" and "1:2" are far more often a score, a ratio or a version number than 2m01s. A real
        // timestamp always writes its seconds with two digits, and YouTube only links two-digit ones.
        assertNull(YouTubeCommentParser.timestampFromText("my team won 2:1"))
        assertNull(YouTubeCommentParser.timestampFromText("version 1:2"))
    }

    @Test
    fun `text with no time in it yields nothing`() {
        assertNull(YouTubeCommentParser.timestampFromText("this song is amazing"))
        assertNull(YouTubeCommentParser.timestampFromText(""))
        assertNull(YouTubeCommentParser.timestampFromText("12345"))
        // A colon on its own is not a separator.
        assertNull(YouTubeCommentParser.timestampFromText("wow: incredible"))
    }

    @Test
    fun `an absurd time is rejected rather than pinned hours into the track`() {
        // 999 minutes is 16 hours. normalize would reject it against a known duration, but a local file
        // has no duration, and a marker 16 hours in would sit at the far end of the seek bar forever.
        assertNull(YouTubeCommentParser.timestampFromText("999:00"))
        assertNull(YouTubeCommentParser.timestampFromText("99:99:99"))
    }

    // ── structured beats textual ───────────────────────────────────────────

    @Test
    fun `a linked seek position wins over what the text says`() {
        // YouTube already parsed the moment into startTimeSeconds. Trusting its parse over a re-parse
        // of the text means the two can never disagree about a comment's own link.
        val runs = listOf(run("see "), run("2:00", startSeconds = 83), run(" here"))
        assertEquals(83_000L, YouTubeCommentParser.timestampMs(runs))
    }

    @Test
    fun `an unlinked time falls back to the text`() {
        // Brackets typo'd, or a plain time YouTube chose not to link. Still a real moment.
        assertEquals(120_000L, YouTubeCommentParser.timestampMs(listOf(run("check [2:00] out"))))
    }

    @Test
    fun `a comment with no time anywhere is not a timed comment`() {
        assertNull(YouTubeCommentParser.timestampMs(listOf(run("just vibing"))))
        assertNull(YouTubeCommentParser.timestampMs(emptyList()))
        assertNull(YouTubeCommentParser.timestampMs(null))
    }

    @Test
    fun `a negative seek position is ignored rather than clamped`() {
        // Clamping to 0 would pin the comment to the start of the song, which is a claim nobody made.
        val runs = listOf(run("1:23", startSeconds = -5))
        assertEquals(83_000L, YouTubeCommentParser.timestampMs(runs))
    }

    // ── engagement counts ──────────────────────────────────────────────────

    @Test
    fun `display counts are read the way YouTube writes them`() {
        assertEquals(5, YouTubeCommentParser.parseCount("5"))
        assertEquals(1_200, YouTubeCommentParser.parseCount("1.2K"))
        assertEquals(12_000, YouTubeCommentParser.parseCount("12K"))
        assertEquals(1_500_000, YouTubeCommentParser.parseCount("1.5M"))
        assertEquals(3_400, YouTubeCommentParser.parseCount("3,4 K"))
    }

    @Test
    fun `an absent count is unknown, not zero`() {
        // "No likes" and "likes not shown" are different facts and the model can say both.
        assertNull(YouTubeCommentParser.parseCount(null))
        assertNull(YouTubeCommentParser.parseCount(""))
        assertNull(YouTubeCommentParser.parseCount("Like"))
    }

    // ── track identity ─────────────────────────────────────────────────────

    @Test
    fun `a video id is recognised by its shape`() {
        assertTrue(YouTubeCommentParser.looksLikeVideoId("dQw4w9WgXcQ"))
        assertTrue(YouTubeCommentParser.looksLikeVideoId("aaaaaaaaaaa"))
        assertTrue(YouTubeCommentParser.looksLikeVideoId("_-_-_-_-_-_"))
    }

    @Test
    fun `non video ids are rejected before a request is spent`() {
        assertTrue(!YouTubeCommentParser.looksLikeVideoId("12345"))
        assertTrue(!YouTubeCommentParser.looksLikeVideoId("dQw4w9WgXc")) // ten characters
        assertTrue(!YouTubeCommentParser.looksLikeVideoId("dQw4w9WgXcQx")) // twelve
        assertTrue(!YouTubeCommentParser.looksLikeVideoId("dQw4w9WgXc!")) // illegal character
        // Eleven digits is a legal id in principle but overwhelmingly a local/MediaStore row id.
        assertTrue(!YouTubeCommentParser.looksLikeVideoId("12345678901"))
        assertTrue(!YouTubeCommentParser.looksLikeVideoId(null))
        assertTrue(!YouTubeCommentParser.looksLikeVideoId(""))
    }

    // ── renderer mapping ───────────────────────────────────────────────────

    @Test
    fun `a timed comment carries its author, avatar, likes and a permalink`() {
        val result = YouTubeCommentParser.toComment(
            renderer(
                id = "abc123",
                body = listOf(run("the drop at "), run("[1:23]", startSeconds = 83)),
                author = "alice",
                votes = "1.2K",
                replies = 3,
                avatar = "https://yt/img.jpg",
                channel = "UCxyz",
            ),
            videoId = "dQw4w9WgXcQ",
            sourceName = "YouTube",
            trackDurationMs = 250_000L,
        )

        assertEquals("abc123", result?.id)
        assertEquals(83_000L, result?.timestampMs)
        assertEquals("alice", result?.authorName)
        assertEquals("https://yt/img.jpg", result?.avatarUrl)
        assertEquals(1_200, result?.likeCount)
        assertEquals(3, result?.replyCount)
        assertEquals("https://www.youtube.com/channel/UCxyz", result?.authorUrl)
        assertEquals(
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ&lc=abc123",
            result?.permalink,
        )
        // The body is what the viewer wrote, timestamp included — the marker carries the position, the
        // text stays verbatim rather than having the time cut out of it.
        assertEquals("the drop at [1:23]", result?.text)
        // Relative "3 days ago" cannot become an epoch without inventing precision.
        assertNull(result?.createdAtEpochMs)
    }

    @Test
    fun `a comment with no timestamp is dropped, not pinned to zero`() {
        assertNull(
            YouTubeCommentParser.toComment(
                renderer(body = listOf(run("best song ever"))),
                videoId = "dQw4w9WgXcQ",
                sourceName = "YouTube",
                trackDurationMs = 250_000L,
            )
        )
    }

    @Test
    fun `an unattributable comment is dropped rather than given an invented author`() {
        assertNull(
            YouTubeCommentParser.toComment(
                renderer(body = listOf(run("1:23 wow")), author = null),
                videoId = "dQw4w9WgXcQ",
                sourceName = "YouTube",
                trackDurationMs = 250_000L,
            )
        )
        assertNull(
            YouTubeCommentParser.toComment(
                renderer(body = listOf(run("1:23 wow")), author = "   "),
                videoId = "dQw4w9WgXcQ",
                sourceName = "YouTube",
                trackDurationMs = 250_000L,
            )
        )
    }

    @Test
    fun `a comment with no id or no text is dropped`() {
        assertNull(
            YouTubeCommentParser.toComment(
                renderer(id = "", body = listOf(run("1:23 wow"))),
                videoId = "v", sourceName = "YouTube", trackDurationMs = null,
            )
        )
        // Isolating "no text" means the timestamp has to come from the seek link rather than from the
        // body, because a time written into the body IS body text. The old fixture here read
        // `run("1:23 "), run("   ")` and expected a drop, which contradicted the two tests that keep
        // `run("1:00", startSeconds = 60)` — a viewer who writes only a time is marking a moment, and
        // that moment is the whole point of this source. What is genuinely empty is a linked comment
        // with nothing written at all.
        assertNull(
            YouTubeCommentParser.toComment(
                renderer(body = listOf(run("   ", startSeconds = 83))),
                videoId = "v", sourceName = "YouTube", trackDurationMs = null,
            )
        )
        assertNull(
            YouTubeCommentParser.toComment(
                renderer(body = listOf(run("", startSeconds = 83))),
                videoId = "v", sourceName = "YouTube", trackDurationMs = null,
            )
        )
    }

    @Test
    fun `a timestamp past the end of the track is dropped`() {
        // seekTo beyond the media item's length is not something the player should be asked to do on
        // the strength of a stranger's comment.
        assertNull(
            YouTubeCommentParser.toComment(
                renderer(body = listOf(run("9:59", startSeconds = 599))),
                videoId = "v", sourceName = "YouTube", trackDurationMs = 250_000L,
            )
        )
        // Unknown duration means the check is skipped, not that everything is rejected.
        assertTrue(
            YouTubeCommentParser.toComment(
                renderer(body = listOf(run("9:59", startSeconds = 599))),
                videoId = "v", sourceName = "YouTube", trackDurationMs = null,
            ) != null
        )
    }

    @Test
    fun `a page of threads keeps only the timed ones`() {
        val results = YouTubeCommentParser.toComments(
            threads = thread(
                renderer(id = "timed", body = listOf(run("1:00", startSeconds = 60))),
                renderer(id = "untimed", body = listOf(run("love this"))),
                renderer(id = "timed2", body = listOf(run("at 2:30 !!"))),
            ),
            videoId = "dQw4w9WgXcQ",
            sourceName = "YouTube",
            trackDurationMs = 250_000L,
        )
        assertEquals(listOf("timed", "timed2"), results.map { it.id })
    }

    @Test
    fun `a thread with no renderer is skipped`() {
        val results = YouTubeCommentParser.toComments(
            threads = listOf(CommentThreadRenderer(comment = null)),
            videoId = "v",
            sourceName = "YouTube",
            trackDurationMs = null,
        )
        assertTrue(results.isEmpty())
    }
}
