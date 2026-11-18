package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCircleCircleFill: ImageVector
    get() {
        if (_sFCircleCircleFill != null) {
            return _sFCircleCircleFill!!
        }
        _sFCircleCircleFill = sfIcon(
            name = "Monochrome.SFCircleCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM5.82031 12.7246C5.82031 16.5332 8.90625 19.6191 12.7148 19.6191C16.5332 19.6191 19.6094 16.5332 19.6094 12.7246C19.6094 8.90625 16.5332 5.83008 12.7148 5.83008C8.90625 5.83008 5.82031 8.90625 5.82031 12.7246ZM17.8809 12.7246C17.8809 15.5664 15.5664 17.8809 12.7148 17.8809C9.86328 17.8809 7.55859 15.5664 7.55859 12.7246C7.55859 9.87305 9.86328 7.55859 12.7148 7.55859C15.5664 7.55859 17.8809 9.87305 17.8809 12.7246Z", fillAlpha = 0.85f)
        }
        return _sFCircleCircleFill!!
    }

private var _sFCircleCircleFill: ImageVector? = null
