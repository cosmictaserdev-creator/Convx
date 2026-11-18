package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusMinusCapsuleFill (monochrome)
 * Viewport: 32.3828 x 23.8184
 */
public val SfSymbols.Monochrome.SFPlusMinusCapsuleFill: ImageVector
    get() {
        if (_sFPlusMinusCapsuleFill != null) {
            return _sFPlusMinusCapsuleFill!!
        }
        _sFPlusMinusCapsuleFill = sfIcon(
            name = "Monochrome.SFPlusMinusCapsuleFill",
            viewportWidth = 32.3828f,
            viewportHeight = 23.8184f
        ) {
            addSfPath("M32.0215 11.9043C32.0215 19.1211 27.1289 23.8086 19.7168 23.8086L12.3047 23.8086C4.89258 23.8086 0 19.1211 0 11.9043C0 4.67773 4.89258 0 12.3047 0L19.7168 0C27.1289 0 32.0215 4.67773 32.0215 11.9043ZM9.4043 8.65234L9.4043 11.0156L7.06055 11.0156C6.57227 11.0156 6.18164 11.4062 6.18164 11.9043C6.18164 12.3926 6.57227 12.7832 7.06055 12.7832L9.4043 12.7832L9.4043 15.1367C9.4043 15.625 9.79492 16.0156 10.2832 16.0156C10.7715 16.0156 11.1621 15.625 11.1621 15.1367L11.1621 12.7832L13.5254 12.7832C14.0234 12.7832 14.4043 12.3926 14.4043 11.9043C14.4043 11.4062 14.0234 11.0156 13.5254 11.0156L11.1621 11.0156L11.1621 8.65234C11.1621 8.17383 10.7715 7.7832 10.2832 7.7832C9.79492 7.7832 9.4043 8.17383 9.4043 8.65234ZM18.8672 11.0156C18.3789 11.0156 17.998 11.4062 17.998 11.9043C17.998 12.3926 18.3789 12.7832 18.8672 12.7832L24.8926 12.7832C25.3809 12.7832 25.7715 12.3926 25.7715 11.9043C25.7715 11.4062 25.3809 11.0156 24.8926 11.0156Z", fillAlpha = 0.85f)
        }
        return _sFPlusMinusCapsuleFill!!
    }

private var _sFPlusMinusCapsuleFill: ImageVector? = null
