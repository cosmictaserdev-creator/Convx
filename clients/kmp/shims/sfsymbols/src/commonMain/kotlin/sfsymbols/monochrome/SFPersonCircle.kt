package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPersonCircle: ImageVector
    get() {
        if (_sFPersonCircle != null) {
            return _sFPersonCircle!!
        }
        _sFPersonCircle = sfIcon(
            name = "Monochrome.SFPersonCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M6.88477 19.1406L18.5254 19.1406C18.9746 19.1406 19.1895 18.8477 19.1895 18.418C19.1895 17.041 17.1191 13.3984 12.7051 13.3984C8.30078 13.3984 6.23047 17.041 6.23047 18.418C6.23047 18.8477 6.43555 19.1406 6.88477 19.1406ZM12.7051 12.373C14.4922 12.3828 15.9277 10.8691 15.9277 8.85742C15.9277 6.96289 14.4922 5.40039 12.7051 5.40039C10.918 5.40039 9.48242 6.96289 9.48242 8.85742C9.48242 10.8691 10.918 12.3633 12.7051 12.373Z", fillAlpha = 0.85f)
        }
        return _sFPersonCircle!!
    }

private var _sFPersonCircle: ImageVector? = null
