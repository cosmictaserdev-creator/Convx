package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMove3d (monochrome)
 * Viewport: 26.6496 x 23.9454
 */
public val SfSymbols.Monochrome.SFMove3d: ImageVector
    get() {
        if (_sFMove3d != null) {
            return _sFMove3d!!
        }
        _sFMove3d = sfIcon(
            name = "Monochrome.SFMove3d",
            viewportWidth = 26.6496f,
            viewportHeight = 23.9454f
        ) {
            addSfPath("M3.3492 19.3311L4.21834 20.8155L13.1441 16.333L22.0797 20.8155L22.939 19.3311L14.0133 14.8584L14.0133 4.28225L12.2847 4.28225L12.2847 14.8584ZM0.146078 21.1084C-0.205485 21.8799 0.0874842 22.3682 0.917562 22.4561L6.08358 22.9834C7.11873 23.1006 7.76327 22.4268 7.21639 21.499L4.12069 16.1475C3.58358 15.2198 2.69491 15.4248 2.27498 16.3721ZM26.1519 21.1084L24.0133 16.3721C23.5933 15.4248 22.7047 15.2198 22.1773 16.1475L19.0719 21.499C18.5347 22.4268 19.1695 23.1006 20.2144 22.9834L25.3707 22.4561C26.2008 22.3682 26.4937 21.8799 26.1519 21.1084ZM12.3531 0.512721L9.32577 4.71194C8.70077 5.55178 8.98397 6.45022 10.0484 6.45022L16.2203 6.45022C17.3238 6.45022 17.5777 5.57132 16.9527 4.71194L13.9156 0.512721C13.4176-0.161107 12.8512-0.180638 12.3531 0.512721Z", fillAlpha = 0.85f)
        }
        return _sFMove3d!!
    }

private var _sFMove3d: ImageVector? = null
