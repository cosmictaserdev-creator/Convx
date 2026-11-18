package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFRSquareFill: ImageVector
    get() {
        if (_sFRSquareFill != null) {
            return _sFRSquareFill!!
        }
        _sFRSquareFill = sfIcon(
            name = "Dualtone.SFRSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.18359 17.3633C7.62695 17.3633 7.31445 16.9629 7.31445 16.377L7.31445 6.54297C7.31445 5.94727 7.61719 5.53711 8.17383 5.53711L11.8555 5.53711C14.3457 5.53711 15.957 6.99219 15.957 9.23828C15.957 10.8594 14.9609 12.1875 13.4277 12.6465L15.4785 15.9375C15.625 16.1719 15.6836 16.3477 15.6836 16.6016C15.6836 17.0508 15.3613 17.3633 14.8828 17.3633C14.4824 17.3633 14.2578 17.1973 13.9941 16.7676L11.6699 12.9004L9.0918 12.9004L9.0918 16.377C9.0918 16.9824 8.76953 17.3633 8.18359 17.3633ZM9.0918 11.5625L11.6211 11.5625C13.1836 11.5625 14.1992 10.7129 14.1992 9.27734C14.1992 7.80273 13.1641 6.92383 11.6602 6.92383L9.0918 6.92383Z", fillAlpha = 0.85f)
        }
        return _sFRSquareFill!!
    }

private var _sFRSquareFill: ImageVector? = null
