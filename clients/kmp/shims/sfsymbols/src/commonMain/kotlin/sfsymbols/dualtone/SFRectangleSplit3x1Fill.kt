package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleSplit3x1Fill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFRectangleSplit3x1Fill: ImageVector
    get() {
        if (_sFRectangleSplit3x1Fill != null) {
            return _sFRectangleSplit3x1Fill!!
        }
        _sFRectangleSplit3x1Fill = sfIcon(
            name = "Dualtone.SFRectangleSplit3x1Fill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M8.7207 22.959L8.7207 0L10.4492 0L10.4492 22.959ZM19.1406 22.959L19.1406 0L20.8691 0L20.8691 22.959ZM3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.85f)
        }
        return _sFRectangleSplit3x1Fill!!
    }

private var _sFRectangleSplit3x1Fill: ImageVector? = null
