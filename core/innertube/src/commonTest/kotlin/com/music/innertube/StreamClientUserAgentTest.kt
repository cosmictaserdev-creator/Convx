package com.music.innertube

import com.music.innertube.models.YouTubeClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class StreamClientUserAgentTest {
    @Test
    fun signedStreamClientKeepsItsMatchingAgent() {
        val client = YouTubeClient.ANDROID_VR_1_61_48
        assertEquals(client.userAgent, YouTubeClient.userAgentForStreamUrl(
            "https://a.googlevideo.com/videoplayback?c=${client.clientName}&cver=${client.clientVersion}"))
    }

    @Test
    fun unknownClientsAndMalformedVersionsDoNotCreateInvalidHeaders() {
        assertEquals(YouTubeClient.USER_AGENT_WEB,
            YouTubeClient.userAgentForStreamUrl("https://a.googlevideo.com/videoplayback?c=unknown"))
        val agent = YouTubeClient.userAgentForStreamUrl("https://a.googlevideo.com/videoplayback?c=IOS&cver=%0D%0AInjected")
        assertFalse(agent.contains('\r'))
        assertFalse(agent.contains('\n'))
    }
}
