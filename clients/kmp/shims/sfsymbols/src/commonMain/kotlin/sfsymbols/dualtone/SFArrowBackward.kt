package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowBackward (dualtone)
 * Viewport: 23.7598 x 18.916
 */
public val SfSymbols.Dualtone.SFArrowBackward: ImageVector
    get() {
        if (_sFArrowBackward != null) {
            return _sFArrowBackward!!
        }
        _sFArrowBackward = sfIcon(
            name = "Dualtone.SFArrowBackward",
            viewportWidth = 23.7598f,
            viewportHeight = 18.916f
        ) {
            addSfPath("M9.45312 18.9062C9.99023 18.9062 10.3906 18.5156 10.3906 17.9883C10.3906 17.7344 10.3027 17.4805 10.1367 17.3047L6.70898 13.8477L1.94336 9.45312L6.70898 5.05859L10.1367 1.5918C10.3027 1.41602 10.3906 1.17188 10.3906 0.917969C10.3906 0.390625 9.99023 0 9.45312 0C9.19922 0 8.96484 0.0878906 8.71094 0.341797L0.322266 8.74023C0.107422 8.94531 0 9.18945 0 9.45312C0 9.7168 0.107422 9.96094 0.322266 10.1562L8.76953 18.6133C8.98438 18.8086 9.19922 18.9062 9.45312 18.9062ZM5.77148 10.4004L22.4609 10.4004C23.0078 10.4004 23.3984 10 23.3984 9.45312C23.3984 8.89648 23.0078 8.50586 22.4609 8.50586L5.77148 8.50586L1.92383 8.71094C1.49414 8.71094 1.18164 9.01367 1.18164 9.45312C1.18164 9.88281 1.49414 10.1855 1.93359 10.1855Z", fillAlpha = 0.85f)
        }
        return _sFArrowBackward!!
    }

private var _sFArrowBackward: ImageVector? = null
