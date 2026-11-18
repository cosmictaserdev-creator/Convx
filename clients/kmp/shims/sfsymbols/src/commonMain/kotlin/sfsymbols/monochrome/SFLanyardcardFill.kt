package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLanyardcardFill (monochrome)
 * Viewport: 19.7168 x 26.9238
 */
public val SfSymbols.Monochrome.SFLanyardcardFill: ImageVector
    get() {
        if (_sFLanyardcardFill != null) {
            return _sFLanyardcardFill!!
        }
        _sFLanyardcardFill = sfIcon(
            name = "Monochrome.SFLanyardcardFill",
            viewportWidth = 19.7168f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L15.5957 26.9238C18.0957 26.9238 19.3555 25.6445 19.3555 23.125L19.3555 3.80859C19.3555 1.28906 18.0957 0.00976562 15.5957 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859ZM6.52344 4.16016C6.09375 4.16016 5.75195 3.80859 5.75195 3.38867C5.75195 2.96875 6.09375 2.61719 6.52344 2.61719L12.832 2.61719C13.2617 2.61719 13.6133 2.96875 13.6133 3.38867C13.6133 3.80859 13.2617 4.16016 12.832 4.16016Z", fillAlpha = 0.85f)
        }
        return _sFLanyardcardFill!!
    }

private var _sFLanyardcardFill: ImageVector? = null
