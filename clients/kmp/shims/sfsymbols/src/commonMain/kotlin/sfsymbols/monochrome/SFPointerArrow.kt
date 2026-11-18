package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrow (monochrome)
 * Viewport: 13.9551 x 25.0313
 */
public val SfSymbols.Monochrome.SFPointerArrow: ImageVector
    get() {
        if (_sFPointerArrow != null) {
            return _sFPointerArrow!!
        }
        _sFPointerArrow = sfIcon(
            name = "Monochrome.SFPointerArrow",
            viewportWidth = 13.9551f,
            viewportHeight = 25.0313f
        ) {
            addSfPath("M10.498 24.7666C11.377 24.4248 11.7871 23.4287 11.4258 22.5596L7.34375 12.9111L6.78711 13.8193L12.8809 14.1416C13.6328 14.1904 14.0039 13.3701 13.4668 12.8135L1.38672 0.254886C0.888672-0.252927 0.0976562 0.040042 0.0976562 0.762698L0 18.0381C0 18.8096 0.878906 19.1416 1.37695 18.5557L5.36133 14.2197L4.27734 14.0147L8.26172 23.8486C8.63281 24.708 9.61914 25.1279 10.498 24.7666Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrow!!
    }

private var _sFPointerArrow: ImageVector? = null
