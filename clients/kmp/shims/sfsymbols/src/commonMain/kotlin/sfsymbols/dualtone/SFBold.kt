package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBold (dualtone)
 * Viewport: 15.0488 x 18.0566
 */
public val SfSymbols.Dualtone.SFBold: ImageVector
    get() {
        if (_sFBold != null) {
            return _sFBold!!
        }
        _sFBold = sfIcon(
            name = "Dualtone.SFBold",
            viewportWidth = 15.0488f,
            viewportHeight = 18.0566f
        ) {
            addSfPath("M1.92383 18.0566L8.39844 18.0566C12.2656 18.0566 14.6875 16.0449 14.6875 12.9199C14.6875 10.459 12.9102 8.69141 10.3516 8.53516L10.3516 8.42773C12.4219 8.13477 13.8379 6.52344 13.8379 4.53125C13.8379 1.73828 11.6895 0 8.24219 0L1.92383 0C0.703125 0 0 0.722656 0 1.96289L0 16.084C0 17.334 0.703125 18.0566 1.92383 18.0566ZM3.7793 15.3223L3.7793 10.0293L7.17773 10.0293C9.54102 10.0293 10.8398 10.9473 10.8398 12.6562C10.8398 14.3945 9.58008 15.3223 7.28516 15.3223ZM3.7793 7.56836L3.7793 2.75391L7.13867 2.75391C9.00391 2.75391 10.1074 3.61328 10.1074 5.08789C10.1074 6.64062 8.87695 7.56836 6.77734 7.56836Z", fillAlpha = 0.85f)
        }
        return _sFBold!!
    }

private var _sFBold: ImageVector? = null
