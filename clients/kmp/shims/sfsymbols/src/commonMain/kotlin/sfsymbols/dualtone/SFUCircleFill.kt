package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFUCircleFill: ImageVector
    get() {
        if (_sFUCircleFill != null) {
            return _sFUCircleFill!!
        }
        _sFUCircleFill = sfIcon(
            name = "Dualtone.SFUCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 18.7402C9.80469 18.7402 7.8125 17.041 7.8125 14.3848L7.8125 7.64648C7.8125 7.02148 8.1543 6.65039 8.70117 6.65039C9.27734 6.65039 9.59961 7.02148 9.59961 7.64648L9.59961 14.2188C9.59961 16.0742 10.8594 17.2461 12.7148 17.2461C14.5703 17.2461 15.8301 16.0742 15.8301 14.2188L15.8301 7.64648C15.8301 7.02148 16.1621 6.65039 16.7188 6.65039C17.2852 6.65039 17.6172 7.02148 17.6172 7.64648L17.6172 14.3848C17.6172 17.041 15.625 18.7402 12.7148 18.7402Z", fillAlpha = 0.85f)
        }
        return _sFUCircleFill!!
    }

private var _sFUCircleFill: ImageVector? = null
