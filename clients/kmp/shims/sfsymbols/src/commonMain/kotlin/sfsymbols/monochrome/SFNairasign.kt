package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNairasign (monochrome)
 * Viewport: 19.1992 x 23.0371
 */
public val SfSymbols.Monochrome.SFNairasign: ImageVector
    get() {
        if (_sFNairasign != null) {
            return _sFNairasign!!
        }
        _sFNairasign = sfIcon(
            name = "Monochrome.SFNairasign",
            viewportWidth = 19.1992f,
            viewportHeight = 23.0371f
        ) {
            addSfPath("M3.11523 23.0078C3.76953 23.0078 4.17969 22.5781 4.17969 21.9141L4.17969 3.84766L4.30664 3.84766L13.7891 21.9531C14.082 22.5488 14.5605 23.0078 15.3516 23.0078C16.2402 23.0078 16.7871 22.4316 16.7871 21.6016L16.7871 1.07422C16.7871 0.410156 16.377 0 15.7324 0C15.0781 0 14.6875 0.410156 14.6875 1.07422L14.6875 19.1797L14.5508 19.1797L5.08789 1.08398C4.73633 0.390625 4.26758 0 3.49609 0C2.67578 0 2.08008 0.537109 2.08008 1.39648L2.08008 21.9141C2.08008 22.5781 2.48047 23.0078 3.11523 23.0078ZM0 11.5625C0 11.9434 0.322266 12.2754 0.712891 12.2754L18.1348 12.2754C18.5156 12.2754 18.8379 11.9434 18.8379 11.5625C18.8379 11.1719 18.5156 10.8496 18.1348 10.8496L0.712891 10.8496C0.322266 10.8496 0 11.1719 0 11.5625Z", fillAlpha = 0.85f)
        }
        return _sFNairasign!!
    }

private var _sFNairasign: ImageVector? = null
