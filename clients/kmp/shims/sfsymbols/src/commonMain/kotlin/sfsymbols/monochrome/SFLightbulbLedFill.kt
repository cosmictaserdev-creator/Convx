package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightbulbLedFill (monochrome)
 * Viewport: 17.0117 x 29.9121
 */
public val SfSymbols.Monochrome.SFLightbulbLedFill: ImageVector
    get() {
        if (_sFLightbulbLedFill != null) {
            return _sFLightbulbLedFill!!
        }
        _sFLightbulbLedFill = sfIcon(
            name = "Monochrome.SFLightbulbLedFill",
            viewportWidth = 17.0117f,
            viewportHeight = 29.9121f
        ) {
            addSfPath("M4.4043 25.4492L12.2461 25.4492C12.627 25.4492 12.9199 25.1465 12.9199 24.7656C12.9199 24.3848 12.627 24.082 12.2461 24.082L4.4043 24.082C4.02344 24.082 3.73047 24.3848 3.73047 24.7656C3.73047 25.1465 4.02344 25.4492 4.4043 25.4492ZM8.32031 29.0234C10.2637 29.0234 11.8359 28.0859 11.9434 26.6699L4.70703 26.6699C4.79492 28.0859 6.36719 29.0234 8.32031 29.0234Z", fillAlpha = 0.85f)
            addSfPath("M3.66211 22.1582C3.68164 22.5977 3.93555 22.8613 4.41406 22.8613L12.2363 22.8613C12.7148 22.8613 12.9688 22.5977 12.9883 22.1582C13.3105 16.6406 14.7852 14.1602 15.7324 11.8457L0.917969 11.8457C1.86523 14.1602 3.33984 16.6406 3.66211 22.1582Z", fillAlpha = 0.85f)
            addSfPath("M16.6504 7.66602C16.6504 3.41797 12.9785 0 8.32031 0C3.67188 0 0 3.41797 0 7.66602C0 8.70117 0.15625 9.58008 0.400391 10.4102L16.25 10.4102C16.4941 9.58008 16.6504 8.70117 16.6504 7.66602Z", fillAlpha = 0.85f)
        }
        return _sFLightbulbLedFill!!
    }

private var _sFLightbulbLedFill: ImageVector? = null
