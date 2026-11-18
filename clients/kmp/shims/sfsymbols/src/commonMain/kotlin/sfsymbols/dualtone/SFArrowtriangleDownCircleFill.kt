package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleDownCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowtriangleDownCircleFill: ImageVector
    get() {
        if (_sFArrowtriangleDownCircleFill != null) {
            return _sFArrowtriangleDownCircleFill!!
        }
        _sFArrowtriangleDownCircleFill = sfIcon(
            name = "Dualtone.SFArrowtriangleDownCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.0605 17.5488L7.66602 10.1855C7.35352 9.66797 7.60742 8.97461 8.18359 8.97461L17.2461 8.97461C17.8223 8.97461 18.0762 9.62891 17.7539 10.1855L13.3887 17.5488C13.0762 18.0859 12.3633 18.0566 12.0605 17.5488Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleDownCircleFill!!
    }

private var _sFArrowtriangleDownCircleFill: ImageVector? = null
