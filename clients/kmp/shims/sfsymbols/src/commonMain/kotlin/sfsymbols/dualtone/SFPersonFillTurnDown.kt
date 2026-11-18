package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonFillTurnDown (dualtone)
 * Viewport: 21.4941 x 22.8613
 */
public val SfSymbols.Dualtone.SFPersonFillTurnDown: ImageVector
    get() {
        if (_sFPersonFillTurnDown != null) {
            return _sFPersonFillTurnDown!!
        }
        _sFPersonFillTurnDown = sfIcon(
            name = "Dualtone.SFPersonFillTurnDown",
            viewportWidth = 21.4941f,
            viewportHeight = 22.8613f
        ) {
            addSfPath("M2.50977 0.332031C0.703125 0.332031 0 0.859375 0 1.95312C0 5.19531 4.05273 9.66797 10.5664 9.66797C17.0801 9.66797 21.1328 5.19531 21.1328 1.95312C21.1328 0.859375 20.4297 0.332031 18.623 0.332031ZM10.5762 11.9238C7.86133 11.9238 5.57617 14.3359 5.57617 17.4512C5.56641 20.4883 7.88086 22.8613 10.5762 22.8613C13.2812 22.8613 15.5859 20.5273 15.5859 17.4707C15.5859 14.3359 13.3008 11.9238 10.5762 11.9238Z", fillAlpha = 0.85f)
        }
        return _sFPersonFillTurnDown!!
    }

private var _sFPersonFillTurnDown: ImageVector? = null
