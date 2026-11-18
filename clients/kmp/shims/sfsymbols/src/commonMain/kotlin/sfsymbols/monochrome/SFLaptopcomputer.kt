package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLaptopcomputer (monochrome)
 * Viewport: 36.0449 x 20.1074
 */
public val SfSymbols.Monochrome.SFLaptopcomputer: ImageVector
    get() {
        if (_sFLaptopcomputer != null) {
            return _sFLaptopcomputer!!
        }
        _sFLaptopcomputer = sfIcon(
            name = "Monochrome.SFLaptopcomputer",
            viewportWidth = 36.0449f,
            viewportHeight = 20.1074f
        ) {
            addSfPath("M0 18.8086C0 19.5312 0.566406 20.1074 1.2793 20.1074L34.4043 20.1074C35.1172 20.1074 35.6836 19.5312 35.6836 18.8086C35.6836 18.0859 35.1172 17.5098 34.4043 17.5098L31.6406 17.5098L31.6406 2.62695C31.6406 0.908203 30.7324 0.0195312 29.0137 0.0195312L6.66992 0.0195312C5.0293 0.0195312 4.04297 0.908203 4.04297 2.62695L4.04297 17.5098L1.2793 17.5098C0.566406 17.5098 0 18.0859 0 18.8086ZM5.77148 17.5098L5.77148 3.08594C5.77148 2.1875 6.2207 1.74805 7.10938 1.74805L28.5742 1.74805C29.4629 1.74805 29.9121 2.1875 29.9121 3.08594L29.9121 17.5098Z", fillAlpha = 0.85f)
        }
        return _sFLaptopcomputer!!
    }

private var _sFLaptopcomputer: ImageVector? = null
