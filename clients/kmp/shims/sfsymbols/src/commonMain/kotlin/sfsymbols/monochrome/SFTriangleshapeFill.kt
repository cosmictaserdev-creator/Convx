package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangleshapeFill (monochrome)
 * Viewport: 27.7344 x 24.5801
 */
public val SfSymbols.Monochrome.SFTriangleshapeFill: ImageVector
    get() {
        if (_sFTriangleshapeFill != null) {
            return _sFTriangleshapeFill!!
        }
        _sFTriangleshapeFill = sfIcon(
            name = "Monochrome.SFTriangleshapeFill",
            viewportWidth = 27.7344f,
            viewportHeight = 24.5801f
        ) {
            addSfPath("M1.62109 23.9746L25.752 23.9746C26.8848 23.9746 27.373 23.3691 27.373 22.6562C27.373 22.3535 27.2852 22.0215 27.0996 21.709L14.9805 0.703125C14.7168 0.224609 14.209 0 13.6914 0C13.1641 0 12.666 0.224609 12.3926 0.703125L0.273438 21.709C0.0976562 22.0215 0 22.3535 0 22.6562C0 23.3691 0.498047 23.9746 1.62109 23.9746Z", fillAlpha = 0.85f)
        }
        return _sFTriangleshapeFill!!
    }

private var _sFTriangleshapeFill: ImageVector? = null
