package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTagFill (dualtone)
 * Viewport: 28.2935 x 29.3042
 */
public val SfSymbols.Dualtone.SFTagFill: ImageVector
    get() {
        if (_sFTagFill != null) {
            return _sFTagFill!!
        }
        _sFTagFill = sfIcon(
            name = "Dualtone.SFTagFill",
            viewportWidth = 28.2935f,
            viewportHeight = 29.3042f
        ) {
            addSfPath("M14.6399 27.9871L26.9739 15.6238C27.8626 14.7351 27.9309 14.2566 27.9309 12.9285L27.9309 8.13355C27.9309 6.86402 27.6672 6.48316 26.7102 5.53589L23.7805 2.6062C22.8333 1.64917 22.4524 1.3855 21.1829 1.3855L16.3879 1.3855C15.0598 1.3855 14.5813 1.45386 13.6926 2.34253L1.30982 14.6765C-0.428464 16.4148-0.447995 18.2312 1.31958 19.989L9.3274 27.9773C11.1047 29.7546 12.9016 29.7351 14.6399 27.9871ZM19.093 11.1804C18.1848 11.1804 17.4915 10.4773 17.4915 9.57886C17.4915 8.68042 18.1848 7.9773 19.093 7.9773C20.011 7.9773 20.7043 8.68042 20.7043 9.57886C20.7043 10.4773 20.011 11.1804 19.093 11.1804Z", fillAlpha = 0.85f)
        }
        return _sFTagFill!!
    }

private var _sFTagFill: ImageVector? = null
