package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkDiamond (dualtone)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Dualtone.SFCheckmarkDiamond: ImageVector
    get() {
        if (_sFCheckmarkDiamond != null) {
            return _sFCheckmarkDiamond!!
        }
        _sFCheckmarkDiamond = sfIcon(
            name = "Dualtone.SFCheckmarkDiamond",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128ZM2.60019 15.6214C1.62363 14.6546 1.61386 13.6194 2.60995 12.6331L12.6295 2.61355C13.6158 1.61745 14.6412 1.62722 15.6178 2.60378L25.6471 12.6331C26.6139 13.5999 26.6432 14.6448 25.6471 15.6311L15.6275 25.6409C14.6314 26.637 13.5865 26.6175 12.6393 25.6604Z", fillAlpha = 0.425f)
            addSfPath("M12.7564 20.1038C13.1178 20.1038 13.4107 19.9378 13.6256 19.6057L19.6119 10.221C19.7389 10.0257 19.8658 9.78152 19.8658 9.54714C19.8658 9.06863 19.4361 8.74636 18.9869 8.74636C18.7135 8.74636 18.44 8.92214 18.2447 9.23464L12.7174 18.0139L9.96347 14.5667C9.70956 14.2346 9.46542 14.137 9.17245 14.137C8.7037 14.137 8.33261 14.5178 8.33261 14.9964C8.33261 15.2307 8.43027 15.4651 8.58652 15.6702L11.8482 19.6057C12.1217 19.9573 12.3951 20.1038 12.7564 20.1038Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkDiamond!!
    }

private var _sFCheckmarkDiamond: ImageVector? = null
