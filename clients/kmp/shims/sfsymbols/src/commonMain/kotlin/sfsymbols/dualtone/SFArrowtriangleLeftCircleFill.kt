package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleLeftCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowtriangleLeftCircleFill: ImageVector
    get() {
        if (_sFArrowtriangleLeftCircleFill != null) {
            return _sFArrowtriangleLeftCircleFill!!
        }
        _sFArrowtriangleLeftCircleFill = sfIcon(
            name = "Dualtone.SFArrowtriangleLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.57812 13.3984C7.06055 13.0859 7.07031 12.373 7.57812 12.0703L14.9512 7.68555C15.4688 7.36328 16.1621 7.62695 16.1621 8.20312L16.1621 17.2559C16.1621 17.832 15.5078 18.1055 14.9512 17.7637Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleLeftCircleFill!!
    }

private var _sFArrowtriangleLeftCircleFill: ImageVector? = null
