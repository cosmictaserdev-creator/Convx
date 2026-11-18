package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClockFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFClockFill: ImageVector
    get() {
        if (_sFClockFill != null) {
            return _sFClockFill!!
        }
        _sFClockFill = sfIcon(
            name = "Monochrome.SFClockFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M5.95703 14.0039C5.52734 14.0039 5.20508 13.6719 5.20508 13.2422C5.20508 12.8223 5.52734 12.4902 5.95703 12.4902L11.9531 12.4902L11.9531 4.53125C11.9531 4.11133 12.2949 3.7793 12.7051 3.7793C13.1348 3.7793 13.4668 4.11133 13.4668 4.53125L13.4668 13.2422C13.4668 13.6719 13.1348 14.0039 12.7051 14.0039ZM12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFClockFill!!
    }

private var _sFClockFill: ImageVector? = null
