package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronCompactUp (dualtone)
 * Viewport: 22.4219 x 6.06445
 */
public val SfSymbols.Dualtone.SFChevronCompactUp: ImageVector
    get() {
        if (_sFChevronCompactUp != null) {
            return _sFChevronCompactUp!!
        }
        _sFChevronCompactUp = sfIcon(
            name = "Dualtone.SFChevronCompactUp",
            viewportWidth = 22.4219f,
            viewportHeight = 6.06445f
        ) {
            addSfPath("M0.664062 3.89648C0.253906 4.0625 0 4.42383 0 4.79492C0 5.41992 0.458984 5.88867 1.07422 5.88867C1.38672 5.88867 1.80664 5.71289 2.07031 5.61523L11.7578 1.78711L10.293 1.78711L19.9805 5.61523C20.2539 5.71289 20.6738 5.88867 20.9863 5.88867C21.6016 5.88867 22.0605 5.41992 22.0605 4.79492C22.0605 4.42383 21.7969 4.0625 21.3867 3.89648L12.5684 0.419922C12.0898 0.224609 11.5039 0 11.0254 0C10.5566 0 9.98047 0.224609 9.48242 0.419922Z", fillAlpha = 0.85f)
        }
        return _sFChevronCompactUp!!
    }

private var _sFChevronCompactUp: ImageVector? = null
