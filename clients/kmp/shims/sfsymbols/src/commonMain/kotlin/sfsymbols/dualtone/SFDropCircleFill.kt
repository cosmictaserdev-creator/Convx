package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDropCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFDropCircleFill: ImageVector
    get() {
        if (_sFDropCircleFill != null) {
            return _sFDropCircleFill!!
        }
        _sFDropCircleFill = sfIcon(
            name = "Dualtone.SFDropCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 19.1406C10.0586 19.1406 8.26172 17.4219 8.26172 14.834C8.26172 13.5938 8.7793 12.4121 9.22852 11.4258C9.94141 9.89258 11.0547 8.03711 12.0605 6.50391C12.2559 6.21094 12.4512 6.08398 12.7148 6.08398C12.9785 6.08398 13.1836 6.21094 13.3691 6.50391C14.3848 8.03711 15.498 9.89258 16.2012 11.4258C16.6504 12.4121 17.1777 13.5938 17.1777 14.834C17.1777 17.4219 15.3711 19.1406 12.7148 19.1406Z", fillAlpha = 0.85f)
        }
        return _sFDropCircleFill!!
    }

private var _sFDropCircleFill: ImageVector? = null
