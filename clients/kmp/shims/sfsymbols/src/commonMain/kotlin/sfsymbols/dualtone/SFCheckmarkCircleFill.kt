package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCheckmarkCircleFill: ImageVector
    get() {
        if (_sFCheckmarkCircleFill != null) {
            return _sFCheckmarkCircleFill!!
        }
        _sFCheckmarkCircleFill = sfIcon(
            name = "Dualtone.SFCheckmarkCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M11.3184 18.8477C10.9473 18.8477 10.6641 18.6914 10.3809 18.3301L7.05078 14.3066C6.88477 14.0918 6.78711 13.8477 6.78711 13.6035C6.78711 13.1152 7.16797 12.7148 7.65625 12.7148C7.95898 12.7148 8.20312 12.8223 8.4668 13.1641L11.2793 16.6797L16.9238 7.70508C17.1191 7.38281 17.4023 7.20703 17.6953 7.20703C18.1641 7.20703 18.5938 7.53906 18.5938 8.03711C18.5938 8.27148 18.4668 8.52539 18.3398 8.73047L12.2168 18.3301C11.9922 18.6719 11.6895 18.8477 11.3184 18.8477Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkCircleFill!!
    }

private var _sFCheckmarkCircleFill: ImageVector? = null
