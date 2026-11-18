package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFYCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFYCircle: ImageVector
    get() {
        if (_sFYCircle != null) {
            return _sFYCircle!!
        }
        _sFYCircle = sfIcon(
            name = "Dualtone.SFYCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.6953 18.4668C13.2617 18.4668 13.5742 18.0957 13.5742 17.5098L13.5742 13.916L17.2168 8.06641C17.3145 7.91016 17.3633 7.73438 17.3633 7.56836C17.3633 7.12891 17.041 6.80664 16.582 6.80664C16.2109 6.80664 16.0059 6.94336 15.7812 7.33398L12.7539 12.2754L12.6758 12.2754L9.64844 7.33398C9.42383 6.93359 9.21875 6.80664 8.82812 6.80664C8.37891 6.80664 8.03711 7.13867 8.03711 7.54883C8.03711 7.72461 8.08594 7.89062 8.18359 8.06641L11.8457 13.9355L11.8457 17.5098C11.8457 18.0762 12.1484 18.4668 12.6953 18.4668Z", fillAlpha = 0.85f)
        }
        return _sFYCircle!!
    }

private var _sFYCircle: ImageVector? = null
