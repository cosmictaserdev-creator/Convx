package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFFSquareFill: ImageVector
    get() {
        if (_sFFSquareFill != null) {
            return _sFFSquareFill!!
        }
        _sFFSquareFill = sfIcon(
            name = "Monochrome.SFFSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.62305 5.53711C8.05664 5.53711 7.75391 5.94727 7.75391 6.54297L7.75391 16.3672C7.75391 16.9531 8.06641 17.3633 8.63281 17.3633C9.21875 17.3633 9.53125 16.9727 9.53125 16.3672L9.53125 12.1387L14.3359 12.1387C14.7754 12.1387 15.0879 11.8848 15.0879 11.4453C15.0879 10.9863 14.7754 10.7227 14.3359 10.7227L9.53125 10.7227L9.53125 7.02148L14.8242 7.02148C15.2734 7.02148 15.5762 6.71875 15.5762 6.2793C15.5762 5.82031 15.2734 5.53711 14.8242 5.53711Z", fillAlpha = 0.85f)
        }
        return _sFFSquareFill!!
    }

private var _sFFSquareFill: ImageVector? = null
