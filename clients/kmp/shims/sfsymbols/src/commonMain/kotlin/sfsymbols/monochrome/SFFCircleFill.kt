package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFFCircleFill: ImageVector
    get() {
        if (_sFFCircleFill != null) {
            return _sFFCircleFill!!
        }
        _sFFCircleFill = sfIcon(
            name = "Monochrome.SFFCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.85352 6.79688C9.28711 6.79688 8.98438 7.20703 8.98438 7.8125L8.98438 17.627C8.98438 18.2227 9.29688 18.6328 9.86328 18.6328C10.4492 18.6328 10.7715 18.2422 10.7715 17.627L10.7715 13.4082L15.5664 13.4082C16.0156 13.4082 16.3184 13.1543 16.3184 12.7051C16.3184 12.2559 16.0156 11.9922 15.5664 11.9922L10.7715 11.9922L10.7715 8.28125L16.0547 8.28125C16.5039 8.28125 16.8066 7.98828 16.8066 7.54883C16.8066 7.08008 16.5039 6.79688 16.0547 6.79688Z", fillAlpha = 0.85f)
        }
        return _sFFCircleFill!!
    }

private var _sFFCircleFill: ImageVector? = null
