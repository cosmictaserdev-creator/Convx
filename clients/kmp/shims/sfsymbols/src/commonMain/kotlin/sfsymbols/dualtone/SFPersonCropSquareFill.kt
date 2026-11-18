package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPersonCropSquareFill: ImageVector
    get() {
        if (_sFPersonCropSquareFill != null) {
            return _sFPersonCropSquareFill!!
        }
        _sFPersonCropSquareFill = sfIcon(
            name = "Dualtone.SFPersonCropSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M2.92969 21.3477C3.75977 18.1152 7.26562 15.6055 11.4941 15.6055C15.7129 15.6055 19.2285 18.1152 20.0488 21.3477ZM11.4844 13.5156C9.0918 13.4961 7.2168 11.5039 7.20703 8.82812C7.19727 6.30859 9.0918 4.22852 11.4844 4.22852C13.877 4.22852 15.7617 6.30859 15.7617 8.82812C15.7617 11.5039 13.877 13.5352 11.4844 13.5156Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropSquareFill!!
    }

private var _sFPersonCropSquareFill: ImageVector? = null
