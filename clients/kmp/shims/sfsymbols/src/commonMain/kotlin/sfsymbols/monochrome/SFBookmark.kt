package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookmark (monochrome)
 * Viewport: 17.002 x 26.6895
 */
public val SfSymbols.Monochrome.SFBookmark: ImageVector
    get() {
        if (_sFBookmark != null) {
            return _sFBookmark!!
        }
        _sFBookmark = sfIcon(
            name = "Monochrome.SFBookmark",
            viewportWidth = 17.002f,
            viewportHeight = 26.6895f
        ) {
            addSfPath("M1.21094 26.6602C1.74805 26.6602 2.08984 26.3574 3.03711 25.4297L8.21289 20.2637C8.27148 20.2051 8.36914 20.2051 8.42773 20.2637L13.6035 25.4297C14.5508 26.3477 14.8828 26.6602 15.4297 26.6602C16.1914 26.6602 16.6406 26.1426 16.6406 25.2637L16.6406 3.45703C16.6406 1.17188 15.4785 0 13.2129 0L3.42773 0C1.16211 0 0 1.17188 0 3.45703L0 25.2637C0 26.1426 0.449219 26.6602 1.21094 26.6602ZM2.08984 23.8086C1.9043 23.9941 1.69922 23.9453 1.69922 23.6816L1.69922 3.48633C1.69922 2.30469 2.31445 1.69922 3.50586 1.69922L13.1348 1.69922C14.3262 1.69922 14.9414 2.30469 14.9414 3.48633L14.9414 23.6816C14.9414 23.9453 14.7461 23.9941 14.5508 23.8086L8.92578 18.252C8.56445 17.9004 8.07617 17.9004 7.71484 18.252Z", fillAlpha = 0.85f)
        }
        return _sFBookmark!!
    }

private var _sFBookmark: ImageVector? = null
