package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF10CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF10CircleFill: ImageVector
    get() {
        if (_sF10CircleFill != null) {
            return _sF10CircleFill!!
        }
        _sF10CircleFill = sfIcon(
            name = "Monochrome.SF10CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.1875 12.7246C12.1875 15.2441 12.9004 18.1934 15.4297 18.1934C17.9492 18.1934 18.6621 15.2441 18.6621 12.7246C18.6621 10.2051 17.9492 7.25586 15.4297 7.25586C12.9004 7.25586 12.1875 10.2051 12.1875 12.7246ZM8.05664 7.8125L6.55273 9.01367C6.34766 9.16992 6.20117 9.38477 6.20117 9.67773C6.20117 10.0391 6.42578 10.293 6.77734 10.293C6.96289 10.293 7.07031 10.2344 7.19727 10.1367L8.47656 9.11133L8.47656 17.2363C8.47656 17.7051 8.84766 18.0762 9.31641 18.0762C9.75586 18.0762 10.1367 17.7051 10.1367 17.2363L10.1367 8.27148C10.1367 7.69531 9.76562 7.34375 9.18945 7.34375C8.71094 7.34375 8.33008 7.60742 8.05664 7.8125ZM17.0117 12.7246C17.0117 14.873 16.6309 16.8359 15.4297 16.8359C14.2188 16.8359 13.8477 14.873 13.8477 12.7246C13.8477 10.5664 14.2188 8.61328 15.4297 8.61328C16.6309 8.61328 17.0117 10.5664 17.0117 12.7246Z", fillAlpha = 0.85f)
        }
        return _sF10CircleFill!!
    }

private var _sF10CircleFill: ImageVector? = null
