package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFJCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFJCircle: ImageVector
    get() {
        if (_sFJCircle != null) {
            return _sFJCircle!!
        }
        _sFJCircle = sfIcon(
            name = "Dualtone.SFJCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.1387 18.5645C14.4824 18.5645 15.7617 17.2559 15.7617 14.9707L15.7617 7.76367C15.7617 7.16797 15.459 6.80664 14.8926 6.80664C14.3457 6.80664 14.043 7.1875 14.043 7.76367L14.043 14.9512C14.043 16.2695 13.3496 17.0801 12.2266 17.0801C11.2012 17.0801 10.4785 16.5234 10.2148 15.6348C10.0293 15.2148 9.78516 15.0391 9.41406 15.0391C8.93555 15.0391 8.62305 15.3613 8.62305 15.8496C8.62305 16.0254 8.64258 16.2012 8.70117 16.377C9.0918 17.6855 10.4395 18.5645 12.1387 18.5645Z", fillAlpha = 0.85f)
        }
        return _sFJCircle!!
    }

private var _sFJCircle: ImageVector? = null
