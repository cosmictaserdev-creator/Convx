package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRefrigeratorFill (dualtone)
 * Viewport: 17.6562 x 27.998
 */
public val SfSymbols.Dualtone.SFRefrigeratorFill: ImageVector
    get() {
        if (_sFRefrigeratorFill != null) {
            return _sFRefrigeratorFill!!
        }
        _sFRefrigeratorFill = sfIcon(
            name = "Dualtone.SFRefrigeratorFill",
            viewportWidth = 17.6562f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M0 9.31641L17.2949 9.31641L17.2949 2.57812C17.2949 1.03516 16.3086 0 14.834 0L2.46094 0C0.986328 0 0 1.03516 0 2.57812ZM3.44727 7.00195C3.125 7.00195 2.88086 6.76758 2.88086 6.43555L2.88086 3.50586C2.88086 3.18359 3.11523 2.93945 3.44727 2.93945C3.76953 2.93945 4.01367 3.17383 4.01367 3.50586L4.01367 6.43555C4.01367 6.75781 3.7793 7.00195 3.44727 7.00195ZM0 23.2031L17.2949 23.2031L17.2949 10.6738L0 10.6738ZM3.44727 16.5527C3.125 16.5527 2.88086 16.3184 2.88086 15.9766L2.88086 13.0566C2.88086 12.7344 3.11523 12.4902 3.44727 12.4902C3.76953 12.4902 4.01367 12.7246 4.01367 13.0566L4.01367 15.9766C4.01367 16.3086 3.7793 16.5527 3.44727 16.5527ZM0 24.5605L0 25.4199C0 26.9629 0.986328 27.998 2.46094 27.998L14.834 27.998C16.3086 27.998 17.2949 26.9629 17.2949 25.4199L17.2949 24.5605Z", fillAlpha = 0.85f)
        }
        return _sFRefrigeratorFill!!
    }

private var _sFRefrigeratorFill: ImageVector? = null
