package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFControl (dualtone)
 * Viewport: 21.6895 x 23.6035
 */
public val SfSymbols.Dualtone.SFControl: ImageVector
    get() {
        if (_sFControl != null) {
            return _sFControl!!
        }
        _sFControl = sfIcon(
            name = "Dualtone.SFControl",
            viewportWidth = 21.6895f,
            viewportHeight = 23.6035f
        ) {
            addSfPath("M0.292969 10.3613C0.117188 10.5469 0 10.7812 0 11.0547C0 11.6113 0.419922 12.0312 0.976562 12.0312C1.24023 12.0312 1.48438 11.9238 1.66016 11.748L11.2695 1.76758L10.0684 1.76758L19.6582 11.748C19.8438 11.9238 20.0977 12.0312 20.3516 12.0312C20.9082 12.0312 21.3281 11.6113 21.3281 11.0547C21.3281 10.7812 21.2207 10.5566 21.0352 10.3711L11.4062 0.341797C11.2109 0.126953 10.9473 0 10.6641 0C10.3809 0 10.1367 0.117188 9.92188 0.332031Z", fillAlpha = 0.85f)
        }
        return _sFControl!!
    }

private var _sFControl: ImageVector? = null
