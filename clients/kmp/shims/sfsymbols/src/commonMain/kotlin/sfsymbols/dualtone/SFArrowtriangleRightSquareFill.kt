package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleRightSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowtriangleRightSquareFill: ImageVector
    get() {
        if (_sFArrowtriangleRightSquareFill != null) {
            return _sFArrowtriangleRightSquareFill!!
        }
        _sFArrowtriangleRightSquareFill = sfIcon(
            name = "Dualtone.SFArrowtriangleRightSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M9.07227 16.5234C8.51562 16.8652 7.86133 16.582 7.86133 16.0059L7.86133 6.95312C7.86133 6.37695 8.55469 6.13281 9.07227 6.44531L16.4355 10.8301C16.9434 11.1328 16.9531 11.8457 16.4355 12.1484Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleRightSquareFill!!
    }

private var _sFArrowtriangleRightSquareFill: ImageVector? = null
