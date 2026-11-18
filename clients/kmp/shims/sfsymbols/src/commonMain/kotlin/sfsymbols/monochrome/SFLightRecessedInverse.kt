package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightRecessedInverse (monochrome)
 * Viewport: 35.0098 x 20.8301
 */
public val SfSymbols.Monochrome.SFLightRecessedInverse: ImageVector
    get() {
        if (_sFLightRecessedInverse != null) {
            return _sFLightRecessedInverse!!
        }
        _sFLightRecessedInverse = sfIcon(
            name = "Monochrome.SFLightRecessedInverse",
            viewportWidth = 35.0098f,
            viewportHeight = 20.8301f
        ) {
            addSfPath("M17.3047 20.8203C27.5488 20.8203 34.6484 16.1426 34.6484 10.4102C34.6484 4.67773 27.5488 0 17.3047 0C7.08984 0 0 4.67773 0 10.4102C0 16.1426 7.08984 20.8203 17.3047 20.8203ZM17.3047 17.1582C10.7617 17.1582 5.81055 14.4336 5.81055 10.752C5.81055 7.07031 10.7617 4.3457 17.3047 4.3457C23.8867 4.3457 28.8477 7.07031 28.8477 10.752C28.8477 14.4238 23.8672 17.1582 17.3047 17.1582ZM17.2852 11.3574C20.9863 11.3574 24.2285 9.7168 25.5176 7.41211C23.7207 6.23047 20.8691 5.50781 17.3047 5.50781C13.7402 5.50781 10.8594 6.25 9.0625 7.46094C10.3711 9.74609 13.6035 11.3574 17.2852 11.3574Z", fillAlpha = 0.85f)
        }
        return _sFLightRecessedInverse!!
    }

private var _sFLightRecessedInverse: ImageVector? = null
