package com.convxy.music.comments.audius

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Parsing an Audius payload into something the timeline can trust.
 *
 * The fixtures below are trimmed from real `api.audius.co` responses — same field names, same shapes,
 * including the two things most likely to be got wrong: `track_timestamp_s` is in **seconds** where
 * SoundCloud's equivalent is milliseconds, and `created_at` carries a variable-precision fractional
 * part (`…:54.20994Z` and `…:00Z` both appear in one response).
 *
 * The other half of these tests is about what gets *dropped*. A comment with no timestamp, a deleted
 * comment, a moderated one, and one whose author cannot be resolved all have to disappear rather than
 * be shown with a guessed value, because each of those guesses would look plausible on screen.
 */
class AudiusCommentParserTest {

    /** A real-shaped comments page: one timed comment, one untimed, two referenced users, the track. */
    private val pageJson = """
        {
          "data": [
            {"id":"M6WoEGV","entity_type":"Track","entity_id":"Jb3xzj7","user_id":"e4Ybn",
             "message":"this remix bouta be blowing up speakers","track_timestamp_s":99,
             "is_muted":false,"is_edited":false,"is_tombstone":false,"react_count":4,
             "reply_count":2,"parent_comment_id":null,
             "created_at":"2026-09-01T20:30:45.851749Z"},
            {"id":"ydj1mG7","entity_type":"Track","entity_id":"Jb3xzj7","user_id":"BRkKbl1",
             "message":"no timestamp on this one","track_timestamp_s":null,
             "is_tombstone":false,"react_count":0,"reply_count":0,
             "created_at":"2026-08-30T22:12:32.017228Z"}
          ],
          "related": {
            "tracks": [{"id":"Jb3xzj7","title":"Washed Out - Feel It All Around (Jay Bird Remix)",
                        "duration":285,"permalink":"/jayb1rdmusic/washed-out",
                        "comment_count":16,"comments_disabled":false,"isrc":null}],
            "users": [
              {"id":"e4Ybn","name":"Ana","handle":"anamusic","is_verified":false,
               "profile_picture":{"150x150":"https://img/150.jpg","480x480":"https://img/480.jpg"}},
              {"id":"BRkKbl1","name":"Bo","handle":"bo"}
            ]
          }
        }
    """.trimIndent()

    private fun page(json: String = pageJson) = AudiusCommentParser.parseCommentPage(json)

    // ── the comments page ──────────────────────────────────────────────────

    @Test
    fun `a timed comment keeps its position, author, avatar and engagement`() {
        val comments = AudiusCommentParser.toComments(
            page = page(),
            trackId = "Jb3xzj7",
            sourceName = "Audius",
            trackDurationMs = 285_000L,
        )

        assertEquals(1, comments.size)
        val only = comments.single()
        assertEquals("M6WoEGV", only.id)
        // Seconds on the wire, milliseconds in the model — the one unit conversion in this source.
        assertEquals(99_000L, only.timestampMs)
        assertEquals("this remix bouta be blowing up speakers", only.text)
        assertEquals("Ana", only.authorName)
        // Smallest available size: a comment row avatar is ~32dp, so 150px is already several times that.
        assertEquals("https://img/150.jpg", only.avatarUrl)
        assertEquals("https://audius.co/anamusic", only.authorUrl)
        // Audius exposes both counts, unlike SoundCloud's official API, so these are real numbers.
        assertEquals(4, only.likeCount)
        assertEquals(2, only.replyCount)
        assertEquals("Audius", only.sourceName)
    }

    @Test
    fun `the permalink comes from the inlined track when the caller has none`() {
        val comments = AudiusCommentParser.toComments(
            page = page(),
            trackId = "Jb3xzj7",
            sourceName = "Audius",
            trackDurationMs = null,
        )
        assertEquals("https://audius.co/jayb1rdmusic/washed-out", comments.single().permalink)
    }

