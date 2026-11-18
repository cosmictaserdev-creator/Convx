package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeForwardCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowshapeForwardCircleFill: ImageVector
    get() {
        if (_sFArrowshapeForwardCircleFill != null) {
            return _sFArrowshapeForwardCircleFill!!
        }
        _sFArrowshapeForwardCircleFill = sfIcon(
            name = "Monochrome.SFArrowshapeForwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.8223 7.04102L12.8223 9.99023L6.81641 9.99023C5.81055 9.99023 5.24414 10.5566 5.24414 11.5234L5.24414 13.9453C5.24414 14.9121 5.81055 15.4688 6.81641 15.4688L12.8223 15.4688L12.8223 18.4277C12.8223 18.877 13.1348 19.1895 13.584 19.1895C13.8965 19.1895 14.1504 19.043 14.4434 18.7793L20.0781 13.4766C20.3809 13.1934 20.459 12.9395 20.459 12.7246C20.459 12.4902 20.3809 12.2461 20.0781 11.9531L14.4434 6.69922C14.1211 6.39648 13.9062 6.25 13.6035 6.25C13.1348 6.25 12.8223 6.62109 12.8223 7.04102Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeForwardCircleFill!!
    }

private var _sFArrowshapeForwardCircleFill: ImageVector? = null
