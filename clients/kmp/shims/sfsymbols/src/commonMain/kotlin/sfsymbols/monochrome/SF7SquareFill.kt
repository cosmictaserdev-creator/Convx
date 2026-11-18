package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF7SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF7SquareFill: ImageVector
    get() {
        if (_sF7SquareFill != null) {
            return _sF7SquareFill!!
        }
        _sF7SquareFill = sfIcon(
            name = "Monochrome.SF7SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.21289 5.64453C7.8125 5.64453 7.5293 5.9375 7.5293 6.35742C7.5293 6.75781 7.82227 7.05078 8.21289 7.05078L13.8477 7.05078L13.8477 7.14844L9.02344 16.1328C8.91602 16.3379 8.86719 16.4941 8.86719 16.7188C8.86719 17.1973 9.25781 17.5293 9.75586 17.5293C10.1367 17.5293 10.4102 17.3535 10.6348 16.9141L15.5078 7.63672C15.7324 7.2168 15.7812 6.99219 15.7812 6.73828C15.7812 6.11328 15.2832 5.64453 14.541 5.64453Z", fillAlpha = 0.85f)
        }
        return _sF7SquareFill!!
    }

private var _sF7SquareFill: ImageVector? = null
