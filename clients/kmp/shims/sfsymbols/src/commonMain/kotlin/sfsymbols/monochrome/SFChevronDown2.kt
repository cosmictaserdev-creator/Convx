package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDown2 (monochrome)
 * Viewport: 21.6895 x 23.3105
 */
public val SfSymbols.Monochrome.SFChevronDown2: ImageVector
    get() {
        if (_sFChevronDown2 != null) {
            return _sFChevronDown2!!
        }
        _sFChevronDown2 = sfIcon(
            name = "Monochrome.SFChevronDown2",
            viewportWidth = 21.6895f,
            viewportHeight = 23.3105f
        ) {
            addSfPath("M0.292969 12.9297L9.92188 22.959C10.1367 23.1738 10.3809 23.291 10.6641 23.291C10.9473 23.291 11.2012 23.1543 11.4062 22.959L21.0352 12.9297C21.2207 12.7344 21.3281 12.5098 21.3281 12.2363C21.3281 11.6895 20.9082 11.2598 20.3516 11.2598C20.0977 11.2598 19.8438 11.3672 19.6582 11.543L10.0684 21.5234L11.2695 21.5234L1.66016 11.543C1.48438 11.3672 1.24023 11.2598 0.976562 11.2598C0.419922 11.2598 0 11.6895 0 12.2363C0 12.5098 0.117188 12.7441 0.292969 12.9297Z", fillAlpha = 0.85f)
            addSfPath("M0.292969 1.66992L9.92188 11.6992C10.1367 11.9141 10.3809 12.0312 10.6641 12.0312C10.9473 12.0312 11.2109 11.9043 11.4062 11.6895L21.0352 1.66016C21.2207 1.47461 21.3281 1.25 21.3281 0.976562C21.3281 0.419922 20.9082 0 20.3516 0C20.0977 0 19.8438 0.107422 19.6582 0.273438L10.0684 10.2637L11.2695 10.2637L1.66016 0.273438C1.48438 0.107422 1.24023 0 0.976562 0C0.419922 0 0 0.419922 0 0.976562C0 1.25 0.117188 1.48438 0.292969 1.66992Z", fillAlpha = 0.85f)
        }
        return _sFChevronDown2!!
    }

private var _sFChevronDown2: ImageVector? = null
