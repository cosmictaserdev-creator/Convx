package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardFrameFill (dualtone)
 * Viewport: 30.3809 x 18.9746
 */
public val SfSymbols.Dualtone.SFForwardFrameFill: ImageVector
    get() {
        if (_sFForwardFrameFill != null) {
            return _sFForwardFrameFill!!
        }
        _sFForwardFrameFill = sfIcon(
            name = "Dualtone.SFForwardFrameFill",
            viewportWidth = 30.3809f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M5.25391 18.8965L8.07617 18.8965C9.11133 18.8965 9.62891 18.3789 9.62891 17.334L9.62891 1.61133C9.62891 0.527344 9.11133 0.0488281 8.07617 0.0488281L5.25391 0.0488281C4.21875 0.0488281 3.69141 0.576172 3.69141 1.61133L3.69141 17.334C3.69141 18.3789 4.21875 18.8965 5.25391 18.8965ZM13.6133 17.3633C13.6133 18.457 14.248 18.9551 15 18.9551C15.3223 18.9551 15.6641 18.8574 15.9863 18.6719L29.2188 10.9082C30.0488 10.4297 30.3809 10.0586 30.3809 9.48242C30.3809 8.90625 30.0488 8.52539 29.2188 8.04688L15.9863 0.283203C15.6641 0.0976562 15.3223 0 15 0C14.248 0 13.6133 0.507812 13.6133 1.60156Z", fillAlpha = 0.85f)
        }
        return _sFForwardFrameFill!!
    }

private var _sFForwardFrameFill: ImageVector? = null
