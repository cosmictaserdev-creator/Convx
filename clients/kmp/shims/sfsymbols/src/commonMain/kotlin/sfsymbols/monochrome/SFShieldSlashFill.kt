package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShieldSlashFill (monochrome)
 * Viewport: 29.2996 x 27.6638
 */
public val SfSymbols.Monochrome.SFShieldSlashFill: ImageVector
    get() {
        if (_sFShieldSlashFill != null) {
            return _sFShieldSlashFill!!
        }
        _sFShieldSlashFill = sfIcon(
            name = "Monochrome.SFShieldSlashFill",
            viewportWidth = 29.2996f,
            viewportHeight = 27.6638f
        ) {
            addSfPath("M20.2605 23.347C18.9711 24.2138 17.3323 25.1444 15.2943 26.2489C15.0013 26.4052 14.6888 26.5028 14.4642 26.5028C14.2494 26.5028 13.9369 26.4149 13.6342 26.2489C6.65174 22.2059 4.27869 20.663 4.27869 16.2977L4.27869 7.37867ZM15.4799 1.79579C17.14 2.3622 21.3588 3.9247 23.0189 4.58876C24.142 5.04774 24.6498 5.47743 24.6498 6.79579L24.6498 16.2977C24.6498 17.9583 24.3233 19.2387 23.525 20.399L7.20822 4.0891C9.19057 3.33502 12.129 2.24669 13.4584 1.79579C13.7904 1.69813 14.1224 1.61024 14.4642 1.61024C14.8158 1.61024 15.1478 1.68837 15.4799 1.79579Z", fillAlpha = 0.85f)
            addSfPath("M24.8939 26.0536C25.2162 26.3759 25.7435 26.3661 26.0658 26.0536C26.3978 25.7313 26.3978 25.1942 26.0658 24.872L2.78455 1.60048C2.47205 1.27821 1.93494 1.27821 1.60291 1.60048C1.28065 1.91298 1.28065 2.45985 1.60291 2.78212Z", fillAlpha = 0.85f)
        }
        return _sFShieldSlashFill!!
    }

private var _sFShieldSlashFill: ImageVector? = null
