package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEjectCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFEjectCircle: ImageVector
    get() {
        if (_sFEjectCircle != null) {
            return _sFEjectCircle!!
        }
        _sFEjectCircle = sfIcon(
            name = "Monochrome.SFEjectCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.67578 17.9004L17.7344 17.9004C18.457 17.9004 18.877 17.5 18.877 16.7773L18.877 15.9863C18.877 15.2637 18.457 14.8633 17.7344 14.8633L7.67578 14.8633C6.96289 14.8633 6.5332 15.2637 6.5332 15.9863L6.5332 16.7773C6.5332 17.5 6.96289 17.9004 7.67578 17.9004ZM7.40234 13.2324L17.998 13.2324C18.4766 13.2324 18.7695 12.9785 18.7695 12.5586C18.7695 12.3438 18.6621 12.1484 18.4766 11.9531L13.4473 6.30859C13.252 6.07422 12.998 5.9375 12.7051 5.9375C12.4121 5.9375 12.1582 6.09375 11.9727 6.30859L6.91406 11.9531C6.72852 12.1387 6.64062 12.3438 6.65039 12.5586C6.67969 12.9785 6.91406 13.2324 7.40234 13.2324Z", fillAlpha = 0.85f)
        }
        return _sFEjectCircle!!
    }

private var _sFEjectCircle: ImageVector? = null
