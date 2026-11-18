package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFJSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFJSquareFill: ImageVector
    get() {
        if (_sFJSquareFill != null) {
            return _sFJSquareFill!!
        }
        _sFJSquareFill = sfIcon(
            name = "Dualtone.SFJSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M10.9082 17.4609C9.16016 17.4609 7.7832 16.5625 7.38281 15.2246C7.31445 15.0391 7.28516 14.8633 7.28516 14.6777C7.28516 14.1699 7.61719 13.8379 8.11523 13.8379C8.49609 13.8379 8.75 14.0234 8.93555 14.4531C9.21875 15.3711 9.95117 15.9277 10.9863 15.9277C12.1289 15.9277 12.8516 15.1074 12.8516 13.75L12.8516 6.39648C12.8516 5.80078 13.1641 5.40039 13.7305 5.40039C14.3164 5.40039 14.6289 5.78125 14.6289 6.39648L14.6289 13.7695C14.6289 16.123 13.3105 17.4609 10.9082 17.4609Z", fillAlpha = 0.85f)
        }
        return _sFJSquareFill!!
    }

private var _sFJSquareFill: ImageVector? = null
