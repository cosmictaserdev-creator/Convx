package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownForwardCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowDownForwardCircleFill: ImageVector
    get() {
        if (_sFArrowDownForwardCircleFill != null) {
            return _sFArrowDownForwardCircleFill!!
        }
        _sFArrowDownForwardCircleFill = sfIcon(
            name = "Dualtone.SFArrowDownForwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M15.459 16.5137C16.2988 17.2949 17.3242 16.3281 16.4941 15.459L14.4629 13.3008L9.22852 8.04688C9.0625 7.89062 8.85742 7.79297 8.57422 7.79297C8.08594 7.79297 7.76367 8.11523 7.76367 8.62305C7.76367 8.84766 7.87109 9.0625 8.03711 9.22852L13.291 14.4727ZM16.0352 13.0469L16.3281 16.3574L13.252 16.0547L10.4395 16.0547C9.91211 16.0547 9.56055 16.377 9.56055 16.875C9.56055 17.3633 9.90234 17.6855 10.4199 17.6855L16.7578 17.6855C17.3145 17.6855 17.6758 17.4121 17.6758 16.7676L17.6758 10.4688C17.6758 9.94141 17.3438 9.58008 16.8652 9.58008C16.3672 9.58008 16.0352 9.91211 16.0352 10.4492Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownForwardCircleFill!!
    }

private var _sFArrowDownForwardCircleFill: ImageVector? = null
