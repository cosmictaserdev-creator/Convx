package com.convx.music.client.desktop

import javafx.scene.web.WebEngine
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class DesktopLoginSessionTest {
    @Test fun sessionCaptureReadsCurrentAndLegacyYouTubeConfigurationInTheActualWebEngine() {
        val result = CompletableFuture<List<String>>()
        JavaFxRuntime.run {
            try {
                val engine = WebEngine()
                val results = listOf(
                    "window.ytcfg = {get: function(key) { return key === 'VISITOR_DATA' ? 'current-visitor' : 'current-channel'; }};",
                    "window.ytcfg = {data_: {VISITOR_DATA: 'data-visitor', DATASYNC_ID: 'data-channel'}};",
                    "window.ytcfg = undefined; window.yt = {config_: {VISITOR_DATA: 'legacy-visitor', DATASYNC_ID: 'legacy-channel'}};",
                    "window.ytcfg = undefined; window.yt = undefined;",
                ).flatMap { setup ->
                    engine.executeScript(setup)
                    listOf("VISITOR_DATA", "DATASYNC_ID").map { engine.executeScript(desktopSessionConfigScript(it)).toString() }
                }
                engine.load(null)
                result.complete(results)
            } catch (error: Throwable) { result.completeExceptionally(error) }
        }
        assertEquals(listOf("current-visitor", "current-channel", "data-visitor", "data-channel", "legacy-visitor", "legacy-channel", "", ""), result.get(20, TimeUnit.SECONDS))
    }
}
