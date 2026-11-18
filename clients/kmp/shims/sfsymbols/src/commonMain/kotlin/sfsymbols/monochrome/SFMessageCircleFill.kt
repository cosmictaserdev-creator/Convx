package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessageCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMessageCircleFill: ImageVector
    get() {
        if (_sFMessageCircleFill != null) {
            return _sFMessageCircleFill!!
        }
        _sFMessageCircleFill = sfIcon(
            name = "Monochrome.SFMessageCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM5.54688 12.7344C5.54688 15.2344 6.92383 15.8984 6.92383 17.041C6.92383 17.5293 6.74805 17.8613 6.37695 18.1934C6.13281 18.418 6.25 18.6914 6.61133 18.6914C7.45117 18.6914 8.34961 18.3887 8.97461 17.9102C10.0586 18.4375 11.3184 18.6914 12.7051 18.6914C16.8652 18.6914 19.873 16.1914 19.873 12.7344C19.873 9.27734 16.8652 6.78711 12.7051 6.78711C8.55469 6.78711 5.54688 9.27734 5.54688 12.7344Z", fillAlpha = 0.85f)
        }
        return _sFMessageCircleFill!!
    }

private var _sFMessageCircleFill: ImageVector? = null
