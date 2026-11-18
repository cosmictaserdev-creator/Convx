package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusDiamond (monochrome)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Monochrome.SFPlusDiamond: ImageVector
    get() {
        if (_sFPlusDiamond != null) {
            return _sFPlusDiamond!!
        }
        _sFPlusDiamond = sfIcon(
            name = "Monochrome.SFPlusDiamond",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128ZM2.60019 15.6214C1.62363 14.6546 1.61386 13.6194 2.60995 12.6331L12.6295 2.61355C13.6158 1.61745 14.6412 1.62722 15.6178 2.60378L25.6471 12.6331C26.6139 13.5999 26.6432 14.6448 25.6471 15.6311L15.6275 25.6409C14.6314 26.637 13.5865 26.6175 12.6393 25.6604Z", fillAlpha = 0.85f)
            addSfPath("M15.0221 18.9612L15.0221 9.26394C15.0221 8.71706 14.651 8.3362 14.1041 8.3362C13.5865 8.3362 13.2252 8.71706 13.2252 9.26394L13.2252 18.9612C13.2252 19.4983 13.5865 19.8792 14.1041 19.8792C14.651 19.8792 15.0221 19.5081 15.0221 18.9612ZM9.27988 15.0061L18.9771 15.0061C19.5143 15.0061 19.8951 14.6448 19.8951 14.1272C19.8951 13.5803 19.524 13.2093 18.9771 13.2093L9.27988 13.2093C8.733 13.2093 8.35214 13.5803 8.35214 14.1272C8.35214 14.6448 8.733 15.0061 9.27988 15.0061Z", fillAlpha = 0.85f)
        }
        return _sFPlusDiamond!!
    }

private var _sFPlusDiamond: ImageVector? = null
