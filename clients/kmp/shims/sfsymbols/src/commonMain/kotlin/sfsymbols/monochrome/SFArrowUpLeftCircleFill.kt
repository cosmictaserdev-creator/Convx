package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpLeftCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowUpLeftCircleFill: ImageVector
    get() {
        if (_sFArrowUpLeftCircleFill != null) {
            return _sFArrowUpLeftCircleFill!!
        }
        _sFArrowUpLeftCircleFill = sfIcon(
            name = "Monochrome.SFArrowUpLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.67188 7.77344C8.10547 7.77344 7.75391 8.03711 7.75391 8.68164L7.75391 14.9805C7.75391 15.5078 8.07617 15.8691 8.56445 15.8691C9.0625 15.8691 9.38477 15.5371 9.38477 15L9.38477 12.4121L9.20426 10.2959L10.957 12.1582L16.2012 17.4023C16.3672 17.5684 16.5723 17.666 16.8457 17.666C17.334 17.666 17.666 17.334 17.666 16.8262C17.666 16.6016 17.5586 16.3867 17.3926 16.2207L12.1387 10.9766L10.2518 9.20864L12.1777 9.4043L14.9805 9.4043C15.5176 9.4043 15.8691 9.07227 15.8691 8.57422C15.8691 8.0957 15.5273 7.77344 15 7.77344Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpLeftCircleFill!!
    }

private var _sFArrowUpLeftCircleFill: ImageVector? = null
