package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonphaseFirstQuarter (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMoonphaseFirstQuarter: ImageVector
    get() {
        if (_sFMoonphaseFirstQuarter != null) {
            return _sFMoonphaseFirstQuarter!!
        }
        _sFMoonphaseFirstQuarter = sfIcon(
            name = "Dualtone.SFMoonphaseFirstQuarter",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 23.6719L12.7148 1.875C18.7402 1.875 23.6133 6.74805 23.6133 12.7734C23.6133 18.7988 18.7402 23.6719 12.7148 23.6719ZM12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFMoonphaseFirstQuarter!!
    }

private var _sFMoonphaseFirstQuarter: ImageVector? = null
