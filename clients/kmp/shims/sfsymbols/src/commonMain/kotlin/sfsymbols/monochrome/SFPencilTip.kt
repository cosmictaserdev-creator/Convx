package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilTip (monochrome)
 * Viewport: 16.0901 x 23.6816
 */
public val SfSymbols.Monochrome.SFPencilTip: ImageVector
    get() {
        if (_sFPencilTip != null) {
            return _sFPencilTip!!
        }
        _sFPencilTip = sfIcon(
            name = "Monochrome.SFPencilTip",
            viewportWidth = 16.0901f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M0.867299 23.6816C1.37511 23.6816 1.61925 23.4082 1.70714 22.9688L4.2755 10.1465L3.19152 11.0352L12.5372 11.0352L11.4532 10.1465L14.0216 22.9688C14.1095 23.4082 14.3536 23.6816 14.8614 23.6816C15.5841 23.6816 15.8282 23.1738 15.711 22.5977L13.2696 10.4492C13.1329 9.73633 12.547 9.24805 11.8439 9.24805L3.88488 9.24805C3.19152 9.24805 2.60558 9.73633 2.46886 10.4492L0.0274548 22.5977C-0.0897327 23.1934 0.164174 23.6816 0.867299 23.6816ZM3.79699 10.0586L11.922 10.0586L8.82628 0.732422C8.67003 0.263672 8.35753 0 7.87902 0C7.38097 0 7.02941 0.263672 6.88292 0.732422Z", fillAlpha = 0.85f)
        }
        return _sFPencilTip!!
    }

private var _sFPencilTip: ImageVector? = null
