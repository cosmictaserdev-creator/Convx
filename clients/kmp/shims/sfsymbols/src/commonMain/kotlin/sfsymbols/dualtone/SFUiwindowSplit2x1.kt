package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUiwindowSplit2x1 (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFUiwindowSplit2x1: ImageVector
    get() {
        if (_sFUiwindowSplit2x1 != null) {
            return _sFUiwindowSplit2x1!!
        }
        _sFUiwindowSplit2x1 = sfIcon(
            name = "Dualtone.SFUiwindowSplit2x1",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M0.927734 8.84766L13.9258 8.84766L13.9258 22.1973L15.6543 22.1973L15.6543 8.84766L28.8574 8.84766L28.8574 7.2168L0.927734 7.2168ZM5.08789 22.959L24.7461 22.959C27.8027 22.959 29.5898 21.0938 29.5898 17.8809L29.5898 5.08789C29.5898 1.86523 27.8027 0 24.7461 0L5.08789 0C1.86523 0 0 1.86523 0 5.08789L0 17.8809C0 21.0938 1.86523 22.959 5.08789 22.959ZM5.09766 21.2305C2.97852 21.2305 1.72852 19.9805 1.72852 17.8613L1.72852 5.09766C1.72852 2.97852 2.97852 1.72852 5.09766 1.72852L24.4922 1.72852C26.6113 1.72852 27.8516 2.97852 27.8516 5.09766L27.8516 17.8613C27.8516 19.9805 26.6113 21.2305 24.4922 21.2305Z", fillAlpha = 0.85f)
        }
        return _sFUiwindowSplit2x1!!
    }

private var _sFUiwindowSplit2x1: ImageVector? = null
