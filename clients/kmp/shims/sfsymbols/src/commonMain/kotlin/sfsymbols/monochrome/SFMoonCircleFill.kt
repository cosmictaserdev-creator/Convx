package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMoonCircleFill: ImageVector
    get() {
        if (_sFMoonCircleFill != null) {
            return _sFMoonCircleFill!!
        }
        _sFMoonCircleFill = sfIcon(
            name = "Monochrome.SFMoonCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.93164 6.25977C7.57812 7.26562 5.51758 9.77539 5.51758 12.9785C5.51758 16.9922 8.78906 20.2734 12.8223 20.2734C15.8398 20.2734 18.3105 18.457 19.4141 16.0547C19.6191 15.6445 19.3457 15.3516 18.9453 15.4785C18.457 15.6641 17.5586 15.8691 16.6797 15.8691C12.4316 15.8691 10.0293 13.457 10.0293 9.20898C10.0293 8.35938 10.2148 7.5 10.4883 6.80664C10.6641 6.34766 10.3711 6.09375 9.93164 6.25977Z", fillAlpha = 0.85f)
        }
        return _sFMoonCircleFill!!
    }

private var _sFMoonCircleFill: ImageVector? = null
