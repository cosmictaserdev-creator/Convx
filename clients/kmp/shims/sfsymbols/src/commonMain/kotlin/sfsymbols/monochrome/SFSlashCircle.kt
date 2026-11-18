package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSlashCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSlashCircle: ImageVector
    get() {
        if (_sFSlashCircle != null) {
            return _sFSlashCircle!!
        }
        _sFSlashCircle = sfIcon(
            name = "Monochrome.SFSlashCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M8.1543 18.2031C8.44727 18.2031 8.67188 18.0859 8.90625 17.8516L17.7832 8.87695C18.0078 8.65234 18.1348 8.38867 18.1348 8.13477C18.1348 7.63672 17.7246 7.24609 17.2363 7.24609C16.9824 7.24609 16.7285 7.37305 16.4941 7.59766L7.63672 16.5625C7.40234 16.7969 7.27539 17.0605 7.27539 17.3145C7.27539 17.7832 7.62695 18.2031 8.1543 18.2031Z", fillAlpha = 0.85f)
        }
        return _sFSlashCircle!!
    }

private var _sFSlashCircle: ImageVector? = null
