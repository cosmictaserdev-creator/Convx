package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilLine (monochrome)
 * Viewport: 27.0824 x 23.0973
 */
public val SfSymbols.Monochrome.SFPencilLine: ImageVector
    get() {
        if (_sFPencilLine != null) {
            return _sFPencilLine!!
        }
        _sFPencilLine = sfIcon(
            name = "Monochrome.SFPencilLine",
            viewportWidth = 27.0824f,
            viewportHeight = 23.0973f
        ) {
            addSfPath("M25.1428 20.6844C25.1428 21.1629 24.7521 21.5535 24.2834 21.5535L5.827 21.5535L7.56371 19.825L24.2834 19.825C24.7521 19.825 25.1428 20.2156 25.1428 20.6844Z", fillAlpha = 0.85f)
            addSfPath("M4.83025 20.3621L18.9123 6.29963L16.9103 4.28791L2.8283 18.3504L1.6076 21.0457C1.49041 21.3192 1.78338 21.6317 2.05682 21.5145ZM19.9572 5.28401L21.1388 4.11213C21.7541 3.50666 21.7834 2.8719 21.2365 2.33479L20.8849 1.98322C20.3576 1.45588 19.7131 1.50471 19.1174 2.09065L17.926 3.26252Z", fillAlpha = 0.85f)
        }
        return _sFPencilLine!!
    }

private var _sFPencilLine: ImageVector? = null
