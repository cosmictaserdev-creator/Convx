package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeadlightFogFill (dualtone)
 * Viewport: 32.6758 x 21.9712
 */
public val SfSymbols.Dualtone.SFHeadlightFogFill: ImageVector
    get() {
        if (_sFHeadlightFogFill != null) {
            return _sFHeadlightFogFill!!
        }
        _sFHeadlightFogFill = sfIcon(
            name = "Dualtone.SFHeadlightFogFill",
            viewportWidth = 32.6758f,
            viewportHeight = 21.9712f
        ) {
            addSfPath("M0.869141 5.34594C0.400391 5.34594 0 5.74633 0 6.21508C0 6.69359 0.410156 7.08422 0.869141 7.08422L10.1953 7.09398C10.2637 6.51781 10.3516 5.92211 10.459 5.34594ZM0.869141 10.1116C0.400391 10.1116 0 10.5217 0 10.9905C0 11.4592 0.400391 11.8401 0.869141 11.8401L10.0098 11.8401C10 11.508 10 10.4534 10.0098 10.1116ZM0.869141 14.8772C0.400391 14.8772 0 15.2776 0 15.7463C0 16.2151 0.400391 16.6057 0.869141 16.6057L10.459 16.6155C10.3516 16.0295 10.2637 15.4534 10.1953 14.887ZM4.26758 20.4436C3.37891 21.3323 4.56055 22.5139 5.47852 21.6545C7.82227 19.3303 8.27148 15.1018 6.69922 11.1077L6.46484 10.5315C5.08789 7.00609 5.30273 3.41234 7.09961 1.50805C7.99805 0.629139 6.81641-0.562267 5.88867 0.297108C3.53516 2.63109 3.10547 6.84984 4.6875 10.844L4.91211 11.4202C6.29883 14.9553 6.07422 18.5393 4.26758 20.4436Z", fillAlpha = 0.425f)
            addSfPath("M11.5527 10.9807C11.5332 13.4709 11.7383 15.7659 12.3926 18.0803C13.125 20.5803 14.6191 21.4983 17.5488 21.4983L18.2031 21.4983C25.5566 21.4983 32.3145 16.6545 32.3145 10.9807C32.3145 5.30687 25.5566 0.443592 18.2031 0.443592L17.5488 0.443592C14.6289 0.443592 13.1348 1.37133 12.3926 3.87133C11.7383 6.18578 11.5332 8.4807 11.5527 10.9807Z", fillAlpha = 0.85f)
        }
        return _sFHeadlightFogFill!!
    }

private var _sFHeadlightFogFill: ImageVector? = null
