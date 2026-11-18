package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFISquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFISquareFill: ImageVector
    get() {
        if (_sFISquareFill != null) {
            return _sFISquareFill!!
        }
        _sFISquareFill = sfIcon(
            name = "Monochrome.SFISquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.5957 6.40625L10.5957 16.3672C10.5957 16.9531 10.9082 17.3633 11.4746 17.3633C12.0605 17.3633 12.373 16.9824 12.373 16.3672L12.373 6.40625C12.373 5.78125 12.0605 5.40039 11.4746 5.40039C10.9082 5.40039 10.5957 5.81055 10.5957 6.40625Z", fillAlpha = 0.85f)
        }
        return _sFISquareFill!!
    }

private var _sFISquareFill: ImageVector? = null
