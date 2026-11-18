package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightbulb (monochrome)
 * Viewport: 17.0117 x 29.9121
 */
public val SfSymbols.Monochrome.SFLightbulb: ImageVector
    get() {
        if (_sFLightbulb != null) {
            return _sFLightbulb!!
        }
        _sFLightbulb = sfIcon(
            name = "Monochrome.SFLightbulb",
            viewportWidth = 17.0117f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M0 7.66602C0 12.3633 2.87109 13.623 3.66211 22.1582C3.70117 22.5977 3.93555 22.8613 4.41406 22.8613L12.2363 22.8613C12.7148 22.8613 12.9492 22.5977 12.9883 22.1582C13.7793 13.623 16.6504 12.3633 16.6504 7.66602C16.6504 3.41797 12.9785 0 8.32031 0C3.67188 0 0 3.41797 0 7.66602ZM1.63086 7.66602C1.63086 4.21875 4.69727 1.63086 8.32031 1.63086C11.9531 1.63086 15.0195 4.21875 15.0195 7.66602C15.0195 11.3086 12.5488 12.3047 11.4844 21.2305L5.16602 21.2305C4.0918 12.3047 1.63086 11.3086 1.63086 7.66602ZM4.4043 25.4492L12.2461 25.4492C12.627 25.4492 12.9199 25.1465 12.9199 24.7656C12.9199 24.3848 12.627 24.082 12.2461 24.082L4.4043 24.082C4.02344 24.082 3.73047 24.3848 3.73047 24.7656C3.73047 25.1465 4.02344 25.4492 4.4043 25.4492ZM8.32031 29.0234C10.2637 29.0234 11.8359 28.0859 11.9434 26.6699L4.70703 26.6699C4.79492 28.0859 6.36719 29.0234 8.32031 29.0234Z", fillAlpha = 0.85f)
        }
        return _sFLightbulb!!
    }

private var _sFLightbulb: ImageVector? = null
