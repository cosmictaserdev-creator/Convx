package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLCircleFill: ImageVector
    get() {
        if (_sFLCircleFill != null) {
            return _sFLCircleFill!!
        }
        _sFLCircleFill = sfIcon(
            name = "Monochrome.SFLCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.36523 7.64648L9.36523 17.4707C9.36523 18.0664 9.67773 18.4766 10.2441 18.4766L16.2402 18.4766C16.6895 18.4766 16.9922 18.1836 16.9922 17.7246C16.9922 17.2656 16.6797 16.9922 16.2402 16.9922L11.1523 16.9922L11.1523 7.64648C11.1523 7.03125 10.8301 6.64062 10.2441 6.64062C9.67773 6.64062 9.36523 7.05078 9.36523 7.64648Z", fillAlpha = 0.85f)
        }
        return _sFLCircleFill!!
    }

private var _sFLCircleFill: ImageVector? = null
