package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencil (monochrome)
 * Viewport: 20.3949 x 19.9823
 */
public val SfSymbols.Monochrome.SFPencil: ImageVector
    get() {
        if (_sFPencil != null) {
            return _sFPencil!!
        }
        _sFPencil = sfIcon(
            name = "Monochrome.SFPencil",
            viewportWidth = 20.3949f,
            viewportHeight = 19.9823f
        ) {
            addSfPath("M3.24919 18.8046L17.3312 4.74211L15.3293 2.73039L1.24723 16.7929L0.0265306 19.4882C-0.0906569 19.7616 0.202312 20.0741 0.475749 19.957ZM18.3761 3.72649L19.5578 2.55461C20.1632 1.94914 20.1925 1.31438 19.6554 0.777268L19.3039 0.425706C18.7765-0.101638 18.132-0.0528096 17.5363 0.533128L16.3449 1.705Z", fillAlpha = 0.85f)
        }
        return _sFPencil!!
    }

private var _sFPencil: ImageVector? = null
