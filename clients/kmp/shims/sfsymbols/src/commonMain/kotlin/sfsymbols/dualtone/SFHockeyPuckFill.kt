package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHockeyPuckFill (dualtone)
 * Viewport: 25.7715 x 19.1504
 */
public val SfSymbols.Dualtone.SFHockeyPuckFill: ImageVector
    get() {
        if (_sFHockeyPuckFill != null) {
            return _sFHockeyPuckFill!!
        }
        _sFHockeyPuckFill = sfIcon(
            name = "Dualtone.SFHockeyPuckFill",
            viewportWidth = 25.7715f,
            viewportHeight = 19.1504f
        ) {
            addSfPath("M12.6953 11.3672C19.8145 11.3672 25.4102 8.82812 25.4102 5.60547C25.4102 2.48047 19.8145 0 12.6953 0C5.57617 0 0 2.48047 0 5.60547C0 8.82812 5.57617 11.3672 12.6953 11.3672ZM12.6953 19.1309C19.9023 19.1309 25.4102 15.9473 25.4102 12.041L25.4102 9.07227C23.0469 11.4844 18.1543 12.9297 12.6953 12.9297C7.25586 12.9297 2.37305 11.4941 0 9.07227L0 12.041C0 15.9473 5.47852 19.1309 12.6953 19.1309Z", fillAlpha = 0.85f)
        }
        return _sFHockeyPuckFill!!
    }

private var _sFHockeyPuckFill: ImageVector? = null
