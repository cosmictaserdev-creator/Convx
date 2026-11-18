package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleBadgeMinus (monochrome)
 * Viewport: 37.666 x 28.8574
 */
public val SfSymbols.Monochrome.SFCircleBadgeMinus: ImageVector
    get() {
        if (_sFCircleBadgeMinus != null) {
            return _sFCircleBadgeMinus!!
        }
        _sFCircleBadgeMinus = sfIcon(
            name = "Monochrome.SFCircleBadgeMinus",
            viewportWidth = 37.666f,
            viewportHeight = 28.8574f
        ) {
            addSfPath("M31.3185 13.2677C30.8255 13.1668 30.3158 13.1152 29.7949 13.1152C29.6846 13.1152 29.5747 13.1176 29.4677 13.132C28.8363 7.71578 24.2398 3.52539 18.6523 3.52539C12.627 3.52539 7.75391 8.39844 7.75391 14.4238C7.75391 20.4492 12.627 25.3223 18.6523 25.3223C20.1096 25.3223 21.4994 25.0372 22.7665 24.5145C23.04 25.0705 23.3838 25.5859 23.7839 26.0522C22.2168 26.7533 20.4792 27.1387 18.6523 27.1387C11.6309 27.1387 5.9375 21.4453 5.9375 14.4238C5.9375 7.40234 11.6309 1.69922 18.6523 1.69922C25.2836 1.69922 30.7389 6.78597 31.3185 13.2677Z", fillAlpha = 0.85f)
            addSfPath("M36.1035 20.9863C36.1035 24.4434 33.2227 27.3047 29.7949 27.3047C26.3281 27.3047 23.4766 24.4629 23.4766 20.9863C23.4766 17.5293 26.3281 14.668 29.7949 14.668C33.252 14.668 36.1035 17.5195 36.1035 20.9863ZM26.5039 20.2734C26.123 20.2734 25.8008 20.6055 25.8008 20.9863C25.8008 21.3574 26.123 21.6992 26.5039 21.6992L33.0664 21.6992C33.457 21.6992 33.7793 21.3574 33.7793 20.9863C33.7793 20.6055 33.457 20.2734 33.0664 20.2734Z", fillAlpha = 0.85f)
        }
        return _sFCircleBadgeMinus!!
    }

private var _sFCircleBadgeMinus: ImageVector? = null
