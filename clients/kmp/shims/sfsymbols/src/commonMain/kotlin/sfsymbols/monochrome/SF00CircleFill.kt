package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF00CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF00CircleFill: ImageVector
    get() {
        if (_sF00CircleFill != null) {
            return _sF00CircleFill!!
        }
        _sF00CircleFill = sfIcon(
            name = "Monochrome.SF00CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM5.46875 12.7246C5.46875 15.2441 6.19141 18.1934 8.7207 18.1934C11.2402 18.1934 11.9531 15.2441 11.9531 12.7246C11.9531 10.2051 11.2402 7.25586 8.7207 7.25586C6.19141 7.25586 5.46875 10.2051 5.46875 12.7246ZM13.4766 12.7246C13.4766 15.2441 14.1992 18.1934 16.7188 18.1934C19.248 18.1934 19.9609 15.2441 19.9609 12.7246C19.9609 10.2051 19.248 7.25586 16.7188 7.25586C14.1992 7.25586 13.4766 10.2051 13.4766 12.7246ZM10.3027 12.7246C10.3027 14.873 9.92188 16.8359 8.7207 16.8359C7.50977 16.8359 7.13867 14.873 7.13867 12.7246C7.13867 10.5664 7.50977 8.61328 8.7207 8.61328C9.92188 8.61328 10.3027 10.5664 10.3027 12.7246ZM18.3008 12.7246C18.3008 14.873 17.9199 16.8359 16.7188 16.8359C15.5078 16.8359 15.1367 14.873 15.1367 12.7246C15.1367 10.5664 15.5078 8.61328 16.7188 8.61328C17.9199 8.61328 18.3008 10.5664 18.3008 12.7246Z", fillAlpha = 0.85f)
        }
        return _sF00CircleFill!!
    }

private var _sF00CircleFill: ImageVector? = null
