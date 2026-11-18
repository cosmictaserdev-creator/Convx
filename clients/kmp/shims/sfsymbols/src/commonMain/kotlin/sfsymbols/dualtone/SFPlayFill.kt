package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayFill (dualtone)
 * Viewport: 20.6934 x 20.8691
 */
public val SfSymbols.Dualtone.SFPlayFill: ImageVector
    get() {
        if (_sFPlayFill != null) {
            return _sFPlayFill!!
        }
        _sFPlayFill = sfIcon(
            name = "Dualtone.SFPlayFill",
            viewportWidth = 20.6934f,
            viewportHeight = 20.8691f
        ) {
            addSfPath("M2.25586 19.1699C2.25586 20.3223 2.92969 20.8594 3.7207 20.8594C4.0625 20.8594 4.42383 20.752 4.76562 20.5566L19.4531 11.9238C20.3613 11.3965 20.6934 11.0254 20.6934 10.4297C20.6934 9.82422 20.3613 9.45312 19.4531 8.92578L4.76562 0.292969C4.42383 0.0976562 4.0625 0 3.7207 0C2.92969 0 2.25586 0.527344 2.25586 1.67969Z", fillAlpha = 0.85f)
        }
        return _sFPlayFill!!
    }

private var _sFPlayFill: ImageVector? = null
