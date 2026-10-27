/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.listentogether

/** Client-side control checks; the server remains authoritative for room actions. */
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
