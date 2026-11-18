package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOption (monochrome)
 * Viewport: 26.1426 x 23.1348
 */
public val SfSymbols.Monochrome.SFOption: ImageVector
    get() {
        if (_sFOption != null) {
            return _sFOption!!
        }
        _sFOption = sfIcon(
            name = "Monochrome.SFOption",
            viewportWidth = 26.1426f,
            viewportHeight = 23.1348f
        ) {
            addSfPath("M7.12891 0L1.00586 0C0.439453 0 0 0.419922 0 0.966797C0 1.50391 0.439453 1.93359 1.00586 1.93359L6.71875 1.93359C7.17773 1.93359 7.54883 2.16797 7.74414 2.58789L16.2598 21.6797C16.6895 22.6562 17.4219 23.1348 18.5352 23.1348L24.7754 23.1348C25.332 23.1348 25.7812 22.7051 25.7812 22.168C25.7812 21.6406 25.332 21.2012 24.7754 21.2012L18.9746 21.2012C18.457 21.2012 18.1152 20.9961 17.9199 20.5762L9.42383 1.50391C9.01367 0.576172 8.16406 0 7.12891 0ZM24.7754 0L16.25 0C15.6836 0 15.2539 0.410156 15.2539 0.957031C15.2539 1.49414 15.6836 1.91406 16.25 1.91406L24.7754 1.91406C25.3418 1.91406 25.7715 1.49414 25.7715 0.957031C25.7715 0.410156 25.3418 0 24.7754 0Z", fillAlpha = 0.85f)
        }
        return _sFOption!!
    }

private var _sFOption: ImageVector? = null
