package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPersonCropSquare: ImageVector
    get() {
        if (_sFPersonCropSquare != null) {
            return _sFPersonCropSquare!!
        }
        _sFPersonCropSquare = sfIcon(
            name = "Monochrome.SFPersonCropSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305ZM2.79297 21.8262L20.1855 21.8262C19.541 18.3203 15.9082 15.6055 11.4941 15.6055C7.07031 15.6055 3.4375 18.3203 2.79297 21.8262ZM11.4844 13.5156C13.877 13.5352 15.7617 11.5039 15.7617 8.82812C15.7617 6.30859 13.877 4.22852 11.4844 4.22852C9.0918 4.22852 7.19727 6.30859 7.20703 8.82812C7.2168 11.5039 9.0918 13.4961 11.4844 13.5156Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropSquare!!
    }

private var _sFPersonCropSquare: ImageVector? = null
