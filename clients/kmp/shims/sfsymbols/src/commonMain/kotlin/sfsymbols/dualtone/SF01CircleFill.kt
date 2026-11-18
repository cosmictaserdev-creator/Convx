package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF01CircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF01CircleFill: ImageVector
    get() {
        if (_sF01CircleFill != null) {
            return _sF01CircleFill!!
        }
        _sF01CircleFill = sfIcon(
            name = "Dualtone.SF01CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.64844 18.1934C7.12891 18.1934 6.41602 15.2441 6.41602 12.7246C6.41602 10.2051 7.12891 7.25586 9.64844 7.25586C12.1777 7.25586 12.9004 10.2051 12.9004 12.7246C12.9004 15.2441 12.1777 18.1934 9.64844 18.1934ZM9.64844 16.8359C10.8496 16.8359 11.2305 14.873 11.2305 12.7246C11.2305 10.5664 10.8496 8.61328 9.64844 8.61328C8.44727 8.61328 8.06641 10.5664 8.06641 12.7246C8.06641 14.873 8.44727 16.8359 9.64844 16.8359ZM17.2266 18.0762C16.7578 18.0762 16.3867 17.7051 16.3867 17.2363L16.3867 9.11133L15.0977 10.1367C14.9805 10.2344 14.873 10.293 14.6875 10.293C14.3359 10.293 14.0918 10.0391 14.0918 9.67773C14.0918 9.38477 14.2578 9.16992 14.4531 9.01367L15.9668 7.8125C16.2305 7.60742 16.6211 7.34375 17.0898 7.34375C17.6758 7.34375 18.0371 7.69531 18.0371 8.27148L18.0371 17.2363C18.0371 17.7051 17.666 18.0762 17.2266 18.0762Z", fillAlpha = 0.85f)
        }
        return _sF01CircleFill!!
    }

private var _sF01CircleFill: ImageVector? = null
