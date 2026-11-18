package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMenucardFill (monochrome)
 * Viewport: 20.3711 x 28.4495
 */
public val SfSymbols.Monochrome.SFMenucardFill: ImageVector
    get() {
        if (_sFMenucardFill != null) {
            return _sFMenucardFill!!
        }
        _sFMenucardFill = sfIcon(
            name = "Monochrome.SFMenucardFill",
            viewportWidth = 20.3711f,
            viewportHeight = 28.4495f
        ) {
            addSfPath("M2.96875 3.46789L16.416 5.84094C18.8086 6.26086 20.0098 7.17883 20.0098 9.51282L20.0098 24.8351C20.0098 27.462 18.5156 28.7999 16.084 28.3702L2.27539 25.9288C0.751953 25.6652 0 24.7863 0 23.2921L0 5.84094C0 4.09289 1.16211 3.15539 2.96875 3.46789ZM3.85742 14.5128C3.85742 14.9132 4.08203 15.1866 4.46289 15.255L14.6875 17.0616C15.2246 17.1495 15.5566 16.8175 15.5566 16.3488C15.5566 15.9191 15.332 15.6847 14.9316 15.6163L4.74609 13.839C4.21875 13.7511 3.85742 14.0636 3.85742 14.5128ZM3.85742 9.53235C3.85742 9.93274 4.08203 10.2062 4.46289 10.2745L14.6875 12.0714C15.2246 12.1593 15.5566 11.8273 15.5566 11.3683C15.5566 10.9288 15.332 10.7042 14.9316 10.6261L4.74609 8.84875C4.21875 8.76086 3.85742 9.07336 3.85742 9.53235ZM19.1992 3.95618L19.1992 5.12805C18.5156 4.75696 17.6855 4.48352 16.6895 4.30774L5.5957 2.35461L14.6777 0.75305C17.5195 0.245238 19.1992 1.44641 19.1992 3.95618Z", fillAlpha = 0.85f)
        }
        return _sFMenucardFill!!
    }

private var _sFMenucardFill: ImageVector? = null
