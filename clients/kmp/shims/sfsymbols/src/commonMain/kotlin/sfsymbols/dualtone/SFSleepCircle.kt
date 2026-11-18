package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSleepCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSleepCircle: ImageVector
    get() {
        if (_sFSleepCircle != null) {
            return _sFSleepCircle!!
        }
        _sFSleepCircle = sfIcon(
            name = "Dualtone.SFSleepCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7148 20.4297C16.9727 20.4297 20.4297 16.9727 20.4297 12.7246C20.4297 8.4668 16.9727 5.00977 12.7148 5.00977C8.45703 5.00977 5.00977 8.4668 5.00977 12.7246C5.00977 16.9727 8.45703 20.4297 12.7148 20.4297ZM6.57227 12.7246C6.57227 9.32617 9.32617 6.5625 12.7246 6.5625C16.123 6.5625 18.8867 9.32617 18.8867 12.7246C18.8867 13.5059 18.7305 14.2578 18.457 14.9512L7.00195 14.9512C6.71875 14.2578 6.57227 13.5059 6.57227 12.7246ZM7.80273 16.3965L17.6465 16.3965C16.543 17.9004 14.7461 18.877 12.7246 18.877C10.7031 18.877 8.91602 17.9004 7.80273 16.3965Z", fillAlpha = 0.85f)
        }
        return _sFSleepCircle!!
    }

private var _sFSleepCircle: ImageVector? = null
