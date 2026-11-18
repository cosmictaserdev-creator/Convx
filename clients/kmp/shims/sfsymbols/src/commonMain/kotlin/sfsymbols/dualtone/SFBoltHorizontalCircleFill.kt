package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltHorizontalCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBoltHorizontalCircleFill: ImageVector
    get() {
        if (_sFBoltHorizontalCircleFill != null) {
            return _sFBoltHorizontalCircleFill!!
        }
        _sFBoltHorizontalCircleFill = sfIcon(
            name = "Dualtone.SFBoltHorizontalCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M4.43359 15.293L8.81836 9.63867C9.04297 9.35547 9.27734 9.22852 9.53125 9.22852C9.72656 9.22852 9.92188 9.29688 10.1367 9.41406L15.8789 12.5L20.2051 10.127C20.8984 9.75586 21.4844 10.4199 21.0059 11.0449L16.6211 16.709C16.3965 16.9922 16.1523 17.1289 15.8984 17.1289C15.7129 17.1289 15.5078 17.0508 15.293 16.9336L9.55078 13.8574L5.23438 16.2207C4.58008 16.5723 3.91602 15.9668 4.43359 15.293Z", fillAlpha = 0.85f)
        }
        return _sFBoltHorizontalCircleFill!!
    }

private var _sFBoltHorizontalCircleFill: ImageVector? = null
