package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonphaseFirstQuarterInverse (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMoonphaseFirstQuarterInverse: ImageVector
    get() {
        if (_sFMoonphaseFirstQuarterInverse != null) {
            return _sFMoonphaseFirstQuarterInverse!!
        }
        _sFMoonphaseFirstQuarterInverse = sfIcon(
            name = "Monochrome.SFMoonphaseFirstQuarterInverse",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 23.6719C6.68945 23.6719 1.81641 18.7988 1.81641 12.7734C1.81641 6.74805 6.68945 1.875 12.7148 1.875ZM12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFMoonphaseFirstQuarterInverse!!
    }

private var _sFMoonphaseFirstQuarterInverse: ImageVector? = null
