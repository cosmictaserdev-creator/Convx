package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLessthanCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLessthanCircleFill: ImageVector
    get() {
        if (_sFLessthanCircleFill != null) {
            return _sFLessthanCircleFill!!
        }
        _sFLessthanCircleFill = sfIcon(
            name = "Monochrome.SFLessthanCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.4688 7.62695L7.88086 11.7383C7.44141 11.9824 7.22656 12.3047 7.22656 12.7246C7.22656 13.1934 7.42188 13.4766 7.88086 13.7109L15.4688 17.7051C15.6445 17.793 15.791 17.8516 16.0156 17.8516C16.543 17.8516 16.9434 17.4609 16.9434 16.9531C16.9434 16.5332 16.748 16.2695 16.2891 16.0547L9.67773 12.7344L9.67773 12.6562L16.2891 9.28711C16.7383 9.0625 16.9434 8.78906 16.9434 8.36914C16.9434 7.87109 16.5527 7.49023 16.0352 7.49023C15.791 7.49023 15.6543 7.53906 15.4688 7.62695Z", fillAlpha = 0.85f)
        }
        return _sFLessthanCircleFill!!
    }

private var _sFLessthanCircleFill: ImageVector? = null
