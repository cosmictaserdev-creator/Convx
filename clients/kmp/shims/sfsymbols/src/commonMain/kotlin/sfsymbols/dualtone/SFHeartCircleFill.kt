package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFHeartCircleFill: ImageVector
    get() {
        if (_sFHeartCircleFill != null) {
            return _sFHeartCircleFill!!
        }
        _sFHeartCircleFill = sfIcon(
            name = "Dualtone.SFHeartCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.60938 6.99219C10.9961 6.99219 12.0801 7.80273 12.7148 8.94531C13.3496 7.80273 14.4531 6.99219 15.8203 6.99219C17.9883 6.99219 19.5703 8.64258 19.5703 10.8984C19.5703 14.3652 15.8008 17.4512 13.3105 19.1211C13.1152 19.2578 12.8906 19.4043 12.7344 19.4043C12.5879 19.4043 12.3242 19.2578 12.1191 19.1211C9.58008 17.5195 5.85938 14.3652 5.85938 10.8984C5.85938 8.64258 7.44141 6.99219 9.60938 6.99219Z", fillAlpha = 0.85f)
        }
        return _sFHeartCircleFill!!
    }

private var _sFHeartCircleFill: ImageVector? = null
