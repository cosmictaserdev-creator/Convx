package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayDiamond (dualtone)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Dualtone.SFPlayDiamond: ImageVector
    get() {
        if (_sFPlayDiamond != null) {
            return _sFPlayDiamond!!
        }
        _sFPlayDiamond = sfIcon(
            name = "Dualtone.SFPlayDiamond",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128ZM2.60019 15.6214C1.62363 14.6546 1.61386 13.6194 2.60995 12.6331L12.6295 2.61355C13.6158 1.61745 14.6412 1.62722 15.6178 2.60378L25.6471 12.6331C26.6139 13.5999 26.6432 14.6448 25.6471 15.6311L15.6275 25.6409C14.6314 26.637 13.5865 26.6175 12.6393 25.6604Z", fillAlpha = 0.425f)
            addSfPath("M12.2389 19.0491L19.4264 14.7815C19.9342 14.4788 19.9244 13.7952 19.4264 13.4925L12.2389 9.20534C11.7213 8.90261 11.0572 9.14675 11.0572 9.71316L11.0572 18.551C11.0572 19.1175 11.6822 19.3811 12.2389 19.0491Z", fillAlpha = 0.85f)
        }
        return _sFPlayDiamond!!
    }

private var _sFPlayDiamond: ImageVector? = null
