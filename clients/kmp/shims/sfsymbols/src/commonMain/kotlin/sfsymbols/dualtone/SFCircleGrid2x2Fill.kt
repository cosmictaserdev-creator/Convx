package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleGrid2x2Fill (dualtone)
 * Viewport: 24.3262 x 23.9551
 */
public val SfSymbols.Dualtone.SFCircleGrid2x2Fill: ImageVector
    get() {
        if (_sFCircleGrid2x2Fill != null) {
            return _sFCircleGrid2x2Fill!!
        }
        _sFCircleGrid2x2Fill = sfIcon(
            name = "Dualtone.SFCircleGrid2x2Fill",
            viewportWidth = 24.3262f,
            viewportHeight = 23.9551f
        ) {
            addSfPath("M18.4766 23.9355C21.5039 23.9355 23.9648 21.4844 23.9648 18.4473C23.9648 15.4199 21.5039 12.959 18.4766 12.959C15.4395 12.959 12.9883 15.4199 12.9883 18.4473C12.9883 21.4844 15.4395 23.9355 18.4766 23.9355Z", fillAlpha = 0.85f)
            addSfPath("M5.48828 23.9355C8.52539 23.9355 10.9766 21.4844 10.9766 18.4473C10.9766 15.4199 8.52539 12.959 5.48828 12.959C2.45117 12.959 0 15.4199 0 18.4473C0 21.4844 2.45117 23.9355 5.48828 23.9355Z", fillAlpha = 0.85f)
            addSfPath("M18.4766 10.9766C21.5039 10.9766 23.9648 8.52539 23.9648 5.48828C23.9648 2.46094 21.5039 0 18.4766 0C15.4395 0 12.9883 2.46094 12.9883 5.48828C12.9883 8.52539 15.4395 10.9766 18.4766 10.9766Z", fillAlpha = 0.85f)
            addSfPath("M5.48828 10.9766C8.52539 10.9766 10.9766 8.52539 10.9766 5.48828C10.9766 2.46094 8.52539 0 5.48828 0C2.45117 0 0 2.46094 0 5.48828C0 8.52539 2.45117 10.9766 5.48828 10.9766Z", fillAlpha = 0.85f)
        }
        return _sFCircleGrid2x2Fill!!
    }

private var _sFCircleGrid2x2Fill: ImageVector? = null
