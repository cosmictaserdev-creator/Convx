package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHockeyPuck (monochrome)
 * Viewport: 25.7715 x 19.1504
 */
public val SfSymbols.Monochrome.SFHockeyPuck: ImageVector
    get() {
        if (_sFHockeyPuck != null) {
            return _sFHockeyPuck!!
        }
        _sFHockeyPuck = sfIcon(
            name = "Monochrome.SFHockeyPuck",
            viewportWidth = 25.7715f,
            viewportHeight = 19.1504f
        ) {
            addSfPath("M12.6953 19.1309C19.9023 19.1309 25.4102 15.9473 25.4102 12.041L25.4102 5.69336L23.6816 7.16797L23.6816 12.041C23.6816 15 19.4141 17.4023 12.6953 17.4023C5.97656 17.4023 1.71875 15 1.71875 12.041L1.71875 7.16797L0 5.69336L0 12.041C0 15.9473 5.47852 19.1309 12.6953 19.1309ZM12.6953 11.3672C19.8145 11.3672 25.4102 8.82812 25.4102 5.60547C25.4102 2.48047 19.8145 0 12.6953 0C5.57617 0 0 2.48047 0 5.60547C0 8.82812 5.57617 11.3672 12.6953 11.3672ZM12.6953 9.63867C6.47461 9.63867 1.71875 7.85156 1.71875 5.60547C1.71875 3.44727 6.47461 1.72852 12.6953 1.72852C18.916 1.72852 23.6816 3.44727 23.6816 5.60547C23.6816 7.85156 18.916 9.63867 12.6953 9.63867Z", fillAlpha = 0.85f)
        }
        return _sFHockeyPuck!!
    }

private var _sFHockeyPuck: ImageVector? = null
