package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCarseatRight (monochrome)
 * Viewport: 28.2792 x 26.3184
 */
public val SfSymbols.Monochrome.SFCarseatRight: ImageVector
    get() {
        if (_sFCarseatRight != null) {
            return _sFCarseatRight!!
        }
        _sFCarseatRight = sfIcon(
            name = "Monochrome.SFCarseatRight",
            viewportWidth = 28.2792f,
            viewportHeight = 26.3184f
        ) {
            addSfPath("M27.7236 21.084C28.6904 17.4609 26.0732 15.8984 19.5888 15.8984C16.3954 15.8984 13.1923 16.1621 10.4579 16.6504L10.9658 17.2852C11.3954 11.8945 9.75482 7.27539 6.76654 5.27344L6.97162 6.31836C7.99701 2.53906 5.73139 0 3.01654 0C0.799745 0-0.489318 1.60156 0.174745 4.24805L4.90131 23.4668C5.35053 25.293 6.57123 26.2988 8.3681 26.2988L22.9677 26.2988C25.4677 26.2988 26.6689 25.0781 27.7236 21.084ZM26.0732 20.625C25.165 24.0234 24.4521 24.5605 22.9677 24.5605L8.3681 24.5605C7.45014 24.5605 6.82514 24.0234 6.58099 23.0469L1.8349 3.81836C1.50287 2.49023 2.07904 1.72852 3.02631 1.72852C4.45209 1.72852 5.77045 3.00781 5.43842 5.42969C5.32123 6.16211 5.38959 6.38672 5.81928 6.71875C8.20209 8.37891 9.53998 12.4512 9.38373 17.2363C9.37396 17.9395 9.80365 18.3789 10.497 18.3398C12.1376 18.1738 15.3701 17.627 19.5888 17.627C25.038 17.627 26.6396 18.4863 26.0732 20.625Z", fillAlpha = 0.85f)
        }
        return _sFCarseatRight!!
    }

private var _sFCarseatRight: ImageVector? = null
