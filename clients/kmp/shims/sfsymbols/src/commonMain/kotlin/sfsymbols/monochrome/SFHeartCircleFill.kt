package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFHeartCircleFill: ImageVector
    get() {
        if (_sFHeartCircleFill != null) {
            return _sFHeartCircleFill!!
        }
        _sFHeartCircleFill = sfIcon(
            name = "Monochrome.SFHeartCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.7148 8.94531C12.0801 7.80273 10.9961 6.99219 9.60938 6.99219C7.44141 6.99219 5.85938 8.64258 5.85938 10.8984C5.85938 14.3652 9.58008 17.5195 12.1191 19.1211C12.3242 19.2578 12.5879 19.4043 12.7344 19.4043C12.8906 19.4043 13.1152 19.2578 13.3105 19.1211C15.8008 17.4512 19.5703 14.3652 19.5703 10.8984C19.5703 8.64258 17.9883 6.99219 15.8203 6.99219C14.4531 6.99219 13.3496 7.80273 12.7148 8.94531Z", fillAlpha = 0.85f)
        }
        return _sFHeartCircleFill!!
    }

private var _sFHeartCircleFill: ImageVector? = null
