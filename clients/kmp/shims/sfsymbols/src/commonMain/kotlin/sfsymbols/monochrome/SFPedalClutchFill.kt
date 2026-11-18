package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPedalClutchFill (monochrome)
 * Viewport: 21.4522 x 26.9434
 */
public val SfSymbols.Monochrome.SFPedalClutchFill: ImageVector
    get() {
        if (_sFPedalClutchFill != null) {
            return _sFPedalClutchFill!!
        }
        _sFPedalClutchFill = sfIcon(
            name = "Monochrome.SFPedalClutchFill",
            viewportWidth = 21.4522f,
            viewportHeight = 26.9434f
        ) {
            addSfPath("M17.3716 1.25C19.8228 1.86523 21.0923 3.32031 21.0923 5.56641L21.0825 23.3008C21.0825 25.5859 19.7153 26.9434 17.4107 26.9434L5.67237 26.9434C2.77198 26.9434 0.818855 25.2539 0.447761 22.4609C-0.177239 17.832-0.147942 10.5469 0.535652 5.41992C1.063 1.5918 3.70948-0.0585938 8.03565 0.0292969C11.3853 0.0878906 14.4321 0.517578 17.3716 1.25ZM5.26221 17.9199C4.85206 17.9199 4.51026 18.2617 4.51026 18.6621C4.51026 19.0723 4.85206 19.4043 5.26221 19.4043L16.1314 19.4043C16.5415 19.4043 16.8735 19.0723 16.8735 18.6621C16.8735 18.2617 16.5415 17.9199 16.1314 17.9199ZM5.26221 13.6426C4.85206 13.6426 4.51026 13.9746 4.51026 14.3848C4.51026 14.7852 4.85206 15.1172 5.26221 15.1172L16.1314 15.1172C16.5415 15.1172 16.8735 14.7852 16.8735 14.3848C16.8735 13.9746 16.5415 13.6426 16.1314 13.6426ZM5.26221 9.35547C4.85206 9.35547 4.51026 9.6875 4.51026 10.0977C4.51026 10.5078 4.85206 10.8398 5.26221 10.8398L16.1314 10.8398C16.5415 10.8398 16.8735 10.5078 16.8735 10.0977C16.8735 9.6875 16.5415 9.35547 16.1314 9.35547Z", fillAlpha = 0.85f)
        }
        return _sFPedalClutchFill!!
    }

private var _sFPedalClutchFill: ImageVector? = null
