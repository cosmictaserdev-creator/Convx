package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCheckmarkCircleFill: ImageVector
    get() {
        if (_sFCheckmarkCircleFill != null) {
            return _sFCheckmarkCircleFill!!
        }
        _sFCheckmarkCircleFill = sfIcon(
            name = "Monochrome.SFCheckmarkCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM16.9238 7.70508L11.2793 16.6797L8.4668 13.1641C8.20312 12.8223 7.95898 12.7148 7.65625 12.7148C7.16797 12.7148 6.78711 13.1152 6.78711 13.6035C6.78711 13.8477 6.88477 14.0918 7.05078 14.3066L10.3809 18.3301C10.6641 18.6914 10.9473 18.8477 11.3184 18.8477C11.6895 18.8477 11.9922 18.6719 12.2168 18.3301L18.3398 8.73047C18.4668 8.52539 18.5938 8.27148 18.5938 8.03711C18.5938 7.53906 18.1641 7.20703 17.6953 7.20703C17.4023 7.20703 17.1191 7.38281 16.9238 7.70508Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkCircleFill!!
    }

private var _sFCheckmarkCircleFill: ImageVector? = null
