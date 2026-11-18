package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEraserLineDashed (monochrome)
 * Viewport: 32.5789 x 28.0715
 */
public val SfSymbols.Monochrome.SFEraserLineDashed: ImageVector
    get() {
        if (_sFEraserLineDashed != null) {
            return _sFEraserLineDashed!!
        }
        _sFEraserLineDashed = sfIcon(
            name = "Monochrome.SFEraserLineDashed",
            viewportWidth = 32.5789f,
            viewportHeight = 28.0715f
        ) {
            addSfPath("M22.0316 24.8023C22.0316 25.2711 21.6312 25.6617 21.1625 25.6617L15.0097 25.6617L16.7368 23.9332L21.1625 23.9332C21.6312 23.9332 22.0316 24.3238 22.0316 24.8023Z", fillAlpha = 0.85f)
            addSfPath("M30.6449 24.8023C30.6449 25.2711 30.2543 25.6617 29.7855 25.6617L24.6 25.6617C24.1312 25.6617 23.7308 25.2711 23.7308 24.8023C23.7308 24.3238 24.1312 23.9332 24.6 23.9332L29.7855 23.9332C30.2543 23.9332 30.6449 24.3238 30.6449 24.8023Z", fillAlpha = 0.85f)
            addSfPath("M2.91053 19.9879L7.88124 24.9488C9.64881 26.7066 11.6996 26.775 13.3695 25.0953L25.518 12.9371C26.5824 11.8922 26.5726 10.3687 25.4887 9.29453L18.5648 2.39023C17.4906 1.30625 15.9867 1.28672 14.9223 2.35117L2.77381 14.4996C1.10389 16.1793 1.16249 18.2301 2.91053 19.9879ZM4.13124 18.7672C3.16444 17.7809 3.11561 16.609 4.05311 15.6617L16.0844 3.63047C16.4652 3.24961 16.9926 3.24961 17.3539 3.6207L24.2582 10.5152C24.6195 10.8863 24.6098 11.4039 24.2484 11.775L12.1976 23.816C11.2699 24.7535 10.0883 24.7145 9.11171 23.7281ZM4.70741 13.7965L14.0824 23.1812L15.1664 22.0875L5.81092 12.732Z", fillAlpha = 0.85f)
        }
        return _sFEraserLineDashed!!
    }

private var _sFEraserLineDashed: ImageVector? = null
