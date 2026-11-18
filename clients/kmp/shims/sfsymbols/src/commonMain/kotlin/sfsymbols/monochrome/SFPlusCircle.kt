package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPlusCircle: ImageVector
    get() {
        if (_sFPlusCircle != null) {
            return _sFPlusCircle!!
        }
        _sFPlusCircle = sfIcon(
            name = "Monochrome.SFPlusCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M13.6035 17.5586L13.6035 7.86133C13.6035 7.31445 13.2324 6.93359 12.6953 6.93359C12.1777 6.93359 11.8066 7.31445 11.8066 7.86133L11.8066 17.5586C11.8066 18.0957 12.1777 18.4766 12.6953 18.4766C13.2324 18.4766 13.6035 18.1055 13.6035 17.5586ZM7.86133 13.6035L17.5586 13.6035C18.0957 13.6035 18.4766 13.2422 18.4766 12.7246C18.4766 12.1777 18.1055 11.8066 17.5586 11.8066L7.86133 11.8066C7.31445 11.8066 6.94336 12.1777 6.94336 12.7246C6.94336 13.2422 7.31445 13.6035 7.86133 13.6035Z", fillAlpha = 0.85f)
        }
        return _sFPlusCircle!!
    }

private var _sFPlusCircle: ImageVector? = null
