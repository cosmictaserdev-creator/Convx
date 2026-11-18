package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCircleCircleFill: ImageVector
    get() {
        if (_sFCircleCircleFill != null) {
            return _sFCircleCircleFill!!
        }
        _sFCircleCircleFill = sfIcon(
            name = "Dualtone.SFCircleCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 19.6191C8.90625 19.6191 5.82031 16.5332 5.82031 12.7246C5.82031 8.90625 8.90625 5.83008 12.7148 5.83008C16.5332 5.83008 19.6094 8.90625 19.6094 12.7246C19.6094 16.5332 16.5332 19.6191 12.7148 19.6191ZM12.7148 17.8809C15.5664 17.8809 17.8809 15.5664 17.8809 12.7246C17.8809 9.87305 15.5664 7.55859 12.7148 7.55859C9.86328 7.55859 7.55859 9.87305 7.55859 12.7246C7.55859 15.5664 9.86328 17.8809 12.7148 17.8809Z", fillAlpha = 0.85f)
        }
        return _sFCircleCircleFill!!
    }

private var _sFCircleCircleFill: ImageVector? = null
