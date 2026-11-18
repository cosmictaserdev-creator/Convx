package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPause (dualtone)
 * Viewport: 10.4883 x 20.5566
 */
public val SfSymbols.Dualtone.SFPause: ImageVector
    get() {
        if (_sFPause != null) {
            return _sFPause!!
        }
        _sFPause = sfIcon(
            name = "Dualtone.SFPause",
            viewportWidth = 10.4883f,
            viewportHeight = 20.5566f
        ) {
            addSfPath("M0.917969 20.5371C1.41602 20.5371 1.82617 20.1465 1.82617 19.6484L1.82617 0.898438C1.82617 0.390625 1.41602 0 0.917969 0C0.410156 0 0 0.390625 0 0.898438L0 19.6484C0 20.1465 0.410156 20.5371 0.917969 20.5371ZM9.21875 20.5371C9.7168 20.5371 10.127 20.1465 10.127 19.6484L10.127 0.898438C10.127 0.390625 9.7168 0 9.21875 0C8.71094 0 8.30078 0.390625 8.30078 0.898438L8.30078 19.6484C8.30078 20.1465 8.71094 20.5371 9.21875 20.5371Z", fillAlpha = 0.85f)
        }
        return _sFPause!!
    }

private var _sFPause: ImageVector? = null
