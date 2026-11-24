@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.convx.music.client.ios

import com.convx.music.client.watch.WatchCompanion
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue

/** Connects the native watch session to the injected shared playback owner. */
internal class IosWatchCompanion(private val companion: WatchCompanion) {
    private val notifications = NSNotificationCenter.defaultCenter
    private val token = notifications.addObserverForName("ConvxWatchCommand", null, NSOperationQueue.mainQueue) { note ->
        (note?.userInfo?.get("json") as? String)?.let(companion::receive)
    }
    private val observation = companion.observeSnapshots { snapshot ->
        notifications.postNotificationName("ConvxWatchSnapshot", null, mapOf("json" to snapshot))
    }

    fun close() {
        observation.close()
        notifications.removeObserver(token)
        companion.close()
    }
}
