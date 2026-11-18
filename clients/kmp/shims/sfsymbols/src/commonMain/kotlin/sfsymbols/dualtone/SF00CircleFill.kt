package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF00CircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF00CircleFill: ImageVector
    get() {
        if (_sF00CircleFill != null) {
            return _sF00CircleFill!!
        }
        _sF00CircleFill = sfIcon(
            name = "Dualtone.SF00CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.7207 18.1934C6.19141 18.1934 5.46875 15.2441 5.46875 12.7246C5.46875 10.2051 6.19141 7.25586 8.7207 7.25586C11.2402 7.25586 11.9531 10.2051 11.9531 12.7246C11.9531 15.2441 11.2402 18.1934 8.7207 18.1934ZM8.7207 16.8359C9.92188 16.8359 10.3027 14.873 10.3027 12.7246C10.3027 10.5664 9.92188 8.61328 8.7207 8.61328C7.50977 8.61328 7.13867 10.5664 7.13867 12.7246C7.13867 14.873 7.50977 16.8359 8.7207 16.8359ZM16.7188 18.1934C14.1992 18.1934 13.4766 15.2441 13.4766 12.7246C13.4766 10.2051 14.1992 7.25586 16.7188 7.25586C19.248 7.25586 19.9609 10.2051 19.9609 12.7246C19.9609 15.2441 19.248 18.1934 16.7188 18.1934ZM16.7188 16.8359C17.9199 16.8359 18.3008 14.873 18.3008 12.7246C18.3008 10.5664 17.9199 8.61328 16.7188 8.61328C15.5078 8.61328 15.1367 10.5664 15.1367 12.7246C15.1367 14.873 15.5078 16.8359 16.7188 16.8359Z", fillAlpha = 0.85f)
        }
        return _sF00CircleFill!!
    }

private var _sF00CircleFill: ImageVector? = null
