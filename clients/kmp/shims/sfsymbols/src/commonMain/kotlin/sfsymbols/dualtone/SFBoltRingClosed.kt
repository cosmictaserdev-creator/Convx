package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltRingClosed (dualtone)
 * Viewport: 25.8301 x 25.4785
 */
public val SfSymbols.Dualtone.SFBoltRingClosed: ImageVector
    get() {
        if (_sFBoltRingClosed != null) {
            return _sFBoltRingClosed!!
        }
        _sFBoltRingClosed = sfIcon(
            name = "Dualtone.SFBoltRingClosed",
            viewportWidth = 25.8301f,
            viewportHeight = 25.4785f
        ) {
            addSfPath("M0 12.7344C0 19.7559 5.71289 25.4688 12.7344 25.4688C19.7559 25.4688 25.4688 19.7559 25.4688 12.7344C25.4688 6.32812 20.7031 1.00586 14.5312 0.136719C14.7949 0.498047 14.9609 0.9375 14.9609 1.43555C14.9609 2.04102 14.707 2.58789 14.3164 2.98828C18.9941 3.75 22.6074 7.83203 22.6074 12.7344C22.6074 18.1738 18.1836 22.5977 12.7344 22.5977C7.28516 22.5977 2.86133 18.1738 2.86133 12.7344C2.86133 7.28516 7.28516 2.86133 12.7344 2.86133C13.5156 2.86133 14.1602 2.2168 14.1602 1.43555C14.1602 0.644531 13.5156 0 12.7344 0C5.71289 0 0 5.71289 0 12.7344Z", fillAlpha = 0.425f)
            addSfPath("M7.58789 13.5547C7.58789 13.8281 7.80273 14.0234 8.08594 14.0234L12.0605 14.0234L9.93164 19.7559C9.67773 20.4492 10.4102 20.8301 10.8594 20.2734L17.3047 12.1875C17.4121 12.041 17.4805 11.8945 17.4805 11.7676C17.4805 11.4844 17.2656 11.2891 16.9824 11.2891L13.0078 11.2891L15.1367 5.55664C15.3906 4.86328 14.6582 4.48242 14.209 5.04883L7.76367 13.1348C7.65625 13.2812 7.58789 13.4277 7.58789 13.5547Z", fillAlpha = 0.85f)
        }
        return _sFBoltRingClosed!!
    }

private var _sFBoltRingClosed: ImageVector? = null
