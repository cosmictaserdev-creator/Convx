package com.convx.music.playback

import coil3.disk.DiskCache

/** Refuse new artwork edits before the response body is consumed when automatic storage is full. */
internal class AdmissionDiskCache(private val delegate: DiskCache) : DiskCache by delegate {
    override fun openEditor(key: String): DiskCache.Editor? =
        if (PlayerCacheBudgetRegistry.canAdmitArtwork()) delegate.openEditor(key)?.let(::wrapEditor) else null

    override fun openSnapshot(key: String): DiskCache.Snapshot? = delegate.openSnapshot(key)?.let(::wrap)

    private fun wrap(snapshot: DiskCache.Snapshot): DiskCache.Snapshot = object : DiskCache.Snapshot by snapshot {
        override fun closeAndOpenEditor(): DiskCache.Editor? {
            if (!PlayerCacheBudgetRegistry.canAdmitArtwork()) {
                snapshot.close()
                return null
            }
            return snapshot.closeAndOpenEditor()?.let(::wrapEditor)
        }
    }

    private fun wrapEditor(editor: DiskCache.Editor): DiskCache.Editor = object : DiskCache.Editor by editor {
        override fun commitAndOpenSnapshot(): DiskCache.Snapshot? = editor.commitAndOpenSnapshot()?.let(::wrap)
    }
}
