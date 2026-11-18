package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUp2 (dualtone)
 * Viewport: 21.6895 x 23.3105
 */
public val SfSymbols.Dualtone.SFChevronUp2: ImageVector
    get() {
        if (_sFChevronUp2 != null) {
            return _sFChevronUp2!!
        }
        _sFChevronUp2 = sfIcon(
            name = "Dualtone.SFChevronUp2",
            viewportWidth = 21.6895f,
            viewportHeight = 23.3105f
        ) {
            addSfPath("M0.292969 21.6211C0.117188 21.8164 0 22.0508 0 22.3242C0 22.8711 0.419922 23.291 0.976562 23.291C1.24023 23.291 1.48438 23.1934 1.66016 23.0176L11.2695 13.0371L10.0684 13.0371L19.6582 23.0176C19.8438 23.1934 20.0977 23.291 20.3516 23.291C20.9082 23.291 21.3281 22.8711 21.3281 22.3242C21.3281 22.0508 21.2207 21.8164 21.0352 21.6309L11.4062 11.6016C11.2109 11.3867 10.9473 11.2598 10.6641 11.2598C10.3809 11.2598 10.1367 11.3867 9.92188 11.5918Z", fillAlpha = 0.85f)
            addSfPath("M0.292969 10.3613C0.117188 10.5469 0 10.7812 0 11.0547C0 11.6113 0.419922 12.0312 0.976562 12.0312C1.24023 12.0312 1.48438 11.9238 1.66016 11.748L11.2695 1.76758L10.0684 1.76758L19.6582 11.748C19.8438 11.9238 20.0977 12.0312 20.3516 12.0312C20.9082 12.0312 21.3281 11.6113 21.3281 11.0547C21.3281 10.7812 21.2207 10.5566 21.0352 10.3711L11.4062 0.341797C11.2012 0.136719 10.9473 0 10.6641 0C10.3809 0 10.1367 0.117188 9.92188 0.332031Z", fillAlpha = 0.85f)
        }
        return _sFChevronUp2!!
    }

private var _sFChevronUp2: ImageVector? = null
