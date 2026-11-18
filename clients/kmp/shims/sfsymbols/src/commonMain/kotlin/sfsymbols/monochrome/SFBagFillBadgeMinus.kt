package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBagFillBadgeMinus (monochrome)
 * Viewport: 38.2031 x 32.9004
 */
public val SfSymbols.Monochrome.SFBagFillBadgeMinus: ImageVector
    get() {
        if (_sFBagFillBadgeMinus != null) {
            return _sFBagFillBadgeMinus!!
        }
        _sFBagFillBadgeMinus = sfIcon(
            name = "Monochrome.SFBagFillBadgeMinus",
            viewportWidth = 38.2031f,
            viewportHeight = 32.9004f
        ) {
            addSfPath("M23.7069 8.28125L26.1621 8.28125C28.6816 8.28125 29.9609 9.56055 29.9609 12.041L29.9609 25.1855C29.9609 27.6758 28.6914 28.9551 26.4551 28.9551L14.6699 28.9551C15.3588 27.7957 15.752 26.4479 15.752 25.0195C15.752 20.7031 12.1973 17.1484 7.88086 17.1484C7.87757 17.1484 7.87427 17.1484 7.87109 17.1489L7.87109 12.041C7.87109 9.55078 9.15039 8.28125 11.6699 8.28125L14.1251 8.28125C14.359 5.81254 16.3577 3.93555 18.916 3.93555C21.4744 3.93555 23.473 5.81254 23.7069 8.28125ZM15.8657 8.28125L21.9664 8.28125C21.7615 6.68671 20.556 5.57617 18.916 5.57617C17.276 5.57617 16.0705 6.68671 15.8657 8.28125Z", fillAlpha = 0.85f)
            addSfPath("M14.1895 25.0195C14.1895 28.4766 11.3086 31.3379 7.88086 31.3379C4.41406 31.3379 1.5625 28.4961 1.5625 25.0195C1.5625 21.5625 4.41406 18.7109 7.88086 18.7109C11.3477 18.7109 14.1895 21.5625 14.1895 25.0195ZM4.58984 24.3164C4.20898 24.3164 3.88672 24.6484 3.88672 25.0195C3.88672 25.4004 4.20898 25.7324 4.58984 25.7324L11.1523 25.7324C11.543 25.7324 11.8652 25.4004 11.8652 25.0195C11.8652 24.6484 11.543 24.3164 11.1523 24.3164Z", fillAlpha = 0.85f)
        }
        return _sFBagFillBadgeMinus!!
    }

private var _sFBagFillBadgeMinus: ImageVector? = null
