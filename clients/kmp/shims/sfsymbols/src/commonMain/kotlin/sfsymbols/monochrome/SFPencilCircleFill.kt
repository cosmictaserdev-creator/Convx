package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPencilCircleFill: ImageVector
    get() {
        if (_sFPencilCircleFill != null) {
            return _sFPencilCircleFill!!
        }
        _sFPencilCircleFill = sfIcon(
            name = "Monochrome.SFPencilCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.10938 16.9336L6.35742 18.584C6.24023 18.8672 6.51367 19.1016 6.74805 19.0137L8.4668 18.3105L17.1973 9.60938L15.8203 8.22266ZM17.4023 6.66016L16.5332 7.51953L17.9102 8.89648L18.7695 8.03711C19.1797 7.63672 19.1895 7.19727 18.8184 6.83594L18.584 6.5918C18.2227 6.24023 17.793 6.2793 17.4023 6.66016Z", fillAlpha = 0.85f)
        }
        return _sFPencilCircleFill!!
    }

private var _sFPencilCircleFill: ImageVector? = null
