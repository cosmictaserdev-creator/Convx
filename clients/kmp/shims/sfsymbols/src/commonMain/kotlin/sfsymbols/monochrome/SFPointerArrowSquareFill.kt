package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPointerArrowSquareFill: ImageVector
    get() {
        if (_sFPointerArrowSquareFill != null) {
            return _sFPointerArrowSquareFill!!
        }
        _sFPointerArrowSquareFill = sfIcon(
            name = "Monochrome.SFPointerArrowSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.90039 4.53125L7.85156 14.8535C7.8418 15.3125 8.37891 15.459 8.68164 15.166L10.6836 13.1543L12.7246 18.2031C12.9395 18.7109 13.5547 18.9648 14.0918 18.75C14.6289 18.5352 14.873 17.9297 14.6484 17.4023L12.5195 12.4609L15.3809 12.3926C15.8398 12.373 16.0547 11.9336 15.7324 11.6016L8.68164 4.21875C8.37891 3.90625 7.91016 4.08203 7.90039 4.53125Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowSquareFill!!
    }

private var _sFPointerArrowSquareFill: ImageVector? = null
