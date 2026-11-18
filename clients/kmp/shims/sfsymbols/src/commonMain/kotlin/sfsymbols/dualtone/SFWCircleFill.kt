package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFWCircleFill: ImageVector
    get() {
        if (_sFWCircleFill != null) {
            return _sFWCircleFill!!
        }
        _sFWCircleFill = sfIcon(
            name = "Dualtone.SFWCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.28711 18.7109C8.69141 18.7109 8.30078 18.3984 8.1543 17.8027L5.57617 7.8418C5.54688 7.71484 5.53711 7.60742 5.53711 7.5C5.53711 7.00195 5.86914 6.66016 6.38672 6.66016C6.9043 6.66016 7.14844 6.9043 7.29492 7.53906L9.3457 16.1621L9.42383 16.1621L11.709 7.44141C11.8359 6.94336 12.168 6.69922 12.7051 6.69922C13.2422 6.69922 13.5547 6.94336 13.6914 7.44141L15.9766 16.1621L16.0645 16.1621L18.125 7.53906C18.2617 6.9043 18.5059 6.66016 19.0234 6.66016C19.541 6.66016 19.8828 7.00195 19.8828 7.5C19.8828 7.60742 19.873 7.71484 19.834 7.8418L17.2559 17.8027C17.0996 18.3984 16.7188 18.7109 16.1035 18.7109C15.498 18.7109 15.0879 18.4082 14.9512 17.832L12.7441 9.32617L12.6562 9.32617L10.4492 17.832C10.3125 18.4082 9.90234 18.7109 9.28711 18.7109Z", fillAlpha = 0.85f)
        }
        return _sFWCircleFill!!
    }

private var _sFWCircleFill: ImageVector? = null
