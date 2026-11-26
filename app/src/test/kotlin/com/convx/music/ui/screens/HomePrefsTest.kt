package com.convx.music.ui.screens

import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import com.convx.music.constants.HomeHeroCardEnabledKey
import com.convx.music.constants.HomeSectionHiddenKey
import com.convx.music.constants.HomeSectionOrderKey
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.RandomizeHomeOrderKey
import com.convx.music.constants.ShowHomeFabKey
import com.convx.music.constants.ShowHomeMoodFiltersKey
import org.junit.Assert.assertEquals
import org.junit.Test

class HomePrefsTest {
    @Test
    fun `nothing stored means the documented defaults`() {
        assertEquals(HomePrefs(), emptyPreferences().toHomePrefs())
    }

    @Test
    fun `every stored setting reaches Home`() {
        val prefs = preferencesOf(
            InnerTubeCookieKey to "SAPISID=x",
            RandomizeHomeOrderKey to false,
            HomeHeroCardEnabledKey to false,
            HomeSectionOrderKey to "a\nb",
            HomeSectionHiddenKey to "c",
            ShowHomeMoodFiltersKey to false,
            ShowHomeFabKey to false,
        ).toHomePrefs()

        assertEquals(
            HomePrefs(
                innerTubeCookie = "SAPISID=x",
                randomizeOrder = false,
                heroCard = false,
                sectionOrder = "a\nb",
                hiddenSections = "c",
                moodFilters = false,
                fab = false,
            ),
            prefs,
        )
    }
}
