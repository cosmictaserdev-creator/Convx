package com.convx.music.library

import kotlin.test.Test
import kotlin.test.assertEquals

class PlaylistDeletionSyncTest {
    @Test
    fun keepsPendingPlaylistsHiddenAndRetriesOnlyThoseStillRemote() {
        val plan = planPlaylistDeletionSync(
            pendingBrowseIds = setOf("still-remote", "already-removed"),
            remoteBrowseIds = setOf("still-remote", "normal-playlist"),
        )

        assertEquals(setOf("still-remote"), plan.browseIdsToRetry)
        assertEquals(setOf("normal-playlist"), plan.browseIdsToImport)
        assertEquals(setOf("still-remote"), plan.remainingPendingBrowseIds)
    }

    @Test
    fun clearsPendingIdsWhenRemoteNoLongerListsThem() {
        val plan = planPlaylistDeletionSync(
            pendingBrowseIds = setOf("deleted-playlist"),
            remoteBrowseIds = setOf("normal-playlist"),
        )

        assertEquals(emptySet(), plan.browseIdsToRetry)
        assertEquals(setOf("normal-playlist"), plan.browseIdsToImport)
        assertEquals(emptySet(), plan.remainingPendingBrowseIds)
    }
}
