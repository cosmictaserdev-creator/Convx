package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFProjective (dualtone)
 * Viewport: 21.6895 x 23.2129
 */
public val SfSymbols.Dualtone.SFProjective: ImageVector
    get() {
        if (_sFProjective != null) {
            return _sFProjective!!
        }
        _sFProjective = sfIcon(
            name = "Dualtone.SFProjective",
            viewportWidth = 21.6895f,
            viewportHeight = 23.2129f
        ) {
            addSfPath("M0.976562 13.9062C1.24023 13.9062 1.47461 13.8184 1.66016 13.6328L11.3477 3.55469L9.99023 3.55469L19.6582 13.6328C19.8438 13.8086 20.0879 13.9062 20.3516 13.9062C20.918 13.9062 21.3281 13.4863 21.3281 12.9297C21.3281 12.6562 21.2207 12.4414 21.0352 12.2461L11.4062 2.20703C11.2012 2.00195 10.957 1.875 10.6641 1.875C10.3809 1.875 10.1367 2.00195 9.92188 2.20703L0.292969 12.2363C0.107422 12.4414 0 12.6562 0 12.9297C0 13.4863 0.419922 13.9062 0.976562 13.9062ZM1.29883 1.91406L20 1.91406C20.5566 1.91406 20.9473 1.50391 20.9473 0.957031C20.9473 0.400391 20.5566 0 20 0L1.29883 0C0.761719 0 0.380859 0.400391 0.380859 0.957031C0.380859 1.50391 0.761719 1.91406 1.29883 1.91406Z", fillAlpha = 0.85f)
        }
        return _sFProjective!!
    }

private var _sFProjective: ImageVector? = null
