package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightCylindricalCeilingFill (dualtone)
 * Viewport: 24.4336 x 25.459
 */
public val SfSymbols.Dualtone.SFLightCylindricalCeilingFill: ImageVector
    get() {
        if (_sFLightCylindricalCeilingFill != null) {
            return _sFLightCylindricalCeilingFill!!
        }
        _sFLightCylindricalCeilingFill = sfIcon(
            name = "Dualtone.SFLightCylindricalCeilingFill",
            viewportWidth = 24.4336f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.0312 12.5586C17.6367 12.5586 21.9238 13.9453 24.0723 16.3379L24.0723 6.64062C24.0723 2.64648 19.3262 0 12.0312 0C4.74609 0 0 2.64648 0 6.64062L0 16.3379C2.13867 13.9453 6.43555 12.5586 12.0312 12.5586ZM12.0312 25.4395C19.0039 25.4395 24.0332 23.2031 24.0332 19.7754C24.0332 16.3574 19.0039 14.1113 12.0312 14.1113C5.06836 14.1113 0.0390625 16.3574 0.0390625 19.7754C0.0390625 23.2031 5.06836 25.4395 12.0312 25.4395ZM12.0312 19.3457C9.29688 19.3457 7.08008 17.9395 6.93359 16.2207C8.41797 15.9082 10.1465 15.7227 12.0312 15.7227C13.9355 15.7227 15.6543 15.9082 17.1387 16.2207C16.9922 17.9395 14.7656 19.3457 12.0312 19.3457Z", fillAlpha = 0.85f)
        }
        return _sFLightCylindricalCeilingFill!!
    }

private var _sFLightCylindricalCeilingFill: ImageVector? = null
