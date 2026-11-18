package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFFlagCircleFill: ImageVector
    get() {
        if (_sFFlagCircleFill != null) {
            return _sFFlagCircleFill!!
        }
        _sFFlagCircleFill = sfIcon(
            name = "Monochrome.SFFlagCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.98828 7.10938C7.48047 7.33398 7.22656 7.70508 7.22656 8.21289L7.22656 19.1016C7.22656 19.3848 7.4707 19.6387 7.77344 19.6387C8.08594 19.6387 8.30078 19.3945 8.30078 19.1016L8.30078 15.2148C8.55469 15.1074 9.08203 14.9512 9.91211 14.9512C12.2266 14.9512 13.6621 16.0938 15.9277 16.0938C16.875 16.0938 17.3047 15.9961 17.793 15.7715C18.3105 15.5176 18.5547 15.1855 18.5547 14.6484L18.5547 8.27148C18.5547 7.91016 18.3594 7.65625 17.8906 7.65625C17.5195 7.65625 16.9434 7.94922 15.8594 7.94922C13.6426 7.94922 12.1875 6.77734 9.84375 6.77734C8.86719 6.77734 8.4375 6.9043 7.98828 7.10938Z", fillAlpha = 0.85f)
        }
        return _sFFlagCircleFill!!
    }

private var _sFFlagCircleFill: ImageVector? = null
