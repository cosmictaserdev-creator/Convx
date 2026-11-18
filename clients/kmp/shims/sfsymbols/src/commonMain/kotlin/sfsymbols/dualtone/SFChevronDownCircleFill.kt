package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDownCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFChevronDownCircleFill: ImageVector
    get() {
        if (_sFChevronDownCircleFill != null) {
            return _sFChevronDownCircleFill!!
        }
        _sFChevronDownCircleFill = sfIcon(
            name = "Dualtone.SFChevronDownCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M13.8379 16.8945C13.1348 17.627 12.3242 17.6367 11.6309 16.8945L6.39648 11.3965C6.05469 11.0352 6.04492 10.5273 6.35742 10.1953C6.69922 9.84375 7.24609 9.83398 7.56836 10.1855L12.7344 15.6055L17.8906 10.1855C18.2129 9.83398 18.75 9.85352 19.1016 10.1953C19.4238 10.5176 19.4043 11.0352 19.0625 11.3965Z", fillAlpha = 0.85f)
        }
        return _sFChevronDownCircleFill!!
    }

private var _sFChevronDownCircleFill: ImageVector? = null
