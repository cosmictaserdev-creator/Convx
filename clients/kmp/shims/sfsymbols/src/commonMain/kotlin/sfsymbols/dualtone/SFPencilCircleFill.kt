package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPencilCircleFill: ImageVector
    get() {
        if (_sFPencilCircleFill != null) {
            return _sFPencilCircleFill!!
        }
        _sFPencilCircleFill = sfIcon(
            name = "Dualtone.SFPencilCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M8.4668 18.3105L6.74805 19.0137C6.51367 19.1016 6.24023 18.8672 6.35742 18.584L7.10938 16.9336L15.8203 8.22266L17.1973 9.60938ZM17.9102 8.89648L16.5332 7.51953L17.4023 6.66016C17.793 6.2793 18.2227 6.24023 18.584 6.5918L18.8184 6.83594C19.1895 7.19727 19.1797 7.63672 18.7695 8.03711Z", fillAlpha = 0.85f)
        }
        return _sFPencilCircleFill!!
    }

private var _sFPencilCircleFill: ImageVector? = null
