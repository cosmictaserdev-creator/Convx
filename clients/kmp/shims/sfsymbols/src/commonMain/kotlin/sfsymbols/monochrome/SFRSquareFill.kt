package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFRSquareFill: ImageVector
    get() {
        if (_sFRSquareFill != null) {
            return _sFRSquareFill!!
        }
        _sFRSquareFill = sfIcon(
            name = "Monochrome.SFRSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.17383 5.53711C7.61719 5.53711 7.31445 5.94727 7.31445 6.54297L7.31445 16.377C7.31445 16.9629 7.62695 17.3633 8.18359 17.3633C8.76953 17.3633 9.0918 16.9824 9.0918 16.377L9.0918 12.9004L11.6699 12.9004L13.9941 16.7676C14.2578 17.1973 14.4824 17.3633 14.8828 17.3633C15.3613 17.3633 15.6836 17.0508 15.6836 16.6016C15.6836 16.3477 15.625 16.1719 15.4785 15.9375L13.4277 12.6465C14.9609 12.1875 15.957 10.8594 15.957 9.23828C15.957 6.99219 14.3457 5.53711 11.8555 5.53711ZM14.1992 9.27734C14.1992 10.7129 13.1836 11.5625 11.6211 11.5625L9.0918 11.5625L9.0918 6.92383L11.6602 6.92383C13.1641 6.92383 14.1992 7.80273 14.1992 9.27734Z", fillAlpha = 0.85f)
        }
        return _sFRSquareFill!!
    }

private var _sFRSquareFill: ImageVector? = null
