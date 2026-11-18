package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMCircleFill: ImageVector
    get() {
        if (_sFMCircleFill != null) {
            return _sFMCircleFill!!
        }
        _sFMCircleFill = sfIcon(
            name = "Monochrome.SFMCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM16.2988 7.41211L12.7539 16.3184L12.6855 16.3184L9.13086 7.41211C8.91602 6.9043 8.55469 6.65039 7.99805 6.65039C7.36328 6.65039 6.92383 7.08008 6.92383 7.70508L6.92383 17.7734C6.92383 18.3301 7.22656 18.6719 7.72461 18.6719C8.24219 18.6719 8.54492 18.3398 8.54492 17.7734L8.54492 9.76562L8.65234 9.76562L11.8164 17.7344C11.9922 18.1445 12.2754 18.3496 12.7051 18.3496C13.1348 18.3496 13.4277 18.1445 13.6035 17.7344L16.7773 9.76562L16.8848 9.76562L16.8848 17.7734C16.8848 18.3301 17.1875 18.6719 17.6758 18.6719C18.1934 18.6719 18.5059 18.3398 18.5059 17.7734L18.5059 7.70508C18.5059 7.08008 18.0664 6.65039 17.4316 6.65039C16.875 6.65039 16.5137 6.9043 16.2988 7.41211Z", fillAlpha = 0.85f)
        }
        return _sFMCircleFill!!
    }

private var _sFMCircleFill: ImageVector? = null
