package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFForwardCircle: ImageVector
    get() {
        if (_sFForwardCircle != null) {
            return _sFForwardCircle!!
        }
        _sFForwardCircle = sfIcon(
            name = "Dualtone.SFForwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M14.4141 17.373C14.6387 17.373 14.834 17.2949 15.0586 17.1582L21.2305 13.5352C21.5918 13.3203 21.7578 13.0469 21.7578 12.7344C21.7578 12.4219 21.5918 12.1484 21.2305 11.9336L15.0586 8.31055C14.834 8.17383 14.6387 8.0957 14.4141 8.0957C13.9844 8.0957 13.6133 8.41797 13.6133 9.04297L13.6133 16.4258C13.6133 17.0508 13.9844 17.373 14.4141 17.373ZM6.30859 17.373C6.52344 17.373 6.72852 17.2949 6.94336 17.1582L13.1152 13.5352C13.4766 13.3203 13.6523 13.0469 13.6523 12.7344C13.6523 12.4219 13.4766 12.1484 13.1152 11.9336L6.94336 8.31055C6.72852 8.17383 6.52344 8.0957 6.30859 8.0957C5.86914 8.0957 5.50781 8.41797 5.50781 9.04297L5.50781 16.4258C5.50781 17.0508 5.86914 17.373 6.30859 17.373Z", fillAlpha = 0.85f)
        }
        return _sFForwardCircle!!
    }

private var _sFForwardCircle: ImageVector? = null
