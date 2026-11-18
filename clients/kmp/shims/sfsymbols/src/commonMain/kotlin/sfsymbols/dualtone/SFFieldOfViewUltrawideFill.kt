package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFieldOfViewUltrawideFill (dualtone)
 * Viewport: 37.4608 x 21.8848
 */
public val SfSymbols.Dualtone.SFFieldOfViewUltrawideFill: ImageVector
    get() {
        if (_sFFieldOfViewUltrawideFill != null) {
            return _sFFieldOfViewUltrawideFill!!
        }
        _sFFieldOfViewUltrawideFill = sfIcon(
            name = "Dualtone.SFFieldOfViewUltrawideFill",
            viewportWidth = 37.4608f,
            viewportHeight = 21.8848f
        ) {
            addSfPath("M1.12296 12.5293L15.9472 20.5469C17.0605 21.1426 17.7343 21.4648 18.5448 21.4648C19.3651 21.4648 20.039 21.1426 21.1523 20.5469L35.9667 12.5293C37.1191 11.9141 37.4609 10.5566 36.6698 9.41406C32.5976 3.68164 25.8495 0 18.5448 0C11.2499 0 4.50187 3.68164 0.429602 9.41406C-0.361414 10.5566-0.0196167 11.9141 1.12296 12.5293Z", fillAlpha = 0.85f)
        }
        return _sFFieldOfViewUltrawideFill!!
    }

private var _sFFieldOfViewUltrawideFill: ImageVector? = null
