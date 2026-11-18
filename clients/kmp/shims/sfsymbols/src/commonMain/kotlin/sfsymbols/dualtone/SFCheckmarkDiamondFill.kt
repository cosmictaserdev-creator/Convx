package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkDiamondFill (dualtone)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Dualtone.SFCheckmarkDiamondFill: ImageVector
    get() {
        if (_sFCheckmarkDiamondFill != null) {
            return _sFCheckmarkDiamondFill!!
        }
        _sFCheckmarkDiamondFill = sfIcon(
            name = "Dualtone.SFCheckmarkDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M1.35995 16.8128L11.4478 26.9007C13.235 28.6878 15.0318 28.6975 16.7994 26.93L26.9264 16.7932C28.6939 15.0257 28.6939 13.2288 26.9068 11.4417L16.8189 1.35378C15.0318-0.443093 13.2447-0.452858 11.4674 1.33425L1.32089 11.4612C-0.456453 13.2385-0.436922 15.0257 1.35995 16.8128Z", fillAlpha = 0.2125f)
            addSfPath("M12.7271 20.2503C12.356 20.2503 12.0728 20.094 11.7896 19.7327L8.45956 15.7093C8.29355 15.4944 8.19589 15.2503 8.19589 15.0061C8.19589 14.5178 8.57675 14.1175 9.06503 14.1175C9.36777 14.1175 9.61191 14.2249 9.87558 14.5667L12.6881 18.0823L18.3326 9.10769C18.5279 8.78542 18.8111 8.60964 19.1041 8.60964C19.5631 8.60964 20.0025 8.94167 20.0025 9.43972C20.0025 9.67409 19.8756 9.928 19.7389 10.1331L13.6256 19.7327C13.401 20.0745 13.0982 20.2503 12.7271 20.2503Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkDiamondFill!!
    }

private var _sFCheckmarkDiamondFill: ImageVector? = null
