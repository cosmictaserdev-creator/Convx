package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleSplit1x2Fill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFRectangleSplit1x2Fill: ImageVector
    get() {
        if (_sFRectangleSplit1x2Fill != null) {
            return _sFRectangleSplit1x2Fill!!
        }
        _sFRectangleSplit1x2Fill = sfIcon(
            name = "Dualtone.SFRectangleSplit1x2Fill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M0 10.6152L29.5898 10.6152L29.5898 12.3438L0 12.3438ZM3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.85f)
        }
        return _sFRectangleSplit1x2Fill!!
    }

private var _sFRectangleSplit1x2Fill: ImageVector? = null
