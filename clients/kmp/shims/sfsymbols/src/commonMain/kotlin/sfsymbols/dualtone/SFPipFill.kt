package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPipFill (dualtone)
 * Viewport: 34.2285 x 29.0137
 */
public val SfSymbols.Dualtone.SFPipFill: ImageVector
    get() {
        if (_sFPipFill != null) {
            return _sFPipFill!!
        }
        _sFPipFill = sfIcon(
            name = "Dualtone.SFPipFill",
            viewportWidth = 34.2285f,
            viewportHeight = 29.0137f
        ) {
            addSfPath("M25.9961 5.3418L25.9961 10.1367L15.9961 10.1367C12.6172 10.1367 10.6348 12.0996 10.6348 15.459L10.6348 21.8457L3.79883 21.8457C1.26953 21.8457 0 20.5762 0 18.0762L0 5.3418C0 2.85156 1.26953 1.58203 3.79883 1.58203L22.1973 1.58203C24.7266 1.58203 25.9961 2.85156 25.9961 5.3418Z", fillAlpha = 0.425f)
            addSfPath("M15.9961 27.4609L28.5059 27.4609C31.0254 27.4609 32.3047 26.1816 32.3047 23.6914L32.3047 15.459C32.3047 12.9785 31.0254 11.6992 28.5059 11.6992L15.9961 11.6992C13.4766 11.6992 12.1973 12.959 12.1973 15.459L12.1973 23.6914C12.1973 26.1914 13.4766 27.4609 15.9961 27.4609Z", fillAlpha = 0.85f)
        }
        return _sFPipFill!!
    }

private var _sFPipFill: ImageVector? = null
