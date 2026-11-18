package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIpadFill (monochrome)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Monochrome.SFCircleFilledIpadFill: ImageVector
    get() {
        if (_sFCircleFilledIpadFill != null) {
            return _sFCircleFilledIpadFill!!
        }
        _sFCircleFilledIpadFill = sfIcon(
            name = "Monochrome.SFCircleFilledIpadFill",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M20.7227 3.32031L20.7227 24.6777C20.7227 26.6992 19.375 27.998 17.2852 27.998L3.4375 27.998C1.34766 27.998 0 26.6992 0 24.6777L0 3.32031C0 1.29883 1.34766 0 3.4375 0L17.2852 0C19.375 0 20.7227 1.29883 20.7227 3.32031ZM3.94531 13.9844C3.94531 17.5391 6.82617 20.4004 10.3613 20.4004C13.8867 20.4004 16.7773 17.5391 16.7773 13.9844C16.7773 10.459 13.8867 7.58789 10.3613 7.58789C6.82617 7.58789 3.94531 10.459 3.94531 13.9844Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIpadFill!!
    }

private var _sFCircleFilledIpadFill: ImageVector? = null
