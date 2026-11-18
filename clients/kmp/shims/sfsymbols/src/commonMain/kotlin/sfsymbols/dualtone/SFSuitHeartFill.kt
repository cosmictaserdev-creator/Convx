package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSuitHeartFill (dualtone)
 * Viewport: 25.0879 x 23.4668
 */
public val SfSymbols.Dualtone.SFSuitHeartFill: ImageVector
    get() {
        if (_sFSuitHeartFill != null) {
            return _sFSuitHeartFill!!
        }
        _sFSuitHeartFill = sfIcon(
            name = "Dualtone.SFSuitHeartFill",
            viewportWidth = 25.0879f,
            viewportHeight = 23.4668f
        ) {
            addSfPath("M12.3633 23.4668C12.6074 23.4668 12.9492 23.3105 13.1934 23.1543C20.1758 18.6523 24.7266 13.457 24.7266 8.1543C24.7266 3.79883 21.7285 0.693359 17.8125 0.693359C15.4199 0.693359 13.4668 2.04102 12.3633 4.11133C11.2695 2.05078 9.31641 0.693359 6.91406 0.693359C2.99805 0.693359 0 3.79883 0 8.1543C0 13.457 4.55078 18.6523 11.543 23.1543C11.7871 23.3105 12.1289 23.4668 12.3633 23.4668Z", fillAlpha = 0.85f)
        }
        return _sFSuitHeartFill!!
    }

private var _sFSuitHeartFill: ImageVector? = null
