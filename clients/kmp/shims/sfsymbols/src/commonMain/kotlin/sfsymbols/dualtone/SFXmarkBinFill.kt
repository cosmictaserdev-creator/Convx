package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkBinFill (dualtone)
 * Viewport: 30.332 x 22.9785
 */
public val SfSymbols.Dualtone.SFXmarkBinFill: ImageVector
    get() {
        if (_sFXmarkBinFill != null) {
            return _sFXmarkBinFill!!
        }
        _sFXmarkBinFill = sfIcon(
            name = "Dualtone.SFXmarkBinFill",
            viewportWidth = 30.332f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M8.42773 22.9785L21.543 22.9785C23.9355 22.9785 24.9316 21.6992 25.2832 19.3359L27.0996 7.25586L2.86133 7.25586L4.6875 19.3359C5.04883 21.7188 6.02539 22.9785 8.42773 22.9785ZM2.04102 5.70312L27.9199 5.70312C29.2676 5.70312 29.9707 4.88281 29.9707 3.55469L29.9707 2.17773C29.9707 0.839844 29.2676 0.0292969 27.9199 0.0292969L2.04102 0.0292969C0.761719 0.0292969 0 0.839844 0 2.17773L0 3.55469C0 4.88281 0.703125 5.70312 2.04102 5.70312Z", fillAlpha = 0.2125f)
            addSfPath("M11.1621 19.4629C11.416 19.4629 11.6113 19.3457 11.7676 19.1992L19.3945 11.5625C19.5508 11.4062 19.6582 11.2109 19.6582 10.9668C19.6582 10.5078 19.2676 10.127 18.7988 10.127C18.5645 10.127 18.3887 10.2246 18.2129 10.3809L10.5762 17.998C10.4199 18.1641 10.3125 18.3398 10.3125 18.5938C10.3125 19.0527 10.7031 19.4629 11.1621 19.4629ZM18.8086 19.4629C19.2676 19.4629 19.6582 19.0527 19.6582 18.5938C19.6582 18.3398 19.5508 18.1641 19.3945 17.998L11.7578 10.3809C11.5918 10.2246 11.4062 10.127 11.1719 10.127C10.7129 10.127 10.3125 10.5078 10.3125 10.9668C10.3125 11.2109 10.4199 11.4062 10.5762 11.5625L18.2031 19.1992C18.3594 19.3457 18.5547 19.4629 18.8086 19.4629Z", fillAlpha = 0.85f)
        }
        return _sFXmarkBinFill!!
    }

private var _sFXmarkBinFill: ImageVector? = null
