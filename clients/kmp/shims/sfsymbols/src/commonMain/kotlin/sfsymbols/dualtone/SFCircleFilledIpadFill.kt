package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIpadFill (dualtone)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Dualtone.SFCircleFilledIpadFill: ImageVector
    get() {
        if (_sFCircleFilledIpadFill != null) {
            return _sFCircleFilledIpadFill!!
        }
        _sFCircleFilledIpadFill = sfIcon(
            name = "Dualtone.SFCircleFilledIpadFill",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M3.4375 27.998L17.2852 27.998C19.375 27.998 20.7227 26.6992 20.7227 24.6777L20.7227 3.32031C20.7227 1.29883 19.375 0 17.2852 0L3.4375 0C1.34766 0 0 1.29883 0 3.32031L0 24.6777C0 26.6992 1.34766 27.998 3.4375 27.998Z", fillAlpha = 0.2125f)
            addSfPath("M10.3613 20.4004C6.82617 20.4004 3.94531 17.5391 3.94531 13.9844C3.94531 10.459 6.82617 7.58789 10.3613 7.58789C13.8867 7.58789 16.7773 10.459 16.7773 13.9844C16.7773 17.5391 13.8867 20.4004 10.3613 20.4004Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIpadFill!!
    }

private var _sFCircleFilledIpadFill: ImageVector? = null
