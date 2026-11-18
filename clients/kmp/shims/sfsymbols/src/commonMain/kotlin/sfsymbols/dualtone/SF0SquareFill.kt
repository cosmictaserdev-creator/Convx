package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF0SquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SF0SquareFill: ImageVector
    get() {
        if (_sF0SquareFill != null) {
            return _sF0SquareFill!!
        }
        _sF0SquareFill = sfIcon(
            name = "Dualtone.SF0SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4844 17.5586C8.82812 17.5586 7.04102 15.1074 7.04102 11.4746C7.04102 7.83203 8.82812 5.39062 11.4844 5.39062C14.1406 5.39062 15.9277 7.83203 15.9277 11.4746C15.9277 15.1074 14.1406 17.5586 11.4844 17.5586ZM11.4844 16.1035C13.1055 16.1035 14.209 14.2383 14.209 11.4746C14.209 8.70117 13.1055 6.83594 11.4844 6.83594C9.86328 6.83594 8.75977 8.70117 8.75977 11.4746C8.75977 14.2383 9.86328 16.1035 11.4844 16.1035Z", fillAlpha = 0.85f)
        }
        return _sF0SquareFill!!
    }

private var _sF0SquareFill: ImageVector? = null
