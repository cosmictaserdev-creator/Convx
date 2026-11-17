package com.convx.music

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRoutePolicyTest {
    @Test fun screensWithTheirOwnBlurDoNotReceiveShellBlur() {
        listOf(
            "mood_and_genres", "new_release", "charts_screen", "local_folders",
            "radio_station/song?title=Title", "radio_station/{seedId}?title={title}&artist={artist}&thumbnail={thumbnail}",
            "youtube_browse/category?params=items", "youtube_browse/{browseId}?params={params}",
        ).forEach { assertFalse(it, AppRoutePolicy.showsFallbackTopHaze(it)) }
    }

    @Test fun immersiveRoutesNeverReceiveTopHaze() {
        listOf("wrapped", "ambient_mode").forEach {
            assertFalse(it, AppRoutePolicy.showsFallbackTopHaze(it))
        }
    }

    @Test fun existingDetailAndRootPoliciesStayUnchanged() {
        assertFalse(AppRoutePolicy.showsFallbackTopHaze("artist/artist-id"))
        assertFalse(AppRoutePolicy.showsFallbackTopHaze("album/album-id"))
        assertFalse(AppRoutePolicy.showsFallbackTopHaze("home"))
        assertFalse(AppRoutePolicy.showsFallbackTopHaze(null))
        assertTrue(AppRoutePolicy.showsFallbackTopHaze("unhandled_route"))
        assertTrue(AppRoutePolicy.showsFallbackTopHaze("radio_stationary/example"))
    }
}
