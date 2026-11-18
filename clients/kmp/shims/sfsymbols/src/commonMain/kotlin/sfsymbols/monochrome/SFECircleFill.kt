package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFECircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFECircleFill: ImageVector
    get() {
        if (_sFECircleFill != null) {
            return _sFECircleFill!!
        }
        _sFECircleFill = sfIcon(
            name = "Monochrome.SFECircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.70703 6.79688C9.14062 6.79688 8.83789 7.19727 8.83789 7.79297L8.83789 17.4707C8.83789 18.0762 9.14062 18.4766 9.70703 18.4766L15.8594 18.4766C16.2988 18.4766 16.6016 18.1836 16.6016 17.7344C16.6016 17.2852 16.2988 17.002 15.8594 17.002L10.6152 17.002L10.6152 13.2422L15.5273 13.2422C15.9668 13.2422 16.2695 12.9785 16.2695 12.5391C16.2695 12.0801 15.9668 11.8262 15.5273 11.8262L10.6152 11.8262L10.6152 8.27148L15.8594 8.27148C16.2988 8.27148 16.6016 7.96875 16.6016 7.5293C16.6016 7.07031 16.2988 6.79688 15.8594 6.79688Z", fillAlpha = 0.85f)
        }
        return _sFECircleFill!!
    }

private var _sFECircleFill: ImageVector? = null
