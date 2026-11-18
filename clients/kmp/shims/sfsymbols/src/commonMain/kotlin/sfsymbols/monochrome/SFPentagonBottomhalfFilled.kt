package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPentagonBottomhalfFilled (monochrome)
 * Viewport: 27.3895 x 26.6113
 */
public val SfSymbols.Monochrome.SFPentagonBottomhalfFilled: ImageVector
    get() {
        if (_sFPentagonBottomhalfFilled != null) {
            return _sFPentagonBottomhalfFilled!!
        }
        _sFPentagonBottomhalfFilled = sfIcon(
            name = "Monochrome.SFPentagonBottomhalfFilled",
            viewportWidth = 27.3895f,
            viewportHeight = 26.6113f
        ) {
            addSfPath("M0.242578 12.4512L3.98281 24.043C4.52969 25.7812 5.6625 26.6113 7.5082 26.6113L19.5297 26.6113C21.3754 26.6113 22.5082 25.7812 23.0551 24.043L26.7758 12.5195C27.352 10.7129 26.9418 9.35547 25.477 8.28125L15.7699 1.14258C14.2367 0.00976562 12.8012 0.00976562 11.268 1.14258L1.56094 8.28125C0.0960933 9.35547-0.323829 10.6836 0.242578 12.4512ZM1.87344 11.9141C1.55117 10.9082 1.74648 10.3125 2.61562 9.66797L12.3031 2.5293C13.2016 1.875 13.8363 1.875 14.7348 2.5293L24.4223 9.66797C25.2914 10.3125 25.4867 10.9277 25.1352 11.9922L24.3539 14.4434L2.69375 14.4434Z", fillAlpha = 0.85f)
        }
        return _sFPentagonBottomhalfFilled!!
    }

private var _sFPentagonBottomhalfFilled: ImageVector? = null
