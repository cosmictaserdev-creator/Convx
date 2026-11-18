package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDoorRightHandClosed (dualtone)
 * Viewport: 18.9453 x 26.4551
 */
public val SfSymbols.Dualtone.SFDoorRightHandClosed: ImageVector
    get() {
        if (_sFDoorRightHandClosed != null) {
            return _sFDoorRightHandClosed!!
        }
        _sFDoorRightHandClosed = sfIcon(
            name = "Dualtone.SFDoorRightHandClosed",
            viewportWidth = 18.9453f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M17.7148 26.4355C18.1934 26.4355 18.584 26.0449 18.584 25.5664L18.584 2.28516C18.584 0.917969 17.6465 0 16.2402 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516L0 25.5664C0 26.0449 0.380859 26.4355 0.869141 26.4355C1.34766 26.4355 1.73828 26.0449 1.73828 25.5664L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L16.2109 1.73828C16.5918 1.73828 16.8457 2.00195 16.8457 2.40234L16.8457 25.5664C16.8457 26.0449 17.2266 26.4355 17.7148 26.4355Z", fillAlpha = 0.425f)
            addSfPath("M15.1172 26.0156C15.3223 26.0156 15.4883 25.8496 15.4883 25.6445L15.4883 3.4668C15.4883 3.26172 15.3223 3.0957 15.1172 3.0957L3.4668 3.0957C3.26172 3.0957 3.0957 3.26172 3.0957 3.4668L3.0957 25.6445C3.0957 25.8496 3.26172 26.0156 3.4668 26.0156ZM6.02539 15.7129C5.49805 15.7129 5.07812 15.293 5.07812 14.7559C5.07812 14.2188 5.49805 13.7988 6.02539 13.7988C6.5625 13.7988 6.98242 14.2188 6.98242 14.7559C6.98242 15.293 6.5625 15.7129 6.02539 15.7129Z", fillAlpha = 0.85f)
        }
        return _sFDoorRightHandClosed!!
    }

private var _sFDoorRightHandClosed: ImageVector? = null
