package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronCompactUpChevronCompactDown (dualtone)
 * Viewport: 22.4219 x 16.5723
 */
public val SfSymbols.Dualtone.SFChevronCompactUpChevronCompactDown: ImageVector
    get() {
        if (_sFChevronCompactUpChevronCompactDown != null) {
            return _sFChevronCompactUpChevronCompactDown!!
        }
        _sFChevronCompactUpChevronCompactDown = sfIcon(
            name = "Dualtone.SFChevronCompactUpChevronCompactDown",
            viewportWidth = 22.4219f,
            viewportHeight = 16.5723f
        ) {
            addSfPath("M0.664062 12.6367L9.48242 16.1328C9.98047 16.3281 10.5664 16.5527 11.0254 16.5527C11.4941 16.5527 12.0898 16.3281 12.5684 16.1328L21.3867 12.6367C21.8066 12.4805 22.0605 12.1094 22.0605 11.7383C22.0605 11.1133 21.6016 10.6445 20.9863 10.6445C20.6738 10.6445 20.2539 10.8203 19.9805 10.918L10.293 14.7656L11.7578 14.7656L2.07031 10.918C1.79688 10.8203 1.38672 10.6445 1.07422 10.6445C0.458984 10.6445 0 11.1133 0 11.7383C0 12.1094 0.253906 12.4805 0.664062 12.6367ZM0.664062 3.91602C0.253906 4.08203 0 4.44336 0 4.82422C0 5.43945 0.458984 5.9082 1.07422 5.9082C1.38672 5.9082 1.79688 5.73242 2.07031 5.63477L11.7578 1.79688L10.293 1.79688L19.9805 5.63477C20.2539 5.73242 20.6738 5.9082 20.9863 5.9082C21.6016 5.9082 22.0605 5.43945 22.0605 4.82422C22.0605 4.44336 21.8066 4.08203 21.3867 3.91602L12.5684 0.419922C12.0898 0.224609 11.4941 0 11.0254 0C10.5664 0 9.98047 0.224609 9.48242 0.419922Z", fillAlpha = 0.85f)
        }
        return _sFChevronCompactUpChevronCompactDown!!
    }

private var _sFChevronCompactUpChevronCompactDown: ImageVector? = null
