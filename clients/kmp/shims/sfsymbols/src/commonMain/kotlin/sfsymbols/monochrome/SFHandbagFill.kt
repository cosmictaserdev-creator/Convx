package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHandbagFill (monochrome)
 * Viewport: 26.0841 x 26.3379
 */
public val SfSymbols.Monochrome.SFHandbagFill: ImageVector
    get() {
        if (_sFHandbagFill != null) {
            return _sFHandbagFill!!
        }
        _sFHandbagFill = sfIcon(
            name = "Monochrome.SFHandbagFill",
            viewportWidth = 26.0841f,
            viewportHeight = 26.3379f
        ) {
            addSfPath("M5.15632 9.17969L6.88483 9.17969L6.88483 7.43164C6.88483 4.17969 9.45319 1.73828 12.8614 1.73828C16.2696 1.73828 18.838 4.17969 18.838 7.43164L18.838 9.17969L20.5762 9.17969L20.5762 7.43164C20.5762 3.19336 17.2755 0 12.8614 0C8.4571 0 5.15632 3.19336 5.15632 7.43164ZM12.8614 15.1758C17.0704 15.1758 20.8106 13.5254 22.8321 9.18945C22.295 8.89648 21.6602 8.75 20.918 8.75L4.81452 8.75C4.06257 8.75 3.4278 8.89648 2.89069 9.18945C4.92194 13.5254 8.65241 15.1758 12.8614 15.1758ZM25.6641 21.4258L24.5801 12.0996C24.4923 11.4062 24.2969 10.8496 23.9747 10.4199C22.0118 14.2383 18.7794 16.1426 15.2735 16.7188L15.2735 17.6465C15.2735 18.4473 14.795 18.9062 13.9942 18.9062L11.7286 18.9062C10.9278 18.9062 10.4493 18.4473 10.4493 17.6465L10.4493 16.7188C6.94343 16.1426 3.711 14.248 1.74811 10.4297C1.43561 10.8496 1.2403 11.4062 1.15241 12.0996L0.0586598 21.4258C-0.312434 24.6094 1.08405 26.3281 4.0528 26.3281L21.67 26.3281C24.6485 26.3281 26.045 24.6094 25.6641 21.4258Z", fillAlpha = 0.85f)
        }
        return _sFHandbagFill!!
    }

private var _sFHandbagFill: ImageVector? = null
