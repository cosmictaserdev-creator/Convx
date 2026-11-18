package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRighttriangleFill (dualtone)
 * Viewport: 24.2578 x 25.6543
 */
public val SfSymbols.Dualtone.SFRighttriangleFill: ImageVector
    get() {
        if (_sFRighttriangleFill != null) {
            return _sFRighttriangleFill!!
        }
        _sFRighttriangleFill = sfIcon(
            name = "Dualtone.SFRighttriangleFill",
            viewportWidth = 24.2578f,
            viewportHeight = 25.6543f
        ) {
            addSfPath("M1.5918 23.2617L21.2012 23.2617C22.627 23.2617 23.2617 22.627 23.2617 21.1914L23.2617 1.5918C23.2617 0.615234 22.6367 0 21.6992 0C21.1035 0 20.6836 0.273438 20.2344 0.722656L0.712891 20.2441C0.263672 20.6836 0 21.1133 0 21.709C0 22.6465 0.605469 23.2617 1.5918 23.2617Z", fillAlpha = 0.85f)
        }
        return _sFRighttriangleFill!!
    }

private var _sFRighttriangleFill: ImageVector? = null
