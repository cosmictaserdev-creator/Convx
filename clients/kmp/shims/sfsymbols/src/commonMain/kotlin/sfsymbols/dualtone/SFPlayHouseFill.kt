package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayHouseFill (dualtone)
 * Viewport: 29.8242 x 26.2207
 */
public val SfSymbols.Dualtone.SFPlayHouseFill: ImageVector
    get() {
        if (_sFPlayHouseFill != null) {
            return _sFPlayHouseFill!!
        }
        _sFPlayHouseFill = sfIcon(
            name = "Dualtone.SFPlayHouseFill",
            viewportWidth = 29.8242f,
            viewportHeight = 26.2207f
        ) {
            addSfPath("M3.73047 23.5645C3.73047 25.2246 4.72656 26.2012 6.40625 26.2012L23.0566 26.2012C24.7363 26.2012 25.7324 25.2246 25.7324 23.5645L25.7324 13.0566L15.498 4.47266C15 4.04297 14.4336 4.0625 13.9551 4.47266L3.73047 13.0371ZM0.927734 13.0176C1.2207 13.0176 1.46484 12.8613 1.67969 12.6758L14.2578 2.11914C14.4043 1.99219 14.5703 1.93359 14.7266 1.93359C14.8926 1.93359 15.0586 1.99219 15.2051 2.11914L27.7832 12.6758C27.998 12.8613 28.2324 13.0176 28.5352 13.0176C29.1113 13.0176 29.4629 12.5977 29.4629 12.168C29.4629 11.9043 29.3555 11.6504 29.1113 11.4453L16.1426 0.566406C15.6934 0.185547 15.2148 0 14.7266 0C14.248 0 13.7695 0.185547 13.3203 0.566406L0.351562 11.4453C0.107422 11.6504 0 11.9043 0 12.168C0 12.5977 0.341797 13.0176 0.927734 13.0176ZM23.0566 6.9043L25.9277 9.32617L25.9277 3.76953C25.9277 3.28125 25.6055 2.96875 25.1172 2.96875L23.877 2.96875C23.3887 2.96875 23.0566 3.28125 23.0566 3.76953Z", fillAlpha = 0.2125f)
            addSfPath("M11.3379 21.4355L11.3379 12.1875C11.3379 11.6309 11.9238 11.4258 12.3828 11.6992L20.127 16.2598C20.5664 16.5137 20.5762 17.1094 20.1367 17.373L12.3828 21.9336C11.9238 22.207 11.3379 21.9824 11.3379 21.4355Z", fillAlpha = 0.85f)
        }
        return _sFPlayHouseFill!!
    }

private var _sFPlayHouseFill: ImageVector? = null
