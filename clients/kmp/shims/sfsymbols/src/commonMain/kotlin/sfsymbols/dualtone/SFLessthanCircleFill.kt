package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLessthanCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLessthanCircleFill: ImageVector
    get() {
        if (_sFLessthanCircleFill != null) {
            return _sFLessthanCircleFill!!
        }
        _sFLessthanCircleFill = sfIcon(
            name = "Dualtone.SFLessthanCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M16.0156 17.8516C15.791 17.8516 15.6445 17.793 15.4688 17.7051L7.88086 13.7109C7.42188 13.4766 7.22656 13.1934 7.22656 12.7246C7.22656 12.3047 7.44141 11.9824 7.88086 11.7383L15.4688 7.62695C15.6543 7.53906 15.791 7.49023 16.0352 7.49023C16.5527 7.49023 16.9434 7.87109 16.9434 8.36914C16.9434 8.78906 16.7383 9.0625 16.2891 9.28711L9.67773 12.6562L9.67773 12.7344L16.2891 16.0547C16.748 16.2695 16.9434 16.5332 16.9434 16.9531C16.9434 17.4609 16.543 17.8516 16.0156 17.8516Z", fillAlpha = 0.85f)
        }
        return _sFLessthanCircleFill!!
    }

private var _sFLessthanCircleFill: ImageVector? = null
