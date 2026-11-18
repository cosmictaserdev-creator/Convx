package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrapezoidAndLineVerticalFill (monochrome)
 * Viewport: 25.6917 x 29.5215
 */
public val SfSymbols.Monochrome.SFTrapezoidAndLineVerticalFill: ImageVector
    get() {
        if (_sFTrapezoidAndLineVerticalFill != null) {
            return _sFTrapezoidAndLineVerticalFill!!
        }
        _sFTrapezoidAndLineVerticalFill = sfIcon(
            name = "Monochrome.SFTrapezoidAndLineVerticalFill",
            viewportWidth = 25.6917f,
            viewportHeight = 29.5215f
        ) {
            addSfPath("M2.67496 24.8438L22.6554 24.8438C24.6378 24.8438 25.673 23.623 25.2336 21.7871L21.6984 7.28516C21.2589 5.51758 20.1457 4.59961 18.3878 4.59961L6.94254 4.59961C5.18472 4.59961 4.07144 5.51758 3.63199 7.28516L0.0968329 21.7871C-0.332855 23.623 0.692536 24.8438 2.67496 24.8438ZM12.6847 29.4336C12.1671 29.4336 11.8546 29.1113 11.8546 28.5742L11.8546 0.869141C11.8546 0.332031 12.1671 0 12.6847 0C13.173 0 13.4855 0.332031 13.4855 0.869141L13.4855 28.5742C13.4855 29.1113 13.173 29.4336 12.6847 29.4336Z", fillAlpha = 0.85f)
        }
        return _sFTrapezoidAndLineVerticalFill!!
    }

private var _sFTrapezoidAndLineVerticalFill: ImageVector? = null
