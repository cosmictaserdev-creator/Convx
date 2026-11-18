package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightRecessedFill (monochrome)
 * Viewport: 35.0098 x 20.8301
 */
public val SfSymbols.Monochrome.SFLightRecessedFill: ImageVector
    get() {
        if (_sFLightRecessedFill != null) {
            return _sFLightRecessedFill!!
        }
        _sFLightRecessedFill = sfIcon(
            name = "Monochrome.SFLightRecessedFill",
            viewportWidth = 35.0098f,
            viewportHeight = 20.8301f
        ) {
            addSfPath("M17.3145 20.8203C27.5488 20.8203 34.6484 16.1426 34.6484 10.4102C34.6484 4.67773 27.5488 0 17.3145 0C7.08984 0 0 4.67773 0 10.4102C0 16.1426 7.08984 20.8203 17.3145 20.8203ZM17.3145 17.3535C10.5957 17.3535 5.60547 14.5312 5.60547 10.752C5.60547 6.97266 10.6055 4.15039 17.3145 4.15039C24.043 4.15039 29.043 6.97266 29.043 10.752C29.043 14.5215 24.0332 17.3535 17.3145 17.3535ZM17.3145 15.9277C23.1055 15.9277 27.6172 13.6133 27.6172 10.752C27.6172 9.53125 26.8066 8.42773 25.4395 7.55859C24.082 9.79492 20.8984 11.3574 17.2656 11.3574C13.6621 11.3574 10.498 9.82422 9.13086 7.60742C7.8125 8.4668 7.04102 9.55078 7.04102 10.752C7.04102 13.6133 11.5137 15.9277 17.3145 15.9277Z", fillAlpha = 0.85f)
        }
        return _sFLightRecessedFill!!
    }

private var _sFLightRecessedFill: ImageVector? = null
