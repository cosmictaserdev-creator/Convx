package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFPersonCropRectangleFill: ImageVector
    get() {
        if (_sFPersonCropRectangleFill != null) {
            return _sFPersonCropRectangleFill!!
        }
        _sFPersonCropRectangleFill = sfIcon(
            name = "Monochrome.SFPersonCropRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM6.24023 21.3379L23.3594 21.3379C22.5391 18.0957 19.0234 15.5859 14.8047 15.5859C10.5762 15.5859 7.06055 18.0957 6.24023 21.3379ZM10.5176 8.80859C10.5273 11.4844 12.4121 13.4766 14.8047 13.4961C17.1875 13.5156 19.082 11.4844 19.082 8.80859C19.082 6.28906 17.1875 4.21875 14.8047 4.21875C12.4121 4.21875 10.5078 6.28906 10.5176 8.80859Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropRectangleFill!!
    }

private var _sFPersonCropRectangleFill: ImageVector? = null
