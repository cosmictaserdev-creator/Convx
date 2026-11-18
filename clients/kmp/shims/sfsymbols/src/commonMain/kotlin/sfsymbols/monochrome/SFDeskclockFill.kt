package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDeskclockFill (monochrome)
 * Viewport: 25.8008 x 25.7324
 */
public val SfSymbols.Monochrome.SFDeskclockFill: ImageVector
    get() {
        if (_sFDeskclockFill != null) {
            return _sFDeskclockFill!!
        }
        _sFDeskclockFill = sfIcon(
            name = "Monochrome.SFDeskclockFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.7324f
        ) {
            addSfPath("M6.67969 25.7324L2.16797 25.7324C0.761719 25.7324 0.0976562 25.0879 0.0976562 23.7012L0.0976562 19.3848C1.57227 22.2559 3.7793 24.3945 6.67969 25.7324ZM25.3418 23.7012C25.3418 25.0879 24.668 25.7324 23.2715 25.7324L18.75 25.7324C21.6504 24.3945 23.8672 22.2559 25.3418 19.3848ZM25.4395 12.8613C25.4395 19.8633 19.7266 25.5762 12.7148 25.5762C5.71289 25.5762 0 19.8633 0 12.8613C0 5.84961 5.71289 0.136719 12.7148 0.136719C19.7266 0.136719 25.4395 5.84961 25.4395 12.8613ZM18.2617 6.32812L12.7051 11.875L9.39453 8.55469C9.07227 8.23242 8.59375 8.24219 8.29102 8.55469C7.97852 8.85742 7.96875 9.33594 8.28125 9.64844L12.1484 13.5254C12.4707 13.8477 12.959 13.8477 13.2715 13.5254L19.3555 7.43164C19.668 7.12891 19.6777 6.64062 19.3555 6.32812C19.0332 6.02539 18.5742 6.00586 18.2617 6.32812Z", fillAlpha = 0.85f)
        }
        return _sFDeskclockFill!!
    }

private var _sFDeskclockFill: ImageVector? = null
