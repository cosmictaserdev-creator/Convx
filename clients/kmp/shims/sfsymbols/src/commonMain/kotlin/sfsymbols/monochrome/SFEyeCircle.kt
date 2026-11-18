package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFEyeCircle: ImageVector
    get() {
        if (_sFEyeCircle != null) {
            return _sFEyeCircle!!
        }
        _sFEyeCircle = sfIcon(
            name = "Monochrome.SFEyeCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 18.1055C17.7148 18.1055 21.2402 13.9941 21.2402 12.7246C21.2402 11.4551 17.6953 7.34375 12.7051 7.34375C7.68555 7.34375 4.16992 11.4551 4.16992 12.7246C4.16992 13.9941 7.73438 18.1055 12.7051 18.1055ZM12.7051 16.25C10.7422 16.25 9.16992 14.6387 9.16992 12.7246C9.16016 10.7617 10.7422 9.19922 12.7051 9.19922C14.6484 9.19922 16.2402 10.7617 16.2402 12.7246C16.2402 14.6387 14.6484 16.25 12.7051 16.25ZM12.7246 14.209C13.5156 14.209 14.1992 13.5156 14.1992 12.7246C14.1992 11.9238 13.5156 11.2402 12.7246 11.2402C11.9043 11.2402 11.2109 11.9238 11.2109 12.7246C11.2109 13.5156 11.9043 14.209 12.7246 14.209Z", fillAlpha = 0.85f)
        }
        return _sFEyeCircle!!
    }

private var _sFEyeCircle: ImageVector? = null
