package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLockCircle: ImageVector
    get() {
        if (_sFLockCircle != null) {
            return _sFLockCircle!!
        }
        _sFLockCircle = sfIcon(
            name = "Dualtone.SFLockCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.14453 17.7246C8.14453 18.6914 8.56445 19.1309 9.45312 19.1309L15.957 19.1309C16.8457 19.1309 17.2754 18.6914 17.2754 17.7246L17.2754 12.6367C17.2754 11.7285 16.8848 11.2793 16.0938 11.2402L16.0938 9.66797C16.0938 7.41211 14.7266 5.89844 12.7051 5.89844C10.6836 5.89844 9.31641 7.41211 9.31641 9.66797L9.31641 11.2402C8.52539 11.2793 8.14453 11.7285 8.14453 12.6367ZM10.4883 11.2305L10.4883 9.56055C10.4883 8.03711 11.377 7.04102 12.7051 7.04102C14.0332 7.04102 14.9316 8.03711 14.9316 9.56055L14.9316 11.2305Z", fillAlpha = 0.85f)
        }
        return _sFLockCircle!!
    }

private var _sFLockCircle: ImageVector? = null
