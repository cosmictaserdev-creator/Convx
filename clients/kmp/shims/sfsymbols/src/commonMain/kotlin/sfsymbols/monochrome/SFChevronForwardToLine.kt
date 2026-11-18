package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronForwardToLine (monochrome)
 * Viewport: 14.2773 x 21.3379
 */
public val SfSymbols.Monochrome.SFChevronForwardToLine: ImageVector
    get() {
        if (_sFChevronForwardToLine != null) {
            return _sFChevronForwardToLine!!
        }
        _sFChevronForwardToLine = sfIcon(
            name = "Monochrome.SFChevronForwardToLine",
            viewportWidth = 14.2773f,
            viewportHeight = 21.3379f
        ) {
            addSfPath("M0.00976562 20.3516C0.00976562 20.9082 0.429688 21.3281 0.976562 21.3281C1.25 21.3281 1.47461 21.2207 1.67969 21.0352L11.709 11.4062C11.9141 11.1914 12.041 10.9473 12.041 10.6641C12.041 10.3711 11.9141 10.127 11.709 9.92188L1.66992 0.292969C1.47461 0.107422 1.25 0 0.966797 0C0.429688 0 0 0.410156 0 0.976562C0 1.24023 0.0976562 1.47461 0.283203 1.66992L10.3613 11.3379L10.3613 9.98047L0.283203 19.668C0.0878906 19.8633 0.00976562 20.0879 0.00976562 20.3516ZM12.002 20.4004C12.002 20.9473 12.4023 21.3281 12.959 21.3281C13.5059 21.3281 13.916 20.9473 13.916 20.4004L13.916 0.947266C13.916 0.390625 13.5059 0 12.959 0C12.4023 0 12.002 0.390625 12.002 0.947266Z", fillAlpha = 0.85f)
        }
        return _sFChevronForwardToLine!!
    }

private var _sFChevronForwardToLine: ImageVector? = null
