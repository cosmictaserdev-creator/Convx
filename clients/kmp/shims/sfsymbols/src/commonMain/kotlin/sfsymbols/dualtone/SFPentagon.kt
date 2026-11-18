package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPentagon (dualtone)
 * Viewport: 27.3869 x 26.6113
 */
public val SfSymbols.Dualtone.SFPentagon: ImageVector
    get() {
        if (_sFPentagon != null) {
            return _sFPentagon!!
        }
        _sFPentagon = sfIcon(
            name = "Dualtone.SFPentagon",
            viewportWidth = 27.3869f,
            viewportHeight = 26.6113f
        ) {
            addSfPath("M0.236405 12.4512L3.9864 24.043C4.53328 25.7812 5.66609 26.6113 7.5118 26.6113L19.5333 26.6113C21.379 26.6113 22.5118 25.7812 23.0587 24.043L26.7794 12.5195C27.3555 10.7129 26.9356 9.35547 25.4805 8.28125L15.7735 1.14258C14.2305 0.0195312 12.8145 0.0195312 11.2716 1.14258L1.56453 8.28125C0.0996859 9.35547-0.320236 10.6836 0.236405 12.4512ZM1.87703 11.9141C1.545 10.9082 1.75008 10.3125 2.60945 9.66797L12.3067 2.5293C13.2052 1.86523 13.8399 1.86523 14.7384 2.5293L24.4259 9.66797C25.295 10.3125 25.4903 10.9277 25.1387 11.9922L21.4376 23.5156C21.1055 24.5215 20.5977 24.8828 19.5333 24.8828L7.5118 24.8828C6.44734 24.8828 5.92976 24.5215 5.6075 23.5156Z", fillAlpha = 0.85f)
        }
        return _sFPentagon!!
    }

private var _sFPentagon: ImageVector? = null
