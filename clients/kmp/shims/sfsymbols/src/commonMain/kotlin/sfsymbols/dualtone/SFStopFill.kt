package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStopFill (dualtone)
 * Viewport: 20.5957 x 20.2637
 */
public val SfSymbols.Dualtone.SFStopFill: ImageVector
    get() {
        if (_sFStopFill != null) {
            return _sFStopFill!!
        }
        _sFStopFill = sfIcon(
            name = "Dualtone.SFStopFill",
            viewportWidth = 20.5957f,
            viewportHeight = 20.2637f
        ) {
            addSfPath("M0 17.6074C0 19.2676 0.996094 20.2441 2.67578 20.2441L17.5586 20.2441C19.248 20.2441 20.2344 19.2676 20.2344 17.6074L20.2344 2.63672C20.2344 0.976562 19.248 0 17.5586 0L2.67578 0C0.996094 0 0 0.976562 0 2.63672Z", fillAlpha = 0.85f)
        }
        return _sFStopFill!!
    }

private var _sFStopFill: ImageVector? = null
