package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMCircleFill: ImageVector
    get() {
        if (_sFMCircleFill != null) {
            return _sFMCircleFill!!
        }
        _sFMCircleFill = sfIcon(
            name = "Dualtone.SFMCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.72461 18.6719C7.22656 18.6719 6.92383 18.3301 6.92383 17.7734L6.92383 7.70508C6.92383 7.08008 7.36328 6.65039 7.99805 6.65039C8.55469 6.65039 8.91602 6.9043 9.13086 7.41211L12.6855 16.3184L12.7539 16.3184L16.2988 7.41211C16.5137 6.9043 16.875 6.65039 17.4316 6.65039C18.0664 6.65039 18.5059 7.08008 18.5059 7.70508L18.5059 17.7734C18.5059 18.3398 18.1934 18.6719 17.6758 18.6719C17.1875 18.6719 16.8848 18.3301 16.8848 17.7734L16.8848 9.76562L16.7773 9.76562L13.6035 17.7344C13.4277 18.1445 13.1348 18.3496 12.7051 18.3496C12.2754 18.3496 11.9922 18.1445 11.8164 17.7344L8.65234 9.76562L8.54492 9.76562L8.54492 17.7734C8.54492 18.3398 8.24219 18.6719 7.72461 18.6719Z", fillAlpha = 0.85f)
        }
        return _sFMCircleFill!!
    }

private var _sFMCircleFill: ImageVector? = null
