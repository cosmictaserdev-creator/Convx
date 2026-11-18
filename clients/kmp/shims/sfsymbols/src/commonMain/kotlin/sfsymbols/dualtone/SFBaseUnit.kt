package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBaseUnit (dualtone)
 * Viewport: 23.7598 x 16.2988
 */
public val SfSymbols.Dualtone.SFBaseUnit: ImageVector
    get() {
        if (_sFBaseUnit != null) {
            return _sFBaseUnit!!
        }
        _sFBaseUnit = sfIcon(
            name = "Dualtone.SFBaseUnit",
            viewportWidth = 23.7598f,
            viewportHeight = 16.2988f
        ) {
            addSfPath("M0.947266 16.2793C1.50391 16.2793 1.89453 15.8887 1.89453 15.3418L1.89453 9.0918L21.5039 9.0918L21.5039 15.3418C21.5039 15.8887 21.9043 16.2793 22.4512 16.2793C23.0078 16.2793 23.3984 15.8887 23.3984 15.3418L23.3984 0.9375C23.3984 0.390625 23.0078 0 22.4512 0C21.9043 0 21.5039 0.390625 21.5039 0.9375L21.5039 7.19727L1.89453 7.19727L1.89453 0.9375C1.89453 0.390625 1.50391 0 0.947266 0C0.390625 0 0 0.390625 0 0.9375L0 15.3418C0 15.8887 0.390625 16.2793 0.947266 16.2793Z", fillAlpha = 0.85f)
        }
        return _sFBaseUnit!!
    }

private var _sFBaseUnit: ImageVector? = null
