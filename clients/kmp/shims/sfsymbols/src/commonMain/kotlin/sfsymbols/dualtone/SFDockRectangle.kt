package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDockRectangle (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFDockRectangle: ImageVector
    get() {
        if (_sFDockRectangle != null) {
            return _sFDockRectangle!!
        }
        _sFDockRectangle = sfIcon(
            name = "Dualtone.SFDockRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M5.08789 22.959L24.7461 22.959C27.8027 22.959 29.5898 21.0938 29.5898 17.8809L29.5898 5.08789C29.5898 1.86523 27.8027 0 24.7461 0L5.08789 0C1.86523 0 0 1.86523 0 5.08789L0 17.8809C0 21.0938 1.86523 22.959 5.08789 22.959ZM5.09766 21.2305C2.97852 21.2305 1.72852 19.9805 1.72852 17.8613L1.72852 5.09766C1.72852 2.97852 2.97852 1.72852 5.09766 1.72852L24.4922 1.72852C26.6113 1.72852 27.8516 2.97852 27.8516 5.09766L27.8516 17.8613C27.8516 19.9805 26.6113 21.2305 24.4922 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M5.27344 18.0762C5.27344 18.7305 5.72266 19.1699 6.39648 19.1699L23.2227 19.1699C23.8965 19.1699 24.3457 18.7305 24.3457 18.0762L24.3457 16.2598C24.3457 15.6055 23.8965 15.166 23.2227 15.166L6.39648 15.166C5.72266 15.166 5.27344 15.6055 5.27344 16.2598Z", fillAlpha = 0.85f)
        }
        return _sFDockRectangle!!
    }

private var _sFDockRectangle: ImageVector? = null
