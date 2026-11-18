package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightRecessed (dualtone)
 * Viewport: 35.0098 x 20.8301
 */
public val SfSymbols.Dualtone.SFLightRecessed: ImageVector
    get() {
        if (_sFLightRecessed != null) {
            return _sFLightRecessed!!
        }
        _sFLightRecessed = sfIcon(
            name = "Dualtone.SFLightRecessed",
            viewportWidth = 35.0098f,
            viewportHeight = 20.8301f
        ) {
            addSfPath("M17.3047 20.8203C27.5488 20.8203 34.6484 16.1426 34.6484 10.4102C34.6484 4.67773 27.5488 0 17.3047 0C7.08984 0 0 4.67773 0 10.4102C0 16.1426 7.08984 20.8203 17.3047 20.8203ZM17.3047 19.1992C8.0957 19.1992 1.62109 15.1855 1.62109 10.4102C1.62109 5.625 8.0957 1.62109 17.3047 1.62109C26.543 1.62109 33.0273 5.625 33.0273 10.4102C33.0273 15.1855 26.543 19.1992 17.3047 19.1992ZM17.3047 17.1582C23.8672 17.1582 28.8477 14.4238 28.8477 10.752C28.8477 7.07031 23.8867 4.3457 17.3047 4.3457C10.7617 4.3457 5.81055 7.07031 5.81055 10.752C5.81055 14.4336 10.7617 17.1582 17.3047 17.1582ZM17.3047 15.5273C11.8066 15.5273 7.44141 13.3691 7.44141 10.752C7.44141 8.125 11.7871 5.97656 17.3047 5.97656C22.8418 5.97656 27.2168 8.125 27.2168 10.752C27.2168 13.3691 22.8223 15.5273 17.3047 15.5273ZM17.2852 11.3574C21.0938 11.3574 24.4141 9.62891 25.6348 7.19727C23.3008 6.02539 20.2637 5.40039 17.3047 5.33203C14.2871 5.40039 11.25 6.03516 8.91602 7.2168C10.1465 9.63867 13.457 11.3574 17.2852 11.3574Z", fillAlpha = 0.85f)
        }
        return _sFLightRecessed!!
    }

private var _sFLightRecessed: ImageVector? = null
