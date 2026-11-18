package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFOCircleFill: ImageVector
    get() {
        if (_sFOCircleFill != null) {
            return _sFOCircleFill!!
        }
        _sFOCircleFill = sfIcon(
            name = "Monochrome.SFOCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.22656 12.6367C7.22656 16.25 9.46289 18.7402 12.7148 18.7402C15.9668 18.7402 18.2031 16.25 18.2031 12.6367C18.2031 9.01367 15.9668 6.52344 12.7148 6.52344C9.46289 6.52344 7.22656 9.01367 7.22656 12.6367ZM16.4258 12.6367C16.4258 15.4492 14.9512 17.3242 12.7148 17.3242C10.4883 17.3242 9.00391 15.4492 9.00391 12.6367C9.00391 9.81445 10.4883 7.94922 12.7148 7.94922C14.9512 7.94922 16.4258 9.81445 16.4258 12.6367Z", fillAlpha = 0.85f)
        }
        return _sFOCircleFill!!
    }

private var _sFOCircleFill: ImageVector? = null
