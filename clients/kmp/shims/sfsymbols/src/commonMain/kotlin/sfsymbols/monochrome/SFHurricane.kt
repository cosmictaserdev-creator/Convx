package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHurricane (monochrome)
 * Viewport: 16.1426 x 26.0254
 */
public val SfSymbols.Monochrome.SFHurricane: ImageVector
    get() {
        if (_sFHurricane != null) {
            return _sFHurricane!!
        }
        _sFHurricane = sfIcon(
            name = "Monochrome.SFHurricane",
            viewportWidth = 16.1426f,
            viewportHeight = 26.0254f
        ) {
            addSfPath("M0 25.1953C0 25.6445 0.361328 26.0156 0.859375 26.0156C8.31055 26.0156 14.1797 20.7715 14.1797 13.0078C14.1797 9.56055 11.3281 6.70898 7.89062 6.70898C6.46484 6.70898 5.14648 7.25586 4.17969 8.14453C5.9375 4.14062 10.0293 1.63086 14.9219 1.63086C15.4199 1.63086 15.7812 1.25977 15.7812 0.810547C15.7812 0.351562 15.4199 0 14.9219 0C7.48047 0 1.61133 5.23438 1.61133 13.0078C1.61133 16.4453 4.45312 19.2969 7.89062 19.2969C9.32617 19.2969 10.6348 18.75 11.6016 17.8711C9.84375 21.8555 5.74219 24.3848 0.859375 24.3848C0.361328 24.3848 0 24.7363 0 25.1953ZM6.15234 13.0078C6.15234 12.041 6.93359 11.2598 7.89062 11.2598C8.85742 11.2598 9.63867 12.041 9.63867 13.0078C9.63867 13.9551 8.85742 14.7461 7.89062 14.7461C6.93359 14.7461 6.15234 13.9648 6.15234 13.0078Z", fillAlpha = 0.85f)
        }
        return _sFHurricane!!
    }

private var _sFHurricane: ImageVector? = null
