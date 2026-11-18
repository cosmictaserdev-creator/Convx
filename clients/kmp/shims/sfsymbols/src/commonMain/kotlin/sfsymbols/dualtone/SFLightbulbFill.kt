package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightbulbFill (dualtone)
 * Viewport: 17.0117 x 29.9121
 */
public val SfSymbols.Dualtone.SFLightbulbFill: ImageVector
    get() {
        if (_sFLightbulbFill != null) {
            return _sFLightbulbFill!!
        }
        _sFLightbulbFill = sfIcon(
            name = "Dualtone.SFLightbulbFill",
            viewportWidth = 17.0117f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M4.4043 25.4492L12.2461 25.4492C12.627 25.4492 12.9199 25.1465 12.9199 24.7656C12.9199 24.3848 12.627 24.082 12.2461 24.082L4.4043 24.082C4.02344 24.082 3.73047 24.3848 3.73047 24.7656C3.73047 25.1465 4.02344 25.4492 4.4043 25.4492ZM8.32031 29.0234C10.2637 29.0234 11.8359 28.0859 11.9434 26.6699L4.70703 26.6699C4.79492 28.0859 6.36719 29.0234 8.32031 29.0234Z", fillAlpha = 0.425f)
            addSfPath("M0 7.66602C0 12.3633 3.13477 13.623 3.66211 22.1582C3.68164 22.5977 3.93555 22.8613 4.41406 22.8613L12.2363 22.8613C12.7148 22.8613 12.9688 22.5977 12.9883 22.1582C13.5156 13.623 16.6504 12.3633 16.6504 7.66602C16.6504 3.41797 12.9785 0 8.32031 0C3.67188 0 0 3.41797 0 7.66602Z", fillAlpha = 0.85f)
        }
        return _sFLightbulbFill!!
    }

private var _sFLightbulbFill: ImageVector? = null
