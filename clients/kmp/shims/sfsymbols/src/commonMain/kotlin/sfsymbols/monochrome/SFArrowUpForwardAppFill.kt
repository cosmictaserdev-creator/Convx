package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpForwardAppFill (monochrome)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Monochrome.SFArrowUpForwardAppFill: ImageVector
    get() {
        if (_sFArrowUpForwardAppFill != null) {
            return _sFArrowUpForwardAppFill!!
        }
        _sFArrowUpForwardAppFill = sfIcon(
            name = "Monochrome.SFArrowUpForwardAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M21.1523 1.74805C22.3145 2.90039 22.9102 4.60938 22.9102 6.81641L22.9102 16.084C22.9102 18.291 22.3047 20.0098 21.1523 21.1523C20.0293 22.2754 18.3105 22.9004 16.0938 22.9004L6.81641 22.9004C4.59961 22.9004 2.89062 22.2852 1.75781 21.1523C0.595703 20 0 18.291 0 16.084L0 6.81641C0 4.60938 0.605469 2.89062 1.75781 1.74805C2.88086 0.625 4.59961 0 6.81641 0L16.0938 0C18.3105 0 20.0195 0.605469 21.1523 1.74805ZM9.16016 6.49414C8.63281 6.49414 8.29102 6.81641 8.29102 7.29492C8.29102 7.79297 8.64258 8.125 9.17969 8.125L11.9824 8.125L13.9084 7.92935L12.0215 9.69727L6.76758 14.9414C6.60156 15.1074 6.49414 15.3223 6.49414 15.5469C6.49414 16.0547 6.82617 16.3867 7.31445 16.3867C7.58789 16.3867 7.79297 16.2891 7.95898 16.123L13.1934 10.8789L14.9569 9.00518L14.7754 11.1328L14.7754 13.7207C14.7754 14.2578 15.0977 14.5898 15.5957 14.5898C16.084 14.5898 16.4062 14.2285 16.4062 13.7012L16.4062 7.40234C16.4062 6.75781 16.0547 6.49414 15.4883 6.49414Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpForwardAppFill!!
    }

private var _sFArrowUpForwardAppFill: ImageVector? = null
