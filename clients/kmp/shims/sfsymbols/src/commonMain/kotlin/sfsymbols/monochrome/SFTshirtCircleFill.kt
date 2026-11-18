package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTshirtCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTshirtCircleFill: ImageVector
    get() {
        if (_sFTshirtCircleFill != null) {
            return _sFTshirtCircleFill!!
        }
        _sFTshirtCircleFill = sfIcon(
            name = "Monochrome.SFTshirtCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.1562 6.18164C14.3652 6.5625 13.6133 6.76758 12.6855 6.76758C11.7676 6.76758 11.0059 6.5625 10.2246 6.18164C10.0293 6.07422 9.81445 6.06445 9.57031 6.17188L5.14648 8.0957C4.62891 8.33008 4.4043 8.7207 4.55078 9.26758L5.12695 11.4355C5.25391 11.9922 5.60547 12.207 6.14258 12.0898L7.75391 11.7383L7.83203 17.959C7.85156 19.0625 8.39844 19.5898 9.52148 19.5898L15.8594 19.5898C16.9824 19.5898 17.5293 19.0625 17.5488 17.959L17.627 11.7383L19.2383 12.0898C19.7754 12.207 20.127 11.9922 20.2637 11.4355L20.8398 9.26758C20.9766 8.7207 20.752 8.33008 20.2344 8.0957L15.8105 6.17188C15.5664 6.06445 15.3613 6.07422 15.1562 6.18164ZM12.6855 8.57422C11.7383 8.57422 10.957 8.125 10.6641 7.31445C11.2988 7.53906 12.002 7.64648 12.6855 7.64648C13.3789 7.64648 14.082 7.53906 14.7168 7.31445C14.4238 8.11523 13.6621 8.57422 12.6855 8.57422Z", fillAlpha = 0.85f)
        }
        return _sFTshirtCircleFill!!
    }

private var _sFTshirtCircleFill: ImageVector? = null
