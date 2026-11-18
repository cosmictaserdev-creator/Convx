package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUp (monochrome)
 * Viewport: 20.3027 x 20.5176
 */
public val SfSymbols.Monochrome.SFArrowtriangleUp: ImageVector
    get() {
        if (_sFArrowtriangleUp != null) {
            return _sFArrowtriangleUp!!
        }
        _sFArrowtriangleUp = sfIcon(
            name = "Monochrome.SFArrowtriangleUp",
            viewportWidth = 20.3027f,
            viewportHeight = 20.5176f
        ) {
            addSfPath("M19.9414 19.1406C19.9414 18.6914 19.7461 18.3398 19.5117 17.8516L11.4844 1.26953C11.0254 0.332031 10.5859 0.00976562 9.9707 0.00976562C9.36523 0.00976562 8.92578 0.332031 8.45703 1.26953L0.439453 17.8516C0.195312 18.3496 0 18.7012 0 19.1504C0 20 0.634766 20.5176 1.64062 20.5176L18.3105 20.5078C19.3066 20.5078 19.9414 19.9902 19.9414 19.1406ZM17.9199 18.6328C17.9199 18.7305 17.8516 18.7891 17.7148 18.7891L2.22656 18.7891C2.08984 18.7891 2.03125 18.7305 2.03125 18.6328C2.03125 18.5645 2.06055 18.5059 2.09961 18.4375L9.77539 2.46094C9.81445 2.38281 9.88281 2.29492 9.9707 2.29492C10.0684 2.29492 10.1367 2.38281 10.1758 2.46094L17.8516 18.4375C17.8809 18.5059 17.9199 18.5645 17.9199 18.6328Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUp!!
    }

private var _sFArrowtriangleUp: ImageVector? = null
