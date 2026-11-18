package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF01CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF01CircleFill: ImageVector
    get() {
        if (_sF01CircleFill != null) {
            return _sF01CircleFill!!
        }
        _sF01CircleFill = sfIcon(
            name = "Monochrome.SF01CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM6.41602 12.7246C6.41602 15.2441 7.12891 18.1934 9.64844 18.1934C12.1777 18.1934 12.9004 15.2441 12.9004 12.7246C12.9004 10.2051 12.1777 7.25586 9.64844 7.25586C7.12891 7.25586 6.41602 10.2051 6.41602 12.7246ZM15.9668 7.8125L14.4531 9.01367C14.2578 9.16992 14.0918 9.38477 14.0918 9.67773C14.0918 10.0391 14.3359 10.293 14.6875 10.293C14.873 10.293 14.9805 10.2344 15.0977 10.1367L16.3867 9.11133L16.3867 17.2363C16.3867 17.7051 16.7578 18.0762 17.2266 18.0762C17.666 18.0762 18.0371 17.7051 18.0371 17.2363L18.0371 8.27148C18.0371 7.69531 17.6758 7.34375 17.0898 7.34375C16.6211 7.34375 16.2305 7.60742 15.9668 7.8125ZM11.2305 12.7246C11.2305 14.873 10.8496 16.8359 9.64844 16.8359C8.44727 16.8359 8.06641 14.873 8.06641 12.7246C8.06641 10.5664 8.44727 8.61328 9.64844 8.61328C10.8496 8.61328 11.2305 10.5664 11.2305 12.7246Z", fillAlpha = 0.85f)
        }
        return _sF01CircleFill!!
    }

private var _sF01CircleFill: ImageVector? = null
