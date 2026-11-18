package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGaugeOpen (monochrome)
 * Viewport: 28.1543 x 24.0332
 */
public val SfSymbols.Monochrome.SFGaugeOpen: ImageVector
    get() {
        if (_sFGaugeOpen != null) {
            return _sFGaugeOpen!!
        }
        _sFGaugeOpen = sfIcon(
            name = "Monochrome.SFGaugeOpen",
            viewportWidth = 28.1543f,
            viewportHeight = 24.0332f
        ) {
            addSfPath("M13.8965 0C6.2207 0 0 6.21094 0 13.8965C0 17.6953 1.52344 21.1621 4.0332 23.6816C4.41406 24.0625 4.98047 24.082 5.39062 23.7012C5.71289 23.3691 5.6543 22.793 5.21484 22.334C3.125 20.1758 1.8457 17.1582 1.8457 13.8965C1.8457 7.16797 7.1875 1.81641 13.8965 1.81641C20.6152 1.81641 25.9473 7.16797 25.9473 13.8965C25.9473 17.1582 24.6777 20.1758 22.5781 22.334C22.1484 22.793 22.0801 23.3691 22.4121 23.7012C22.8125 24.082 23.3887 24.0625 23.7598 23.6816C26.2695 21.1719 27.793 17.6953 27.793 13.8965C27.793 6.21094 21.582 0 13.8965 0Z", fillAlpha = 0.85f)
        }
        return _sFGaugeOpen!!
    }

private var _sFGaugeOpen: ImageVector? = null
