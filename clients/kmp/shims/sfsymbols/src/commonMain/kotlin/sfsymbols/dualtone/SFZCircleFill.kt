package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFZCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFZCircleFill: ImageVector
    get() {
        if (_sFZCircleFill != null) {
            return _sFZCircleFill!!
        }
        _sFZCircleFill = sfIcon(
            name = "Dualtone.SFZCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.01367 18.4766C8.50586 18.4766 8.16406 18.1445 8.16406 17.6855C8.16406 17.4121 8.23242 17.2461 8.48633 16.8848L14.6582 8.36914L14.6582 8.27148L8.90625 8.27148C8.4668 8.27148 8.14453 7.99805 8.14453 7.5293C8.14453 7.08984 8.4668 6.79688 8.90625 6.79688L16.0645 6.79688C16.5723 6.79688 16.9336 7.13867 16.9336 7.61719C16.9336 7.90039 16.8652 8.05664 16.6406 8.37891L10.4785 16.8945L10.4785 17.002L16.5625 17.002C17.002 17.002 17.3145 17.2754 17.3145 17.7344C17.3145 18.1836 17.002 18.4766 16.5625 18.4766Z", fillAlpha = 0.85f)
        }
        return _sFZCircleFill!!
    }

private var _sFZCircleFill: ImageVector? = null
