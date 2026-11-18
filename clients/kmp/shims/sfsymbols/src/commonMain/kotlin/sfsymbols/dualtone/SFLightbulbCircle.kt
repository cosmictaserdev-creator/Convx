package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightbulbCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLightbulbCircle: ImageVector
    get() {
        if (_sFLightbulbCircle != null) {
            return _sFLightbulbCircle!!
        }
        _sFLightbulbCircle = sfIcon(
            name = "Dualtone.SFLightbulbCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.33008 9.07227C8.33008 11.543 10 12.207 10.2539 16.6504C10.2734 16.8848 10.4297 17.0312 10.6445 17.0312L14.7559 17.0312C14.9902 17.0312 15.1367 16.8848 15.1562 16.6504C15.4004 12.207 17.0801 11.5918 17.0801 9.12109C17.0801 6.89453 15.1465 5.08789 12.6953 5.08789C10.2539 5.08789 8.33008 6.8457 8.33008 9.07227ZM10.752 18.6328L14.6582 18.6328C14.9121 18.6328 15.127 18.418 15.127 18.1641C15.127 17.9004 14.9121 17.6953 14.6582 17.6953L10.752 17.6953C10.498 17.6953 10.2832 17.9004 10.2832 18.1641C10.2832 18.418 10.498 18.6328 10.752 18.6328ZM12.7051 20.3516C13.6035 20.3516 14.3066 19.9316 14.3848 19.2969L11.0352 19.2969C11.0938 19.9316 11.7969 20.3516 12.7051 20.3516Z", fillAlpha = 0.85f)
        }
        return _sFLightbulbCircle!!
    }

private var _sFLightbulbCircle: ImageVector? = null
