package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSurfboard (monochrome)
 * Viewport: 30.8812 x 32.1171
 */
public val SfSymbols.Monochrome.SFSurfboard: ImageVector
    get() {
        if (_sFSurfboard != null) {
            return _sFSurfboard!!
        }
        _sFSurfboard = sfIcon(
            name = "Monochrome.SFSurfboard",
            viewportWidth = 30.8812f,
            viewportHeight = 32.1171f
        ) {
            addSfPath("M1.36833 30.751C2.882 32.2646 4.8156 32.5185 7.40349 31.5224C11.6906 29.872 16.6125 26.3174 21.2218 21.708C29.8254 13.1045 32.5207 6.53221 29.0539 3.06541C25.5871-0.401388 19.005 2.29392 10.4113 10.8974C5.80192 15.5068 2.24724 20.4287 0.58708 24.706C-0.399248 27.3037-0.145342 29.2373 1.36833 30.751ZM2.57927 29.54C1.47575 28.4267 1.39763 26.8447 2.30583 24.833C4.25895 20.624 7.63786 16.0928 11.6222 12.0986C19.2785 4.44236 25.0988 1.53221 27.8429 4.27635C30.5871 7.02049 27.6769 12.8408 20.0109 20.497C16.0265 24.4814 11.4953 27.8603 7.2863 29.8037C5.27458 30.7217 3.69255 30.6435 2.57927 29.54ZM1.68083 29.7451L2.36442 30.4287L28.6242 4.17869L27.9406 3.4951Z", fillAlpha = 0.85f)
        }
        return _sFSurfboard!!
    }

private var _sFSurfboard: ImageVector? = null
