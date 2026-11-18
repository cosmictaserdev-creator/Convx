package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTvCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTvCircleFill: ImageVector
    get() {
        if (_sFTvCircleFill != null) {
            return _sFTvCircleFill!!
        }
        _sFTvCircleFill = sfIcon(
            name = "Monochrome.SFTvCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.27734 18.5742C8.95508 18.5742 8.69141 18.8379 8.69141 19.1602C8.69141 19.4922 8.95508 19.7559 9.27734 19.7559L16.1719 19.7559C16.4941 19.7559 16.7578 19.4922 16.7578 19.1602C16.7578 18.8379 16.4941 18.5742 16.1719 18.5742ZM6.83594 6.96289C5.83008 6.96289 5.11719 7.67578 5.11719 8.69141L5.11719 15.459C5.11719 16.4551 5.83008 17.168 6.83594 17.168L18.5742 17.168C19.5801 17.168 20.3027 16.4551 20.3027 15.459L20.3027 8.69141C20.3027 7.67578 19.5801 6.96289 18.5742 6.96289ZM19.082 8.7793L19.082 15.3613C19.082 15.7324 18.8281 15.9961 18.457 15.9961L6.96289 15.9961C6.60156 15.9961 6.33789 15.7324 6.33789 15.3613L6.33789 8.7793C6.33789 8.39844 6.60156 8.13477 6.96289 8.13477L18.457 8.13477C18.8281 8.13477 19.082 8.39844 19.082 8.7793Z", fillAlpha = 0.85f)
        }
        return _sFTvCircleFill!!
    }

private var _sFTvCircleFill: ImageVector? = null
