package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPipEnter (dualtone)
 * Viewport: 32.666 x 25.9082
 */
public val SfSymbols.Dualtone.SFPipEnter: ImageVector
    get() {
        if (_sFPipEnter != null) {
            return _sFPipEnter!!
        }
        _sFPipEnter = sfIcon(
            name = "Dualtone.SFPipEnter",
            viewportWidth = 32.666f,
            viewportHeight = 25.9082f
        ) {
            addSfPath("M25.9961 3.78906L25.9961 10.1465L24.2676 10.1465L24.2676 3.88672C24.2676 2.48047 23.4961 1.75781 22.1582 1.75781L3.83789 1.75781C2.4707 1.75781 1.72852 2.48047 1.72852 3.88672L1.72852 16.4355C1.72852 17.8418 2.5 18.5645 3.83789 18.5645L12.1973 18.5645L12.1973 20.293L3.79883 20.293C1.26953 20.293 0 19.0234 0 16.5234L0 3.78906C0 1.29883 1.26953 0.0292969 3.79883 0.0292969L22.1973 0.0292969C24.7266 0.0292969 25.9961 1.29883 25.9961 3.78906Z", fillAlpha = 0.425f)
            addSfPath("M5.05859 4.12109L7.24609 6.30859L8.66211 4.90234C9.11133 4.44336 9.57031 4.84375 9.61914 5.20508L10.3418 9.66797C10.3711 9.87305 10.3027 10.0684 10.1758 10.2148C10.0391 10.332 9.84375 10.4004 9.63867 10.3711L5.17578 9.64844C4.81445 9.59961 4.41406 9.15039 4.87305 8.70117L6.2793 7.27539L4.0918 5.08789C3.82812 4.82422 3.82812 4.38477 4.0918 4.12109C4.35547 3.85742 4.79492 3.85742 5.05859 4.12109Z", fillAlpha = 0.85f)
            addSfPath("M15.9961 25.9082L28.5059 25.9082C31.0254 25.9082 32.3047 24.6289 32.3047 22.1387L32.3047 13.9062C32.3047 11.4258 31.0254 10.1465 28.5059 10.1465L15.9961 10.1465C13.4766 10.1465 12.1973 11.4062 12.1973 13.9062L12.1973 22.1387C12.1973 24.6387 13.4766 25.9082 15.9961 25.9082Z", fillAlpha = 0.85f)
        }
        return _sFPipEnter!!
    }

private var _sFPipEnter: ImageVector? = null
