package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleRightSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowtriangleRightSquareFill: ImageVector
    get() {
        if (_sFArrowtriangleRightSquareFill != null) {
            return _sFArrowtriangleRightSquareFill!!
        }
        _sFArrowtriangleRightSquareFill = sfIcon(
            name = "Monochrome.SFArrowtriangleRightSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.86133 6.95312L7.86133 16.0059C7.86133 16.582 8.51562 16.8652 9.07227 16.5234L16.4355 12.1484C16.9531 11.8457 16.9434 11.1328 16.4355 10.8301L9.07227 6.44531C8.55469 6.13281 7.86133 6.37695 7.86133 6.95312Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleRightSquareFill!!
    }

private var _sFArrowtriangleRightSquareFill: ImageVector? = null
