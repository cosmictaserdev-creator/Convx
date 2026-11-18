package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFFSquareFill: ImageVector
    get() {
        if (_sFFSquareFill != null) {
            return _sFFSquareFill!!
        }
        _sFFSquareFill = sfIcon(
            name = "Dualtone.SFFSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.63281 17.3633C8.06641 17.3633 7.75391 16.9531 7.75391 16.3672L7.75391 6.54297C7.75391 5.94727 8.05664 5.53711 8.62305 5.53711L14.8242 5.53711C15.2734 5.53711 15.5762 5.82031 15.5762 6.2793C15.5762 6.71875 15.2734 7.02148 14.8242 7.02148L9.53125 7.02148L9.53125 10.7227L14.3359 10.7227C14.7754 10.7227 15.0879 10.9863 15.0879 11.4453C15.0879 11.8848 14.7754 12.1387 14.3359 12.1387L9.53125 12.1387L9.53125 16.3672C9.53125 16.9727 9.21875 17.3633 8.63281 17.3633Z", fillAlpha = 0.85f)
        }
        return _sFFSquareFill!!
    }

private var _sFFSquareFill: ImageVector? = null
