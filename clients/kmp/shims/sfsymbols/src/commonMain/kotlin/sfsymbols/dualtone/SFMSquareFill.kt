package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFMSquareFill: ImageVector
    get() {
        if (_sFMSquareFill != null) {
            return _sFMSquareFill!!
        }
        _sFMSquareFill = sfIcon(
            name = "Dualtone.SFMSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.49414 17.4219C5.99609 17.4219 5.69336 17.0801 5.69336 16.5234L5.69336 6.45508C5.69336 5.83008 6.13281 5.40039 6.76758 5.40039C7.32422 5.40039 7.68555 5.6543 7.90039 6.16211L11.4551 15.0684L11.5234 15.0684L15.0684 6.16211C15.2734 5.6543 15.6445 5.40039 16.2012 5.40039C16.8262 5.40039 17.2754 5.83008 17.2754 6.45508L17.2754 16.5234C17.2754 17.0898 16.9629 17.4219 16.4453 17.4219C15.957 17.4219 15.6543 17.0801 15.6543 16.5234L15.6543 8.51562L15.5469 8.51562L12.373 16.4844C12.1973 16.8945 11.9043 17.0996 11.4648 17.0996C11.0449 17.0996 10.7617 16.8945 10.5859 16.4844L7.41211 8.51562L7.31445 8.51562L7.31445 16.5234C7.31445 17.0898 7.00195 17.4219 6.49414 17.4219Z", fillAlpha = 0.85f)
        }
        return _sFMSquareFill!!
    }

private var _sFMSquareFill: ImageVector? = null
