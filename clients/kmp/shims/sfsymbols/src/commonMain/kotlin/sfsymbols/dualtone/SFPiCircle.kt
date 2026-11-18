package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPiCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPiCircle: ImageVector
    get() {
        if (_sFPiCircle != null) {
            return _sFPiCircle!!
        }
        _sFPiCircle = sfIcon(
            name = "Dualtone.SFPiCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.54102 18.6035C10 18.6035 10.332 18.3008 10.332 17.8418L10.332 8.62305L14.9219 8.62305L14.9219 16.0059C14.9219 17.6758 15.6445 18.5645 17.207 18.5645C18.1152 18.5645 18.623 18.2715 18.623 17.7246C18.623 17.2852 18.3691 17.0508 17.8613 17.0508C17.7148 17.0508 17.5586 17.0703 17.4121 17.0703C16.8164 17.0703 16.5332 16.748 16.5332 15.957L16.5332 8.62305L18.2227 8.62305C18.6328 8.62305 18.9551 8.33008 18.9551 7.88086C18.9551 7.43164 18.6426 7.13867 18.2227 7.13867L7.08008 7.13867C6.65039 7.13867 6.33789 7.43164 6.33789 7.88086C6.33789 8.33008 6.65039 8.62305 7.08008 8.62305L8.7207 8.62305L8.7207 17.8418C8.7207 18.3008 9.05273 18.6035 9.54102 18.6035Z", fillAlpha = 0.85f)
        }
        return _sFPiCircle!!
    }

private var _sFPiCircle: ImageVector? = null
