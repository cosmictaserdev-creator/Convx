package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDoorLeftHandClosed (monochrome)
 * Viewport: 18.9453 x 26.4551
 */
public val SfSymbols.Monochrome.SFDoorLeftHandClosed: ImageVector
    get() {
        if (_sFDoorLeftHandClosed != null) {
            return _sFDoorLeftHandClosed!!
        }
        _sFDoorLeftHandClosed = sfIcon(
            name = "Monochrome.SFDoorLeftHandClosed",
            viewportWidth = 18.9453f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M0.869141 26.4355C1.34766 26.4355 1.73828 26.0449 1.73828 25.5664L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L16.2109 1.73828C16.5918 1.73828 16.8457 2.00195 16.8457 2.40234L16.8457 25.5664C16.8457 26.0449 17.2266 26.4355 17.7148 26.4355C18.1934 26.4355 18.584 26.0449 18.584 25.5664L18.584 2.28516C18.584 0.917969 17.6465 0 16.2402 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516L0 25.5664C0 26.0449 0.380859 26.4355 0.869141 26.4355Z", fillAlpha = 0.85f)
            addSfPath("M3.4668 26.0156L15.1172 26.0156C15.3223 26.0156 15.4883 25.8496 15.4883 25.6445L15.4883 3.4668C15.4883 3.26172 15.3223 3.0957 15.1172 3.0957L3.4668 3.0957C3.26172 3.0957 3.0957 3.26172 3.0957 3.4668L3.0957 25.6445C3.0957 25.8496 3.26172 26.0156 3.4668 26.0156ZM12.5488 15.7129C12.0215 15.7129 11.6016 15.293 11.6016 14.7559C11.6016 14.2188 12.0215 13.7988 12.5488 13.7988C13.0859 13.7988 13.5059 14.2188 13.5059 14.7559C13.5059 15.293 13.0859 15.7129 12.5488 15.7129Z", fillAlpha = 0.85f)
        }
        return _sFDoorLeftHandClosed!!
    }

private var _sFDoorLeftHandClosed: ImageVector? = null
