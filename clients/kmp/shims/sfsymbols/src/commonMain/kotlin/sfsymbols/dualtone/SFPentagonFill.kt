package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPentagonFill (dualtone)
 * Viewport: 27.3869 x 26.6113
 */
public val SfSymbols.Dualtone.SFPentagonFill: ImageVector
    get() {
        if (_sFPentagonFill != null) {
            return _sFPentagonFill!!
        }
        _sFPentagonFill = sfIcon(
            name = "Dualtone.SFPentagonFill",
            viewportWidth = 27.3869f,
            viewportHeight = 26.6113f
        ) {
            addSfPath("M0.236405 12.4512L3.9864 24.043C4.53328 25.7812 5.66609 26.6113 7.5118 26.6113L19.5333 26.6113C21.379 26.6113 22.5118 25.7812 23.0587 24.043L26.7794 12.5195C27.3555 10.7129 26.9356 9.35547 25.4805 8.28125L15.7735 1.14258C14.2305 0.0195312 12.8145 0.0195312 11.2716 1.14258L1.56453 8.28125C0.0996859 9.35547-0.320236 10.6836 0.236405 12.4512Z", fillAlpha = 0.85f)
        }
        return _sFPentagonFill!!
    }

private var _sFPentagonFill: ImageVector? = null
