package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleBackwardCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowtriangleBackwardCircleFill: ImageVector
    get() {
        if (_sFArrowtriangleBackwardCircleFill != null) {
            return _sFArrowtriangleBackwardCircleFill!!
        }
        _sFArrowtriangleBackwardCircleFill = sfIcon(
            name = "Monochrome.SFArrowtriangleBackwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM14.9512 7.68555L7.57812 12.0703C7.07031 12.373 7.06055 13.0859 7.57812 13.3984L14.9512 17.7637C15.5078 18.1055 16.1621 17.832 16.1621 17.2559L16.1621 8.20312C16.1621 7.62695 15.4688 7.36328 14.9512 7.68555Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleBackwardCircleFill!!
    }

private var _sFArrowtriangleBackwardCircleFill: ImageVector? = null
