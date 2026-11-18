package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFZCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFZCircleFill: ImageVector
    get() {
        if (_sFZCircleFill != null) {
            return _sFZCircleFill!!
        }
        _sFZCircleFill = sfIcon(
            name = "Monochrome.SFZCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.90625 6.79688C8.4668 6.79688 8.14453 7.08984 8.14453 7.5293C8.14453 7.99805 8.4668 8.27148 8.90625 8.27148L14.6582 8.27148L14.6582 8.36914L8.48633 16.8848C8.23242 17.2461 8.16406 17.4121 8.16406 17.6855C8.16406 18.1445 8.50586 18.4766 9.01367 18.4766L16.5625 18.4766C17.002 18.4766 17.3145 18.1836 17.3145 17.7344C17.3145 17.2754 17.002 17.002 16.5625 17.002L10.4785 17.002L10.4785 16.8945L16.6406 8.37891C16.8652 8.05664 16.9336 7.90039 16.9336 7.61719C16.9336 7.13867 16.5723 6.79688 16.0645 6.79688Z", fillAlpha = 0.85f)
        }
        return _sFZCircleFill!!
    }

private var _sFZCircleFill: ImageVector? = null
