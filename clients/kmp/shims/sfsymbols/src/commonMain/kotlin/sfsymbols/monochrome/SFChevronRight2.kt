package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronRight2 (monochrome)
 * Viewport: 23.6523 x 21.3574
 */
public val SfSymbols.Monochrome.SFChevronRight2: ImageVector
    get() {
        if (_sFChevronRight2 != null) {
            return _sFChevronRight2!!
        }
        _sFChevronRight2 = sfIcon(
            name = "Monochrome.SFChevronRight2",
            viewportWidth = 23.6523f,
            viewportHeight = 21.3574f
        ) {
            addSfPath("M12.2363 21.3281C12.5098 21.3281 12.7441 21.2207 12.9297 21.0352L22.959 11.4062C23.1738 11.2012 23.291 10.957 23.291 10.6738C23.291 10.3906 23.1641 10.1172 22.9492 9.92188L12.9199 0.292969C12.7344 0.107422 12.5098 0 12.2363 0C11.6797 0 11.2598 0.419922 11.2598 0.976562C11.2598 1.23047 11.3672 1.48438 11.5332 1.66992L21.5234 11.2598L21.5234 10.0684L11.5332 19.668C11.3672 19.8438 11.2598 20.0879 11.2598 20.3613C11.2598 20.9082 11.6797 21.3281 12.2363 21.3281Z", fillAlpha = 0.85f)
            addSfPath("M0.976562 21.3281C1.25 21.3281 1.48438 21.2207 1.66992 21.0352L11.6992 11.4062C11.9141 11.2012 12.0312 10.957 12.0312 10.6738C12.0312 10.3906 11.9043 10.1172 11.6895 9.92188L1.66016 0.292969C1.47461 0.107422 1.25 0 0.976562 0C0.419922 0 0 0.419922 0 0.976562C0 1.23047 0.107422 1.48438 0.273438 1.66992L10.2637 11.2598L10.2637 10.0684L0.273438 19.668C0.107422 19.8438 0 20.0879 0 20.3613C0 20.9082 0.419922 21.3281 0.976562 21.3281Z", fillAlpha = 0.85f)
        }
        return _sFChevronRight2!!
    }

private var _sFChevronRight2: ImageVector? = null
