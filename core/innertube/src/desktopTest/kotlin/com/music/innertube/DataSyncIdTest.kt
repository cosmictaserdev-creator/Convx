package com.music.innertube

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * onBehalfOfUser only accepts a bare id — a DATASYNC_ID still carrying "||"
 * makes every authenticated request fail with HTTP 500, which is what broke login.
 */
class DataSyncIdTest {
    @Test
    fun normalizesDataSyncId() {
        val innerTube = InnerTube()

        innerTube.dataSyncId = "AbCd1234||"          // primary account
        assertEquals("AbCd1234", innerTube.dataSyncId)

        // Leading half, not trailing: the trailing half is the Google account, the
        // same value for every channel on it, so keeping it reverts channel switches.
        innerTube.dataSyncId = "AbCd1234||EfGh5678"  // second/brand channel
        assertEquals("AbCd1234", innerTube.dataSyncId)

        innerTube.dataSyncId = "AbCd1234"            // already clean
        assertEquals("AbCd1234", innerTube.dataSyncId)

        innerTube.dataSyncId = "null"
        assertEquals(null, innerTube.dataSyncId)

        innerTube.dataSyncId = ""
        assertEquals(null, innerTube.dataSyncId)
    }
}
