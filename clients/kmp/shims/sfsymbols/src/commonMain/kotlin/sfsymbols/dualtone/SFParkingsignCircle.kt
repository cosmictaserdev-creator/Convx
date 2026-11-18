package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFParkingsignCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFParkingsignCircle: ImageVector
    get() {
        if (_sFParkingsignCircle != null) {
            return _sFParkingsignCircle!!
        }
        _sFParkingsignCircle = sfIcon(
            name = "Dualtone.SFParkingsignCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M10 18.4668C10.5566 18.4668 10.8691 18.0957 10.8691 17.5098L10.8691 14.4531L13.4961 14.4531C15.8496 14.4531 17.4414 12.9395 17.4414 10.7031C17.4414 8.47656 15.8301 6.93359 13.5059 6.93359L9.98047 6.93359C9.43359 6.93359 9.14062 7.32422 9.14062 7.90039L9.14062 17.5098C9.14062 18.0762 9.44336 18.4668 10 18.4668ZM10.8691 13.1055L10.8691 8.28125L13.2812 8.28125C14.7656 8.28125 15.7324 9.22852 15.7324 10.7031C15.7324 12.1582 14.7754 13.1055 13.2812 13.1055Z", fillAlpha = 0.85f)
        }
        return _sFParkingsignCircle!!
    }

private var _sFParkingsignCircle: ImageVector? = null
