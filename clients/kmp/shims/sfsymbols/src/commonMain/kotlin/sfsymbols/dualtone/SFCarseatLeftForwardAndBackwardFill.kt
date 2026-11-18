package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCarseatLeftForwardAndBackwardFill (dualtone)
 * Viewport: 28.0948 x 35.8691
 */
public val SfSymbols.Dualtone.SFCarseatLeftForwardAndBackwardFill: ImageVector
    get() {
        if (_sFCarseatLeftForwardAndBackwardFill != null) {
            return _sFCarseatLeftForwardAndBackwardFill!!
        }
        _sFCarseatLeftForwardAndBackwardFill = sfIcon(
            name = "Dualtone.SFCarseatLeftForwardAndBackwardFill",
            viewportWidth = 28.0948f,
            viewportHeight = 35.8691f
        ) {
            addSfPath("M0.194886 21.084C1.24957 25.0781 2.45074 26.2891 4.95074 26.2891L19.355 26.2891C21.1519 26.2891 22.3726 25.293 22.8218 23.4668L27.5484 4.24805C28.2711 1.32812 26.7867 0 25.0679 0C22.7925 0 20.3414 2.35352 21.6304 6.33789L21.9429 5.0293C18.2613 6.95312 16.5035 11.6992 17.1578 17.2949L17.5582 16.748C14.941 16.2109 11.6011 15.8984 8.33942 15.8984C1.84528 15.8984-0.771911 17.4609 0.194886 21.084Z", fillAlpha = 0.425f)
            addSfPath("M15.4 34.9805C15.4 35.6055 15.9175 35.8594 16.4449 35.5371L20.9273 32.8027C21.4547 32.4805 21.4351 31.8652 20.9273 31.5527L16.4351 28.7988C15.9078 28.4766 15.4 28.7402 15.4 29.3555Z", fillAlpha = 0.85f)
            addSfPath("M8.77887 34.9805L8.77887 29.3555C8.77887 28.7402 8.27106 28.4766 7.74371 28.7988L3.25153 31.5527C2.74371 31.8652 2.72418 32.4805 3.25153 32.8027L7.74371 35.5371C8.27106 35.8594 8.77887 35.6055 8.77887 34.9805Z", fillAlpha = 0.85f)
        }
        return _sFCarseatLeftForwardAndBackwardFill!!
    }

private var _sFCarseatLeftForwardAndBackwardFill: ImageVector? = null
