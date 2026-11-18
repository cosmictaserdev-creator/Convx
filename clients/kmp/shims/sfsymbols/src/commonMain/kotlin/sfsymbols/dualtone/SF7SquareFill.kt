package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF7SquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SF7SquareFill: ImageVector
    get() {
        if (_sF7SquareFill != null) {
            return _sF7SquareFill!!
        }
        _sF7SquareFill = sfIcon(
            name = "Dualtone.SF7SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M9.75586 17.5293C9.25781 17.5293 8.86719 17.1973 8.86719 16.7188C8.86719 16.4941 8.91602 16.3379 9.02344 16.1328L13.8477 7.14844L13.8477 7.05078L8.21289 7.05078C7.82227 7.05078 7.5293 6.75781 7.5293 6.35742C7.5293 5.9375 7.8125 5.64453 8.21289 5.64453L14.541 5.64453C15.2832 5.64453 15.7812 6.11328 15.7812 6.73828C15.7812 6.99219 15.7324 7.2168 15.5078 7.63672L10.6348 16.9141C10.4102 17.3535 10.1367 17.5293 9.75586 17.5293Z", fillAlpha = 0.85f)
        }
        return _sF7SquareFill!!
    }

private var _sF7SquareFill: ImageVector? = null
