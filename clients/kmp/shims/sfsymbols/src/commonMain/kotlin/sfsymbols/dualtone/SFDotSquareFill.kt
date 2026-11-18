package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDotSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFDotSquareFill: ImageVector
    get() {
        if (_sFDotSquareFill != null) {
            return _sFDotSquareFill!!
        }
        _sFDotSquareFill = sfIcon(
            name = "Dualtone.SFDotSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4844 15.5078C9.24805 15.5078 7.45117 13.7012 7.45117 11.4746C7.45117 9.23828 9.24805 7.43164 11.4844 7.43164C13.7207 7.43164 15.5273 9.23828 15.5273 11.4746C15.5273 13.7012 13.7207 15.5078 11.4844 15.5078Z", fillAlpha = 0.85f)
        }
        return _sFDotSquareFill!!
    }

private var _sFDotSquareFill: ImageVector? = null
