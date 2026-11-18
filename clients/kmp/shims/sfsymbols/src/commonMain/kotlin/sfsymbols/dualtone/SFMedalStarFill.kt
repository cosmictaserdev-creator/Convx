package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMedalStarFill (dualtone)
 * Viewport: 18.0566 x 30.8035
 */
public val SfSymbols.Dualtone.SFMedalStarFill: ImageVector
    get() {
        if (_sFMedalStarFill != null) {
            return _sFMedalStarFill!!
        }
        _sFMedalStarFill = sfIcon(
            name = "Dualtone.SFMedalStarFill",
            viewportWidth = 18.0566f,
            viewportHeight = 30.8035f
        ) {
            addSfPath("M11.2793 16.1488L11.0881 16.2813L10.8105 15.4164C10.166 13.4145 7.53906 13.4145 6.9043 15.4164L6.62159 16.2896L6.40625 16.1391L6.40625 1.05118L11.2793 1.05118ZM17.6953 2.95548L17.6953 10.3578C17.6953 11.3246 17.4121 11.9008 16.6699 12.4086L12.9688 14.9867L12.9688 1.05118L15.7812 1.05118C16.8945 1.05118 17.6953 1.8422 17.6953 2.95548ZM4.7168 14.977L1.02539 12.4086C0.292969 11.9008 0 11.3441 0 10.3578L0 2.95548C0 1.8422 0.791016 1.05118 1.9043 1.05118L4.7168 1.05118Z", fillAlpha = 0.85f)
            addSfPath("M5.16602 29.2543L8.85742 26.5492L12.5488 29.2543C13.1934 29.7328 13.8867 29.2348 13.6328 28.4731L12.1777 24.1176L15.9082 21.4613C16.4746 21.0414 16.3086 20.1527 15.4883 20.1625L10.9082 20.1918L9.51172 15.8168C9.28711 15.0941 8.42773 15.0941 8.19336 15.8168L6.79688 20.1918L2.22656 20.1625C1.41602 20.1527 1.21094 21.0219 1.81641 21.4613L5.52734 24.1176L4.08203 28.4731C3.82812 29.2348 4.52148 29.7328 5.16602 29.2543Z", fillAlpha = 0.85f)
        }
        return _sFMedalStarFill!!
    }

private var _sFMedalStarFill: ImageVector? = null
