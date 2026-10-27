package com.convx.music.client.desktop

import javafx.application.Platform

internal object JavaFxRuntime {
    private val startupLock = Any()
    private val startupComplete = java.util.concurrent.CountDownLatch(1)
    @Volatile private var startupRequested = false
    @Volatile private var initialized = false
    val isInitialized: Boolean get() = initialized

    fun close() { if (initialized) Platform.exit() }

    fun run(action: () -> Unit) {
        ensureStarted()
        if (Platform.isFxApplicationThread()) action() else Platform.runLater(action)
    }

    private fun ensureStarted() {
        if (!startupRequested) synchronized(startupLock) {
            if (!startupRequested) {
                startupRequested = true
                try {
                    Platform.startup {
                        Platform.setImplicitExit(false)
                        initialized = true
                        startupComplete.countDown()
                    }
                } catch (_: IllegalStateException) {
                    // JavaFX may already have been started by another desktop feature.
                    initialized = true
                    startupComplete.countDown()
                }
            }
        }
        startupComplete.await()
    }
}
