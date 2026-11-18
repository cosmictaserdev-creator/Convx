package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRollerShadeClosed (monochrome)
 * Viewport: 27.9492 x 25.6543
 */
public val SfSymbols.Monochrome.SFRollerShadeClosed: ImageVector
    get() {
        if (_sFRollerShadeClosed != null) {
            return _sFRollerShadeClosed!!
        }
        _sFRollerShadeClosed = sfIcon(
            name = "Monochrome.SFRollerShadeClosed",
            viewportWidth = 27.9492f,
            viewportHeight = 25.6543f
        ) {
            addSfPath("M1.42578 6.42578C1.42578 6.8457 1.76758 7.20703 2.19727 7.20703C2.63672 7.20703 2.97852 6.8457 2.97852 6.42578C2.97852 5.99609 2.63672 5.64453 2.19727 5.64453C1.76758 5.64453 1.42578 5.99609 1.42578 6.42578ZM1.42578 9.22852C1.42578 9.6582 1.76758 10.0098 2.19727 10.0098C2.63672 10.0098 2.97852 9.6582 2.97852 9.22852C2.97852 8.79883 2.63672 8.45703 2.19727 8.45703C1.76758 8.45703 1.42578 8.79883 1.42578 9.22852ZM1.33789 15.3027C1.33789 15.7715 1.73828 16.1621 2.19727 16.1621C2.66602 16.1621 3.06641 15.7715 3.06641 15.3027L3.06641 12.1289C3.06641 11.6602 2.67578 11.2695 2.19727 11.2695C1.72852 11.2695 1.33789 11.6602 1.33789 12.1289Z", fillAlpha = 0.85f)
            addSfPath("M0 2.19727C0 3.4082 0.986328 4.38477 2.19727 4.38477C3.4082 4.38477 4.39453 3.4082 4.39453 2.19727C4.39453 0.986328 3.4082 0 2.19727 0C0.986328 0 0 0.986328 0 2.19727ZM7.94922 25.6348L25.3906 25.6348C26.6016 25.6348 27.5879 24.6582 27.5879 23.4473L27.5879 2.19727C27.5879 0.986328 26.6016 0 25.3906 0L5 0C5.50781 0.537109 5.75195 1.25 5.75195 2.19727L5.75195 23.4473C5.75195 24.6582 6.73828 25.6348 7.94922 25.6348Z", fillAlpha = 0.85f)
        }
        return _sFRollerShadeClosed!!
    }

private var _sFRollerShadeClosed: ImageVector? = null
