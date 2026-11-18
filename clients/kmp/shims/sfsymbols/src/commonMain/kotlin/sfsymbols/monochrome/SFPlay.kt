package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlay (monochrome)
 * Viewport: 20.6934 x 20.8691
 */
public val SfSymbols.Monochrome.SFPlay: ImageVector
    get() {
        if (_sFPlay != null) {
            return _sFPlay!!
        }
        _sFPlay = sfIcon(
            name = "Monochrome.SFPlay",
            viewportWidth = 20.6934f,
            viewportHeight = 20.8691f
        ) {
            addSfPath("M2.25586 19.1699C2.25586 20.3223 2.92969 20.8594 3.7207 20.8594C4.0625 20.8594 4.42383 20.752 4.76562 20.5566L19.4531 11.9238C20.3613 11.3965 20.6934 11.0254 20.6934 10.4297C20.6934 9.82422 20.3613 9.45312 19.4531 8.92578L4.76562 0.292969C4.42383 0.0976562 4.0625 0 3.7207 0C2.92969 0 2.25586 0.527344 2.25586 1.67969ZM3.99414 18.4766L3.99414 2.37305C3.99414 2.09961 4.21875 1.99219 4.45312 2.12891L18.2129 10.1953C18.3203 10.2539 18.3691 10.3418 18.3691 10.4297C18.3691 10.5078 18.3203 10.5957 18.2129 10.6543L4.45312 18.7305C4.21875 18.8672 3.99414 18.75 3.99414 18.4766Z", fillAlpha = 0.85f)
        }
        return _sFPlay!!
    }

private var _sFPlay: ImageVector? = null
