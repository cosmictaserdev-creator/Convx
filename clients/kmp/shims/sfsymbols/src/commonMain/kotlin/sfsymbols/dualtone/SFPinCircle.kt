package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPinCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPinCircle: ImageVector
    get() {
        if (_sFPinCircle != null) {
            return _sFPinCircle!!
        }
        _sFPinCircle = sfIcon(
            name = "Dualtone.SFPinCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M7.76367 15.2148C7.76367 15.7227 8.10547 16.0449 8.63281 16.0449L12.0215 16.0449L12.0215 18.5059C12.0215 19.7363 12.5 20.7617 12.7148 20.7617C12.9199 20.7617 13.4082 19.7363 13.4082 18.5059L13.4082 16.0449L16.7871 16.0449C17.3242 16.0449 17.666 15.7227 17.666 15.2148C17.666 13.8281 16.5527 12.3828 14.7559 11.748L14.541 8.70117C15.3906 8.21289 16.1621 7.61719 16.5137 7.16797C16.6797 6.94336 16.7676 6.71875 16.7676 6.52344C16.7676 6.16211 16.4941 5.89844 16.0547 5.89844L9.375 5.89844C8.94531 5.89844 8.66211 6.16211 8.66211 6.52344C8.66211 6.72852 8.75977 6.96289 8.94531 7.1875C9.29688 7.63672 10.0488 8.22266 10.8887 8.70117L10.6738 11.748C8.87695 12.3828 7.76367 13.8281 7.76367 15.2148Z", fillAlpha = 0.85f)
        }
        return _sFPinCircle!!
    }

private var _sFPinCircle: ImageVector? = null
