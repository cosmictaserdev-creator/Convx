package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPillsCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPillsCircle: ImageVector
    get() {
        if (_sFPillsCircle != null) {
            return _sFPillsCircle!!
        }
        _sFPillsCircle = sfIcon(
            name = "Dualtone.SFPillsCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.5098 12.8418L14.6289 10.7324C15.8594 9.53125 15.8691 7.94922 14.6777 6.79688C13.5059 5.61523 11.9434 5.625 10.7422 6.83594L8.63281 8.95508ZM11.8457 13.5156L7.95898 9.62891L5.83008 11.7676C4.62891 12.9785 4.60938 14.5508 5.76172 15.7129C6.95312 16.875 8.50586 16.8555 9.7168 15.6543ZM20.0391 15.3125C19.8438 13.6621 18.3887 12.334 16.6113 12.334C14.873 12.334 13.418 13.6621 13.2129 15.3125ZM20.0391 16.25L13.2129 16.25C13.418 17.8906 14.8633 19.2188 16.6113 19.2188C18.3984 19.2188 19.8438 17.8906 20.0391 16.25Z", fillAlpha = 0.85f)
        }
        return _sFPillsCircle!!
    }

private var _sFPillsCircle: ImageVector? = null
