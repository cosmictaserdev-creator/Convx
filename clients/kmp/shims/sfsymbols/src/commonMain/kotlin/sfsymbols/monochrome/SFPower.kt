package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPower (monochrome)
 * Viewport: 25.8008 x 26.9629
 */
public val SfSymbols.Monochrome.SFPower: ImageVector
    get() {
        if (_sFPower != null) {
            return _sFPower!!
        }
        _sFPower = sfIcon(
            name = "Monochrome.SFPower",
            viewportWidth = 25.8008f,
            viewportHeight = 26.9629f
        ) {
            addSfPath("M12.7148 26.1914C19.7363 26.1914 25.4395 20.498 25.4395 13.4766C25.4395 9.42383 23.6035 6.17188 21.2012 4.0332C20.2539 3.16406 19.043 4.48242 19.9902 5.36133C22.2363 7.36328 23.6133 10.2441 23.6133 13.4766C23.6133 19.502 18.7402 24.375 12.7148 24.375C6.68945 24.375 1.81641 19.502 1.81641 13.4766C1.81641 10.2246 3.21289 7.35352 5.44922 5.35156C6.40625 4.46289 5.20508 3.19336 4.23828 4.04297C1.82617 6.13281 0 9.51172 0 13.4766C0 20.498 5.69336 26.1914 12.7148 26.1914ZM12.7148 13.3105C13.2324 13.3105 13.584 12.9395 13.584 12.4121L13.584 0.917969C13.584 0.380859 13.2324 0 12.7148 0C12.207 0 11.8555 0.380859 11.8555 0.917969L11.8555 12.4121C11.8555 12.9395 12.207 13.3105 12.7148 13.3105Z", fillAlpha = 0.85f)
        }
        return _sFPower!!
    }

private var _sFPower: ImageVector? = null
