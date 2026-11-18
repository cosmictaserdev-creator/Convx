package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltHorizontalCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBoltHorizontalCircleFill: ImageVector
    get() {
        if (_sFBoltHorizontalCircleFill != null) {
            return _sFBoltHorizontalCircleFill!!
        }
        _sFBoltHorizontalCircleFill = sfIcon(
            name = "Monochrome.SFBoltHorizontalCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM8.81836 9.63867L4.43359 15.293C3.91602 15.9668 4.58008 16.5723 5.23438 16.2207L9.55078 13.8574L15.293 16.9336C15.5078 17.0508 15.7129 17.1289 15.8984 17.1289C16.1523 17.1289 16.3965 16.9922 16.6211 16.709L21.0059 11.0449C21.4844 10.4199 20.8984 9.75586 20.2051 10.127L15.8789 12.5L10.1367 9.41406C9.92188 9.29688 9.72656 9.22852 9.53125 9.22852C9.27734 9.22852 9.04297 9.35547 8.81836 9.63867Z", fillAlpha = 0.85f)
        }
        return _sFBoltHorizontalCircleFill!!
    }

private var _sFBoltHorizontalCircleFill: ImageVector? = null
