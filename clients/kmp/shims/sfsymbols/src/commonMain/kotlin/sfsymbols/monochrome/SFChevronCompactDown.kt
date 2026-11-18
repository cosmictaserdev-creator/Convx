package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronCompactDown (monochrome)
 * Viewport: 22.4219 x 6.47461
 */
public val SfSymbols.Monochrome.SFChevronCompactDown: ImageVector
    get() {
        if (_sFChevronCompactDown != null) {
            return _sFChevronCompactDown!!
        }
        _sFChevronCompactDown = sfIcon(
            name = "Monochrome.SFChevronCompactDown",
            viewportWidth = 22.4219f,
            viewportHeight = 6.47461f
        ) {
            addSfPath("M0.664062 1.99219L9.48242 5.46875C9.98047 5.66406 10.5566 5.89844 11.0254 5.89844C11.5039 5.89844 12.0898 5.66406 12.5684 5.46875L21.3867 1.99219C21.7969 1.83594 22.0605 1.47461 22.0605 1.09375C22.0605 0.478516 21.6016 0 20.9863 0C20.6738 0 20.2539 0.175781 19.9805 0.283203L10.293 4.11133L11.7578 4.11133L2.07031 0.283203C1.80664 0.175781 1.38672 0 1.07422 0C0.458984 0 0 0.478516 0 1.09375C0 1.47461 0.253906 1.83594 0.664062 1.99219Z", fillAlpha = 0.85f)
        }
        return _sFChevronCompactDown!!
    }

private var _sFChevronCompactDown: ImageVector? = null
