package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUpCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowtriangleUpCircleFill: ImageVector
    get() {
        if (_sFArrowtriangleUpCircleFill != null) {
            return _sFArrowtriangleUpCircleFill!!
        }
        _sFArrowtriangleUpCircleFill = sfIcon(
            name = "Monochrome.SFArrowtriangleUpCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.0605 8.08594L7.66602 15.459C7.36328 15.9766 7.60742 16.6699 8.18359 16.6699L17.2461 16.6699C17.8223 16.6699 18.0859 16.0156 17.7539 15.459L13.3887 8.08594C13.0762 7.54883 12.3633 7.57812 12.0605 8.08594Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUpCircleFill!!
    }

private var _sFArrowtriangleUpCircleFill: ImageVector? = null
