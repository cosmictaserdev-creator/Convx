package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMediastick (monochrome)
 * Viewport: 33.3693 x 12.8027
 */
public val SfSymbols.Monochrome.SFMediastick: ImageVector
    get() {
        if (_sFMediastick != null) {
            return _sFMediastick!!
        }
        _sFMediastick = sfIcon(
            name = "Monochrome.SFMediastick",
            viewportWidth = 33.3693f,
            viewportHeight = 12.8027f
        ) {
            addSfPath("M4.12116 12.7734L22.0801 12.7734C25.1759 12.7734 26.2012 11.1816 26.2012 6.38672C26.211 1.58203 25.1856 0 22.0801 0L4.12116 0C1.01569 0-0.00969664 1.58203 6.89871e-05 6.38672C6.89871e-05 11.1816 1.02546 12.7734 4.12116 12.7734ZM4.12116 5.46875C3.40827 5.46875 2.84187 4.91211 2.84187 4.19922C2.84187 3.49609 3.40827 2.93945 4.12116 2.93945C4.82429 2.93945 5.38093 3.49609 5.38093 4.19922C5.38093 4.91211 4.82429 5.46875 4.12116 5.46875Z", fillAlpha = 0.85f)
            addSfPath("M27.461 9.89258L30.7423 9.89258C32.1192 9.89258 33.0079 9.0332 33.0079 7.75391L33.0079 5C33.0079 3.7207 32.1192 2.85156 30.7423 2.85156L27.461 2.85156C27.6661 3.83789 27.7637 5.00977 27.7637 6.38672C27.7637 7.74414 27.6563 8.91602 27.461 9.89258Z", fillAlpha = 0.85f)
        }
        return _sFMediastick!!
    }

private var _sFMediastick: ImageVector? = null
