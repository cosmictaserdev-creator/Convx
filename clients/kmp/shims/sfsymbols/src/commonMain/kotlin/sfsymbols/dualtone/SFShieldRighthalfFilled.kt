package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShieldRighthalfFilled (dualtone)
 * Viewport: 20.9863 x 25.8691
 */
public val SfSymbols.Dualtone.SFShieldRighthalfFilled: ImageVector
    get() {
        if (_sFShieldRighthalfFilled != null) {
            return _sFShieldRighthalfFilled!!
        }
        _sFShieldRighthalfFilled = sfIcon(
            name = "Dualtone.SFShieldRighthalfFilled",
            viewportWidth = 20.9863f,
            viewportHeight = 25.8691f
        ) {
            addSfPath("M20.625 15.5273L20.625 5.91797C20.625 4.58008 20.0977 4.13086 18.9746 3.68164C17.2949 3.01758 13.0078 1.38672 11.3379 0.859375C11.0059 0.761719 10.6543 0.664062 10.3125 0.664062C9.9707 0.664062 9.62891 0.742188 9.29688 0.859375C7.60742 1.43555 3.33008 3.00781 1.65039 3.68164C0.527344 4.15039 0 4.58008 0 5.91797L0 15.5273C0 19.9512 2.37305 21.5527 9.47266 25.6055C9.77539 25.7812 10.0977 25.8691 10.3125 25.8691C10.5371 25.8691 10.8594 25.7812 11.1523 25.6055C18.2324 21.5137 20.625 19.9512 20.625 15.5273ZM10.1172 23.8965C9.94141 23.8672 9.74609 23.7695 9.56055 23.6426C3.74023 20.0293 1.70898 19.0039 1.70898 15.1562L1.70898 6.25C1.70898 5.69336 1.81641 5.46875 2.24609 5.3125C4.0332 4.67773 7.98828 3.22266 9.74609 2.5293C9.89258 2.4707 10.0195 2.43164 10.1172 2.42188Z", fillAlpha = 0.85f)
        }
        return _sFShieldRighthalfFilled!!
    }

private var _sFShieldRighthalfFilled: ImageVector? = null
