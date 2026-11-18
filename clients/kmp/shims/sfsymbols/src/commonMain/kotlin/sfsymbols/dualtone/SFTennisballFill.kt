package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTennisballFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTennisballFill: ImageVector
    get() {
        if (_sFTennisballFill != null) {
            return _sFTennisballFill!!
        }
        _sFTennisballFill = sfIcon(
            name = "Dualtone.SFTennisballFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M0.166016 10.6836C0.878906 10.8691 1.64062 10.9668 2.42188 10.9668C6.97266 10.9668 10.4395 7.5 10.4395 2.92969C10.4395 2.01172 10.2832 1.10352 10.0098 0.302734C4.98047 1.38672 0.986328 5.51758 0.166016 10.6836ZM12.7148 25.4395C13.1641 25.4395 13.584 25.4102 14.0137 25.3613C13.7109 24.4336 13.5645 23.4766 13.5645 22.4414C13.5645 17.1582 17.793 12.9297 23.0664 12.9297C23.8867 12.9297 24.6484 13.0176 25.4199 13.2227C25.4297 13.0566 25.4395 12.8906 25.4395 12.7246C25.4395 5.71289 19.7363 0 12.7148 0C12.2949 0 11.8848 0.0292969 11.4941 0.0683594C11.7871 0.966797 11.9238 1.92383 11.9238 2.92969C11.9238 8.21289 7.69531 12.4512 2.42188 12.4512C1.60156 12.4512 0.78125 12.3438 0.0292969 12.1582C0.00976562 12.3438 0 12.5293 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM15.498 25.127C20.5176 23.9941 24.4531 19.8242 25.2832 14.6973C24.5898 14.5117 23.8574 14.4043 23.0664 14.4043C18.5059 14.4043 15.0488 17.8711 15.0488 22.4414C15.0488 23.3984 15.1855 24.3066 15.498 25.127Z", fillAlpha = 0.85f)
        }
        return _sFTennisballFill!!
    }

private var _sFTennisballFill: ImageVector? = null
