package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPersonCropSquareFill: ImageVector
    get() {
        if (_sFPersonCropSquareFill != null) {
            return _sFPersonCropSquareFill!!
        }
        _sFPersonCropSquareFill = sfIcon(
            name = "Monochrome.SFPersonCropSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM2.92969 21.3477L20.0488 21.3477C19.2285 18.1152 15.7129 15.6055 11.4941 15.6055C7.26562 15.6055 3.75977 18.1152 2.92969 21.3477ZM7.20703 8.82812C7.2168 11.5039 9.0918 13.4961 11.4844 13.5156C13.877 13.5352 15.7617 11.5039 15.7617 8.82812C15.7617 6.30859 13.877 4.22852 11.4844 4.22852C9.0918 4.22852 7.19727 6.30859 7.20703 8.82812Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropSquareFill!!
    }

private var _sFPersonCropSquareFill: ImageVector? = null
