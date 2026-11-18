package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaySlash (monochrome)
 * Viewport: 25.0342 x 24.0991
 */
public val SfSymbols.Monochrome.SFPlaySlash: ImageVector
    get() {
        if (_sFPlaySlash != null) {
            return _sFPlaySlash!!
        }
        _sFPlaySlash = sfIcon(
            name = "Monochrome.SFPlaySlash",
            viewportWidth = 25.0342f,
            viewportHeight = 24.0991f
        ) {
            addSfPath("M6.16455 9.26833L6.16455 20.0916C6.16455 20.365 6.38916 20.4822 6.62354 20.3455L13.3169 16.4169L14.5796 17.679L6.93604 22.1716C6.59424 22.3669 6.23291 22.4744 5.89111 22.4744C5.1001 22.4744 4.42627 21.9373 4.42627 20.7849L4.42627 7.53097ZM6.93604 1.90796L21.6235 10.5408C22.5317 11.0681 22.8638 11.4392 22.8638 12.0447C22.8638 12.6404 22.5317 13.0115 21.6235 13.5388L18.4904 15.3804L17.2292 14.1205L20.3833 12.2693C20.4907 12.2107 20.5396 12.1228 20.5396 12.0447C20.5396 11.9568 20.4907 11.8689 20.3833 11.8103L7.15146 4.05339L4.99847 1.90267C5.2473 1.70803 5.55887 1.615 5.89111 1.615C6.23291 1.615 6.59424 1.71265 6.93604 1.90796Z", fillAlpha = 0.85f)
            addSfPath("M20.1196 21.283C20.4419 21.615 20.979 21.615 21.2915 21.283C21.6138 20.9509 21.6235 20.4334 21.2915 20.1111L2.78564 1.60523C2.46338 1.28296 1.92627 1.2732 1.59424 1.60523C1.28174 1.9275 1.28174 2.4646 1.59424 2.78687Z", fillAlpha = 0.85f)
        }
        return _sFPlaySlash!!
    }

private var _sFPlaySlash: ImageVector? = null
