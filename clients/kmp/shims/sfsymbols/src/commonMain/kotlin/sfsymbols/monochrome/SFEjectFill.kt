package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEjectFill (monochrome)
 * Viewport: 24.4922 x 23.3496
 */
public val SfSymbols.Monochrome.SFEjectFill: ImageVector
    get() {
        if (_sFEjectFill != null) {
            return _sFEjectFill!!
        }
        _sFEjectFill = sfIcon(
            name = "Monochrome.SFEjectFill",
            viewportWidth = 24.4922f,
            viewportHeight = 23.3496f
        ) {
            addSfPath("M1.69922 14.2578L22.4121 14.2578C23.3496 14.2578 23.8867 13.7598 23.8867 12.9395C23.8867 12.5488 23.7012 12.1289 23.3691 11.7676L13.4961 0.722656C13.0957 0.292969 12.627 0.00976562 12.0508 0.00976562C11.4941 0.00976562 10.9961 0.3125 10.625 0.722656L0.742188 11.7676C0.400391 12.1387 0.214844 12.5488 0.224609 12.9395C0.234375 13.7598 0.761719 14.2578 1.69922 14.2578ZM1.97266 23.3496L22.1582 23.3496C23.3887 23.3496 24.1309 22.5879 24.1309 21.3477L24.1309 19.209C24.1309 17.9688 23.3887 17.207 22.1582 17.207L1.97266 17.207C0.742188 17.207 0 17.9688 0 19.209L0 21.3477C0 22.5879 0.742188 23.3496 1.97266 23.3496Z", fillAlpha = 0.85f)
        }
        return _sFEjectFill!!
    }

private var _sFEjectFill: ImageVector? = null
