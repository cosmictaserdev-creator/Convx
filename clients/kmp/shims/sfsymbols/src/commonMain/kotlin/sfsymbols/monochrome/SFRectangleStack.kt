package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleStack (monochrome)
 * Viewport: 30.9473 x 31.5918
 */
public val SfSymbols.Monochrome.SFRectangleStack: ImageVector
    get() {
        if (_sFRectangleStack != null) {
            return _sFRectangleStack!!
        }
        _sFRectangleStack = sfIcon(
            name = "Monochrome.SFRectangleStack",
            viewportWidth = 30.9473f,
            viewportHeight = 31.5918f
        ) {
            addSfPath("M23.2553 3.47656L7.34027 3.47656C7.40286 2.59177 7.97731 2.08008 8.91602 2.08008L21.6699 2.08008C22.6086 2.08008 23.1917 2.59177 23.2553 3.47656Z", fillAlpha = 0.85f)
            addSfPath("M25.7757 6.89437C25.4256 6.84087 25.0548 6.81641 24.668 6.81641L5.91797 6.81641C5.53106 6.81641 5.16024 6.84065 4.81033 6.89376C4.94314 5.85538 5.65863 5.27344 6.80664 5.27344L23.7793 5.27344C24.9275 5.27344 25.6431 5.85561 25.7757 6.89437Z", fillAlpha = 0.85f)
            addSfPath("M5.91797 29.4824L24.668 29.4824C27.1875 29.4824 28.4668 28.2031 28.4668 25.7129L28.4668 12.6953C28.4668 10.2051 27.1875 8.93555 24.668 8.93555L5.91797 8.93555C3.38867 8.93555 2.11914 10.1953 2.11914 12.6953L2.11914 25.7129C2.11914 28.2129 3.38867 29.4824 5.91797 29.4824ZM5.94727 27.7539C4.58984 27.7539 3.84766 27.0312 3.84766 25.625L3.84766 12.7832C3.84766 11.3867 4.58984 10.6641 5.94727 10.6641L24.6387 10.6641C25.9766 10.6641 26.7383 11.3867 26.7383 12.7832L26.7383 25.625C26.7383 27.0312 25.9766 27.7539 24.6387 27.7539Z", fillAlpha = 0.85f)
        }
        return _sFRectangleStack!!
    }

private var _sFRectangleStack: ImageVector? = null
