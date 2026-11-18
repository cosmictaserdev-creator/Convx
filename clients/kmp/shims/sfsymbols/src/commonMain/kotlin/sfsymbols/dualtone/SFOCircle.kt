package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFOCircle: ImageVector
    get() {
        if (_sFOCircle != null) {
            return _sFOCircle!!
        }
        _sFOCircle = sfIcon(
            name = "Dualtone.SFOCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7051 18.5938C15.8789 18.5938 18.0566 16.1621 18.0566 12.6367C18.0566 9.10156 15.8789 6.66992 12.7051 6.66992C9.54102 6.66992 7.35352 9.10156 7.35352 12.6367C7.35352 16.1621 9.54102 18.5938 12.7051 18.5938ZM12.7051 17.207C10.5273 17.207 9.08203 15.3906 9.08203 12.6367C9.08203 9.88281 10.5273 8.05664 12.7051 8.05664C14.8926 8.05664 16.3379 9.88281 16.3379 12.6367C16.3379 15.3906 14.8926 17.207 12.7051 17.207Z", fillAlpha = 0.85f)
        }
        return _sFOCircle!!
    }

private var _sFOCircle: ImageVector? = null
