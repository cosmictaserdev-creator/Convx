package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFPersonCropRectangleFill: ImageVector
    get() {
        if (_sFPersonCropRectangleFill != null) {
            return _sFPersonCropRectangleFill!!
        }
        _sFPersonCropRectangleFill = sfIcon(
            name = "Dualtone.SFPersonCropRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.24023 21.3379C7.06055 18.0957 10.5762 15.5859 14.8047 15.5859C19.0234 15.5859 22.5391 18.0957 23.3594 21.3379ZM14.8047 13.4961C12.4121 13.4766 10.5273 11.4844 10.5176 8.80859C10.5078 6.28906 12.4121 4.21875 14.8047 4.21875C17.1875 4.21875 19.082 6.28906 19.082 8.80859C19.082 11.4844 17.1875 13.5156 14.8047 13.4961Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropRectangleFill!!
    }

private var _sFPersonCropRectangleFill: ImageVector? = null
