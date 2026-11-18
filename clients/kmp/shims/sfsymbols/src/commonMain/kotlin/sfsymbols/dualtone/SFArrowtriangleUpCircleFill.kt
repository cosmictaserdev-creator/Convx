package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUpCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowtriangleUpCircleFill: ImageVector
    get() {
        if (_sFArrowtriangleUpCircleFill != null) {
            return _sFArrowtriangleUpCircleFill!!
        }
        _sFArrowtriangleUpCircleFill = sfIcon(
            name = "Dualtone.SFArrowtriangleUpCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.18359 16.6699C7.60742 16.6699 7.36328 15.9766 7.66602 15.459L12.0605 8.08594C12.3633 7.57812 13.0762 7.54883 13.3887 8.08594L17.7539 15.459C18.0859 16.0156 17.8223 16.6699 17.2461 16.6699Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUpCircleFill!!
    }

private var _sFArrowtriangleUpCircleFill: ImageVector? = null
