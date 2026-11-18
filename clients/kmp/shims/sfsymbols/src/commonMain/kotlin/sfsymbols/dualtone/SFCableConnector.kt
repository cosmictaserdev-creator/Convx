package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCableConnector (dualtone)
 * Viewport: 8.47656 x 28.3301
 */
public val SfSymbols.Dualtone.SFCableConnector: ImageVector
    get() {
        if (_sFCableConnector != null) {
            return _sFCableConnector!!
        }
        _sFCableConnector = sfIcon(
            name = "Dualtone.SFCableConnector",
            viewportWidth = 8.47656f,
            viewportHeight = 28.3301f
        ) {
            addSfPath("M3.13477 28.3301L4.99023 28.3301L4.99023 16.377L3.13477 16.377ZM1.47461 18.2715L6.64062 18.2715C7.67578 18.2715 8.11523 17.8418 8.11523 16.8066L8.11523 7.03125C8.11523 5.99609 7.67578 5.55664 6.64062 5.55664L1.47461 5.55664C0.439453 5.55664 0 5.99609 0 7.03125L0 16.8066C0 17.8418 0.439453 18.2715 1.47461 18.2715Z", fillAlpha = 0.85f)
            addSfPath("M1.67969 4.19922L6.43555 4.19922L6.43555 1.46484C6.43555 0.429688 5.98633 0 4.95117 0L3.16406 0C2.12891 0 1.67969 0.429688 1.67969 1.46484Z", fillAlpha = 0.425f)
        }
        return _sFCableConnector!!
    }

private var _sFCableConnector: ImageVector? = null
