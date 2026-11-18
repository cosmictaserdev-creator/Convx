package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFMinusSquareFill: ImageVector
    get() {
        if (_sFMinusSquareFill != null) {
            return _sFMinusSquareFill!!
        }
        _sFMinusSquareFill = sfIcon(
            name = "Dualtone.SFMinusSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.21094 12.4219C5.5957 12.4219 5.15625 12.0801 5.15625 11.5039C5.15625 10.9082 5.57617 10.5566 6.21094 10.5566L16.7676 10.5566C17.4121 10.5566 17.8125 10.9082 17.8125 11.5039C17.8125 12.0801 17.3828 12.4219 16.7676 12.4219Z", fillAlpha = 0.85f)
        }
        return _sFMinusSquareFill!!
    }

private var _sFMinusSquareFill: ImageVector? = null
