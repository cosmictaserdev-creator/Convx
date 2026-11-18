package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleUpFill (monochrome)
 * Viewport: 20.3027 x 20.5176
 */
public val SfSymbols.Monochrome.SFArrowtriangleUpFill: ImageVector
    get() {
        if (_sFArrowtriangleUpFill != null) {
            return _sFArrowtriangleUpFill!!
        }
        _sFArrowtriangleUpFill = sfIcon(
            name = "Monochrome.SFArrowtriangleUpFill",
            viewportWidth = 20.3027f,
            viewportHeight = 20.5176f
        ) {
            addSfPath("M19.9414 19.1406C19.9414 18.6914 19.7461 18.3398 19.5117 17.8516L11.4844 1.26953C11.0254 0.332031 10.5859 0.00976562 9.9707 0.00976562C9.36523 0.00976562 8.92578 0.332031 8.45703 1.26953L0.439453 17.8516C0.195312 18.3496 0 18.7012 0 19.1504C0 20 0.634766 20.5176 1.64062 20.5176L18.3105 20.5078C19.3066 20.5078 19.9414 19.9902 19.9414 19.1406Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleUpFill!!
    }

private var _sFArrowtriangleUpFill: ImageVector? = null
