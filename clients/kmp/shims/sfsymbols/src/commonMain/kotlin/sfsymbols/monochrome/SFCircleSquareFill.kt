package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFCircleSquareFill: ImageVector
    get() {
        if (_sFCircleSquareFill != null) {
            return _sFCircleSquareFill!!
        }
        _sFCircleSquareFill = sfIcon(
            name = "Monochrome.SFCircleSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM4.58008 11.4746C4.58008 15.2832 7.66602 18.3691 11.4746 18.3691C15.293 18.3691 18.3789 15.2832 18.3789 11.4746C18.3789 7.65625 15.293 4.58008 11.4746 4.58008C7.66602 4.58008 4.58008 7.65625 4.58008 11.4746Z", fillAlpha = 0.85f)
        }
        return _sFCircleSquareFill!!
    }

private var _sFCircleSquareFill: ImageVector? = null
