package com.convxy.music.comments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The priority preference is the one piece of this feature a user can corrupt: it is a comma-separated
 * string in DataStore that decides which network source gets asked first, and it is read on every
 * track change. So the parsing is total by construction and these are the cases that prove it —
 * a blank value, a truncated value, a value written by a future build that knows a source this one
 * does not, and a value with the same source listed twice.
 */
class CommentSourceTest {

    @Test
    fun `an unset preference means every source, in default order`() {
        // The fresh-install case. Nothing is written yet, and the feature must work out of the box
        // rather than reporting itself unconfigured.
        assertEquals(CommentSource.DEFAULT_ORDER, CommentSource.parseEnabled(null))
        assertEquals(CommentSource.DEFAULT_ORDER, CommentSource.parseEnabled(""))
        assertEquals(CommentSource.DEFAULT_ORDER, CommentSource.parseEnabled("   "))
    }

    @Test
    fun `a stored order is honoured exactly, including a demotion`() {
        val promoted = CommentSource.parseEnabled("soundcloud,audius,youtube")
        assertEquals(
            listOf(CommentSource.SOUNDCLOUD, CommentSource.AUDIUS, CommentSource.YOUTUBE),
            promoted,
        )
    }

    @Test
    fun `a source missing from the stored list is switched off`() {
        val withoutYouTube = CommentSource.parseEnabled("audius,soundcloud")
        assertEquals(listOf(CommentSource.AUDIUS, CommentSource.SOUNDCLOUD), withoutYouTube)
        assertTrue(CommentSource.YOUTUBE !in withoutYouTube)
    }

    @Test
    fun `all sources off survives a round trip`() {
        // The trap this guards: serializeEnabled(emptyList()) must NOT be "", because "" is what an
        // unwritten preference reads as, and "unset" means "everything on". Without a sentinel a user
        // who switched all three sources off would find them back on at the next player open.
        val serialized = CommentSource.serializeEnabled(emptyList())
        assertTrue("empty must not serialise to blank", serialized.isNotBlank())
        assertTrue(CommentSource.parseEnabled(serialized).isEmpty())
    }

    @Test
    fun `a hand-truncated value degrades to nothing rather than to everything`() {
        // "," is what a partial write or a hand-edit leaves behind: no recognisable id, but also not
        // blank, so it must not fall through to the "never customised" default.
        assertTrue(CommentSource.parseEnabled(",").isEmpty())
    }

    @Test
    fun `unknown ids are dropped rather than fatal`() {
        // A preference written by a newer build that knows a fourth source. This build must read the
        // two it recognises and ignore the rest, not fail and not fall back to "everything on".
        val parsed = CommentSource.parseEnabled("spotify,audius,bandcamp,youtube")
        assertEquals(listOf(CommentSource.AUDIUS, CommentSource.YOUTUBE), parsed)
    }

    @Test
    fun `duplicates collapse to the first position`() {
        val parsed = CommentSource.parseEnabled("youtube,audius,youtube")
        assertEquals(listOf(CommentSource.YOUTUBE, CommentSource.AUDIUS), parsed)
    }

    @Test
    fun `ids are matched case-insensitively and trimmed`() {
        assertEquals(
            listOf(CommentSource.AUDIUS, CommentSource.YOUTUBE),
            CommentSource.parseEnabled(" AUDIUS , YouTube "),
        )
    }

    @Test
    fun `serialising what was parsed round-trips`() {
        for (raw in listOf("youtube,soundcloud,audius", "audius", CommentSource.NONE_ENABLED)) {
            assertEquals(raw, CommentSource.serializeEnabled(CommentSource.parseEnabled(raw)))
        }
    }

    @Test
    fun `every id is unique and stable`() {
        // The persisted form. A collision would make one toggle silently control two sources.
        val ids = CommentSource.entries.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        assertEquals(
            listOf("audius", "youtube", "soundcloud"),
            CommentSource.DEFAULT_ORDER.map { it.id },
        )
    }

    // ── toggling ───────────────────────────────────────────────────────────

    @Test
    fun `switching a source off removes it and leaves the rest in order`() {
        val result = CommentSource.toggled(
            listOf(CommentSource.AUDIUS, CommentSource.YOUTUBE, CommentSource.SOUNDCLOUD),
            CommentSource.YOUTUBE,
            enabled = false,
        )
        assertEquals(listOf(CommentSource.AUDIUS, CommentSource.SOUNDCLOUD), result)
    }

    @Test
    fun `switching a source back on restores its default rank, not the bottom`() {
        // Turning YouTube off and on again is a toggle, not a re-ranking. Appending would quietly
        // demote it below SoundCloud and the user would have no idea their order had changed.
        val off = listOf(CommentSource.AUDIUS, CommentSource.SOUNDCLOUD)
        val backOn = CommentSource.toggled(off, CommentSource.YOUTUBE, enabled = true)
        assertEquals(
            listOf(CommentSource.AUDIUS, CommentSource.YOUTUBE, CommentSource.SOUNDCLOUD),
            backOn,
        )
    }

    @Test
    fun `toggling on an already-enabled source changes nothing`() {
        val order = listOf(CommentSource.SOUNDCLOUD, CommentSource.AUDIUS)
        assertEquals(order, CommentSource.toggled(order, CommentSource.AUDIUS, enabled = true))
    }

    @Test
    fun `re-enabling every source from an empty list restores the default order`() {
        var order = emptyList<CommentSource>()
        // Toggled in an arbitrary order; the result must still be the default ranking, because each
        // source is inserted by its default rank rather than appended.
        listOf(CommentSource.SOUNDCLOUD, CommentSource.AUDIUS, CommentSource.YOUTUBE).forEach {
            order = CommentSource.toggled(order, it, enabled = true)
        }
        assertEquals(CommentSource.DEFAULT_ORDER, order)
    }

    @Test
    fun `toggling never produces duplicates or drops an unrelated source`() {
        var order = CommentSource.DEFAULT_ORDER
        repeat(3) {
            CommentSource.entries.forEach { source ->
                order = CommentSource.toggled(order, source, enabled = false)
                order = CommentSource.toggled(order, source, enabled = true)
                assertEquals(order.size, order.toSet().size)
                assertEquals(CommentSource.entries.size, order.size)
            }
        }
    }
}
