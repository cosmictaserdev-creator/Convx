package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTentCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTentCircleFill: ImageVector
    get() {
        if (_sFTentCircleFill != null) {
            return _sFTentCircleFill!!
        }
        _sFTentCircleFill = sfIcon(
            name = "Dualtone.SFTentCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M6.31836 18.8672C5.52734 18.8672 5.2832 18.1543 5.60547 17.5195L11.748 5.18555C12.1777 4.31641 13.2422 4.29688 13.7012 5.20508L19.8242 17.5195C20.1465 18.1543 19.9023 18.8672 19.1016 18.8672ZM9.24805 17.8516L11.5918 17.8516L12.7148 12.832L13.8281 17.8516L16.1719 17.8516L13.1152 11.3184C13.0273 11.1426 12.8711 11.0449 12.7148 11.0449C12.5586 11.0449 12.3926 11.1426 12.3047 11.3184Z", fillAlpha = 0.85f)
        }
        return _sFTentCircleFill!!
    }

private var _sFTentCircleFill: ImageVector? = null
