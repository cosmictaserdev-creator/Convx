package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAlternatingcurrent (monochrome)
 * Viewport: 26.7554 x 9.73633
 */
public val SfSymbols.Monochrome.SFAlternatingcurrent: ImageVector
    get() {
        if (_sFAlternatingcurrent != null) {
            return _sFAlternatingcurrent!!
        }
        _sFAlternatingcurrent = sfIcon(
            name = "Monochrome.SFAlternatingcurrent",
            viewportWidth = 26.7554f,
            viewportHeight = 9.73633f
        ) {
            addSfPath("M24.6081 4.28711C23.3289 6.21094 21.8054 7.14844 19.9988 7.14844C14.5007 7.14844 12.3621 0 6.42456 0C3.97338 0 1.80541 1.32812 0.194086 3.81836C-0.548101 4.94141 1.02416 5.98633 1.78588 4.89258C3.07495 2.91016 4.64721 1.92383 6.42456 1.92383C11.239 1.92383 13.2996 9.08203 19.9988 9.08203C22.4499 9.08203 24.5789 7.79297 26.1999 5.37109C26.9421 4.25781 25.3503 3.18359 24.6081 4.28711Z", fillAlpha = 0.85f)
        }
        return _sFAlternatingcurrent!!
    }

private var _sFAlternatingcurrent: ImageVector? = null
