package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPanoBadgePlayFill (monochrome)
 * Viewport: 46.8652 x 29.9121
 */
public val SfSymbols.Monochrome.SFPanoBadgePlayFill: ImageVector
    get() {
        if (_sFPanoBadgePlayFill != null) {
            return _sFPanoBadgePlayFill!!
        }
        _sFPanoBadgePlayFill = sfIcon(
            name = "Monochrome.SFPanoBadgePlayFill",
            viewportWidth = 46.8652f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M38.9941 6.51367L38.9941 14.1699C34.6777 14.1699 31.123 17.7246 31.123 22.041C31.123 22.6661 31.1971 23.2749 31.3446 23.856C29.2733 23.2816 26.6461 22.7734 23.252 22.7734C15.4102 22.7734 11.709 25.4688 9.41406 25.4688C8.19336 25.4688 7.50977 24.7266 7.50977 23.4277L7.50977 6.51367C7.50977 5.21484 8.19336 4.47266 9.41406 4.47266C11.6992 4.47266 15.4395 7.17773 23.252 7.17773C31.0547 7.17773 34.8047 4.47266 37.0801 4.47266C38.3105 4.47266 38.9941 5.21484 38.9941 6.51367Z", fillAlpha = 0.85f)
            addSfPath("M45.3125 22.041C45.3125 25.498 42.4219 28.3496 38.9941 28.3496C35.5371 28.3496 32.6855 25.5176 32.6855 22.041C32.6855 18.584 35.5371 15.7227 38.9941 15.7227C42.4609 15.7227 45.3125 18.5742 45.3125 22.041ZM36.9336 19.3848L36.9336 24.6777C36.9336 25.1562 37.4121 25.3516 37.832 25.1074L42.1582 22.5391C42.5684 22.3047 42.5391 21.7676 42.0996 21.5039L37.832 18.9551C37.4219 18.7109 36.9336 18.9062 36.9336 19.3848Z", fillAlpha = 0.85f)
        }
        return _sFPanoBadgePlayFill!!
    }

private var _sFPanoBadgePlayFill: ImageVector? = null