    @Test
    fun `a permalink the caller resolved beats the inlined one`() {
        val comments = AudiusCommentParser.toComments(
            page = page(),
            trackId = "Jb3xzj7",
            sourceName = "Audius",
            trackDurationMs = null,
            trackPermalink = "/someone/else",
        )
        assertEquals("https://audius.co/someone/else", comments.single().permalink)
    }

    @Test
    fun `a comment with no timestamp is dropped rather than pinned to zero`() {
        // Audius lets you comment without the player running. That is a real comment about the whole
        // track, and showing it at 0:00 would assert it belongs at the start.
        val ids = page().comments.mapNotNull { it.id }
        assertEquals(listOf("M6WoEGV", "ydj1mG7"), ids)

        val kept = AudiusCommentParser.toComments(page(), "t", "Audius", null)
        assertEquals(listOf("M6WoEGV"), kept.map { it.id })
    }

    @Test
    fun `deleted and moderated comments are dropped`() {
        val tombstoned = page("""
            {"data":[{"id":"a","entity_type":"Track","user_id":"u","message":"gone",
                      "track_timestamp_s":10,"is_tombstone":true}],
             "related":{"users":[{"id":"u","name":"Ana","handle":"ana"}]}}
        """.trimIndent())
        val muted = page("""
            {"data":[{"id":"b","entity_type":"Track","user_id":"u","message":"held",
                      "track_timestamp_s":10,"is_muted":true}],
             "related":{"users":[{"id":"u","name":"Ana","handle":"ana"}]}}
        """.trimIndent())
        assertTrue(AudiusCommentParser.toComments(tombstoned, "t", "Audius", null).isEmpty())
        assertTrue(AudiusCommentParser.toComments(muted, "t", "Audius", null).isEmpty())
    }

    @Test
    fun `a comment on something other than a track is dropped`() {
        val onPlaylist = page("""
            {"data":[{"id":"p","entity_type":"Playlist","user_id":"u","message":"nice list",
                      "track_timestamp_s":10}],
             "related":{"users":[{"id":"u","name":"Ana","handle":"ana"}]}}
        """.trimIndent())
        assertTrue(AudiusCommentParser.toComments(onPlaylist, "t", "Audius", null).isEmpty())

        // An omitted entity_type is not evidence of the wrong kind, so it is kept.
        val untyped = page("""
            {"data":[{"id":"q","user_id":"u","message":"ok","track_timestamp_s":10}],
             "related":{"users":[{"id":"u","name":"Ana","handle":"ana"}]}}
        """.trimIndent())
        assertEquals(1, AudiusCommentParser.toComments(untyped, "t", "Audius", null).size)
    }

    @Test
    fun `an unattributable comment is dropped rather than given an invented author`() {
        // No matching entry in related.users: we have a user_id and nothing a human would recognise.
        val orphan = page("""
            {"data":[{"id":"o","entity_type":"Track","user_id":"missing","message":"who said this",
                      "track_timestamp_s":10}]}
        """.trimIndent())
        assertTrue(AudiusCommentParser.toComments(orphan, "t", "Audius", null).isEmpty())
    }

    @Test
    fun `a handle stands in for a display name, because it is theirs and not invented`() {
        val handled = page("""
            {"data":[{"id":"h","entity_type":"Track","user_id":"u","message":"ok",
                      "track_timestamp_s":10}],
             "related":{"users":[{"id":"u","name":"   ","handle":"anamusic"}]}}
        """.trimIndent())
        assertEquals("anamusic", AudiusCommentParser.toComments(handled, "t", "Audius", null).single().authorName)
    }

    @Test
    fun `a timestamp past the end of the track is dropped`() {
        val late = page("""
            {"data":[{"id":"l","entity_type":"Track","user_id":"u","message":"way out",
                      "track_timestamp_s":9999}],
             "related":{"users":[{"id":"u","name":"Ana","handle":"ana"}]}}
        """.trimIndent())
        assertTrue(AudiusCommentParser.toComments(late, "t", "Audius", 285_000L).isEmpty())
        // Unknown duration means the check is skipped, not that everything is rejected.
        assertEquals(1, AudiusCommentParser.toComments(late, "t", "Audius", null).size)
    }

