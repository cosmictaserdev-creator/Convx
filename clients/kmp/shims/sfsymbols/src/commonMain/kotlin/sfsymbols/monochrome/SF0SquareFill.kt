package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF0SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF0SquareFill: ImageVector
    get() {
        if (_sF0SquareFill != null) {
            return _sF0SquareFill!!
        }
        _sF0SquareFill = sfIcon(
            name = "Monochrome.SF0SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.04102 11.4746C7.04102 15.1074 8.82812 17.5586 11.4844 17.5586C14.1406 17.5586 15.9277 15.1074 15.9277 11.4746C15.9277 7.83203 14.1406 5.39062 11.4844 5.39062C8.82812 5.39062 7.04102 7.83203 7.04102 11.4746ZM14.209 11.4746C14.209 14.2383 13.1055 16.1035 11.4844 16.1035C9.86328 16.1035 8.75977 14.2383 8.75977 11.4746C8.75977 8.70117 9.86328 6.83594 11.4844 6.83594C13.1055 6.83594 14.209 8.70117 14.209 11.4746Z", fillAlpha = 0.85f)
        }
        return _sF0SquareFill!!
    }

private var _sF0SquareFill: ImageVector? = null
