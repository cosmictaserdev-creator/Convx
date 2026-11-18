package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareGrid2x2Fill (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareGrid2x2Fill: ImageVector
    get() {
        if (_sFSquareGrid2x2Fill != null) {
            return _sFSquareGrid2x2Fill!!
        }
        _sFSquareGrid2x2Fill = sfIcon(
            name = "Monochrome.SFSquareGrid2x2Fill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M14.7949 22.9785L20.7129 22.9785C22.1973 22.9785 22.959 22.2559 22.959 20.7031L22.959 14.8535C22.959 13.3008 22.1973 12.5684 20.7129 12.5684L14.7949 12.5684C13.3008 12.5684 12.5488 13.3008 12.5488 14.8535L12.5488 20.7031C12.5488 22.2559 13.3008 22.9785 14.7949 22.9785Z", fillAlpha = 0.85f)
            addSfPath("M2.24609 22.9785L8.16406 22.9785C9.64844 22.9785 10.4102 22.2559 10.4102 20.7031L10.4102 14.8535C10.4102 13.3008 9.64844 12.5684 8.16406 12.5684L2.24609 12.5684C0.761719 12.5684 0 13.3008 0 14.8535L0 20.7031C0 22.2559 0.761719 22.9785 2.24609 22.9785Z", fillAlpha = 0.85f)
            addSfPath("M14.7949 10.4297L20.7129 10.4297C22.1973 10.4297 22.959 9.70703 22.959 8.1543L22.959 2.30469C22.959 0.751953 22.1973 0.0292969 20.7129 0.0292969L14.7949 0.0292969C13.3008 0.0292969 12.5488 0.751953 12.5488 2.30469L12.5488 8.1543C12.5488 9.70703 13.3008 10.4297 14.7949 10.4297Z", fillAlpha = 0.85f)
            addSfPath("M2.24609 10.4297L8.16406 10.4297C9.64844 10.4297 10.4102 9.70703 10.4102 8.1543L10.4102 2.30469C10.4102 0.751953 9.64844 0.0292969 8.16406 0.0292969L2.24609 0.0292969C0.761719 0.0292969 0 0.751953 0 2.30469L0 8.1543C0 9.70703 0.761719 10.4297 2.24609 10.4297Z", fillAlpha = 0.85f)
        }
        return _sFSquareGrid2x2Fill!!
    }

private var _sFSquareGrid2x2Fill: ImageVector? = null
