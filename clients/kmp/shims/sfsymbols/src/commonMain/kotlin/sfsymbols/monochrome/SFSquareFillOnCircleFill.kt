package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareFillOnCircleFill (monochrome)
 * Viewport: 30.0879 x 29.5215
 */
public val SfSymbols.Monochrome.SFSquareFillOnCircleFill: ImageVector
    get() {
        if (_sFSquareFillOnCircleFill != null) {
            return _sFSquareFillOnCircleFill!!
        }
        _sFSquareFillOnCircleFill = sfIcon(
            name = "Monochrome.SFSquareFillOnCircleFill",
            viewportWidth = 30.0879f,
            viewportHeight = 29.5215f
        ) {
            addSfPath("M19.6785 6.14258L12.0703 6.14258C8.68164 6.14258 6.70898 8.0957 6.70898 11.4648L6.70898 20.1145C2.91954 18.621 0.244141 14.927 0.244141 10.6055C0.244141 4.95117 4.82422 0.371094 10.4785 0.371094C14.5303 0.371094 18.0305 2.72292 19.6785 6.14258Z", fillAlpha = 0.85f)
            addSfPath("M12.0703 27.959L24.7266 27.959C27.2461 27.959 28.5254 26.6992 28.5254 24.1992L28.5254 11.4648C28.5254 8.96484 27.2461 7.70508 24.7266 7.70508L12.0703 7.70508C9.54102 7.70508 8.27148 8.96484 8.27148 11.4648L8.27148 24.1992C8.27148 26.6992 9.54102 27.959 12.0703 27.959Z", fillAlpha = 0.85f)
        }
        return _sFSquareFillOnCircleFill!!
    }

private var _sFSquareFillOnCircleFill: ImageVector? = null
