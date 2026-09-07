/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

/**
 * The catalogues timestamped comments can be read from, in the order the feature prefers them by
 * default.
 *
 * Three sources, three different answers to "whose comments are these":
 *
 *  - [AUDIUS] — native timed comments (`track_timestamp_s` on every comment), public API, no
 *    credentials of any kind. First by default because it needs nothing from the user and returns
 *    the richest data: likes, replies and threading are all real fields rather than guesses.
 *  - [YOUTUBE] — comments on the video Convxy is already playing, read through the same InnerTube
 *    path `ui/screens/CommentSheet.kt` uses. No credentials either, and by far the widest coverage
 *    for this app's catalogue, but the timestamp is a convention inside the comment text rather than
 *    a field, so only the subset of comments where a viewer wrote `[1:23]` is usable.
 *  - [SOUNDCLOUD] — the original provider: a real `timestamp` in milliseconds, but behind a
 *    registered application, so it only answers on a device where someone entered credentials.
 *
 * A stable string [id] rather than the enum name is what gets persisted, so renaming a constant later
 * cannot silently reset somebody's saved preference.
 */
enum class CommentSource(val id: String, val displayName: String) {
    AUDIUS("audius", "Audius"),
    YOUTUBE("youtube", "YouTube"),
    SOUNDCLOUD("soundcloud", "SoundCloud"),
    ;

    companion object {
        /**
         * The out-of-the-box priority: the sources that work with no setup first, the one that needs
         * credentials last. A user who has SoundCloud keys and prefers them can reorder it in
         * Settings → Integrations → Timestamped comments.
         */
        val DEFAULT_ORDER: List<CommentSource> = listOf(AUDIUS, YOUTUBE, SOUNDCLOUD)

        /**
         * What "every source is switched off" is stored as. Not a valid [id], so it can never be
         * confused with a real source, and not `""`, so it can never be confused with "unset".
         */
        const val NONE_ENABLED = "none"

        fun fromId(raw: String?): CommentSource? {
            val needle = raw?.trim()?.lowercase() ?: return null
            return entries.firstOrNull { it.id == needle }
        }

        /**
         * Reads the persisted priority list. The stored string is the *enabled* sources in order, so
         * a source absent from it is switched off.
         *
         * A blank or missing value means "never customised" and resolves to [DEFAULT_ORDER] with
         * everything on — which is what keeps this additive: a fresh install gets all three sources,
         * and nobody who never opened the settings screen can end up with the feature silently off.
         *
         * Unknown ids are dropped and duplicates collapse, so a hand-edited or truncated value
         * degrades to a shorter list rather than to a crash or an empty feature.
         *
         * Note the consequence for future providers: because absence means "off", a source added
         * later is off for anyone who has already customised this list, and on for everyone who has
         * not. That is the deliberate trade — respecting an explicit choice beats auto-enabling a new
         * network source somebody never asked for, and the settings screen shows the new entry so it
         * is one tap away.
         */
        fun parseEnabled(raw: String?): List<CommentSource> {
            // Absent, blank or the sentinel-less garbage of a truncated write all mean "never
            // customised", which resolves to everything on. NONE_ENABLED is the only way to say
            // "deliberately all off" — see serializeEnabled for why it cannot just be "".
            if (raw.isNullOrBlank()) return DEFAULT_ORDER
            if (raw.trim().equals(NONE_ENABLED, ignoreCase = true)) return emptyList()
            val seen = LinkedHashSet<CommentSource>()
            raw.split(',').forEach { piece -> fromId(piece)?.let(seen::add) }
            return seen.toList()
        }

        /**
         * Writes a priority list back. An empty list becomes [NONE_ENABLED] rather than `""`.
         *
         * That is not cosmetic. `""` is what the preference reads as when it has never been written,
         * and the settings screen seeds its state with that same default — so serialising "everything
         * off" to `""` would make it indistinguishable from "never configured", and a user who
         * switched all three sources off would find them all back on the next time they opened the
         * player. A sentinel keeps the two states apart through a round trip.
         */
        fun serializeEnabled(enabled: List<CommentSource>): String =
            if (enabled.isEmpty()) NONE_ENABLED else enabled.joinToString(",") { it.id }

        /**
         * [order] with [source] removed (disabled) or re-inserted at its default position (enabled).
         *
         * Re-inserting by default rank rather than appending is deliberate: turning YouTube off and
         * back on should put it back where it was, not demote it below SoundCloud, because the user
         * toggled it — they did not ask to re-rank it.
         */
        fun toggled(order: List<CommentSource>, source: CommentSource, enabled: Boolean): List<CommentSource> {
            if (!enabled) return order.filterNot { it == source }
            if (order.contains(source)) return order
            val without = order.toMutableList()
            val target = DEFAULT_ORDER.indexOf(source)
            // First position whose default rank is lower than this source's — that keeps the
            // surviving entries' relative order intact and slots the new one in by default rank.
            val insertAt = without.indexOfFirst { DEFAULT_ORDER.indexOf(it) > target }
                .takeIf { it >= 0 } ?: without.size
            without.add(insertAt, source)
            return without
        }
    }
}
