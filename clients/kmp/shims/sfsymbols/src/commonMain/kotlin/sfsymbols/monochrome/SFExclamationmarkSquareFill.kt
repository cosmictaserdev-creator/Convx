package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFExclamationmarkSquareFill: ImageVector
    get() {
        if (_sFExclamationmarkSquareFill != null) {
            return _sFExclamationmarkSquareFill!!
        }
        _sFExclamationmarkSquareFill = sfIcon(
            name = "Monochrome.SFExclamationmarkSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.2051 16.6406C10.2051 17.3145 10.8008 17.8711 11.4844 17.8711C12.168 17.8711 12.7637 17.3242 12.7637 16.6406C12.7637 15.9473 12.1777 15.4004 11.4844 15.4004C10.791 15.4004 10.2051 15.957 10.2051 16.6406ZM10.4492 6.00586L10.5957 12.8809C10.6055 13.4668 10.9277 13.7988 11.4844 13.7988C12.0312 13.7988 12.3438 13.4766 12.3535 12.8809L12.5195 6.01562C12.5293 5.42969 12.0801 5 11.4746 5C10.8594 5 10.4395 5.41992 10.4492 6.00586Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkSquareFill!!
    }

private var _sFExclamationmarkSquareFill: ImageVector? = null
