package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAlt (monochrome)
 * Viewport: 26.1426 x 23.1348
 */
public val SfSymbols.Monochrome.SFAlt: ImageVector
    get() {
        if (_sFAlt != null) {
            return _sFAlt!!
        }
        _sFAlt = sfIcon(
            name = "Monochrome.SFAlt",
            viewportWidth = 26.1426f,
            viewportHeight = 23.1348f
        ) {
            addSfPath("M1.00586 23.1348L7.12891 23.1348C8.16406 23.1348 9.01367 22.5586 9.42383 21.6309L17.9199 2.55859C18.1152 2.13867 18.457 1.93359 18.9746 1.93359L24.7754 1.93359C25.332 1.93359 25.7812 1.49414 25.7812 0.966797C25.7812 0.429688 25.332 0 24.7754 0L18.5352 0C17.4219 0 16.6895 0.478516 16.2598 1.45508L7.74414 20.5469C7.54883 20.9668 7.17773 21.2012 6.71875 21.2012L1.00586 21.2012C0.439453 21.2012 0 21.6309 0 22.168C0 22.7148 0.439453 23.1348 1.00586 23.1348ZM16.25 23.1348L24.7754 23.1348C25.3418 23.1348 25.7715 22.7246 25.7715 22.1777C25.7715 21.6406 25.3418 21.2207 24.7754 21.2207L16.25 21.2207C15.6836 21.2207 15.2539 21.6406 15.2539 22.1777C15.2539 22.7246 15.6836 23.1348 16.25 23.1348Z", fillAlpha = 0.85f)
        }
        return _sFAlt!!
    }

private var _sFAlt: ImageVector? = null
