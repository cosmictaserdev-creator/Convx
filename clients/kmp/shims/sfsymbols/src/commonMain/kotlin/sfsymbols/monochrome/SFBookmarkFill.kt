package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookmarkFill (monochrome)
 * Viewport: 17.002 x 26.6895
 */
public val SfSymbols.Monochrome.SFBookmarkFill: ImageVector
    get() {
        if (_sFBookmarkFill != null) {
            return _sFBookmarkFill!!
        }
        _sFBookmarkFill = sfIcon(
            name = "Monochrome.SFBookmarkFill",
            viewportWidth = 17.002f,
            viewportHeight = 26.6895f
        ) {
            addSfPath("M1.21094 26.6602C1.74805 26.6602 2.08984 26.3574 3.03711 25.4297L8.21289 20.2637C8.27148 20.2051 8.36914 20.2051 8.42773 20.2637L13.6035 25.4297C14.5508 26.3477 14.8828 26.6602 15.4297 26.6602C16.1914 26.6602 16.6406 26.1426 16.6406 25.2637L16.6406 3.45703C16.6406 1.17188 15.4785 0 13.2129 0L3.42773 0C1.16211 0 0 1.17188 0 3.45703L0 25.2637C0 26.1426 0.449219 26.6602 1.21094 26.6602Z", fillAlpha = 0.85f)
        }
        return _sFBookmarkFill!!
    }

private var _sFBookmarkFill: ImageVector? = null
