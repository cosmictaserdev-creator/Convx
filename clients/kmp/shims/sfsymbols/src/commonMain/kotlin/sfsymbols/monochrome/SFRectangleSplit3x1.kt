package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleSplit3x1 (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFRectangleSplit3x1: ImageVector
    get() {
        if (_sFRectangleSplit3x1 != null) {
            return _sFRectangleSplit3x1!!
        }
        _sFRectangleSplit3x1 = sfIcon(
            name = "Monochrome.SFRectangleSplit3x1",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M9.27734 21.8457L11.0156 21.8457L11.0156 1.11328L9.27734 1.11328ZM18.5742 21.8457L20.3027 21.8457L20.3027 1.11328L18.5742 1.11328ZM3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
        }
        return _sFRectangleSplit3x1!!
    }

private var _sFRectangleSplit3x1: ImageVector? = null
