package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMacminiGen2Fill (monochrome)
 * Viewport: 33.4473 x 10.0879
 */
public val SfSymbols.Monochrome.SFMacminiGen2Fill: ImageVector
    get() {
        if (_sFMacminiGen2Fill != null) {
            return _sFMacminiGen2Fill!!
        }
        _sFMacminiGen2Fill = sfIcon(
            name = "Monochrome.SFMacminiGen2Fill",
            viewportWidth = 33.4473f,
            viewportHeight = 10.0879f
        ) {
            addSfPath("M3.63281 10.0684L29.4629 10.0684C31.8652 10.0684 33.0859 8.82812 33.0859 6.48438L33.0859 3.58398C33.0859 1.24023 31.8652 0 29.4629 0L3.63281 0C1.23047 0 0 1.24023 0 3.58398L0 6.48438C0 8.82812 1.23047 10.0684 3.63281 10.0684ZM26.0449 6.83594C25.4688 6.83594 25.0195 6.38672 25.0195 5.80078C25.0195 5.22461 25.4688 4.77539 26.0449 4.77539C26.6211 4.77539 27.0801 5.22461 27.0801 5.80078C27.0801 6.38672 26.6211 6.83594 26.0449 6.83594Z", fillAlpha = 0.85f)
        }
        return _sFMacminiGen2Fill!!
    }

private var _sFMacminiGen2Fill: ImageVector? = null
