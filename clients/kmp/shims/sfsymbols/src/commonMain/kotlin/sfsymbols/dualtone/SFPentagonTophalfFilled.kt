package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPentagonTophalfFilled (dualtone)
 * Viewport: 27.3895 x 26.6113
 */
public val SfSymbols.Dualtone.SFPentagonTophalfFilled: ImageVector
    get() {
        if (_sFPentagonTophalfFilled != null) {
            return _sFPentagonTophalfFilled!!
        }
        _sFPentagonTophalfFilled = sfIcon(
            name = "Dualtone.SFPentagonTophalfFilled",
            viewportWidth = 27.3895f,
            viewportHeight = 26.6113f
        ) {
            addSfPath("M0.242578 12.4512L3.98281 24.043C4.52969 25.7812 5.6625 26.6113 7.5082 26.6113L19.5297 26.6113C21.3754 26.6113 22.5082 25.7812 23.0551 24.043L26.7758 12.5195C27.352 10.7129 26.9418 9.35547 25.477 8.28125L15.7699 1.14258C14.2367 0.00976562 12.8012 0.00976562 11.268 1.14258L1.56094 8.28125C0.0960933 9.35547-0.323829 10.6836 0.242578 12.4512ZM2.69375 14.4434L24.3539 14.4434L21.434 23.5156C21.1117 24.5215 20.5941 24.8828 19.5297 24.8828L7.5082 24.8828C6.44375 24.8828 5.92617 24.5215 5.60391 23.5156Z", fillAlpha = 0.85f)
        }
        return _sFPentagonTophalfFilled!!
    }

private var _sFPentagonTophalfFilled: ImageVector? = null