    @Test
    fun `a blank message or a missing id is dropped`() {
        val blank = page("""
            {"data":[{"id":"x","entity_type":"Track","user_id":"u","message":"   ",
                      "track_timestamp_s":10}],
             "related":{"users":[{"id":"u","name":"Ana","handle":"ana"}]}}
        """.trimIndent())
        assertTrue(AudiusCommentParser.toComments(blank, "t", "Audius", null).isEmpty())

        val idless = page("""
            {"data":[{"entity_type":"Track","user_id":"u","message":"no id",
                      "track_timestamp_s":10}],
             "related":{"users":[{"id":"u","name":"Ana","handle":"ana"}]}}
        """.trimIndent())
        assertTrue(AudiusCommentParser.toComments(idless, "t", "Audius", null).isEmpty())
    }

    @Test
    fun `a negative timestamp is dropped`() {
        val negative = page("""
            {"data":[{"id":"n","entity_type":"Track","user_id":"u","message":"before the start",
                      "track_timestamp_s":-5}],
             "related":{"users":[{"id":"u","name":"Ana","handle":"ana"}]}}
        """.trimIndent())
        assertTrue(AudiusCommentParser.toComments(negative, "t", "Audius", null).isEmpty())
    }

    // ── resilience ─────────────────────────────────────────────────────────

    @Test
    fun `garbage reads as an empty page, never an exception`() {
        // Audius is served by community-run discovery nodes; a half-broken one must cost a "could not
        // load" state, not a crash on the player's coroutine.
        for (body in listOf("", "   ", "not json", """{"error":"no such track"}""", "[", """{"data":[]}""")) {
            val parsed = AudiusCommentParser.parseCommentPage(body)
            assertTrue(parsed.comments.isEmpty())
            assertTrue(parsed.usersById.isEmpty())
        }
    }

    @Test
    fun `a page with no related block still parses`() {
        val bare = page("""{"data":[{"id":"z","entity_type":"Track","user_id":"u",
            "message":"ok","track_timestamp_s":5}]}""")
        assertEquals(1, bare.comments.size)
        assertTrue(bare.usersById.isEmpty())
        assertNull(bare.track)
        // No user to attribute it to, so nothing survives to the timeline.
        assertTrue(AudiusCommentParser.toComments(bare, "t", "Audius", null).isEmpty())
    }

    // ── hosts, tracks, candidates ──────────────────────────────────────────

    @Test
    fun `the discovery document yields its host list`() {
        val hosts = AudiusCommentParser.parseHosts(
            """{"data":["https://audius-01.example.com","https://audius-02.example.com/"]}"""
        )
        assertEquals(2, hosts.size)
        assertTrue(hosts.all { it.isNotBlank() })
        assertTrue(AudiusCommentParser.parseHosts("garbage").isEmpty())
        assertTrue(AudiusCommentParser.parseHosts("""{"data":[]}""").isEmpty())
    }

    @Test
    fun `a search response yields tracks`() {
        val tracks = AudiusCommentParser.parseTracks(
            """{"data":[{"id":"A9m9Rw7","title":"Nightcall","duration":160,
               "permalink":"/Vatroob/nightcall","comment_count":0,"comments_disabled":false,
               "user":{"id":"x5l02","name":"Vatroob Beats","handle":"Vatroob"}}]}"""
        )
        assertEquals(1, tracks.size)
        assertEquals("Nightcall", tracks.single().title)
    }

    @Test
    fun `a bare track object parses as a single track`() {
        val track = AudiusCommentParser.parseTrack("""{"id":"A9m9Rw7","title":"Nightcall","duration":160}""")
        assertEquals("A9m9Rw7", track?.id)
    }

