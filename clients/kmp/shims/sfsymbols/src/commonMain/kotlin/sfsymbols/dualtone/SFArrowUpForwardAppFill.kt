package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpForwardAppFill (dualtone)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Dualtone.SFArrowUpForwardAppFill: ImageVector
    get() {
        if (_sFArrowUpForwardAppFill != null) {
            return _sFArrowUpForwardAppFill!!
        }
        _sFArrowUpForwardAppFill = sfIcon(
            name = "Dualtone.SFArrowUpForwardAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M6.81641 22.9004L16.0938 22.9004C18.3105 22.9004 20.0293 22.2754 21.1523 21.1523C22.3047 20.0098 22.9102 18.291 22.9102 16.084L22.9102 6.81641C22.9102 4.60938 22.3145 2.90039 21.1523 1.74805C20.0195 0.605469 18.3105 0 16.0938 0L6.81641 0C4.59961 0 2.88086 0.625 1.75781 1.74805C0.605469 2.89062 0 4.60938 0 6.81641L0 16.084C0 18.291 0.595703 20 1.75781 21.1523C2.89062 22.2852 4.59961 22.9004 6.81641 22.9004Z", fillAlpha = 0.2125f)
            addSfPath("M14.1895 7.66602L12.0215 9.69727L6.76758 14.9414C6.60156 15.1074 6.49414 15.3223 6.49414 15.5469C6.49414 16.0547 6.82617 16.3867 7.31445 16.3867C7.58789 16.3867 7.79297 16.2891 7.95898 16.123L13.1934 10.8789L15.2246 8.7207C16.0547 7.8418 15.0293 6.875 14.1895 7.66602ZM14.7754 11.1328L14.7754 13.7207C14.7754 14.2578 15.0977 14.5898 15.5957 14.5898C16.084 14.5898 16.4062 14.2285 16.4062 13.7012L16.4062 7.40234C16.4062 6.75781 16.0547 6.49414 15.4883 6.49414L9.16016 6.49414C8.63281 6.49414 8.29102 6.81641 8.29102 7.29492C8.29102 7.79297 8.64258 8.125 9.17969 8.125L11.9824 8.125L15.0586 7.8125Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpForwardAppFill!!
    }

private var _sFArrowUpForwardAppFill: ImageVector? = null
