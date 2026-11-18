package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpRightCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowUpRightCircleFill: ImageVector
    get() {
        if (_sFArrowUpRightCircleFill != null) {
            return _sFArrowUpRightCircleFill!!
        }
        _sFArrowUpRightCircleFill = sfIcon(
            name = "Monochrome.SFArrowUpRightCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM10.4297 7.77344C9.90234 7.77344 9.57031 8.0957 9.57031 8.57422C9.57031 9.07227 9.91211 9.4043 10.4492 9.4043L13.2617 9.4043L15.1775 9.20906L13.291 10.9766L8.03711 16.2207C7.88086 16.3867 7.77344 16.6016 7.77344 16.8262C7.77344 17.334 8.0957 17.666 8.58398 17.666C8.85742 17.666 9.07227 17.5684 9.23828 17.4023L14.4727 12.1582L16.2254 10.2959L16.0449 12.4121L16.0449 15C16.0449 15.5371 16.3672 15.8691 16.875 15.8691C17.3535 15.8691 17.6758 15.5078 17.6758 14.9805L17.6758 8.68164C17.6758 8.03711 17.3242 7.77344 16.7578 7.77344Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpRightCircleFill!!
    }

private var _sFArrowUpRightCircleFill: ImageVector? = null
