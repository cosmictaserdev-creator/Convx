package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppBadge (dualtone)
 * Viewport: 30.4297 x 30.7129
 */
public val SfSymbols.Dualtone.SFAppBadge: ImageVector
    get() {
        if (_sFAppBadge != null) {
            return _sFAppBadge!!
        }
        _sFAppBadge = sfIcon(
            name = "Dualtone.SFAppBadge",
            viewportWidth = 30.4297f,
            viewportHeight = 30.7129f
        ) {
            addSfPath("M18.8464 5.63477L10.3906 5.63477C8.73047 5.63477 7.44141 6.09375 6.60156 6.92383C5.74219 7.77344 5.30273 9.05273 5.30273 10.7227L5.30273 19.9902C5.30273 21.6602 5.74219 22.9395 6.60156 23.7891C7.45117 24.6289 8.73047 25.0781 10.3906 25.0781L19.668 25.0781C21.3379 25.0781 22.6172 24.6191 23.457 23.7891C24.3164 22.9395 24.7559 21.6602 24.7559 19.9902L24.7559 11.5853C25.3593 11.577 25.9406 11.4695 26.4844 11.2771L26.4844 19.9902C26.4844 22.1973 25.8789 23.916 24.7266 25.0586C23.6035 26.1816 21.8848 26.8066 19.668 26.8066L10.3906 26.8066C8.17383 26.8066 6.46484 26.1914 5.33203 25.0586C4.16992 23.9062 3.57422 22.1973 3.57422 19.9902L3.57422 10.7227C3.57422 8.51562 4.17969 6.79688 5.33203 5.6543C6.45508 4.53125 8.17383 3.90625 10.3906 3.90625L19.1646 3.90625C18.9686 4.44879 18.8577 5.03008 18.8464 5.63477Z", fillAlpha = 0.425f)
            addSfPath("M24.6289 10.0488C26.9434 10.0488 28.8672 8.13477 28.8672 5.81055C28.8672 3.47656 26.9434 1.57227 24.6289 1.57227C22.3047 1.57227 20.3906 3.47656 20.3906 5.81055C20.3906 8.13477 22.3047 10.0488 24.6289 10.0488Z", fillAlpha = 0.85f)
        }
        return _sFAppBadge!!
    }

private var _sFAppBadge: ImageVector? = null
