package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGuaranisign (monochrome)
 * Viewport: 18.5645 x 28.0176
 */
public val SfSymbols.Monochrome.SFGuaranisign: ImageVector
    get() {
        if (_sFGuaranisign != null) {
            return _sFGuaranisign!!
        }
        _sFGuaranisign = sfIcon(
            name = "Monochrome.SFGuaranisign",
            viewportWidth = 18.5645f,
            viewportHeight = 28.0176f
        ) {
            addSfPath("M9.63867 25.1074C15.2539 25.1074 18.2031 21.6113 18.2031 16.2012L18.2031 14.8828C18.2031 14.1016 17.7246 13.7207 17.0898 13.7207L9.96094 13.7207L9.96094 15.5664L16.1133 15.5664L16.1133 16.6504C16.1133 20.4492 13.7402 23.1934 9.64844 23.1934C4.66797 23.1934 2.22656 18.9844 2.22656 13.877C2.22656 8.82812 4.69727 4.6875 9.63867 4.6875C12.5684 4.6875 15.2051 6.43555 15.8398 9.13086C16.0449 9.7168 16.2793 10.0195 16.9141 10.0195C17.4805 10.0195 17.8809 9.63867 17.8809 9.05273C17.8809 8.71094 17.793 8.31055 17.6855 7.97852C16.5234 4.59961 13.1641 2.76367 9.63867 2.76367C3.37891 2.76367 0 7.51953 0 13.877C0 20.2734 3.33008 25.1074 9.63867 25.1074ZM9.50195 28.0176C9.93164 28.0176 10.2246 27.7246 10.2246 27.2949L10.2246 0.722656C10.2246 0.302734 9.93164 0.00976562 9.50195 0.00976562C9.08203 0.00976562 8.78906 0.302734 8.78906 0.722656L8.78906 27.2949C8.78906 27.7246 9.08203 28.0176 9.50195 28.0176Z", fillAlpha = 0.85f)
        }
        return _sFGuaranisign!!
    }

private var _sFGuaranisign: ImageVector? = null
