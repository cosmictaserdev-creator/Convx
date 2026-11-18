package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessageCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMessageCircleFill: ImageVector
    get() {
        if (_sFMessageCircleFill != null) {
            return _sFMessageCircleFill!!
        }
        _sFMessageCircleFill = sfIcon(
            name = "Dualtone.SFMessageCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M12.7051 18.6914C11.3184 18.6914 10.0586 18.4375 8.97461 17.9102C8.34961 18.3887 7.45117 18.6914 6.61133 18.6914C6.25 18.6914 6.13281 18.418 6.37695 18.1934C6.74805 17.8613 6.92383 17.5293 6.92383 17.041C6.92383 15.8984 5.54688 15.2344 5.54688 12.7344C5.54688 9.27734 8.55469 6.78711 12.7051 6.78711C16.8652 6.78711 19.873 9.27734 19.873 12.7344C19.873 16.1914 16.8652 18.6914 12.7051 18.6914Z", fillAlpha = 0.85f)
        }
        return _sFMessageCircleFill!!
    }

private var _sFMessageCircleFill: ImageVector? = null
