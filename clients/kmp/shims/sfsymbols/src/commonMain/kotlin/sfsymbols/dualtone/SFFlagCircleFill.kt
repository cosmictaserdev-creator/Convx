package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFFlagCircleFill: ImageVector
    get() {
        if (_sFFlagCircleFill != null) {
            return _sFFlagCircleFill!!
        }
        _sFFlagCircleFill = sfIcon(
            name = "Dualtone.SFFlagCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.77344 19.6387C7.4707 19.6387 7.22656 19.3848 7.22656 19.1016L7.22656 8.21289C7.22656 7.70508 7.48047 7.33398 7.98828 7.10938C8.4375 6.9043 8.86719 6.77734 9.84375 6.77734C12.1875 6.77734 13.6426 7.94922 15.8594 7.94922C16.9434 7.94922 17.5195 7.65625 17.8906 7.65625C18.3594 7.65625 18.5547 7.91016 18.5547 8.27148L18.5547 14.6484C18.5547 15.1855 18.3105 15.5176 17.793 15.7715C17.3047 15.9961 16.875 16.0938 15.9277 16.0938C13.6621 16.0938 12.2266 14.9512 9.91211 14.9512C9.08203 14.9512 8.55469 15.1074 8.30078 15.2148L8.30078 19.1016C8.30078 19.3945 8.08594 19.6387 7.77344 19.6387Z", fillAlpha = 0.85f)
        }
        return _sFFlagCircleFill!!
    }

private var _sFFlagCircleFill: ImageVector? = null
