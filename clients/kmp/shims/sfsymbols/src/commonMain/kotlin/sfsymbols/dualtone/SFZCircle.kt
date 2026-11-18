package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFZCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFZCircle: ImageVector
    get() {
        if (_sFZCircle != null) {
            return _sFZCircle!!
        }
        _sFZCircle = sfIcon(
            name = "Dualtone.SFZCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.0918 18.3398L16.4648 18.3398C16.8848 18.3398 17.1875 18.0566 17.1875 17.6172C17.1875 17.1777 16.8848 16.9043 16.4648 16.9043L10.5078 16.9043L10.5078 16.8066L16.5332 8.47656C16.7578 8.16406 16.8164 8.00781 16.8164 7.74414C16.8164 7.26562 16.4648 6.93359 15.9668 6.93359L8.98438 6.93359C8.56445 6.93359 8.25195 7.2168 8.25195 7.64648C8.25195 8.0957 8.56445 8.36914 8.98438 8.36914L14.6191 8.36914L14.6191 8.45703L8.58398 16.7871C8.33008 17.1387 8.27148 17.3047 8.27148 17.5586C8.27148 18.0176 8.60352 18.3398 9.0918 18.3398Z", fillAlpha = 0.85f)
        }
        return _sFZCircle!!
    }

private var _sFZCircle: ImageVector? = null
