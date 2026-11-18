package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLampTableFill (monochrome)
 * Viewport: 19.2663 x 29.5996
 */
public val SfSymbols.Monochrome.SFLampTableFill: ImageVector
    get() {
        if (_sFLampTableFill != null) {
            return _sFLampTableFill!!
        }
        _sFLampTableFill = sfIcon(
            name = "Monochrome.SFLampTableFill",
            viewportWidth = 19.2663f,
            viewportHeight = 29.5996f
        ) {
            addSfPath("M9.45249 14.4238C12.1869 14.4238 14.8919 14.2676 17.0501 13.9355C18.7298 13.6816 19.3353 12.4316 18.6126 10.791L14.8724 2.34375C14.1791 0.771484 12.9974 0 11.2591 0L7.65562 0C5.91734 0 4.7357 0.771484 4.04234 2.34375L0.302103 10.791C-0.420554 12.4316 0.17515 13.6816 1.85484 13.9355C4.02281 14.2676 6.72788 14.4238 9.45249 14.4238ZM8.55406 16.2793L10.3412 16.2793L10.3412 13.9258L8.55406 13.9258ZM7.43101 29.5801L11.4642 29.5801C14.5209 29.5801 16.3275 27.9883 16.3275 25.293C16.3275 23.5156 15.7709 20.918 14.8236 18.3203C14.2962 16.8457 13.0853 15.9766 11.4154 15.9766L7.4896 15.9766C5.80992 15.9766 4.60874 16.8457 4.07163 18.3203C3.12437 20.918 2.56773 23.5156 2.56773 25.293C2.56773 27.9883 4.37437 29.5801 7.43101 29.5801Z", fillAlpha = 0.85f)
        }
        return _sFLampTableFill!!
    }

private var _sFLampTableFill: ImageVector? = null
