package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayDiamondFill (dualtone)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Dualtone.SFPlayDiamondFill: ImageVector
    get() {
        if (_sFPlayDiamondFill != null) {
            return _sFPlayDiamondFill!!
        }
        _sFPlayDiamondFill = sfIcon(
            name = "Dualtone.SFPlayDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128Z", fillAlpha = 0.2125f)
            addSfPath("M12.1998 19.176C11.6334 19.5178 10.9889 19.2346 10.9889 18.6585L10.9889 9.60574C10.9889 9.02956 11.6725 8.78542 12.1998 9.09792L19.5631 13.4827C20.0709 13.7854 20.0807 14.4983 19.5631 14.801Z", fillAlpha = 0.85f)
        }
        return _sFPlayDiamondFill!!
    }

private var _sFPlayDiamondFill: ImageVector? = null
