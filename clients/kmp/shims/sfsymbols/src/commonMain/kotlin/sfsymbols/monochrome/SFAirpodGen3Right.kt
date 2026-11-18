package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirpodGen3Right (monochrome)
 * Viewport: 13.9648 x 21.6699
 */
public val SfSymbols.Monochrome.SFAirpodGen3Right: ImageVector
    get() {
        if (_sFAirpodGen3Right != null) {
            return _sFAirpodGen3Right!!
        }
        _sFAirpodGen3Right = sfIcon(
            name = "Monochrome.SFAirpodGen3Right",
            viewportWidth = 13.9648f,
            viewportHeight = 21.6699f
        ) {
            addSfPath("M8.32031 0.0878906C4.51172 0.0878906 0 3.87695 0 7.50977C0 10.5176 2.98828 12.9199 5.47852 12.9004C8.90625 12.8809 13.6035 8.89648 13.6035 5.61523C13.6035 2.45117 11.1719 0.0878906 8.32031 0.0878906ZM4.30664 11.2109C3.61328 11.2109 2.54883 10.3516 1.89453 9.27734C1.23047 8.18359 1.25977 7.33398 1.95312 7.33398C2.62695 7.33398 3.70117 8.18359 4.35547 9.27734C5.00977 10.3516 4.98047 11.2109 4.30664 11.2109ZM7.75391 5.68359C7.38281 5.16602 7.50977 4.43359 8.02734 4.0625L9.76562 2.80273C10.2832 2.42188 11.0156 2.54883 11.377 3.08594C11.7871 3.62305 11.6699 4.32617 11.1328 4.7168L9.39453 5.94727C8.84766 6.33789 8.11523 6.23047 7.75391 5.68359ZM8.95508 21.6699L10.498 21.6699C11.2402 21.6699 11.6895 21.2891 11.6895 20.5859L11.6895 11.2695C10.5273 12.3242 9.15039 13.1738 7.75391 13.6328L7.75391 20.5859C7.75391 21.2891 8.21289 21.6699 8.95508 21.6699Z", fillAlpha = 0.85f)
        }
        return _sFAirpodGen3Right!!
    }

private var _sFAirpodGen3Right: ImageVector? = null
