package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBCircle: ImageVector
    get() {
        if (_sFBCircle != null) {
            return _sFBCircle!!
        }
        _sFBCircle = sfIcon(
            name = "Dualtone.SFBCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.61914 18.3691L13.3008 18.3691C15.8594 18.3691 17.4902 17.1289 17.4902 15.1562C17.4902 13.6328 16.4746 12.5488 14.834 12.3438L14.834 12.2754C16.0449 11.9824 16.8066 10.9766 16.8066 9.72656C16.8066 8.02734 15.4199 6.9043 13.3105 6.9043L9.61914 6.9043C9.02344 6.9043 8.67188 7.25586 8.67188 7.86133L8.67188 17.4023C8.67188 18.0078 9.02344 18.3691 9.61914 18.3691ZM10.3516 11.8457L10.3516 8.20312L12.9688 8.20312C14.3359 8.20312 15.1465 8.87695 15.1465 10C15.1465 11.1523 14.1699 11.8457 12.5 11.8457ZM10.3516 17.0605L10.3516 13.0762L13.2324 13.0762C14.8145 13.0762 15.752 13.8281 15.752 15.1074C15.752 16.4258 14.7461 17.0605 12.6172 17.0605Z", fillAlpha = 0.85f)
        }
        return _sFBCircle!!
    }

private var _sFBCircle: ImageVector? = null
