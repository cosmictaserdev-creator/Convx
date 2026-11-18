package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPipExit (dualtone)
 * Viewport: 32.666 x 25.9082
 */
public val SfSymbols.Dualtone.SFPipExit: ImageVector
    get() {
        if (_sFPipExit != null) {
            return _sFPipExit!!
        }
        _sFPipExit = sfIcon(
            name = "Dualtone.SFPipExit",
            viewportWidth = 32.666f,
            viewportHeight = 25.9082f
        ) {
            addSfPath("M25.9961 3.78906L25.9961 10.1465L24.2676 10.1465L24.2676 3.88672C24.2676 2.48047 23.4961 1.75781 22.1582 1.75781L3.83789 1.75781C2.4707 1.75781 1.72852 2.48047 1.72852 3.88672L1.72852 16.4355C1.72852 17.8418 2.5 18.5645 3.83789 18.5645L12.1973 18.5645L12.1973 20.293L3.79883 20.293C1.26953 20.293 0 19.0234 0 16.5234L0 3.78906C0 1.29883 1.26953 0.0292969 3.79883 0.0292969L22.1973 0.0292969C24.7266 0.0292969 25.9961 1.29883 25.9961 3.78906Z", fillAlpha = 0.425f)
            addSfPath("M4.59961 3.93555L9.07227 4.6582C9.43359 4.70703 9.82422 5.15625 9.36523 5.60547L7.95898 7.03125L10.1465 9.20898C10.4102 9.48242 10.4102 9.91211 10.1465 10.1855C9.88281 10.4395 9.44336 10.4395 9.17969 10.1855L7.00195 7.99805L5.57617 9.4043C5.12695 9.86328 4.66797 9.46289 4.61914 9.10156L3.89648 4.63867C3.86719 4.43359 3.93555 4.23828 4.0625 4.0918C4.19922 3.96484 4.39453 3.89648 4.59961 3.93555Z", fillAlpha = 0.85f)
            addSfPath("M15.9961 25.9082L28.5059 25.9082C31.0254 25.9082 32.3047 24.6289 32.3047 22.1387L32.3047 13.9062C32.3047 11.4258 31.0254 10.1465 28.5059 10.1465L15.9961 10.1465C13.4766 10.1465 12.1973 11.4062 12.1973 13.9062L12.1973 22.1387C12.1973 24.6387 13.4766 25.9082 15.9961 25.9082Z", fillAlpha = 0.85f)
        }
        return _sFPipExit!!
    }

private var _sFPipExit: ImageVector? = null
