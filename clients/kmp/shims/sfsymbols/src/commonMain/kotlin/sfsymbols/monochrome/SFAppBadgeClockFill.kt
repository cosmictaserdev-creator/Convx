package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppBadgeClockFill (monochrome)
 * Viewport: 38.3008 x 30.9277
 */
public val SfSymbols.Monochrome.SFAppBadgeClockFill: ImageVector
    get() {
        if (_sFAppBadgeClockFill != null) {
            return _sFAppBadgeClockFill!!
        }
        _sFAppBadgeClockFill = sfIcon(
            name = "Monochrome.SFAppBadgeClockFill",
            viewportWidth = 38.3008f,
            viewportHeight = 30.9277f
        ) {
            addSfPath("M28.6719 5.76172C29.8242 6.91406 30.4297 8.62305 30.4297 10.8301L30.4297 15.1855C26.1133 15.1855 22.5488 18.7402 22.5488 23.0566C22.5488 24.4578 22.92 25.7753 23.5728 26.9141L14.3262 26.9141C12.1191 26.9141 10.4004 26.2988 9.26758 25.166C8.10547 24.0137 7.50977 22.3047 7.50977 20.0977L7.50977 10.8301C7.50977 8.62305 8.11523 6.9043 9.26758 5.76172C10.3906 4.63867 12.1191 4.01367 14.3262 4.01367L23.6035 4.01367C25.8203 4.01367 27.5293 4.61914 28.6719 5.76172Z", fillAlpha = 0.85f)
            addSfPath("M36.7383 23.0566C36.7383 26.5137 33.8574 29.375 30.4297 29.375C26.9629 29.375 24.1113 26.5332 24.1113 23.0566C24.1113 19.5996 26.9629 16.7383 30.4297 16.7383C33.8867 16.7383 36.7383 19.5898 36.7383 23.0566ZM29.7656 19.2773L29.7656 22.5879L27.4023 22.5879C27.0117 22.5879 26.6797 22.9102 26.6797 23.291C26.6797 23.6914 27.002 24.0039 27.4023 24.0039L30.4785 24.0039C30.8789 24.0039 31.2012 23.6914 31.2012 23.291L31.2012 19.2773C31.2012 18.8867 30.8789 18.584 30.4785 18.584C30.0879 18.584 29.7656 18.8867 29.7656 19.2773Z", fillAlpha = 0.85f)
        }
        return _sFAppBadgeClockFill!!
    }

private var _sFAppBadgeClockFill: ImageVector? = null
