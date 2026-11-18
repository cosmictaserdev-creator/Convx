package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppleTerminalCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFAppleTerminalCircleFill: ImageVector
    get() {
        if (_sFAppleTerminalCircleFill != null) {
            return _sFAppleTerminalCircleFill!!
        }
        _sFAppleTerminalCircleFill = sfIcon(
            name = "Dualtone.SFAppleTerminalCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M6.99219 18.7012C5.69336 18.7012 5.01953 18.0664 5.01953 16.7578L5.01953 8.74023C5.01953 7.43164 5.69336 6.79688 6.99219 6.79688L18.4277 6.79688C19.7363 6.79688 20.3906 7.44141 20.3906 8.74023L20.3906 16.7578C20.3906 18.0566 19.7363 18.7012 18.4277 18.7012ZM7.65625 11.7285C7.13867 12.0508 7.58789 12.832 8.1543 12.4902L10.1074 11.25C10.459 11.0254 10.4688 10.4785 10.1074 10.2441L8.1543 9.0332C7.58789 8.68164 7.13867 9.47266 7.65625 9.79492L9.28711 10.7617ZM10.4102 12.4805C10.4102 12.7051 10.6055 12.9102 10.8398 12.9102L13.3887 12.9102C13.6426 12.9102 13.8379 12.7051 13.8379 12.4805C13.8379 12.2363 13.6426 12.0508 13.3887 12.0508L10.8398 12.0508C10.6055 12.0508 10.4102 12.2363 10.4102 12.4805Z", fillAlpha = 0.85f)
        }
        return _sFAppleTerminalCircleFill!!
    }

private var _sFAppleTerminalCircleFill: ImageVector? = null
