package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPip (monochrome)
 * Viewport: 32.666 x 25.9082
 */
public val SfSymbols.Monochrome.SFPip: ImageVector
    get() {
        if (_sFPip != null) {
            return _sFPip!!
        }
        _sFPip = sfIcon(
            name = "Monochrome.SFPip",
            viewportWidth = 32.666f,
            viewportHeight = 25.9082f
        ) {
            addSfPath("M25.9961 3.78906L25.9961 10.1465L24.2676 10.1465L24.2676 3.88672C24.2676 2.48047 23.4961 1.75781 22.1582 1.75781L3.83789 1.75781C2.4707 1.75781 1.72852 2.48047 1.72852 3.88672L1.72852 16.4355C1.72852 17.8418 2.5 18.5645 3.83789 18.5645L12.1973 18.5645L12.1973 20.293L3.79883 20.293C1.26953 20.293 0 19.0234 0 16.5234L0 3.78906C0 1.29883 1.26953 0.0292969 3.79883 0.0292969L22.1973 0.0292969C24.7266 0.0292969 25.9961 1.29883 25.9961 3.78906Z", fillAlpha = 0.85f)
            addSfPath("M15.9961 25.9082L28.5059 25.9082C31.0254 25.9082 32.3047 24.6289 32.3047 22.1387L32.3047 13.9062C32.3047 11.4258 31.0254 10.1465 28.5059 10.1465L15.9961 10.1465C13.4766 10.1465 12.1973 11.4062 12.1973 13.9062L12.1973 22.1387C12.1973 24.6387 13.4766 25.9082 15.9961 25.9082ZM16.0254 24.1797C14.668 24.1797 13.9258 23.457 13.9258 22.0508L13.9258 13.9941C13.9258 12.5977 14.668 11.875 16.0254 11.875L28.4766 11.875C29.8047 11.875 30.5762 12.5977 30.5762 13.9941L30.5762 22.0508C30.5762 23.457 29.8047 24.1797 28.4766 24.1797Z", fillAlpha = 0.85f)
        }
        return _sFPip!!
    }

private var _sFPip: ImageVector? = null
