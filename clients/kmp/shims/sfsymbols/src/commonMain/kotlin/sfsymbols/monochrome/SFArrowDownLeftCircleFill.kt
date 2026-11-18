package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownLeftCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowDownLeftCircleFill: ImageVector
    get() {
        if (_sFArrowDownLeftCircleFill != null) {
            return _sFArrowDownLeftCircleFill!!
        }
        _sFArrowDownLeftCircleFill = sfIcon(
            name = "Monochrome.SFArrowDownLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM16.2109 8.04688L10.9668 13.3008L9.21344 15.1637L9.39453 13.0469L9.39453 10.4492C9.39453 9.91211 9.07227 9.58008 8.57422 9.58008C8.08594 9.58008 7.76367 9.94141 7.76367 10.4688L7.76367 16.7676C7.76367 17.4121 8.11523 17.6855 8.68164 17.6855L15.0098 17.6855C15.5371 17.6855 15.8789 17.3633 15.8789 16.875C15.8789 16.377 15.5273 16.0547 14.9902 16.0547L12.1875 16.0547L10.2673 16.2437L12.1484 14.4727L17.4023 9.22852C17.5684 9.0625 17.6758 8.84766 17.6758 8.62305C17.6758 8.11523 17.3438 7.79297 16.8555 7.79297C16.582 7.79297 16.377 7.89062 16.2109 8.04688Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownLeftCircleFill!!
    }

private var _sFArrowDownLeftCircleFill: ImageVector? = null
