package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDownCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFChevronDownCircleFill: ImageVector
    get() {
        if (_sFChevronDownCircleFill != null) {
            return _sFChevronDownCircleFill!!
        }
        _sFChevronDownCircleFill = sfIcon(
            name = "Monochrome.SFChevronDownCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM6.35742 10.1953C6.04492 10.5273 6.05469 11.0352 6.39648 11.3965L11.6309 16.8945C12.3242 17.6367 13.1348 17.627 13.8379 16.8945L19.0625 11.3965C19.4043 11.0352 19.4238 10.5176 19.1016 10.1953C18.75 9.85352 18.2129 9.83398 17.8906 10.1855L12.7344 15.6055L7.56836 10.1855C7.24609 9.83398 6.69922 9.84375 6.35742 10.1953Z", fillAlpha = 0.85f)
        }
        return _sFChevronDownCircleFill!!
    }

private var _sFChevronDownCircleFill: ImageVector? = null
