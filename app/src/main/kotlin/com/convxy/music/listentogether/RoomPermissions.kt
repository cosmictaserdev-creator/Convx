/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.listentogether

/**
 * Who is allowed to do what inside a room.
 *
 * The server enforces the coarse rule — in [ControlModes.OWNER] it refuses
 * playback actions from anyone but the host. It does not know about the finer
 * one: in [ControlModes.EVERYONE] a track belongs to whoever queued it, and only
 * that person or the host may skip past it. That rule lives here, on the client,
 * and is advisory — it decides what the UI offers, not what the server accepts.
 * A modified client could still send the action; the point is that the ordinary
 * one presents an honest interface rather than buttons that silently fail.
 *
 * Pure functions on purpose: this is the logic that decides whether a control is
 * greyed out, and it is much easier to be sure of when it can be tested directly.
 */
object RoomPermissions {

    /**
     * True when [userId] may send playback actions at all (play/pause/seek).
     *
     * @param isHost whether [userId] owns the room.
     * @param controlMode one of [ControlModes].
     */
    fun canControlPlayback(isHost: Boolean, controlMode: String): Boolean =
        isHost || controlMode == ControlModes.EVERYONE

    /**
     * True when [userId] may skip [track].
     *
     * The host may always skip. Otherwise the room has to be in
     * [ControlModes.EVERYONE], and the track must be one this user queued.
     *
     * A track with no [TrackInfo.suggestedBy] came from the host's own queue
     * rather than from a request, so nobody but the host owns it — guests get a
     * request action instead of a skip.
     */
    fun canSkipTrack(
        track: TrackInfo?,
        userId: String?,
        isHost: Boolean,
        controlMode: String,
    ): Boolean {
        if (isHost) return true
        if (controlMode != ControlModes.EVERYONE) return false
        if (track == null || userId == null) return false
        return track.suggestedBy == userId
    }

    /**
     * True when [userId] may remove [track] from the queue. Same ownership rule
     * as skipping: your own request is yours to withdraw, everything else is the
     * host's.
     */
    fun canRemoveTrack(
        track: TrackInfo?,
        userId: String?,
        isHost: Boolean,
        controlMode: String,
    ): Boolean = canSkipTrack(track, userId, isHost, controlMode)

    /**
     * Why a control is unavailable, for the message shown when someone taps it.
     * Null when the action is allowed.
     */
    fun skipDeniedReason(
        track: TrackInfo?,
        userId: String?,
        isHost: Boolean,
        controlMode: String,
    ): DeniedReason? = when {
        canSkipTrack(track, userId, isHost, controlMode) -> null
        controlMode == ControlModes.OWNER -> DeniedReason.OWNER_ONLY_MODE
        else -> DeniedReason.NOT_YOUR_TRACK
    }

    enum class DeniedReason {
        /** Room is in owner-only mode; only the host controls playback. */
        OWNER_ONLY_MODE,

        /** Everyone-mode, but this track belongs to someone else. */
        NOT_YOUR_TRACK,
    }
}
