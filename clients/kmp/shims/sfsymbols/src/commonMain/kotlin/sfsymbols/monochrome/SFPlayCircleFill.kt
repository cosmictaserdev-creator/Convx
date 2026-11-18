package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPlayCircleFill: ImageVector
    get() {
        if (_sFPlayCircleFill != null) {
            return _sFPlayCircleFill!!
        }
        _sFPlayCircleFill = sfIcon(
            name = "Monochrome.SFPlayCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.0918 8.20312L9.0918 17.2559C9.0918 17.832 9.74609 18.1152 10.3027 17.7734L17.6758 13.3984C18.1934 13.0957 18.1738 12.3828 17.6758 12.0801L10.3027 7.69531C9.78516 7.38281 9.0918 7.62695 9.0918 8.20312Z", fillAlpha = 0.85f)
        }
        return _sFPlayCircleFill!!
    }

private var _sFPlayCircleFill: ImageVector? = null
