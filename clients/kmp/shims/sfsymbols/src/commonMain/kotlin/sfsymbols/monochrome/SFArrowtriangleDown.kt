package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleDown (monochrome)
 * Viewport: 20.3027 x 20.5176
 */
public val SfSymbols.Monochrome.SFArrowtriangleDown: ImageVector
    get() {
        if (_sFArrowtriangleDown != null) {
            return _sFArrowtriangleDown!!
        }
        _sFArrowtriangleDown = sfIcon(
            name = "Monochrome.SFArrowtriangleDown",
            viewportWidth = 20.3027f,
            viewportHeight = 20.5176f
        ) {
            addSfPath("M19.9414 1.38672C19.9414 0.546875 19.3066 0.0195312 18.3105 0.0195312L1.64062 0.00976562C0.634766 0.00976562 0 0.537109 0 1.37695C0 1.83594 0.195312 2.1875 0.439453 2.68555L8.45703 19.2578C8.92578 20.2051 9.36523 20.5176 9.9707 20.5176C10.5859 20.5176 11.0254 20.2051 11.4844 19.2578L19.5117 2.68555C19.7461 2.19727 19.9414 1.8457 19.9414 1.38672ZM17.9199 1.9043C17.9199 1.97266 17.8809 2.04102 17.8516 2.10938L10.1758 18.0762C10.1367 18.1543 10.0684 18.2422 9.9707 18.2422C9.88281 18.2422 9.81445 18.1543 9.77539 18.0762L2.09961 2.09961C2.06055 2.03125 2.03125 1.96289 2.03125 1.89453C2.03125 1.80664 2.08984 1.73828 2.22656 1.73828L17.7148 1.75781C17.8516 1.75781 17.9199 1.81641 17.9199 1.9043Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleDown!!
    }

private var _sFArrowtriangleDown: ImageVector? = null
