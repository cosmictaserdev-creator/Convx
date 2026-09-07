package com.convxy.music.comments.soundcloud

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Wire format in, domain model out — and, more importantly, what happens when the wire format is not
 * what the docs promised.
 *
 * The rule under test throughout: a bad *comment* is skipped, a bad *response* yields an empty list,
 * and neither throws. A parse failure in here would otherwise surface as a crash in the middle of the
 * now-playing screen, which is the one outcome this feature is not allowed to have.
 */
class SoundCloudCommentParserTest {

    private val trackDurationMs = 60_000L

    private fun toComment(
        dto: ScComment,
        durationMs: Long? = trackDurationMs,
        permalink: String? = "https://soundcloud.com/artist/track",
    ) = SoundCloudCommentParser.toComment(
        dto = dto,
        trackId = "99",
        sourceName = "SoundCloud",
        trackDurationMs = durationMs,
        trackPermalinkUrl = permalink,
    )

    private fun user(username: String? = "alice", permalink: String? = "alice") = ScUser(
        id = 7,
        username = username,
        permalink = permalink,
        permalinkUrl = "https://soundcloud.com/$permalink",
        avatarUrl = "https://i1.sndcdn.com/avatars-$permalink.jpg",
    )

    // ── envelope handling ──────────────────────────────────────────────────

    @Test
    fun `parses a bare array`() {
        val body = """
            [
              {"kind":"comment","id":1,"body":"first!","timestamp":12000,
               "created_at":"2024-05-01 10:00:00 +0000","track_id":99,"user":{"id":7,"username":"alice"}},
              {"id":2,"body":"the drop here","timestamp":45000,"user":{"username":"bob"}}
            ]
        """.trimIndent()

        val dtos = SoundCloudCommentParser.parseComments(body)
        assertEquals(2, dtos.size)

        val comments = SoundCloudCommentParser.toComments(dtos, "99", "SoundCloud", trackDurationMs)
        assertEquals(2, comments.size)
        assertEquals("first!", comments[0].text)
        assertEquals(12_000L, comments[0].timestampMs)
        assertEquals("alice", comments[0].authorName)
        assertEquals("bob", comments[1].authorName)
        assertEquals("SoundCloud", comments[0].sourceName)
        assertEquals("99", comments[0].trackId)
    }

    @Test
    fun `parses a linked-partitioning envelope and its next page`() {
        val body = """
            {"collection":[{"id":3,"body":"x","timestamp":1000,"user":{"username":"c"}}],
             "next_href":"https://api.soundcloud.com/tracks/99/comments?offset=abc&limit=200"}
        """.trimIndent()

        assertEquals(1, SoundCloudCommentParser.parseComments(body).size)
        assertEquals(
            "https://api.soundcloud.com/tracks/99/comments?offset=abc&limit=200",
            SoundCloudCommentParser.nextHref(body),
        )
    }

    @Test
    fun `a bare array has no next page`() {
        assertNull(SoundCloudCommentParser.nextHref("""[{"id":1}]"""))
        assertNull(SoundCloudCommentParser.nextHref("""{"collection":[]}"""))
        assertNull(SoundCloudCommentParser.nextHref("""{"collection":[],"next_href":"  "}"""))
    }

    @Test
    fun `unknown fields are ignored rather than fatal`() {
        val body = """[{"id":1,"body":"x","timestamp":1000,"user":{"username":"c"},"brand_new_field":{"a":1}}]"""
        assertEquals(1, SoundCloudCommentParser.parseComments(body).size)
    }

    @Test
    fun `garbage reads as an empty list, never an exception`() {
        for (body in listOf("", "   ", "not json", """{"error":"401 Unauthorized"}""", "[", """[{"id":}]""")) {
            assertTrue(
                "expected empty for <$body>",
                SoundCloudCommentParser.parseComments(body).isEmpty(),
            )
            assertTrue(SoundCloudCommentParser.parseTracks(body).isEmpty())
        }
    }

    @Test
    fun `parses a single track object`() {
        val body = """
            {"id":99,"title":"Nightcall","duration":250000,"permalink_url":"https://soundcloud.com/a/b",
             "comment_count":12,"commentable":true,"user":{"username":"kavinsky"}}
        """.trimIndent()

        val track = SoundCloudCommentParser.parseTrack(body)
        assertNotNull(track)
        assertEquals(99L, track!!.id)
        assertEquals("Nightcall", track.title)
        assertEquals(250_000L, track.duration)
        assertEquals(12, track.commentCount)
        assertEquals(true, track.commentable)
    }

    @Test
    fun `a single track parse rejects non-objects and error bodies`() {
        assertNull(SoundCloudCommentParser.parseTrack(""))
        assertNull(SoundCloudCommentParser.parseTrack("""[{"id":1}]"""))
        assertNull(SoundCloudCommentParser.parseTrack("nope"))
        // An error envelope decodes to an all-null track, which cannot become a candidate.
        val error = SoundCloudCommentParser.parseTrack("""{"error":"404 - Not Found"}""")
        assertNull(error?.id)
        assertNull(SoundCloudCommentParser.toCandidate(error!!))
    }

    // ── DTO to domain ──────────────────────────────────────────────────────

    @Test
    fun `carries avatar, author url and permalink through`() {
        val comment = toComment(
            ScComment(id = 1, body = "nice", timestamp = 5_000, user = user()),
        )!!

        assertEquals("https://i1.sndcdn.com/avatars-alice.jpg", comment.avatarUrl)
        assertEquals("https://soundcloud.com/alice", comment.authorUrl)
        assertEquals("https://soundcloud.com/artist/track", comment.permalink)
        assertEquals("1", comment.id)
    }

