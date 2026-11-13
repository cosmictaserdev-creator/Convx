package com.music.innertube.pages

import com.music.innertube.models.Menu
import com.music.innertube.models.MusicResponsiveListItemRenderer.FlexColumn
import com.music.innertube.models.Run

object PageHelper {
    // Bookmark icons represent library state; KEEP/KEEP_OFF are Listen Again pins.
    private val LIBRARY_ADD_ICONS = setOf("LIBRARY_ADD", "BOOKMARK_BORDER")
    private val LIBRARY_SAVED_ICONS = setOf("LIBRARY_SAVED", "BOOKMARK", "LIBRARY_REMOVE")
    private val ALL_LIBRARY_ICONS = LIBRARY_ADD_ICONS + LIBRARY_SAVED_ICONS

    /**
     * Data class to hold both library feedback tokens extracted from a menu
     */
    data class LibraryFeedbackTokens(
        val addToken: String?,      // Token to add song to library (from BOOKMARK_BORDER)
        val removeToken: String?    // Token to remove song from library (from BOOKMARK)
    )

    /** Filters out Listen Again pins; only library add/remove icons count. */
    fun isLibraryIcon(iconType: String?): Boolean {
        return iconType != null &&
            (iconType in ALL_LIBRARY_ICONS || iconType.startsWith("LIBRARY_"))
    }

    fun isAddLibraryIcon(iconType: String?): Boolean {
        return iconType in LIBRARY_ADD_ICONS
    }

    fun isSavedLibraryIcon(iconType: String?): Boolean {
        return iconType in LIBRARY_SAVED_ICONS
    }

    fun extractRuns(columns: List<FlexColumn>, typeLike: String): List<Run> {
        val filteredRuns = mutableListOf<Run>()
        for (column in columns) {
            val runs = column.musicResponsiveListItemFlexColumnRenderer.text?.runs
                ?: continue

            for (run in runs) {
                val typeStr = run.navigationEndpoint?.watchEndpoint?.watchEndpointMusicSupportedConfigs?.watchEndpointMusicConfig?.musicVideoType
                    ?: run.navigationEndpoint?.browseEndpoint?.browseEndpointContextSupportedConfigs?.browseEndpointContextMusicConfig?.pageType
                    ?: continue

                if (typeLike in typeStr) {
                    filteredRuns.add(run)
                }
            }
        }
        return filteredRuns
    }

    /** Returns library add/remove tokens while ignoring unrelated "Listen Again" pins. */
    fun extractLibraryTokensFromMenuItems(
        menuItems: List<Menu.MenuRenderer.Item>?
    ): LibraryFeedbackTokens {
        if (menuItems == null) return LibraryFeedbackTokens(null, null)

        var addToken: String? = null
        var removeToken: String? = null

        for (item in menuItems) {
            val toggleRenderer = item.toggleMenuServiceItemRenderer ?: continue
            val iconType = toggleRenderer.defaultIcon.iconType

            if (!isLibraryIcon(iconType)) continue

            val defaultToken = toggleRenderer.defaultServiceEndpoint.feedbackEndpoint?.feedbackToken
            val toggledToken = toggleRenderer.toggledServiceEndpoint?.feedbackEndpoint?.feedbackToken

            when {
                isAddLibraryIcon(iconType) -> {
                    // BOOKMARK_BORDER or LIBRARY_ADD: default=add, toggled=remove
                    if (addToken == null) addToken = defaultToken
                    if (removeToken == null) removeToken = toggledToken
                }
                isSavedLibraryIcon(iconType) -> {
                    // BOOKMARK or LIBRARY_SAVED/REMOVE: default=remove, toggled=add
                    if (removeToken == null) removeToken = defaultToken
                    if (addToken == null) addToken = toggledToken
                }
            }

            if (addToken != null && removeToken != null) break
        }

        return LibraryFeedbackTokens(addToken, removeToken)
    }

    /**
     * Extract feedback token for library operations.
     *
     * YouTube's new icon system (Feb 2026):
     * - BOOKMARK_BORDER: Song NOT in library -> defaultToken = ADD, toggledToken = REMOVE
     * - BOOKMARK: Song IS in library -> defaultToken = REMOVE, toggledToken = ADD
     *
     * @param menu The toggle menu renderer containing the feedback tokens
     * @param type "LIBRARY_ADD" to get the add token, "LIBRARY_REMOVE" to get the remove token
     * @return The appropriate feedback token, or null if not found
     */
    fun extractFeedbackToken(menu: Menu.MenuRenderer.Item.ToggleMenuServiceRenderer?, type: String): String? {
        if (menu == null) return null
        val defaultToken = menu.defaultServiceEndpoint.feedbackEndpoint?.feedbackToken
        val toggledToken = menu.toggledServiceEndpoint?.feedbackEndpoint?.feedbackToken
        val iconType = menu.defaultIcon.iconType

        // Add-state uses default for add; saved-state uses default for remove.
        val songNotInLibrary = iconType in LIBRARY_ADD_ICONS

        return when (type) {
            "LIBRARY_ADD" -> {
                if (songNotInLibrary) {
                    defaultToken
                } else {
                    toggledToken
                }
            }
            "LIBRARY_REMOVE", "LIBRARY_SAVED" -> {
                if (songNotInLibrary) {
                    toggledToken
                } else {
                    defaultToken
                }
            }
            else -> if (iconType == type) defaultToken else toggledToken
        }
    }
}
