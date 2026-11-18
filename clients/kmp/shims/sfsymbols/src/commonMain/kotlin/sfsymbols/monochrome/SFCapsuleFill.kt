package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsuleFill (monochrome)
 * Viewport: 32.3828 x 23.8184
 */
public val SfSymbols.Monochrome.SFCapsuleFill: ImageVector
    get() {
        if (_sFCapsuleFill != null) {
            return _sFCapsuleFill!!
        }
        _sFCapsuleFill = sfIcon(
            name = "Monochrome.SFCapsuleFill",
            viewportWidth = 32.3828f,
            viewportHeight = 23.8184f
        ) {
            addSfPath("M0 11.9043C0 19.1211 4.89258 23.8086 12.3047 23.8086L19.7168 23.8086C27.1289 23.8086 32.0215 19.1211 32.0215 11.9043C32.0215 4.67773 27.1289 0 19.7168 0L12.3047 0C4.89258 0 0 4.67773 0 11.9043Z", fillAlpha = 0.85f)
        }
        return _sFCapsuleFill!!
    }

private var _sFCapsuleFill: ImageVector? = null
