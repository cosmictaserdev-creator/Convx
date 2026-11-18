package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDeskclockFill (dualtone)
 * Viewport: 25.8008 x 25.7324
 */
public val SfSymbols.Dualtone.SFDeskclockFill: ImageVector
    get() {
        if (_sFDeskclockFill != null) {
            return _sFDeskclockFill!!
        }
        _sFDeskclockFill = sfIcon(
            name = "Dualtone.SFDeskclockFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.7324f
        ) {
            addSfPath("M0.0976562 19.3848L0.0976562 23.7012C0.0976562 25.0879 0.761719 25.7324 2.16797 25.7324L6.67969 25.7324C3.7793 24.3945 1.57227 22.2559 0.0976562 19.3848ZM25.3418 19.3848C23.8672 22.2559 21.6504 24.3945 18.75 25.7324L23.2715 25.7324C24.668 25.7324 25.3418 25.0879 25.3418 23.7012ZM12.7148 25.5762C19.7266 25.5762 25.4395 19.8633 25.4395 12.8613C25.4395 5.84961 19.7266 0.136719 12.7148 0.136719C5.71289 0.136719 0 5.84961 0 12.8613C0 19.8633 5.71289 25.5762 12.7148 25.5762Z", fillAlpha = 0.2125f)
            addSfPath("M8.28125 9.64844C7.96875 9.33594 7.97852 8.85742 8.29102 8.55469C8.59375 8.24219 9.07227 8.23242 9.39453 8.55469L12.7051 11.875L18.2617 6.32812C18.5742 6.00586 19.0332 6.02539 19.3555 6.32812C19.6777 6.64062 19.668 7.12891 19.3555 7.43164L13.2715 13.5254C12.959 13.8477 12.4707 13.8477 12.1484 13.5254Z", fillAlpha = 0.85f)
        }
        return _sFDeskclockFill!!
    }

private var _sFDeskclockFill: ImageVector? = null
