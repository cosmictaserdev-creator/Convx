package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonRoundedbottomHorizontal (dualtone)
 * Viewport: 28.9941 x 21.6504
 */
public val SfSymbols.Dualtone.SFButtonRoundedbottomHorizontal: ImageVector
    get() {
        if (_sFButtonRoundedbottomHorizontal != null) {
            return _sFButtonRoundedbottomHorizontal!!
        }
        _sFButtonRoundedbottomHorizontal = sfIcon(
            name = "Dualtone.SFButtonRoundedbottomHorizontal",
            viewportWidth = 28.9941f,
            viewportHeight = 21.6504f
        ) {
            addSfPath("M10.2441 21.6309L18.3887 21.6309C25.5371 21.6309 28.6328 18.3203 28.6328 11.6406L28.6328 3.67188C28.6328 1.25977 27.373 0 24.9316 0L3.71094 0C1.25977 0 0 1.25 0 3.67188L0 11.6406C0 18.3203 3.10547 21.6309 10.2441 21.6309ZM10.2637 19.9023C4.25781 19.9023 1.72852 17.2559 1.72852 11.7383L1.72852 3.75977C1.72852 2.42188 2.43164 1.73828 3.75 1.73828L24.8828 1.73828C26.1816 1.73828 26.9043 2.42188 26.9043 3.75977L26.9043 11.7383C26.9043 17.2559 24.375 19.9023 18.3789 19.9023Z", fillAlpha = 0.85f)
        }
        return _sFButtonRoundedbottomHorizontal!!
    }

private var _sFButtonRoundedbottomHorizontal: ImageVector? = null
