package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFECircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFECircleFill: ImageVector
    get() {
        if (_sFECircleFill != null) {
            return _sFECircleFill!!
        }
        _sFECircleFill = sfIcon(
            name = "Dualtone.SFECircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.70703 18.4766C9.14062 18.4766 8.83789 18.0762 8.83789 17.4707L8.83789 7.79297C8.83789 7.19727 9.14062 6.79688 9.70703 6.79688L15.8594 6.79688C16.2988 6.79688 16.6016 7.07031 16.6016 7.5293C16.6016 7.96875 16.2988 8.27148 15.8594 8.27148L10.6152 8.27148L10.6152 11.8262L15.5273 11.8262C15.9668 11.8262 16.2695 12.0801 16.2695 12.5391C16.2695 12.9785 15.9668 13.2422 15.5273 13.2422L10.6152 13.2422L10.6152 17.002L15.8594 17.002C16.2988 17.002 16.6016 17.2852 16.6016 17.7344C16.6016 18.1836 16.2988 18.4766 15.8594 18.4766Z", fillAlpha = 0.85f)
        }
        return _sFECircleFill!!
    }

private var _sFECircleFill: ImageVector? = null
