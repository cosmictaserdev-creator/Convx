package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpoonServing (dualtone)
 * Viewport: 9.95117 x 31.9043
 */
public val SfSymbols.Dualtone.SFSpoonServing: ImageVector
    get() {
        if (_sFSpoonServing != null) {
            return _sFSpoonServing!!
        }
        _sFSpoonServing = sfIcon(
            name = "Dualtone.SFSpoonServing",
            viewportWidth = 9.95117f,
            viewportHeight = 31.9043f
        ) {
            addSfPath("M4.79492 29.5605C5.94727 29.5605 6.40625 28.6914 6.40625 27.5977L6.40625 16.4453C6.40625 12.3438 9.58984 11.0547 9.58984 5.75195C9.58984 2.50977 7.74414 0 4.79492 0C1.85547 0 0 2.50977 0 5.75195C0 11.0547 3.18359 12.3438 3.18359 16.4453L3.18359 27.5977C3.18359 28.6914 3.64258 29.5605 4.79492 29.5605Z", fillAlpha = 0.85f)
        }
        return _sFSpoonServing!!
    }

private var _sFSpoonServing: ImageVector? = null
