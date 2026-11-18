package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeFill (dualtone)
 * Viewport: 34.1992 x 21.3379
 */
public val SfSymbols.Dualtone.SFEyeFill: ImageVector
    get() {
        if (_sFEyeFill != null) {
            return _sFEyeFill!!
        }
        _sFEyeFill = sfIcon(
            name = "Dualtone.SFEyeFill",
            viewportWidth = 34.1992f,
            viewportHeight = 21.3379f
        ) {
            addSfPath("M16.9238 21.3184C26.875 21.3184 33.8379 13.1836 33.8379 10.6641C33.8379 8.125 26.8457 0 16.9238 0C7.08984 0 0 8.125 0 10.6641C0 13.1836 7.07031 21.3184 16.9238 21.3184ZM16.9238 17.6758C13.0566 17.6758 9.91211 14.5312 9.91211 10.6641C9.91211 6.78711 13.0566 3.64258 16.9238 3.64258C20.791 3.64258 23.9355 6.78711 23.9355 10.6641C23.9355 14.5312 20.791 17.6758 16.9238 17.6758ZM16.9238 13.125C18.291 13.125 19.3945 12.0312 19.3945 10.6641C19.3945 9.29688 18.291 8.19336 16.9238 8.19336C15.5566 8.19336 14.4531 9.29688 14.4531 10.6641C14.4531 12.0312 15.5566 13.125 16.9238 13.125Z", fillAlpha = 0.85f)
        }
        return _sFEyeFill!!
    }

private var _sFEyeFill: ImageVector? = null
