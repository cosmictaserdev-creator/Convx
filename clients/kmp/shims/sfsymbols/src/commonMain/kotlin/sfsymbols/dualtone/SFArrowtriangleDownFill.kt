package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleDownFill (dualtone)
 * Viewport: 20.3027 x 20.5176
 */
public val SfSymbols.Dualtone.SFArrowtriangleDownFill: ImageVector
    get() {
        if (_sFArrowtriangleDownFill != null) {
            return _sFArrowtriangleDownFill!!
        }
        _sFArrowtriangleDownFill = sfIcon(
            name = "Dualtone.SFArrowtriangleDownFill",
            viewportWidth = 20.3027f,
            viewportHeight = 20.5176f
        ) {
            addSfPath("M19.9414 1.38672C19.9414 0.546875 19.3066 0.0195312 18.3105 0.0195312L1.64062 0.00976562C0.634766 0.00976562 0 0.537109 0 1.37695C0 1.83594 0.195312 2.1875 0.439453 2.68555L8.45703 19.2578C8.92578 20.2051 9.36523 20.5176 9.9707 20.5176C10.5859 20.5176 11.0254 20.2051 11.4844 19.2578L19.5117 2.68555C19.7461 2.19727 19.9414 1.8457 19.9414 1.38672Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleDownFill!!
    }

private var _sFArrowtriangleDownFill: ImageVector? = null
