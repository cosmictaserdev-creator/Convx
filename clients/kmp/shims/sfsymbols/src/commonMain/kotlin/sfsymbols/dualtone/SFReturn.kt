package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFReturn (dualtone)
 * Viewport: 26.6699 x 24.1504
 */
public val SfSymbols.Dualtone.SFReturn: ImageVector
    get() {
        if (_sFReturn != null) {
            return _sFReturn!!
        }
        _sFReturn = sfIcon(
            name = "Dualtone.SFReturn",
            viewportWidth = 26.6699f,
            viewportHeight = 24.1504f
        ) {
            addSfPath("M1.08398 14.4824C1.08398 14.9219 1.44531 15.2637 1.88477 15.2832L5.76172 15.4297L22.3926 15.4297C25.1172 15.4297 26.3086 14.1406 26.3086 11.4551L26.3086 3.91602C26.3086 1.19141 25.1172 0 22.3926 0L14.9219 0C14.3359 0 13.9551 0.419922 13.9551 0.947266C13.9551 1.46484 14.3359 1.89453 14.9121 1.89453L22.3926 1.89453C23.7695 1.89453 24.4141 2.53906 24.4141 3.91602L24.4141 11.4551C24.4141 12.8906 23.7598 13.5352 22.3926 13.5352L5.76172 13.5352L1.88477 13.6914C1.44531 13.7109 1.08398 14.043 1.08398 14.4824ZM0 14.4824C0 14.7461 0.0976562 14.9902 0.3125 15.1953L7.79297 22.5781C7.97852 22.7637 8.25195 22.8711 8.48633 22.8711C9.05273 22.8711 9.42383 22.4805 9.42383 21.9336C9.42383 21.6602 9.33594 21.4453 9.16992 21.2793L5.3125 17.4902L2.27539 14.8145L2.27539 14.1504L5.3125 11.4746L9.16992 7.68555C9.33594 7.51953 9.42383 7.30469 9.42383 7.03125C9.42383 6.48438 9.05273 6.09375 8.48633 6.09375C8.25195 6.09375 7.97852 6.21094 7.79297 6.38672L0.3125 13.7793C0.0976562 13.9746 0 14.2188 0 14.4824Z", fillAlpha = 0.85f)
        }
        return _sFReturn!!
    }

private var _sFReturn: ImageVector? = null
