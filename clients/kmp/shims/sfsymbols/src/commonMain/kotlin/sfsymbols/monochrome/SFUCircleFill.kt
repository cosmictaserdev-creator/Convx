package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFUCircleFill: ImageVector
    get() {
        if (_sFUCircleFill != null) {
            return _sFUCircleFill!!
        }
        _sFUCircleFill = sfIcon(
            name = "Monochrome.SFUCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.8301 7.64648L15.8301 14.2188C15.8301 16.0742 14.5703 17.2461 12.7148 17.2461C10.8594 17.2461 9.59961 16.0742 9.59961 14.2188L9.59961 7.64648C9.59961 7.02148 9.27734 6.65039 8.70117 6.65039C8.1543 6.65039 7.8125 7.02148 7.8125 7.64648L7.8125 14.3848C7.8125 17.041 9.80469 18.7402 12.7148 18.7402C15.625 18.7402 17.6172 17.041 17.6172 14.3848L17.6172 7.64648C17.6172 7.02148 17.2852 6.65039 16.7188 6.65039C16.1621 6.65039 15.8301 7.02148 15.8301 7.64648Z", fillAlpha = 0.85f)
        }
        return _sFUCircleFill!!
    }

private var _sFUCircleFill: ImageVector? = null
