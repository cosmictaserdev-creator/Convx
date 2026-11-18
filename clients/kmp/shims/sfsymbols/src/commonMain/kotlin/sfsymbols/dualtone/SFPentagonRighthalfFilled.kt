package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPentagonRighthalfFilled (dualtone)
 * Viewport: 27.3851 x 26.6113
 */
public val SfSymbols.Dualtone.SFPentagonRighthalfFilled: ImageVector
    get() {
        if (_sFPentagonRighthalfFilled != null) {
            return _sFPentagonRighthalfFilled!!
        }
        _sFPentagonRighthalfFilled = sfIcon(
            name = "Dualtone.SFPentagonRighthalfFilled",
            viewportWidth = 27.3851f,
            viewportHeight = 26.6113f
        ) {
            addSfPath("M26.7882 12.4512C27.3449 10.6836 26.925 9.35547 25.4601 8.28125L15.7628 1.14258C14.2199 0.0195312 12.7941 0.0195312 11.2609 1.14258L1.55386 8.28125C0.0890194 9.35547-0.330902 10.7129 0.255035 12.5195L3.96597 24.043C4.52261 25.7812 5.65543 26.6113 7.50113 26.6113L19.5226 26.6113C21.3586 26.6113 22.4914 25.7812 23.048 24.043ZM13.5168 2.03125L13.5168 24.8828L7.50113 24.8828C6.43668 24.8828 5.9191 24.5215 5.59683 23.5156L1.88589 11.9922C1.5441 10.9277 1.73941 10.3125 2.59879 9.66797L12.2961 2.5293C12.7453 2.1875 13.1359 2.03125 13.5168 2.03125Z", fillAlpha = 0.85f)
        }
        return _sFPentagonRighthalfFilled!!
    }

private var _sFPentagonRighthalfFilled: ImageVector? = null