    @Test
    fun `a comment with no timestamp is dropped`() {
        // SoundCloud allows untimed comments on a track. Those are ordinary comments, and placing
        // one on a seek bar would be a lie about where it belongs.
        assertNull(toComment(ScComment(id = 1, body = "great track", user = user())))
    }

    @Test
    fun `a negative or out-of-range timestamp is dropped`() {
        assertNull(toComment(ScComment(id = 1, body = "x", timestamp = -1, user = user())))
        assertNull(toComment(ScComment(id = 1, body = "x", timestamp = trackDurationMs + 1, user = user())))
        // Exactly at the end is still inside the media item.
        assertNotNull(toComment(ScComment(id = 1, body = "x", timestamp = trackDurationMs, user = user())))
    }

    @Test
    fun `an unknown duration means the upper bound is not enforced`() {
        assertNotNull(toComment(ScComment(id = 1, body = "x", timestamp = 999_999, user = user()), durationMs = null))
        assertNotNull(toComment(ScComment(id = 1, body = "x", timestamp = 999_999, user = user()), durationMs = 0))
    }

    @Test
    fun `a blank body or a missing id is dropped`() {
        assertNull(toComment(ScComment(id = 1, body = "   ", timestamp = 1_000, user = user())))
        assertNull(toComment(ScComment(id = 1, body = null, timestamp = 1_000, user = user())))
        // Ids are the list key and the dedupe key.
        assertNull(toComment(ScComment(id = null, body = "x", timestamp = 1_000, user = user())))
        assertNull(toComment(ScComment(id = 0, body = "x", timestamp = 1_000, user = user())))
    }

    @Test
    fun `an unattributable comment is dropped rather than given an invented author`() {
        assertNull(toComment(ScComment(id = 1, body = "x", timestamp = 1_000, user = null)))
        assertNull(toComment(ScComment(id = 1, body = "x", timestamp = 1_000, user = ScUser())))
        assertNull(
            toComment(
                ScComment(id = 1, body = "x", timestamp = 1_000, user = ScUser(username = "  ", permalink = "")),
            ),
        )
    }

    @Test
    fun `falls back to the permalink when there is no display name`() {
        val comment = toComment(
            ScComment(id = 1, body = "x", timestamp = 1_000, user = ScUser(username = null, permalink = "alice-2")),
        )
        assertEquals("alice-2", comment?.authorName)
    }

    @Test
    fun `like and reply counts stay null when the API does not expose them`() {
        // Null rather than zero, so the UI omits the affordance instead of asserting a false "0 likes".
        val comment = toComment(ScComment(id = 1, body = "x", timestamp = 1_000, user = user()))!!
        assertNull(comment.likeCount)
        assertNull(comment.replyCount)
    }

    @Test
    fun `toComments drops the unusable ones and keeps the rest`() {
        val out = SoundCloudCommentParser.toComments(
            listOf(
                ScComment(id = 1, body = "keep", timestamp = 1_000, user = user()),
                ScComment(id = 2, body = "no timestamp", user = user()),
                ScComment(id = 3, body = "keep too", timestamp = 2_000, user = user("bob")),
            ),
            trackId = "99",
            sourceName = "SoundCloud",
            trackDurationMs = trackDurationMs,
        )
        assertEquals(listOf("keep", "keep too"), out.map { it.text })
    }

    @Test
    fun `toCandidate keeps the fields matching needs and rejects an id-less track`() {
        val candidate = SoundCloudCommentParser.toCandidate(
            ScTrack(
                id = 99,
                title = "Nightcall",
                duration = 250_000,
                permalinkUrl = "https://soundcloud.com/a/b",
                commentCount = 12,
                commentable = false,
                user = user("kavinsky"),
            ),
        )!!
        assertEquals("99", candidate.id)
        assertEquals("Nightcall", candidate.title)
        assertEquals("kavinsky", candidate.uploaderName)
        assertEquals(250_000L, candidate.durationMs)
        assertEquals(false, candidate.commentable)
        assertEquals(12, candidate.commentCount)

        assertNull(SoundCloudCommentParser.toCandidate(ScTrack(title = "no id")))
        assertNull(SoundCloudCommentParser.toCandidate(ScTrack(id = 0, title = "zero")))
    }

    // ── created_at ─────────────────────────────────────────────────────────

    @Test
    fun `created_at parses in both shapes SoundCloud uses`() {
        val expected = LocalDateTime.of(2024, 5, 1, 10, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli()
        assertEquals(expected, SoundCloudCommentParser.parseCreatedAt("2024-05-01T10:00:00Z"))
        assertEquals(expected, SoundCloudCommentParser.parseCreatedAt("2024-05-01 10:00:00 +0000"))
        assertEquals(expected, SoundCloudCommentParser.parseCreatedAt("2024-05-01T10:00:00+00:00"))
    }

    @Test
    fun `an unparseable created_at is null rather than a thrown date exception`() {
        // Only ever used to order comments that share a timestamp, so "no creation date" is a fine
        // degradation and must not take down a response that otherwise parsed.
        assertNull(SoundCloudCommentParser.parseCreatedAt(null))
        assertNull(SoundCloudCommentParser.parseCreatedAt(""))
        assertNull(SoundCloudCommentParser.parseCreatedAt("yesterday"))
        assertNull(SoundCloudCommentParser.parseCreatedAt("2024-13-45T99:99:99Z"))
    }
}
