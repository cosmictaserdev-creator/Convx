package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClock (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFClock: ImageVector
    get() {
        if (_sFClock != null) {
            return _sFClock!!
        }
        _sFClock = sfIcon(
            name = "Dualtone.SFClock",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M5.95703 14.0039L12.7051 14.0039C13.1348 14.0039 13.4668 13.6719 13.4668 13.2422L13.4668 4.53125C13.4668 4.11133 13.1348 3.7793 12.7051 3.7793C12.2949 3.7793 11.9531 4.11133 11.9531 4.53125L11.9531 12.4902L5.95703 12.4902C5.52734 12.4902 5.20508 12.8223 5.20508 13.2422C5.20508 13.6719 5.52734 14.0039 5.95703 14.0039Z", fillAlpha = 0.85f)
        }
        return _sFClock!!
    }

private var _sFClock: ImageVector? = null
