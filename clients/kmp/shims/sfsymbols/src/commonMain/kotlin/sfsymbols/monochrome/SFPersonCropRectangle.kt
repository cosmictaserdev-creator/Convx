package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFPersonCropRectangle: ImageVector
    get() {
        if (_sFPersonCropRectangle != null) {
            return _sFPersonCropRectangle!!
        }
        _sFPersonCropRectangle = sfIcon(
            name = "Monochrome.SFPersonCropRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305ZM6.10352 21.8262L23.4961 21.8262C22.8516 18.3203 19.2188 15.6055 14.8047 15.6055C10.3809 15.6055 6.74805 18.3203 6.10352 21.8262ZM14.8047 13.5156C17.1875 13.5352 19.082 11.5039 19.082 8.82812C19.082 6.30859 17.1875 4.22852 14.8047 4.22852C12.4121 4.22852 10.5078 6.30859 10.5176 8.82812C10.5273 11.5039 12.4121 13.4961 14.8047 13.5156Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropRectangle!!
    }

private var _sFPersonCropRectangle: ImageVector? = null
