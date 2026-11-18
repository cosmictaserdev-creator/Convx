package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDropTriangle (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFDropTriangle: ImageVector
    get() {
        if (_sFDropTriangle != null) {
            return _sFDropTriangle!!
        }
        _sFDropTriangle = sfIcon(
            name = "Monochrome.SFDropTriangle",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M3.26172 23.8672L23.0176 23.8672C25.0586 23.8672 26.2891 22.4414 26.2891 20.6348C26.2891 20.0488 26.123 19.4434 25.8008 18.8867L15.9277 1.62109C15.3125 0.537109 14.2285 0 13.1445 0C12.0508 0 10.9766 0.537109 10.3613 1.62109L0.488281 18.8867C0.15625 19.4531 0 20.0488 0 20.6348C0 22.4414 1.23047 23.8672 3.26172 23.8672ZM3.28125 22.1777C2.31445 22.1777 1.73828 21.4453 1.73828 20.625C1.73828 20.332 1.81641 20 1.98242 19.6875L11.8359 2.44141C12.1289 1.92383 12.6465 1.71875 13.1445 1.71875C13.6426 1.71875 14.1504 1.92383 14.4336 2.44141L24.2969 19.6973C24.4629 20.0098 24.541 20.332 24.541 20.625C24.541 21.4453 23.9551 22.1777 22.9883 22.1777Z", fillAlpha = 0.85f)
            addSfPath("M13.1445 19.1016C15.2539 19.1016 16.6602 17.7344 16.6602 15.6836C16.6602 14.707 16.25 13.7793 15.8887 13.0078C15.332 11.7871 14.4434 10.332 13.6621 9.12109C13.5059 8.89648 13.3496 8.78906 13.1445 8.78906C12.9297 8.78906 12.7734 8.89648 12.627 9.12109C11.8359 10.332 10.957 11.7871 10.3906 13.0078C10.0391 13.7793 9.62891 14.707 9.62891 15.6836C9.62891 17.7344 11.0352 19.1016 13.1445 19.1016Z", fillAlpha = 0.85f)
        }
        return _sFDropTriangle!!
    }

private var _sFDropTriangle: ImageVector? = null
