package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardFill (monochrome)
 * Viewport: 35.7422 x 18.9746
 */
public val SfSymbols.Monochrome.SFForwardFill: ImageVector
    get() {
        if (_sFForwardFill != null) {
            return _sFForwardFill!!
        }
        _sFForwardFill = sfIcon(
            name = "Monochrome.SFForwardFill",
            viewportWidth = 35.7422f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M2.25586 17.3633C2.25586 18.457 2.90039 18.9551 3.64258 18.9551C3.97461 18.9551 4.30664 18.8574 4.63867 18.6719L17.8711 10.9082C18.7012 10.4297 19.0332 10.0586 19.0332 9.48242C19.0332 8.90625 18.7012 8.52539 17.8711 8.04688L4.63867 0.283203C4.30664 0.0976562 3.97461 0 3.64258 0C2.90039 0 2.25586 0.507812 2.25586 1.60156ZM18.9648 17.3633C18.9648 18.457 19.6094 18.9551 20.3516 18.9551C20.6836 18.9551 21.0156 18.8574 21.3477 18.6719L34.5703 10.9082C35.4102 10.4297 35.7422 10.0586 35.7422 9.48242C35.7422 8.90625 35.4102 8.52539 34.5703 8.04688L21.3477 0.283203C21.0156 0.0976562 20.6836 0 20.3516 0C19.6094 0 18.9648 0.507812 18.9648 1.60156Z", fillAlpha = 0.85f)
        }
        return _sFForwardFill!!
    }

private var _sFForwardFill: ImageVector? = null
