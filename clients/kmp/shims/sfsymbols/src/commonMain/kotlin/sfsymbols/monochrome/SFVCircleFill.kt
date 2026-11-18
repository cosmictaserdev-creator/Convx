package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFVCircleFill: ImageVector
    get() {
        if (_sFVCircleFill != null) {
            return _sFVCircleFill!!
        }
        _sFVCircleFill = sfIcon(
            name = "Monochrome.SFVCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.9961 7.43164L12.7539 16.582L12.6758 16.582L9.43359 7.43164C9.23828 6.88477 8.97461 6.66016 8.48633 6.66016C7.95898 6.66016 7.58789 7.01172 7.58789 7.5C7.58789 7.66602 7.60742 7.80273 7.67578 7.98828L11.3867 17.6758C11.6699 18.418 12.041 18.7109 12.7246 18.7109C13.3789 18.7109 13.7891 18.3789 14.0527 17.6562L17.7539 7.98828C17.8223 7.80273 17.8418 7.66602 17.8418 7.5C17.8418 7.00195 17.4707 6.66016 16.9434 6.66016C16.4551 6.66016 16.1914 6.89453 15.9961 7.43164Z", fillAlpha = 0.85f)
        }
        return _sFVCircleFill!!
    }

private var _sFVCircleFill: ImageVector? = null
