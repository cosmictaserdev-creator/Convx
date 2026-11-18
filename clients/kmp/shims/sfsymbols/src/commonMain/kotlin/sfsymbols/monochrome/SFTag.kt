package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTag (monochrome)
 * Viewport: 28.2983 x 29.3042
 */
public val SfSymbols.Monochrome.SFTag: ImageVector
    get() {
        if (_sFTag != null) {
            return _sFTag!!
        }
        _sFTag = sfIcon(
            name = "Monochrome.SFTag",
            viewportWidth = 28.2983f,
            viewportHeight = 29.3042f
        ) {
            addSfPath("M14.6375 27.9871L26.9714 15.6238C27.8699 14.7351 27.9382 14.2566 27.9382 12.9285L27.9382 8.13355C27.9382 6.86402 27.6648 6.48316 26.7078 5.53589L23.7878 2.6062C22.8308 1.64917 22.45 1.3855 21.1804 1.3855L16.3953 1.3855C15.0574 1.3855 14.5886 1.45386 13.6902 2.34253L1.31714 14.6765C-0.430906 16.4148-0.450437 18.2312 1.32691 19.989L9.32495 27.9773C11.1023 29.7546 12.8992 29.7351 14.6375 27.9871ZM13.4656 26.698C12.4988 27.6746 11.4734 27.6843 10.4773 26.6882L2.61597 18.8269C1.61988 17.8308 1.63941 16.7957 2.6062 15.8386L14.9109 3.56323C15.2039 3.28003 15.5066 3.11402 15.9558 3.11402L21.2683 3.11402C21.6882 3.11402 21.991 3.27027 22.2937 3.56323L25.7507 7.02027C26.0535 7.323 26.2 7.62573 26.2 8.05542L26.2 13.3582C26.2 13.8171 26.0437 14.1199 25.7507 14.4031ZM19.7449 11.1804C20.6531 11.1804 21.3464 10.4773 21.3464 9.57886C21.3464 8.68042 20.6531 7.9773 19.7449 7.9773C18.8269 7.9773 18.1335 8.68042 18.1335 9.57886C18.1335 10.4773 18.8269 11.1804 19.7449 11.1804Z", fillAlpha = 0.85f)
        }
        return _sFTag!!
    }

private var _sFTag: ImageVector? = null
