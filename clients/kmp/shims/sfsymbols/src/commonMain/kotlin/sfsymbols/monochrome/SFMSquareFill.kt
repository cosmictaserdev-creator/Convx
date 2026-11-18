package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFMSquareFill: ImageVector
    get() {
        if (_sFMSquareFill != null) {
            return _sFMSquareFill!!
        }
        _sFMSquareFill = sfIcon(
            name = "Monochrome.SFMSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM15.0684 6.16211L11.5234 15.0684L11.4551 15.0684L7.90039 6.16211C7.68555 5.6543 7.32422 5.40039 6.76758 5.40039C6.13281 5.40039 5.69336 5.83008 5.69336 6.45508L5.69336 16.5234C5.69336 17.0801 5.99609 17.4219 6.49414 17.4219C7.00195 17.4219 7.31445 17.0898 7.31445 16.5234L7.31445 8.51562L7.41211 8.51562L10.5859 16.4844C10.7617 16.8945 11.0449 17.0996 11.4648 17.0996C11.9043 17.0996 12.1973 16.8945 12.373 16.4844L15.5469 8.51562L15.6543 8.51562L15.6543 16.5234C15.6543 17.0801 15.957 17.4219 16.4453 17.4219C16.9629 17.4219 17.2754 17.0898 17.2754 16.5234L17.2754 6.45508C17.2754 5.83008 16.8262 5.40039 16.2012 5.40039C15.6445 5.40039 15.2734 5.6543 15.0684 6.16211Z", fillAlpha = 0.85f)
        }
        return _sFMSquareFill!!
    }

private var _sFMSquareFill: ImageVector? = null
