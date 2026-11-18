package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFStarCircleFill: ImageVector
    get() {
        if (_sFStarCircleFill != null) {
            return _sFStarCircleFill!!
        }
        _sFStarCircleFill = sfIcon(
            name = "Dualtone.SFStarCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.93555 19.3848C8.28125 19.8828 7.56836 19.3652 7.83203 18.5742L9.30664 14.1309L5.49805 11.4062C4.87305 10.957 5.08789 10.0586 5.92773 10.0684L10.6055 10.0977L12.041 5.625C12.2754 4.86328 13.1543 4.86328 13.3984 5.625L14.8242 10.0977L19.502 10.0684C20.3613 10.0586 20.5371 10.9766 19.9414 11.3965L16.1328 14.1309L17.6074 18.5742C17.8711 19.3652 17.168 19.8828 16.4941 19.3848L12.7148 16.6211Z", fillAlpha = 0.85f)
        }
        return _sFStarCircleFill!!
    }

private var _sFStarCircleFill: ImageVector? = null
