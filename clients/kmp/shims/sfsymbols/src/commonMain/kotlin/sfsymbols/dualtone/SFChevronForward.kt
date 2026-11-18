package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronForward (dualtone)
 * Viewport: 14.873 x 21.3184
 */
public val SfSymbols.Dualtone.SFChevronForward: ImageVector
    get() {
        if (_sFChevronForward != null) {
            return _sFChevronForward!!
        }
        _sFChevronForward = sfIcon(
            name = "Dualtone.SFChevronForward",
            viewportWidth = 14.873f,
            viewportHeight = 21.3184f
        ) {
            addSfPath("M14.873 10.6543C14.873 10.3711 14.7656 10.1367 14.5605 9.94141L4.52148 0.283203C4.33594 0.0976562 4.10156 0 3.82812 0C3.28125 0 2.85156 0.410156 2.85156 0.966797C2.85156 1.23047 2.95898 1.47461 3.13477 1.65039L12.4902 10.6543L3.13477 19.6484C2.95898 19.8242 2.85156 20.0586 2.85156 20.332C2.85156 20.8887 3.28125 21.3086 3.82812 21.3086C4.10156 21.3086 4.33594 21.2012 4.52148 21.0254L14.5605 11.3672C14.7656 11.1621 14.873 10.9277 14.873 10.6543Z", fillAlpha = 0.85f)
        }
        return _sFChevronForward!!
    }

private var _sFChevronForward: ImageVector? = null
