package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayDiamondFill (monochrome)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Monochrome.SFPlayDiamondFill: ImageVector
    get() {
        if (_sFPlayDiamondFill != null) {
            return _sFPlayDiamondFill!!
        }
        _sFPlayDiamondFill = sfIcon(
            name = "Monochrome.SFPlayDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M16.8189 1.35378L26.9068 11.4417C28.6939 13.2288 28.6939 15.0257 26.9264 16.7932L16.7994 26.93C15.0318 28.6975 13.235 28.6878 11.4478 26.9007L1.35995 16.8128C-0.436922 15.0257-0.456453 13.2385 1.32089 11.4612L11.4674 1.33425C13.2447-0.452858 15.0318-0.443093 16.8189 1.35378ZM10.9889 9.60574L10.9889 18.6585C10.9889 19.2346 11.6334 19.5178 12.1998 19.176L19.5631 14.801C20.0807 14.4983 20.0709 13.7854 19.5631 13.4827L12.1998 9.09792C11.6725 8.78542 10.9889 9.02956 10.9889 9.60574Z", fillAlpha = 0.85f)
        }
        return _sFPlayDiamondFill!!
    }

private var _sFPlayDiamondFill: ImageVector? = null
