package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUp2Fill (dualtone)
 * Viewport: 15.9668 x 27.4121
 */
public val SfSymbols.Dualtone.SFArrowtriangleUp2Fill: ImageVector
    get() {
        if (_sFArrowtriangleUp2Fill != null) {
            return _sFArrowtriangleUp2Fill!!
        }
        _sFArrowtriangleUp2Fill = sfIcon(
            name = "Dualtone.SFArrowtriangleUp2Fill",
            viewportWidth = 15.9668f,
            viewportHeight = 27.4121f
        ) {
            addSfPath("M1.41602 27.4121L14.1797 27.4121C15.1465 27.4121 15.6055 26.8359 15.6055 26.1719C15.6055 25.8789 15.5078 25.5664 15.3613 25.3027L9.0625 14.5898C8.65234 13.8867 8.33008 13.5742 7.80273 13.5742C7.27539 13.5742 6.94336 13.8867 6.5332 14.5898L0.244141 25.3027C0.0976562 25.5664 0 25.8789 0 26.1719C0 26.8359 0.458984 27.4121 1.41602 27.4121Z", fillAlpha = 0.85f)
            addSfPath("M1.41602 13.877L14.1797 13.877C15.1465 13.877 15.6055 13.3008 15.6055 12.6367C15.6055 12.3438 15.5078 12.0312 15.3613 11.7676L9.0625 1.05469C8.66211 0.351562 8.33008 0.0390625 7.80273 0.0390625C7.27539 0.0390625 6.94336 0.351562 6.5332 1.05469L0.244141 11.7676C0.0976562 12.0312 0 12.3438 0 12.6367C0 13.3008 0.458984 13.877 1.41602 13.877Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUp2Fill!!
    }

private var _sFArrowtriangleUp2Fill: ImageVector? = null
