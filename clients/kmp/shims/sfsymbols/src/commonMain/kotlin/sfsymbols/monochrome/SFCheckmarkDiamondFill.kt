package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkDiamondFill (monochrome)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Monochrome.SFCheckmarkDiamondFill: ImageVector
    get() {
        if (_sFCheckmarkDiamondFill != null) {
            return _sFCheckmarkDiamondFill!!
        }
        _sFCheckmarkDiamondFill = sfIcon(
            name = "Monochrome.SFCheckmarkDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M16.8189 1.35378L26.9068 11.4417C28.6939 13.2288 28.6939 15.0257 26.9264 16.7932L16.7994 26.93C15.0318 28.6975 13.235 28.6878 11.4478 26.9007L1.35995 16.8128C-0.436922 15.0257-0.456453 13.2385 1.32089 11.4612L11.4674 1.33425C13.2447-0.452858 15.0318-0.443093 16.8189 1.35378ZM18.3326 9.10769L12.6881 18.0823L9.87558 14.5667C9.61191 14.2249 9.36777 14.1175 9.06503 14.1175C8.57675 14.1175 8.19589 14.5178 8.19589 15.0061C8.19589 15.2503 8.29355 15.4944 8.45956 15.7093L11.7896 19.7327C12.0728 20.094 12.356 20.2503 12.7271 20.2503C13.0982 20.2503 13.401 20.0745 13.6256 19.7327L19.7389 10.1331C19.8756 9.928 20.0025 9.67409 20.0025 9.43972C20.0025 8.94167 19.5631 8.60964 19.1041 8.60964C18.8111 8.60964 18.5279 8.78542 18.3326 9.10769Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkDiamondFill!!
    }

private var _sFCheckmarkDiamondFill: ImageVector? = null
