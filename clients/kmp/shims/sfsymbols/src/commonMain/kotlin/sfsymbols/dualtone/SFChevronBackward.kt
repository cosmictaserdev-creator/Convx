package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronBackward (dualtone)
 * Viewport: 15.5957 x 21.3184
 */
public val SfSymbols.Dualtone.SFChevronBackward: ImageVector
    get() {
        if (_sFChevronBackward != null) {
            return _sFChevronBackward!!
        }
        _sFChevronBackward = sfIcon(
            name = "Dualtone.SFChevronBackward",
            viewportWidth = 15.5957f,
            viewportHeight = 21.3184f
        ) {
            addSfPath("M0 10.6543C0 10.9277 0.107422 11.1621 0.3125 11.3672L10.3516 21.0254C10.5371 21.2012 10.7715 21.3086 11.0449 21.3086C11.5918 21.3086 12.0215 20.8887 12.0215 20.332C12.0215 20.0586 11.9141 19.8242 11.7383 19.6484L2.38281 10.6543L11.7383 1.65039C11.9141 1.47461 12.0215 1.23047 12.0215 0.966797C12.0215 0.410156 11.5918 0 11.0449 0C10.7715 0 10.5371 0.0976562 10.3516 0.283203L0.3125 9.94141C0.107422 10.1367 0 10.3711 0 10.6543Z", fillAlpha = 0.85f)
        }
        return _sFChevronBackward!!
    }

private var _sFChevronBackward: ImageVector? = null
