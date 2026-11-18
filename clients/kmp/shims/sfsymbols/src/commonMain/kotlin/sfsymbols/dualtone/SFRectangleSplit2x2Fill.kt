package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleSplit2x2Fill (dualtone)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Dualtone.SFRectangleSplit2x2Fill: ImageVector
    get() {
        if (_sFRectangleSplit2x2Fill != null) {
            return _sFRectangleSplit2x2Fill!!
        }
        _sFRectangleSplit2x2Fill = sfIcon(
            name = "Dualtone.SFRectangleSplit2x2Fill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 12.3633L0 10.6348L13.9258 10.6348L13.9258 0.0195312L15.6543 0.0195312L15.6543 10.6348L29.5898 10.6348L29.5898 12.3633L15.6543 12.3633L15.6543 22.9785L13.9258 22.9785L13.9258 12.3633ZM3.79883 22.9785L25.7812 22.9785C28.3105 22.9785 29.5898 21.6992 29.5898 19.209L29.5898 3.7793C29.5898 1.29883 28.3105 0.0195312 25.7812 0.0195312L3.79883 0.0195312C1.2793 0.0195312 0 1.2793 0 3.7793L0 19.209C0 21.709 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.85f)
        }
        return _sFRectangleSplit2x2Fill!!
    }

private var _sFRectangleSplit2x2Fill: ImageVector? = null
