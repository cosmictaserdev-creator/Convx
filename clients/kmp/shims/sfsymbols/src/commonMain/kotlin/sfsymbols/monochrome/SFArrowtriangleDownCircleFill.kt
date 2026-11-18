package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleDownCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowtriangleDownCircleFill: ImageVector
    get() {
        if (_sFArrowtriangleDownCircleFill != null) {
            return _sFArrowtriangleDownCircleFill!!
        }
        _sFArrowtriangleDownCircleFill = sfIcon(
            name = "Monochrome.SFArrowtriangleDownCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.18359 8.97461C7.60742 8.97461 7.35352 9.66797 7.66602 10.1855L12.0605 17.5488C12.3633 18.0566 13.0762 18.0859 13.3887 17.5488L17.7539 10.1855C18.0762 9.62891 17.8223 8.97461 17.2461 8.97461Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleDownCircleFill!!
    }

private var _sFArrowtriangleDownCircleFill: ImageVector? = null
