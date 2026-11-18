package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFStarCircleFill: ImageVector
    get() {
        if (_sFStarCircleFill != null) {
            return _sFStarCircleFill!!
        }
        _sFStarCircleFill = sfIcon(
            name = "Monochrome.SFStarCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.041 5.625L10.6055 10.0977L5.92773 10.0684C5.08789 10.0586 4.87305 10.957 5.49805 11.4062L9.30664 14.1309L7.83203 18.5742C7.56836 19.3652 8.28125 19.8828 8.93555 19.3848L12.7148 16.6211L16.4941 19.3848C17.168 19.8828 17.8711 19.3652 17.6074 18.5742L16.1328 14.1309L19.9414 11.3965C20.5371 10.9766 20.3613 10.0586 19.502 10.0684L14.8242 10.0977L13.3984 5.625C13.1543 4.86328 12.2754 4.86328 12.041 5.625Z", fillAlpha = 0.85f)
        }
        return _sFStarCircleFill!!
    }

private var _sFStarCircleFill: ImageVector? = null
