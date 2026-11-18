package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowDownCircle: ImageVector
    get() {
        if (_sFArrowDownCircle != null) {
            return _sFArrowDownCircle!!
        }
        _sFArrowDownCircle = sfIcon(
            name = "Dualtone.SFArrowDownCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7148 6.41602C12.2559 6.41602 11.8652 6.79688 11.8652 7.25586L11.8652 14.7949L11.9336 17.2656C11.9531 17.7344 12.3242 18.0078 12.7148 18.0078C13.1055 18.0078 13.4863 17.7344 13.4961 17.2656L13.5742 14.7949L13.5742 7.25586C13.5742 6.79688 13.1836 6.41602 12.7148 6.41602ZM12.7148 19.0234C12.9395 19.0234 13.125 18.9551 13.3398 18.7402L17.5195 14.6777C17.6758 14.5117 17.7734 14.3359 17.7734 14.1113C17.7734 13.6719 17.4316 13.3496 16.9922 13.3496C16.7773 13.3496 16.5527 13.4375 16.3965 13.6133L14.3262 15.791L12.7148 17.4805L12.7148 17.4805L11.1035 15.791L9.02344 13.6133C8.87695 13.4375 8.64258 13.3496 8.42773 13.3496C7.97852 13.3496 7.65625 13.6719 7.65625 14.1113C7.65625 14.3359 7.74414 14.5117 7.91016 14.6777L12.0996 18.7402C12.3047 18.9551 12.5 19.0234 12.7148 19.0234Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownCircle!!
    }

private var _sFArrowDownCircle: ImageVector? = null
