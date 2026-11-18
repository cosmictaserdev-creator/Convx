package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFECircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFECircle: ImageVector
    get() {
        if (_sFECircle != null) {
            return _sFECircle!!
        }
        _sFECircle = sfIcon(
            name = "Monochrome.SFECircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.76562 18.3398L15.7617 18.3398C16.2012 18.3398 16.4941 18.0566 16.4941 17.6172C16.4941 17.1777 16.2012 16.9043 15.7617 16.9043L10.6445 16.9043L10.6445 13.2227L15.4492 13.2227C15.8789 13.2227 16.1719 12.9688 16.1719 12.5391C16.1719 12.0996 15.8789 11.8457 15.4492 11.8457L10.6445 11.8457L10.6445 8.36914L15.7617 8.36914C16.2012 8.36914 16.4941 8.07617 16.4941 7.64648C16.4941 7.20703 16.2012 6.93359 15.7617 6.93359L9.76562 6.93359C9.21875 6.93359 8.92578 7.32422 8.92578 7.90039L8.92578 17.3633C8.92578 17.9395 9.21875 18.3398 9.76562 18.3398Z", fillAlpha = 0.85f)
        }
        return _sFECircle!!
    }

private var _sFECircle: ImageVector? = null
