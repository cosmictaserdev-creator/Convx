package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeyCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFKeyCircle: ImageVector
    get() {
        if (_sFKeyCircle != null) {
            return _sFKeyCircle!!
        }
        _sFKeyCircle = sfIcon(
            name = "Monochrome.SFKeyCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7148 5.09766C10.4492 5.09766 8.63281 6.9043 8.63281 9.17969C8.63281 10.8887 9.6582 12.4023 11.2793 13.0078L11.2793 19.3945C11.2793 19.541 11.3184 19.6582 11.4258 19.7852L12.5098 20.8496C12.6074 20.957 12.8125 20.9863 12.9297 20.8496L14.9805 18.8184C15.0977 18.7012 15.0879 18.5254 14.9805 18.4082L13.6816 17.1289L15.4492 15.3711C15.5664 15.2637 15.5664 15.0781 15.4395 14.9609L13.6816 13.1836C15.6836 12.4023 16.7969 10.9375 16.7969 9.17969C16.7969 6.91406 14.9902 5.09766 12.7148 5.09766ZM12.7148 9.0332C12.0703 9.0332 11.5625 8.52539 11.5625 7.88086C11.5625 7.23633 12.0703 6.71875 12.7148 6.71875C13.3594 6.71875 13.877 7.23633 13.877 7.88086C13.877 8.52539 13.3594 9.0332 12.7148 9.0332Z", fillAlpha = 0.85f)
        }
        return _sFKeyCircle!!
    }

private var _sFKeyCircle: ImageVector? = null
