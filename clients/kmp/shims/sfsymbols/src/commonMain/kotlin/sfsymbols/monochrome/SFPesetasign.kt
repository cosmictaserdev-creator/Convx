package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPesetasign (monochrome)
 * Viewport: 19.1992 x 22.7637
 */
public val SfSymbols.Monochrome.SFPesetasign: ImageVector
    get() {
        if (_sFPesetasign != null) {
            return _sFPesetasign!!
        }
        _sFPesetasign = sfIcon(
            name = "Monochrome.SFPesetasign",
            viewportWidth = 19.1992f,
            viewportHeight = 22.7637f
        ) {
            addSfPath("M3.51562 22.7246C4.13086 22.7246 4.57031 22.2656 4.57031 21.6504L4.57031 14.043L9.45312 14.043C13.8184 14.043 16.7773 11.1816 16.7773 7.01172C16.7773 2.7832 13.7793 0 9.47266 0L3.50586 0C2.88086 0 2.44141 0.458984 2.44141 1.10352L2.44141 21.6504C2.44141 22.2656 2.90039 22.7246 3.51562 22.7246ZM4.57031 12.207L4.57031 1.8457L8.98438 1.8457C12.4023 1.8457 14.5898 3.68164 14.5898 7.01172C14.5898 10.3906 12.3828 12.207 8.98438 12.207ZM0 6.88477C0 7.29492 0.3125 7.59766 0.722656 7.59766L18.1152 7.59766C18.5254 7.59766 18.8379 7.28516 18.8379 6.88477C18.8379 6.47461 18.5254 6.17188 18.1152 6.17188L0.722656 6.17188C0.3125 6.17188 0 6.47461 0 6.88477Z", fillAlpha = 0.85f)
        }
        return _sFPesetasign!!
    }

private var _sFPesetasign: ImageVector? = null
