package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowClick (monochrome)
 * Viewport: 25.8008 x 25.7132
 */
public val SfSymbols.Monochrome.SFPointerArrowClick: ImageVector
    get() {
        if (_sFPointerArrowClick != null) {
            return _sFPointerArrowClick!!
        }
        _sFPointerArrowClick = sfIcon(
            name = "Monochrome.SFPointerArrowClick",
            viewportWidth = 25.8008f,
            viewportHeight = 25.7132f
        ) {
            addSfPath("M0 12.8517C0 17.6564 2.77344 21.8947 6.62109 23.965C7.75391 24.6095 8.66211 23.0959 7.43164 22.383C4.14062 20.5959 1.82617 16.9923 1.82617 12.8517C1.82617 6.8068 6.66992 1.95328 12.7051 1.95328C18.75 1.95328 23.6133 6.8068 23.6133 12.8517C23.6133 13.4767 24.1016 13.799 24.5703 13.799C25.0098 13.799 25.4395 13.467 25.4395 12.8517C25.4395 5.88883 19.668 0.127112 12.7051 0.127112C5.75195 0.127112 0 5.88883 0 12.8517Z", fillAlpha = 0.85f)
            addSfPath("M18.4082 25.6349C18.9258 25.4298 19.1699 24.8537 18.9453 24.3263L16.9434 19.6779L19.7461 19.6095C20.1758 19.5998 20.3906 19.1701 20.0879 18.838L13.2031 11.631C12.9102 11.3283 12.4512 11.5041 12.4414 11.9338L12.3926 22.0119C12.3828 22.4611 12.9102 22.6076 13.2031 22.3146L15.1562 20.3517L17.0801 25.1076C17.2852 25.6056 17.8906 25.8498 18.4082 25.6349Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowClick!!
    }

private var _sFPointerArrowClick: ImageVector? = null
