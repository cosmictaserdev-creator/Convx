package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleAndLineHorizontalFill (dualtone)
 * Viewport: 35.0488 x 25.459
 */
public val SfSymbols.Dualtone.SFCircleAndLineHorizontalFill: ImageVector
    get() {
        if (_sFCircleAndLineHorizontalFill != null) {
            return _sFCircleAndLineHorizontalFill!!
        }
        _sFCircleAndLineHorizontalFill = sfIcon(
            name = "Dualtone.SFCircleAndLineHorizontalFill",
            viewportWidth = 35.0488f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M0 12.7344C0 12.2656 0.380859 11.875 0.839844 11.875L33.8477 11.875C34.3066 11.875 34.6875 12.2656 34.6875 12.7344C34.6875 13.1934 34.3066 13.5938 33.8477 13.5938L0.839844 13.5938C0.380859 13.5938 0 13.1934 0 12.7344ZM17.3535 25.4395C24.3555 25.4395 30.0684 19.7266 30.0684 12.7246C30.0684 5.71289 24.3555 0 17.3535 0C10.3418 0 4.62891 5.71289 4.62891 12.7246C4.62891 19.7266 10.3418 25.4395 17.3535 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFCircleAndLineHorizontalFill!!
    }

private var _sFCircleAndLineHorizontalFill: ImageVector? = null
