package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFVideoSquare: ImageVector
    get() {
        if (_sFVideoSquare != null) {
            return _sFVideoSquare!!
        }
        _sFVideoSquare = sfIcon(
            name = "Monochrome.SFVideoSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M6.26953 16.5137L12.9297 16.5137C14.1895 16.5137 14.9219 15.791 14.9219 14.541L14.9219 8.4375C14.9219 7.17773 14.2383 6.46484 12.9785 6.46484L6.26953 6.46484C5.04883 6.46484 4.26758 7.17773 4.26758 8.4375L4.26758 14.541C4.26758 15.791 5.00977 16.5137 6.26953 16.5137ZM15.6152 13.2129L18.0078 15.2344C18.2324 15.4102 18.4668 15.5371 18.6914 15.5371C19.1406 15.5371 19.4434 15.2051 19.4434 14.6973L19.4434 8.26172C19.4434 7.77344 19.1406 7.44141 18.6914 7.44141C18.4668 7.44141 18.2227 7.56836 18.0078 7.73438L15.6152 9.75586Z", fillAlpha = 0.85f)
        }
        return _sFVideoSquare!!
    }

private var _sFVideoSquare: ImageVector? = null
