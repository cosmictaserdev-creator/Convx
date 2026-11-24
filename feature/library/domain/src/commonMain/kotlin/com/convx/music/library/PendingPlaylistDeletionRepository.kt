package com.convx.music.library

/** Persistence boundary for remote playlist deletions awaiting confirmation from sync. */
interface PendingPlaylistDeletionRepository {
    suspend fun pendingBrowseIds(): Set<String>
    suspend fun recordPendingDelete(browseId: String)
    suspend fun removePendingBrowseIds(browseIds: Set<String>)
}

data class PlaylistDeletionSyncPlan(
    val browseIdsToRetry: Set<String>,
    val browseIdsToImport: Set<String>,
    val remainingPendingBrowseIds: Set<String>,
)

/** Retries remote deletes while keeping pending playlists out of the imported library. */
fun planPlaylistDeletionSync(
    pendingBrowseIds: Set<String>,
    remoteBrowseIds: Set<String>,
): PlaylistDeletionSyncPlan {
    val stillRemote = pendingBrowseIds intersect remoteBrowseIds
    return PlaylistDeletionSyncPlan(
        browseIdsToRetry = stillRemote,
        browseIdsToImport = remoteBrowseIds - pendingBrowseIds,
        remainingPendingBrowseIds = stillRemote,
    )
}
