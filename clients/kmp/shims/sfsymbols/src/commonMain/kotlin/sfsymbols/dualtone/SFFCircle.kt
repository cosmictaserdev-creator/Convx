package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFFCircle: ImageVector
    get() {
        if (_sFFCircle != null) {
            return _sFFCircle!!
        }
        _sFFCircle = sfIcon(
            name = "Dualtone.SFFCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.92188 18.4668C10.4883 18.4668 10.8008 18.0957 10.8008 17.5098L10.8008 13.3691L15.4883 13.3691C15.918 13.3691 16.2207 13.1152 16.2207 12.6953C16.2207 12.2461 15.918 11.9922 15.4883 11.9922L10.8008 11.9922L10.8008 8.36914L15.9668 8.36914C16.4062 8.36914 16.6992 8.07617 16.6992 7.64648C16.6992 7.20703 16.4062 6.93359 15.9668 6.93359L9.91211 6.93359C9.36523 6.93359 9.07227 7.32422 9.07227 7.90039L9.07227 17.5098C9.07227 18.0762 9.375 18.4668 9.92188 18.4668Z", fillAlpha = 0.85f)
        }
        return _sFFCircle!!
    }

private var _sFFCircle: ImageVector? = null
