package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGaugeWithNeedleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFGaugeWithNeedleFill: ImageVector
    get() {
        if (_sFGaugeWithNeedleFill != null) {
            return _sFGaugeWithNeedleFill!!
        }
        _sFGaugeWithNeedleFill = sfIcon(
            name = "Dualtone.SFGaugeWithNeedleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 14.6973C11.9922 14.6973 11.3965 14.3066 10.9961 13.7012L6.60156 7.26562C6.19141 6.66016 6.78711 6.06445 7.38281 6.47461L13.8477 11.0938C14.375 11.4648 14.707 12.0508 14.707 12.7246C14.707 13.8184 13.8184 14.6973 12.7148 14.6973Z", fillAlpha = 0.85f)
        }
        return _sFGaugeWithNeedleFill!!
    }

private var _sFGaugeWithNeedleFill: ImageVector? = null
