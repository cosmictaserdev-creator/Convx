package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowForward (monochrome)
 * Viewport: 23.7598 x 18.916
 */
public val SfSymbols.Monochrome.SFArrowForward: ImageVector
    get() {
        if (_sFArrowForward != null) {
            return _sFArrowForward!!
        }
        _sFArrowForward = sfIcon(
            name = "Monochrome.SFArrowForward",
            viewportWidth = 23.7598f,
            viewportHeight = 18.916f
        ) {
            addSfPath("M13.9453 18.9062C14.1992 18.9062 14.4238 18.8086 14.6387 18.6133L23.0762 10.1562C23.291 9.96094 23.3984 9.7168 23.3984 9.45312C23.3984 9.18945 23.291 8.94531 23.0762 8.74023L14.6875 0.341797C14.4336 0.0878906 14.1992 0 13.9453 0C13.418 0 13.0078 0.390625 13.0078 0.917969C13.0078 1.17188 13.0957 1.41602 13.2715 1.5918L16.6895 5.05859L21.4551 9.45312L16.6895 13.8477L13.2715 17.3047C13.1055 17.4805 13.0078 17.7344 13.0078 17.9883C13.0078 18.5156 13.418 18.9062 13.9453 18.9062ZM0.9375 10.4004L17.6367 10.4004L21.4648 10.1855C21.9141 10.1562 22.2168 9.88281 22.2168 9.45312C22.2168 9.01367 21.9141 8.74023 21.4746 8.71094L17.6367 8.50586L0.9375 8.50586C0.390625 8.50586 0 8.89648 0 9.45312C0 10 0.390625 10.4004 0.9375 10.4004Z", fillAlpha = 0.85f)
        }
        return _sFArrowForward!!
    }

private var _sFArrowForward: ImageVector? = null
