package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronBackwardToLine (dualtone)
 * Viewport: 14.2773 x 21.3379
 */
public val SfSymbols.Dualtone.SFChevronBackwardToLine: ImageVector
    get() {
        if (_sFChevronBackwardToLine != null) {
            return _sFChevronBackwardToLine!!
        }
        _sFChevronBackwardToLine = sfIcon(
            name = "Dualtone.SFChevronBackwardToLine",
            viewportWidth = 14.2773f,
            viewportHeight = 21.3379f
        ) {
            addSfPath("M13.9062 20.3516C13.9062 20.0879 13.8184 19.8633 13.6328 19.668L3.55469 9.98047L3.55469 11.3379L13.6328 1.66992C13.8184 1.47461 13.916 1.24023 13.916 0.976562C13.916 0.410156 13.4863 0 12.9492 0C12.6562 0 12.4414 0.107422 12.2461 0.292969L2.20703 9.92188C2.00195 10.127 1.875 10.3711 1.875 10.6641C1.875 10.9473 1.99219 11.1914 2.20703 11.4062L12.2363 21.0352C12.4316 21.2207 12.6562 21.3281 12.9297 21.3281C13.4863 21.3281 13.9062 20.9082 13.9062 20.3516ZM1.9043 20.4004L1.9043 0.947266C1.9043 0.390625 1.50391 0 0.957031 0C0.400391 0 0 0.390625 0 0.947266L0 20.4004C0 20.9473 0.400391 21.3281 0.957031 21.3281C1.50391 21.3281 1.9043 20.9473 1.9043 20.4004Z", fillAlpha = 0.85f)
        }
        return _sFChevronBackwardToLine!!
    }

private var _sFChevronBackwardToLine: ImageVector? = null
