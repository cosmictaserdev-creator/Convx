package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPinCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPinCircleFill: ImageVector
    get() {
        if (_sFPinCircleFill != null) {
            return _sFPinCircleFill!!
        }
        _sFPinCircleFill = sfIcon(
            name = "Dualtone.SFPinCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.65625 15.2637C7.65625 13.8477 8.7793 12.373 10.6348 11.7285L10.8496 8.60352C9.99023 8.10547 9.21875 7.50977 8.84766 7.06055C8.67188 6.81641 8.56445 6.58203 8.56445 6.37695C8.56445 5.99609 8.85742 5.72266 9.28711 5.72266L16.1523 5.72266C16.6016 5.72266 16.8848 5.99609 16.8848 6.37695C16.8848 6.57227 16.7871 6.80664 16.6211 7.03125C16.2598 7.5 15.4688 8.0957 14.5996 8.60352L14.8145 11.7285C16.6602 12.373 17.793 13.8477 17.793 15.2637C17.793 15.791 17.4414 16.123 16.8945 16.123L13.4375 16.123L13.4375 18.6426C13.4375 19.9023 12.9395 20.9668 12.7246 20.9668C12.5 20.9668 12.0117 19.9023 12.0117 18.6426L12.0117 16.123L8.54492 16.123C8.00781 16.123 7.65625 15.791 7.65625 15.2637Z", fillAlpha = 0.85f)
        }
        return _sFPinCircleFill!!
    }

private var _sFPinCircleFill: ImageVector? = null
