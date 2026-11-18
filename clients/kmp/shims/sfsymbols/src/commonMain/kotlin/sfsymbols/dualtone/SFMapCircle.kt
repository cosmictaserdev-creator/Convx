package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMapCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMapCircle: ImageVector
    get() {
        if (_sFMapCircle != null) {
            return _sFMapCircle!!
        }
        _sFMapCircle = sfIcon(
            name = "Dualtone.SFMapCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M5.91797 18.2227C5.91797 18.6621 6.17188 18.9258 6.60156 18.9258C6.75781 18.9258 6.91406 18.8672 7.09961 18.7695L10 17.1777L10 6.33789C9.88281 6.38672 9.75586 6.45508 9.61914 6.52344L6.44531 8.36914C6.07422 8.58398 5.91797 8.83789 5.91797 9.26758ZM10.8203 17.0898L14.1602 19.0137C14.2578 19.0723 14.375 19.1113 14.4727 19.1406L14.4727 8.39844L11.2109 6.41602C11.084 6.33789 10.957 6.28906 10.8203 6.25977ZM15.2734 19.1113C15.3516 19.0918 15.4297 19.0625 15.5078 19.0234L18.9355 17.0508C19.3164 16.8359 19.4629 16.5918 19.4629 16.1621L19.4629 7.19727C19.4629 6.73828 19.209 6.50391 18.7793 6.50391C18.623 6.50391 18.4668 6.55273 18.2812 6.66016L15.2734 8.31055Z", fillAlpha = 0.85f)
        }
        return _sFMapCircle!!
    }

private var _sFMapCircle: ImageVector? = null
