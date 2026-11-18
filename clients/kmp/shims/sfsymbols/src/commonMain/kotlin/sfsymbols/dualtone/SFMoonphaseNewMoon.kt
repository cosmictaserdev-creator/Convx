package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonphaseNewMoon (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMoonphaseNewMoon: ImageVector
    get() {
        if (_sFMoonphaseNewMoon != null) {
            return _sFMoonphaseNewMoon!!
        }
        _sFMoonphaseNewMoon = sfIcon(
            name = "Dualtone.SFMoonphaseNewMoon",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFMoonphaseNewMoon!!
    }

private var _sFMoonphaseNewMoon: ImageVector? = null
