package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTentCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTentCircleFill: ImageVector
    get() {
        if (_sFTentCircleFill != null) {
            return _sFTentCircleFill!!
        }
        _sFTentCircleFill = sfIcon(
            name = "Monochrome.SFTentCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM11.748 5.18555L5.60547 17.5195C5.2832 18.1543 5.52734 18.8672 6.31836 18.8672L19.1016 18.8672C19.9023 18.8672 20.1465 18.1543 19.8242 17.5195L13.7012 5.20508C13.2422 4.29688 12.1777 4.31641 11.748 5.18555ZM13.1152 11.3184L16.1719 17.8516L13.8281 17.8516L12.7148 12.832L11.5918 17.8516L9.24805 17.8516L12.3047 11.3184C12.3926 11.1426 12.5586 11.0449 12.7148 11.0449C12.8711 11.0449 13.0273 11.1426 13.1152 11.3184Z", fillAlpha = 0.85f)
        }
        return _sFTentCircleFill!!
    }

private var _sFTentCircleFill: ImageVector? = null
