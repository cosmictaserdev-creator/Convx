package com.convx.music.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.constants.HomeHeroCardEnabledKey
import com.convx.music.constants.HomeSectionHiddenKey
import com.convx.music.constants.HomeSectionOrderKey
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.RandomizeHomeOrderKey
import com.convx.music.constants.ShowHomeFabKey
import com.convx.music.constants.ShowHomeMoodFiltersKey
import com.convx.music.utils.cachedPreferenceSnapshot
import com.convx.music.utils.dataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Every read-only setting Home reacts to, behind one collector instead of one per key. */
@Immutable
internal data class HomePrefs(
    val innerTubeCookie: String = "",
    val randomizeOrder: Boolean = true,
    val heroCard: Boolean = true,
    val sectionOrder: String = "",
    val hiddenSections: String = "",
    val moodFilters: Boolean = true,
    val fab: Boolean = true,
)

internal fun Preferences.toHomePrefs() = HomePrefs(
    innerTubeCookie = this[InnerTubeCookieKey] ?: "",
    randomizeOrder = this[RandomizeHomeOrderKey] ?: true,
    heroCard = this[HomeHeroCardEnabledKey] ?: true,
    sectionOrder = this[HomeSectionOrderKey] ?: "",
    hiddenSections = this[HomeSectionHiddenKey] ?: "",
    moodFilters = this[ShowHomeMoodFiltersKey] ?: true,
    fab = this[ShowHomeFabKey] ?: true,
)

@Composable
internal fun rememberHomePrefs(): HomePrefs {
    val store = LocalContext.current.dataStore
    val initial = remember(store) { store.cachedPreferenceSnapshot()?.toHomePrefs() ?: HomePrefs() }
    return remember(store) { store.data.map { it.toHomePrefs() }.distinctUntilChanged() }
        .collectAsStateWithLifecycle(initial).value
}
