package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonRoundedbottomHorizontalFill (dualtone)
 * Viewport: 28.9941 x 21.6504
 */
public val SfSymbols.Dualtone.SFButtonRoundedbottomHorizontalFill: ImageVector
    get() {
        if (_sFButtonRoundedbottomHorizontalFill != null) {
            return _sFButtonRoundedbottomHorizontalFill!!
        }
        _sFButtonRoundedbottomHorizontalFill = sfIcon(
            name = "Dualtone.SFButtonRoundedbottomHorizontalFill",
            viewportWidth = 28.9941f,
            viewportHeight = 21.6504f
        ) {
            addSfPath("M10.2441 21.6309L18.3887 21.6309C25.5371 21.6309 28.6328 18.3203 28.6328 11.6406L28.6328 3.67188C28.6328 1.25977 27.373 0 24.9316 0L3.71094 0C1.25977 0 0 1.25 0 3.67188L0 11.6406C0 18.3203 3.10547 21.6309 10.2441 21.6309Z", fillAlpha = 0.85f)
        }
        return _sFButtonRoundedbottomHorizontalFill!!
    }

private var _sFButtonRoundedbottomHorizontalFill: ImageVector? = null
