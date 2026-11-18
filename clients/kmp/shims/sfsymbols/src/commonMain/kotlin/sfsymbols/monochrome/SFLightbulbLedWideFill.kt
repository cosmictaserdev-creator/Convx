package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightbulbLedWideFill (monochrome)
 * Viewport: 20.6152 x 28.291
 */
public val SfSymbols.Monochrome.SFLightbulbLedWideFill: ImageVector
    get() {
        if (_sFLightbulbLedWideFill != null) {
            return _sFLightbulbLedWideFill!!
        }
        _sFLightbulbLedWideFill = sfIcon(
            name = "Monochrome.SFLightbulbLedWideFill",
            viewportWidth = 20.6152f,
            viewportHeight = 28.291f
        ) {
            addSfPath("M7.16797 24.7559L13.0859 24.7559C13.4668 24.7559 13.7695 24.4629 13.7695 24.082C13.7695 23.7012 13.4668 23.3984 13.0859 23.3984L7.16797 23.3984C6.78711 23.3984 6.49414 23.7012 6.49414 24.082C6.49414 24.4629 6.78711 24.7559 7.16797 24.7559ZM10.127 28.2129C11.8164 28.2129 12.9785 27.3438 13.2031 25.9863L7.05078 25.9863C7.25586 27.3438 8.41797 28.2129 10.127 28.2129Z", fillAlpha = 0.85f)
            addSfPath("M7.35352 22.0508L12.9004 22.0508C13.3008 22.0508 13.5059 21.8359 13.5059 21.4648L13.5059 20.3223C13.5059 14.1113 17.9492 10.752 19.6289 7.34375L0.644531 7.34375C2.33398 10.7129 6.75781 14.1602 6.75781 20.3223L6.75781 21.4648C6.75781 21.8359 6.96289 22.0508 7.35352 22.0508Z", fillAlpha = 0.85f)
            addSfPath("M0.136719 5.9082L20.1172 5.9082C20.2051 5.58594 20.2539 5.25391 20.2539 4.92188C20.2539 1.77734 16.5625 0 10.127 0C3.70117 0 0 1.77734 0 4.92188C0 5.25391 0.0488281 5.57617 0.136719 5.9082Z", fillAlpha = 0.85f)
        }
        return _sFLightbulbLedWideFill!!
    }

private var _sFLightbulbLedWideFill: ImageVector? = null
