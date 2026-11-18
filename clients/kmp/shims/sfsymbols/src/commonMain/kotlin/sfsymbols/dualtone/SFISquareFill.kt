package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFISquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFISquareFill: ImageVector
    get() {
        if (_sFISquareFill != null) {
            return _sFISquareFill!!
        }
        _sFISquareFill = sfIcon(
            name = "Dualtone.SFISquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4746 17.3633C10.9082 17.3633 10.5957 16.9531 10.5957 16.3672L10.5957 6.40625C10.5957 5.81055 10.9082 5.40039 11.4746 5.40039C12.0605 5.40039 12.373 5.78125 12.373 6.40625L12.373 16.3672C12.373 16.9824 12.0605 17.3633 11.4746 17.3633Z", fillAlpha = 0.85f)
        }
        return _sFISquareFill!!
    }

private var _sFISquareFill: ImageVector? = null
