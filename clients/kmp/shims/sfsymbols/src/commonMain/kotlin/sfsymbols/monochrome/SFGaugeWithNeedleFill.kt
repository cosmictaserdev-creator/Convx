package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGaugeWithNeedleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFGaugeWithNeedleFill: ImageVector
    get() {
        if (_sFGaugeWithNeedleFill != null) {
            return _sFGaugeWithNeedleFill!!
        }
        _sFGaugeWithNeedleFill = sfIcon(
            name = "Monochrome.SFGaugeWithNeedleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM6.60156 7.26562L10.9961 13.7012C11.3965 14.3066 11.9922 14.6973 12.7148 14.6973C13.8184 14.6973 14.707 13.8184 14.707 12.7246C14.707 12.0508 14.375 11.4648 13.8477 11.0938L7.38281 6.47461C6.78711 6.06445 6.19141 6.66016 6.60156 7.26562Z", fillAlpha = 0.85f)
        }
        return _sFGaugeWithNeedleFill!!
    }

private var _sFGaugeWithNeedleFill: ImageVector? = null
