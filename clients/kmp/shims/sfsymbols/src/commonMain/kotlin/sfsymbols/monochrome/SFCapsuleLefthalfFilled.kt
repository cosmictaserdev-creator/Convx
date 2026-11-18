package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsuleLefthalfFilled (monochrome)
 * Viewport: 32.3828 x 23.8184
 */
public val SfSymbols.Monochrome.SFCapsuleLefthalfFilled: ImageVector
    get() {
        if (_sFCapsuleLefthalfFilled != null) {
            return _sFCapsuleLefthalfFilled!!
        }
        _sFCapsuleLefthalfFilled = sfIcon(
            name = "Monochrome.SFCapsuleLefthalfFilled",
            viewportWidth = 32.3828f,
            viewportHeight = 23.8184f
        ) {
            addSfPath("M0 11.9043C0 19.1211 4.89258 23.8086 12.3047 23.8086L19.7168 23.8086C27.1289 23.8086 32.0215 19.1211 32.0215 11.9043C32.0215 4.67773 27.1289 0 19.7168 0L12.3047 0C4.89258 0 0 4.67773 0 11.9043ZM16.0156 22.0703L16.0156 1.72852L19.7168 1.72852C26.0645 1.72852 30.293 5.75195 30.293 11.9043C30.293 18.0469 26.0645 22.0703 19.7168 22.0703Z", fillAlpha = 0.85f)
        }
        return _sFCapsuleLefthalfFilled!!
    }

private var _sFCapsuleLefthalfFilled: ImageVector? = null
