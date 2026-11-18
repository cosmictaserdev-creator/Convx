package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWrongwaysign (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFWrongwaysign: ImageVector
    get() {
        if (_sFWrongwaysign != null) {
            return _sFWrongwaysign!!
        }
        _sFWrongwaysign = sfIcon(
            name = "Dualtone.SFWrongwaysign",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M6.31836 15.5957L19.1211 15.5957C19.7656 15.5957 20.2246 15.127 20.2246 14.4824L20.2246 10.9277C20.2246 10.293 19.7656 9.82422 19.1211 9.82422L6.31836 9.82422C5.67383 9.82422 5.21484 10.293 5.21484 10.9277L5.21484 14.502C5.21484 15.1465 5.67383 15.5957 6.31836 15.5957Z", fillAlpha = 0.85f)
        }
        return _sFWrongwaysign!!
    }

private var _sFWrongwaysign: ImageVector? = null
