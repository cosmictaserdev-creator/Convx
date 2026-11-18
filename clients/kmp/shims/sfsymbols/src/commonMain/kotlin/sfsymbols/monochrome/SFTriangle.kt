package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangle (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFTriangle: ImageVector
    get() {
        if (_sFTriangle != null) {
            return _sFTriangle!!
        }
        _sFTriangle = sfIcon(
            name = "Monochrome.SFTriangle",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M3.26172 23.8672L23.0176 23.8672C25.0586 23.8672 26.2891 22.4414 26.2891 20.6348C26.2891 20.0488 26.123 19.4434 25.8008 18.8867L15.9277 1.62109C15.3125 0.537109 14.2285 0 13.1445 0C12.0508 0 10.9766 0.537109 10.3613 1.62109L0.488281 18.8867C0.15625 19.4531 0 20.0488 0 20.6348C0 22.4414 1.23047 23.8672 3.26172 23.8672ZM3.28125 22.1777C2.31445 22.1777 1.73828 21.4453 1.73828 20.625C1.73828 20.332 1.81641 20 1.98242 19.6875L11.8359 2.44141C12.1289 1.92383 12.6465 1.71875 13.1445 1.71875C13.6426 1.71875 14.1504 1.92383 14.4336 2.44141L24.2969 19.6973C24.4629 20.0098 24.541 20.332 24.541 20.625C24.541 21.4453 23.9551 22.1777 22.9883 22.1777Z", fillAlpha = 0.85f)
        }
        return _sFTriangle!!
    }

private var _sFTriangle: ImageVector? = null
