package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCarseatRightFill (dualtone)
 * Viewport: 28.0991 x 26.2988
 */
public val SfSymbols.Dualtone.SFCarseatRightFill: ImageVector
    get() {
        if (_sFCarseatRightFill != null) {
            return _sFCarseatRightFill!!
        }
        _sFCarseatRightFill = sfIcon(
            name = "Dualtone.SFCarseatRightFill",
            viewportWidth = 28.0991f,
            viewportHeight = 26.2988f
        ) {
            addSfPath("M27.5408 21.0742C28.5173 17.4512 25.8904 15.8887 19.406 15.8887C16.1345 15.8887 12.7947 16.2012 10.1775 16.748L10.5779 17.2852C11.2419 11.6895 9.47435 6.94336 5.79271 5.01953L6.10521 6.32812C7.39427 2.34375 4.9431 0 2.66771 0C0.94896 0-0.535415 1.31836 0.187241 4.23828L4.9138 23.457C5.36302 25.2832 6.59349 26.2891 8.39037 26.2891L22.7849 26.2891C25.2849 26.2891 26.4861 25.0684 27.5408 21.0742Z", fillAlpha = 0.85f)
        }
        return _sFCarseatRightFill!!
    }

private var _sFCarseatRightFill: ImageVector? = null
