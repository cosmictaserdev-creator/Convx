package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinus (monochrome)
 * Viewport: 20.918 x 1.94336
 */
public val SfSymbols.Monochrome.SFMinus: ImageVector
    get() {
        if (_sFMinus != null) {
            return _sFMinus!!
        }
        _sFMinus = sfIcon(
            name = "Monochrome.SFMinus",
            viewportWidth = 20.918f,
            viewportHeight = 1.94336f
        ) {
            addSfPath("M0.957031 1.94336L19.5996 1.94336C20.1172 1.94336 20.5566 1.50391 20.5566 0.986328C20.5566 0.458984 20.1172 0.0292969 19.5996 0.0292969L0.957031 0.0292969C0.439453 0.0292969 0 0.458984 0 0.986328C0 1.50391 0.439453 1.94336 0.957031 1.94336Z", fillAlpha = 0.85f)
        }
        return _sFMinus!!
    }

private var _sFMinus: ImageVector? = null
