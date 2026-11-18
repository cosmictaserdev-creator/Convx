package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLeftCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLeftCircleFill: ImageVector
    get() {
        if (_sFLeftCircleFill != null) {
            return _sFLeftCircleFill!!
        }
        _sFLeftCircleFill = sfIcon(
            name = "Dualtone.SFLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M10.2441 18.4766C9.67773 18.4766 9.36523 18.0664 9.36523 17.4707L9.36523 7.64648C9.36523 7.05078 9.67773 6.64062 10.2441 6.64062C10.8301 6.64062 11.1523 7.03125 11.1523 7.64648L11.1523 16.9922L16.2402 16.9922C16.6797 16.9922 16.9922 17.2656 16.9922 17.7246C16.9922 18.1836 16.6895 18.4766 16.2402 18.4766Z", fillAlpha = 0.85f)
        }
        return _sFLeftCircleFill!!
    }

private var _sFLeftCircleFill: ImageVector? = null
