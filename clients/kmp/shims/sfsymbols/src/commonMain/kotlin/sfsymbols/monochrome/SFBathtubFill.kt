package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBathtubFill (monochrome)
 * Viewport: 31.9531 x 19.3066
 */
public val SfSymbols.Monochrome.SFBathtubFill: ImageVector
    get() {
        if (_sFBathtubFill != null) {
            return _sFBathtubFill!!
        }
        _sFBathtubFill = sfIcon(
            name = "Monochrome.SFBathtubFill",
            viewportWidth = 31.9531f,
            viewportHeight = 19.3066f
        ) {
            addSfPath("M0 1.5332C0 2.38281 0.693359 3.07617 1.54297 3.07617L30.0488 3.07617C30.8984 3.07617 31.5918 2.38281 31.5918 1.5332C31.5918 0.683594 30.8984 0 30.0488 0L1.54297 0C0.693359 0 0 0.683594 0 1.5332ZM8.59375 16.3965L22.9883 16.3965C27.6172 16.3965 29.9902 14.1504 29.9902 9.76562L29.9902 4.62891L1.60156 4.62891L1.60156 9.76562C1.60156 14.1504 3.97461 16.3965 8.59375 16.3965ZM5.18555 19.1211C5.63477 19.2871 6.14258 19.043 6.29883 18.6035L7.34375 15.7227L5.72266 15.1367L4.67773 18.0078C4.52148 18.457 4.74609 18.9648 5.18555 19.1211ZM26.3965 19.1211C26.8457 18.9648 27.0703 18.457 26.9141 18.0078L25.8691 15.1367L24.248 15.7227L25.2832 18.6035C25.4492 19.043 25.957 19.2871 26.3965 19.1211Z", fillAlpha = 0.85f)
        }
        return _sFBathtubFill!!
    }

private var _sFBathtubFill: ImageVector? = null
