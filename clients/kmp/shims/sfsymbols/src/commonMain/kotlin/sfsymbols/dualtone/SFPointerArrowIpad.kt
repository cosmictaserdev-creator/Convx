package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowIpad (dualtone)
 * Viewport: 16.084 x 21.3267
 */
public val SfSymbols.Dualtone.SFPointerArrowIpad: ImageVector
    get() {
        if (_sFPointerArrowIpad != null) {
            return _sFPointerArrowIpad!!
        }
        _sFPointerArrowIpad = sfIcon(
            name = "Dualtone.SFPointerArrowIpad",
            viewportWidth = 16.084f,
            viewportHeight = 21.3267f
        ) {
            addSfPath("M7.30469 15.5608L14.0723 15.5608C15.6152 15.5608 16.2109 14.1546 15.2832 13.2171L2.58789 0.52174C1.57227-0.493885 0 0.0432249 0 1.5276L0 19.5452C0 20.8733 1.41602 21.4299 2.50977 20.346Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowIpad!!
    }

private var _sFPointerArrowIpad: ImageVector? = null