    @Test
    fun `a candidate converts seconds to milliseconds once, here`() {
        val track = AudiusCommentParser.parseTrack(
            """{"id":"A9m9Rw7","title":"Nightcall","duration":160,"permalink":"/Vatroob/nightcall",
               "comment_count":3,"comments_disabled":false,
               "user":{"id":"x5l02","name":"Vatroob Beats","handle":"Vatroob"}}"""
        )!!
        val candidate = AudiusCommentParser.toCandidate(track)

        assertEquals("A9m9Rw7", candidate?.id)
        // 160s, not 160ms. Getting this wrong would fail every duration gate and match nothing.
        assertEquals(160_000L, candidate?.durationMs)
        assertEquals("Vatroob Beats", candidate?.artistName)
        assertEquals("/Vatroob/nightcall", candidate?.permalink)
        assertEquals(false, candidate?.commentsDisabled)
        assertEquals(3, candidate?.commentCount)
    }

    @Test
    fun `a candidate falls back to the handle when the artist set no display name`() {
        val handled = AudiusCommentParser.toCandidate(
            AudiusTrack(id = "x", title = "Song", duration = 100, user = AudiusUser(id = "u", name = "", handle = "someone"))
        )
        assertEquals("someone", handled?.artistName)

        val nameless = AudiusCommentParser.toCandidate(
            AudiusTrack(id = "x", title = "Song", duration = 100, user = null)
        )
        assertEquals("", nameless?.artistName)
    }

    @Test
    fun `a candidate with no id or no title cannot be used`() {
        assertNull(AudiusCommentParser.toCandidate(AudiusTrack(id = null, title = "Song")))
        assertNull(AudiusCommentParser.toCandidate(AudiusTrack(id = "x", title = "   ")))
    }

    @Test
    fun `a missing duration does not disqualify a candidate`() {
        // Unknown length means the duration gate is skipped, not that the track is unusable.
        val candidate = AudiusCommentParser.toCandidate(AudiusTrack(id = "x", title = "Song", duration = null))
        assertEquals(0L, candidate?.durationMs)
    }

    // ── created_at ─────────────────────────────────────────────────────────

    @Test
    fun `created_at parses at whatever precision Audius wrote it`() {
        // Both of these appear in one real response: microsecond precision and none at all.
        val precise = AudiusCommentParser.parseCreatedAt("2026-09-01T20:30:45.851749Z")
        val whole = AudiusCommentParser.parseCreatedAt("2026-08-30T21:48:00Z")
        assertNotNull(precise)
        assertNotNull(whole)
        assertTrue(precise!! > whole!!)
        assertEquals(whole, AudiusCommentParser.parseCreatedAt("2026-08-30T21:48:00.000Z"))
    }

    @Test
    fun `an unparseable created_at is null rather than a thrown date exception`() {
        assertNull(AudiusCommentParser.parseCreatedAt(null))
        assertNull(AudiusCommentParser.parseCreatedAt(""))
        assertNull(AudiusCommentParser.parseCreatedAt("3 days ago"))
        assertNull(AudiusCommentParser.parseCreatedAt("not-a-date"))
    }

    @Test
    fun `a space instead of a T is still read`() {
        // Defensive: a node writing a non-canonical separator should cost a missing "posted" label,
        // not the comment.
        assertNotNull(AudiusCommentParser.parseCreatedAt("2026-08-30 21:48:00Z"))
    }

    // ── artwork ────────────────────────────────────────────────────────────

    @Test
    fun `the smallest available avatar size wins`() {
        assertEquals(
            "https://img/150.jpg",
            AudiusArtwork(x150 = "https://img/150.jpg", x480 = "https://img/480.jpg").avatarUrl,
        )
        assertEquals(
            "https://img/480.jpg",
            AudiusArtwork(x150 = null, x480 = "https://img/480.jpg", x1000 = "https://img/1000.jpg").avatarUrl,
        )
        assertNull(AudiusArtwork().avatarUrl)
    }
}
