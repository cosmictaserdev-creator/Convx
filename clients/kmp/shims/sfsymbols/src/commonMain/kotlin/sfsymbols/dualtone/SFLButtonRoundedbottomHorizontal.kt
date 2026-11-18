package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLButtonRoundedbottomHorizontal (dualtone)
 * Viewport: 28.9941 x 21.6504
 */
public val SfSymbols.Dualtone.SFLButtonRoundedbottomHorizontal: ImageVector
    get() {
        if (_sFLButtonRoundedbottomHorizontal != null) {
            return _sFLButtonRoundedbottomHorizontal!!
        }
        _sFLButtonRoundedbottomHorizontal = sfIcon(
            name = "Dualtone.SFLButtonRoundedbottomHorizontal",
            viewportWidth = 28.9941f,
            viewportHeight = 21.6504f
        ) {
            addSfPath("M10.2441 21.6309L18.3887 21.6309C25.5371 21.6309 28.6328 18.3203 28.6328 11.6406L28.6328 3.67188C28.6328 1.25977 27.373 0 24.9316 0L3.71094 0C1.25977 0 0 1.25 0 3.67188L0 11.6406C0 18.3203 3.10547 21.6309 10.2441 21.6309ZM10.2637 19.9023C4.25781 19.9023 1.72852 17.2559 1.72852 11.7383L1.72852 3.75977C1.72852 2.42188 2.43164 1.73828 3.75 1.73828L24.8828 1.73828C26.1816 1.73828 26.9043 2.42188 26.9043 3.75977L26.9043 11.7383C26.9043 17.2559 24.375 19.9023 18.3789 19.9023Z", fillAlpha = 0.425f)
            addSfPath("M12.2656 15.9375L17.2559 15.9375C17.6758 15.9375 18.0273 15.6445 18.0273 15.2051C18.0273 14.7656 17.666 14.4531 17.2559 14.4531L13.1543 14.4531L13.1543 6.51367C13.1543 5.9375 12.8027 5.54688 12.2656 5.54688C11.7188 5.54688 11.4062 5.94727 11.4062 6.51367L11.4062 14.9902C11.4062 15.5469 11.7285 15.9375 12.2656 15.9375Z", fillAlpha = 0.85f)
        }
        return _sFLButtonRoundedbottomHorizontal!!
    }

private var _sFLButtonRoundedbottomHorizontal: ImageVector? = null
