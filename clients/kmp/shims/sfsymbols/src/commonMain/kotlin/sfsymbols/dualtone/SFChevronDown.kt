package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDown (dualtone)
 * Viewport: 21.6895 x 12.959
 */
public val SfSymbols.Dualtone.SFChevronDown: ImageVector
    get() {
        if (_sFChevronDown != null) {
            return _sFChevronDown!!
        }
        _sFChevronDown = sfIcon(
            name = "Dualtone.SFChevronDown",
            viewportWidth = 21.6895f,
            viewportHeight = 12.959f
        ) {
            addSfPath("M10.6641 12.959C10.9473 12.959 11.2109 12.832 11.4062 12.6172L21.0352 2.58789C21.2207 2.40234 21.3281 2.16797 21.3281 1.89453C21.3281 1.34766 20.9082 0.927734 20.3516 0.927734C20.0977 0.927734 19.8438 1.02539 19.6582 1.20117L10.0684 11.1816L11.2695 11.1816L1.66016 1.20117C1.48438 1.02539 1.24023 0.927734 0.976562 0.927734C0.419922 0.927734 0 1.34766 0 1.89453C0 2.16797 0.117188 2.40234 0.292969 2.59766L9.92188 12.627C10.1367 12.832 10.3809 12.959 10.6641 12.959Z", fillAlpha = 0.85f)
        }
        return _sFChevronDown!!
    }

private var _sFChevronDown: ImageVector? = null
