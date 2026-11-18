package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFCircleSquareFill: ImageVector
    get() {
        if (_sFCircleSquareFill != null) {
            return _sFCircleSquareFill!!
        }
        _sFCircleSquareFill = sfIcon(
            name = "Dualtone.SFCircleSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4746 18.3691C7.66602 18.3691 4.58008 15.2832 4.58008 11.4746C4.58008 7.65625 7.66602 4.58008 11.4746 4.58008C15.293 4.58008 18.3789 7.65625 18.3789 11.4746C18.3789 15.2832 15.293 18.3691 11.4746 18.3691Z", fillAlpha = 0.85f)
        }
        return _sFCircleSquareFill!!
    }

private var _sFCircleSquareFill: ImageVector? = null
