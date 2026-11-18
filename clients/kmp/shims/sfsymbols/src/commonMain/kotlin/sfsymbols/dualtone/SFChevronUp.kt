package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUp (dualtone)
 * Viewport: 21.6895 x 12.8613
 */
public val SfSymbols.Dualtone.SFChevronUp: ImageVector
    get() {
        if (_sFChevronUp != null) {
            return _sFChevronUp!!
        }
        _sFChevronUp = sfIcon(
            name = "Dualtone.SFChevronUp",
            viewportWidth = 21.6895f,
            viewportHeight = 12.8613f
        ) {
            addSfPath("M0.292969 10.3613C0.117188 10.5371 0 10.7812 0 11.0547C0 11.6113 0.419922 12.0312 0.976562 12.0312C1.24023 12.0312 1.49414 11.9336 1.66016 11.748L11.2695 1.76758L10.0684 1.76758L19.6582 11.748C19.834 11.9336 20.0977 12.0312 20.3516 12.0312C20.9082 12.0312 21.3281 11.6113 21.3281 11.0547C21.3281 10.7812 21.2207 10.5469 21.0352 10.3613L11.4062 0.332031C11.2109 0.126953 10.9473 0 10.6641 0C10.3809 0 10.127 0.117188 9.92188 0.332031Z", fillAlpha = 0.85f)
        }
        return _sFChevronUp!!
    }

private var _sFChevronUp: ImageVector? = null
