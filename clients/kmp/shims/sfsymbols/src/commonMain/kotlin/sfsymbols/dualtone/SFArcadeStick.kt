package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArcadeStick (dualtone)
 * Viewport: 14.1211 x 25.6348
 */
public val SfSymbols.Dualtone.SFArcadeStick: ImageVector
    get() {
        if (_sFArcadeStick != null) {
            return _sFArcadeStick!!
        }
        _sFArcadeStick = sfIcon(
            name = "Dualtone.SFArcadeStick",
            viewportWidth = 14.1211f,
            viewportHeight = 25.6348f
        ) {
            addSfPath("M13.7598 19.9121C13.7598 23.1445 10.7812 25.6152 6.88477 25.6152C2.97852 25.6152 0 23.1445 0 19.9121C0 17.4553 1.73105 15.4299 4.28711 14.6098L4.28711 16.4537C2.73906 17.1232 1.73828 18.3964 1.73828 19.9121C1.73828 22.168 3.95508 23.8867 6.88477 23.8867C9.81445 23.8867 12.0215 22.168 12.0215 19.9121C12.0215 18.381 11.0047 17.0972 9.43359 16.4323L9.43359 14.597C12.0093 15.4072 13.7598 17.4416 13.7598 19.9121Z", fillAlpha = 0.425f)
            addSfPath("M6.8457 20.9375C7.5293 20.9375 8.07617 20.6152 8.07617 20.1562L8.07617 7.46094L5.64453 7.46094L5.64453 20.1562C5.64453 20.6152 6.17188 20.9375 6.8457 20.9375ZM6.8457 10.4004C9.7168 10.4004 12.0312 8.06641 12.0312 5.18555C12.0312 2.31445 9.69727 0 6.8457 0C3.97461 0 1.64062 2.33398 1.64062 5.18555C1.64062 8.08594 3.97461 10.4004 6.8457 10.4004Z", fillAlpha = 0.85f)
        }
        return _sFArcadeStick!!
    }

private var _sFArcadeStick: ImageVector? = null
