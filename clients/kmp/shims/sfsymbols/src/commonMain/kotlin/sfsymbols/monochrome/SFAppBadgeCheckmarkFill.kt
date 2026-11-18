package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppBadgeCheckmarkFill (monochrome)
 * Viewport: 38.3008 x 30.9277
 */
public val SfSymbols.Monochrome.SFAppBadgeCheckmarkFill: ImageVector
    get() {
        if (_sFAppBadgeCheckmarkFill != null) {
            return _sFAppBadgeCheckmarkFill!!
        }
        _sFAppBadgeCheckmarkFill = sfIcon(
            name = "Monochrome.SFAppBadgeCheckmarkFill",
            viewportWidth = 38.3008f,
            viewportHeight = 30.9277f
        ) {
            addSfPath("M28.6719 5.76172C29.8242 6.91406 30.4297 8.62305 30.4297 10.8301L30.4297 15.1855C26.1133 15.1855 22.5488 18.7402 22.5488 23.0566C22.5488 24.4578 22.92 25.7753 23.5728 26.9141L14.3262 26.9141C12.1191 26.9141 10.4004 26.2988 9.26758 25.166C8.10547 24.0137 7.50977 22.3047 7.50977 20.0977L7.50977 10.8301C7.50977 8.62305 8.11523 6.9043 9.26758 5.76172C10.3906 4.63867 12.1191 4.01367 14.3262 4.01367L23.6035 4.01367C25.8203 4.01367 27.5293 4.61914 28.6719 5.76172Z", fillAlpha = 0.85f)
            addSfPath("M36.7383 23.0566C36.7383 26.5137 33.8574 29.375 30.4297 29.375C26.9629 29.375 24.1113 26.5332 24.1113 23.0566C24.1113 19.5996 26.9629 16.7383 30.4297 16.7383C33.8867 16.7383 36.7383 19.5898 36.7383 23.0566ZM32.7734 20.3027L29.6973 24.5508L28.1348 22.8613C28.0078 22.7246 27.8223 22.6074 27.5684 22.6074C27.168 22.6074 26.8555 22.9199 26.8555 23.3301C26.8555 23.4961 26.9141 23.7012 27.0508 23.8574L29.1504 26.1621C29.3066 26.3281 29.5312 26.416 29.7266 26.416C29.9902 26.416 30.2051 26.3086 30.332 26.1328L33.9453 21.1328C34.043 20.9863 34.1016 20.8301 34.1016 20.6934C34.1016 20.293 33.7695 19.9805 33.3789 19.9805C33.1152 19.9805 32.9102 20.1074 32.7734 20.3027Z", fillAlpha = 0.85f)
        }
        return _sFAppBadgeCheckmarkFill!!
    }

private var _sFAppBadgeCheckmarkFill: ImageVector? = null
