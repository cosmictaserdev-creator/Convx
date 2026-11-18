package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTvCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTvCircleFill: ImageVector
    get() {
        if (_sFTvCircleFill != null) {
            return _sFTvCircleFill!!
        }
        _sFTvCircleFill = sfIcon(
            name = "Dualtone.SFTvCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M6.83594 17.168C5.83008 17.168 5.11719 16.4551 5.11719 15.459L5.11719 8.69141C5.11719 7.67578 5.83008 6.96289 6.83594 6.96289L18.5742 6.96289C19.5801 6.96289 20.3027 7.67578 20.3027 8.69141L20.3027 15.459C20.3027 16.4551 19.5801 17.168 18.5742 17.168ZM6.96289 15.9961L18.457 15.9961C18.8281 15.9961 19.082 15.7324 19.082 15.3613L19.082 8.7793C19.082 8.39844 18.8281 8.13477 18.457 8.13477L6.96289 8.13477C6.60156 8.13477 6.33789 8.39844 6.33789 8.7793L6.33789 15.3613C6.33789 15.7324 6.60156 15.9961 6.96289 15.9961ZM9.27734 19.7559C8.95508 19.7559 8.69141 19.4922 8.69141 19.1602C8.69141 18.8379 8.95508 18.5742 9.27734 18.5742L16.1719 18.5742C16.4941 18.5742 16.7578 18.8379 16.7578 19.1602C16.7578 19.4922 16.4941 19.7559 16.1719 19.7559Z", fillAlpha = 0.85f)
        }
        return _sFTvCircleFill!!
    }

private var _sFTvCircleFill: ImageVector? = null
