package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVisionProCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFVisionProCircleFill: ImageVector
    get() {
        if (_sFVisionProCircleFill != null) {
            return _sFVisionProCircleFill!!
        }
        _sFVisionProCircleFill = sfIcon(
            name = "Dualtone.SFVisionProCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 15.6055C11.6211 15.6055 10.4102 17.5879 8.01758 17.5879C5.6543 17.5879 3.91602 15.4492 3.91602 12.6855C3.91602 8.13477 7.91016 7.63672 12.7148 7.63672C17.5195 7.63672 21.5234 8.13477 21.5234 12.6855C21.5234 15.4492 19.7754 17.5879 17.4219 17.5879C15.0195 17.5879 13.8086 15.6055 12.7148 15.6055ZM12.7148 14.3555C14.5898 14.3555 15.459 16.3281 17.4219 16.3281C19.1699 16.3281 20.2734 14.9805 20.2734 12.6855C20.2734 9.23828 17.3926 8.89648 12.7148 8.89648C8.03711 8.89648 5.15625 9.23828 5.15625 12.6855C5.15625 14.9805 6.25977 16.3281 8.01758 16.3281C9.98047 16.3281 10.8398 14.3555 12.7148 14.3555Z", fillAlpha = 0.85f)
        }
        return _sFVisionProCircleFill!!
    }

private var _sFVisionProCircleFill: ImageVector? = null
