package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFScalemassFill (monochrome)
 * Viewport: 25.0042 x 26.2793
 */
public val SfSymbols.Monochrome.SFScalemassFill: ImageVector
    get() {
        if (_sFScalemassFill != null) {
            return _sFScalemassFill!!
        }
        _sFScalemassFill = sfIcon(
            name = "Monochrome.SFScalemassFill",
            viewportWidth = 25.0042f,
            viewportHeight = 26.2793f
        ) {
            addSfPath("M0.182746 22.3242C-0.45202 24.8438 0.583137 26.2695 3.03431 26.2695L21.6085 26.2695C24.0597 26.2695 25.1046 24.8438 24.4699 22.3242L21.6476 11.0938C21.1984 9.27734 19.9288 8.26172 18.1417 8.26172L6.50111 8.26172C4.714 8.26172 3.45423 9.27734 2.99525 11.0938ZM11.462 9.46289L13.1906 9.46289L13.1906 6.16211L11.462 6.16211ZM12.3214 7.02148C14.2452 7.02148 15.8468 5.43945 15.8468 3.52539C15.8468 1.5918 14.2355 0 12.3214 0C10.4074 0 8.80579 1.60156 8.80579 3.52539C8.80579 5.43945 10.4074 7.02148 12.3214 7.02148ZM12.3214 5.50781C11.2374 5.50781 10.3292 4.60938 10.3292 3.52539C10.3292 2.43164 11.2277 1.51367 12.3214 1.51367C13.4152 1.51367 14.3234 2.43164 14.3234 3.52539C14.3234 4.59961 13.4152 5.50781 12.3214 5.50781Z", fillAlpha = 0.85f)
        }
        return _sFScalemassFill!!
    }

private var _sFScalemassFill: ImageVector? = null
