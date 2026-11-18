package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLine3Horizontal (monochrome)
 * Viewport: 25.6738 x 12.9395
 */
public val SfSymbols.Monochrome.SFLine3Horizontal: ImageVector
    get() {
        if (_sFLine3Horizontal != null) {
            return _sFLine3Horizontal!!
        }
        _sFLine3Horizontal = sfIcon(
            name = "Monochrome.SFLine3Horizontal",
            viewportWidth = 25.6738f,
            viewportHeight = 12.9395f
        ) {
            addSfPath("M0.820312 12.9395L24.4824 12.9395C24.9414 12.9395 25.3125 12.5781 25.3125 12.1094C25.3125 11.6602 24.9414 11.2988 24.4824 11.2988L0.820312 11.2988C0.361328 11.2988 0 11.6602 0 12.1094C0 12.5781 0.361328 12.9395 0.820312 12.9395Z", fillAlpha = 0.85f)
            addSfPath("M0.820312 7.31445L24.4824 7.31445C24.9414 7.31445 25.3125 6.95312 25.3125 6.49414C25.3125 6.03516 24.9414 5.67383 24.4824 5.67383L0.820312 5.67383C0.361328 5.67383 0 6.03516 0 6.49414C0 6.95312 0.361328 7.31445 0.820312 7.31445Z", fillAlpha = 0.85f)
            addSfPath("M0.820312 1.67969L24.4824 1.67969C24.9414 1.67969 25.3125 1.31836 25.3125 0.869141C25.3125 0.410156 24.9414 0.0488281 24.4824 0.0488281L0.820312 0.0488281C0.361328 0.0488281 0 0.410156 0 0.869141C0 1.31836 0.361328 1.67969 0.820312 1.67969Z", fillAlpha = 0.85f)
        }
        return _sFLine3Horizontal!!
    }

private var _sFLine3Horizontal: ImageVector? = null
