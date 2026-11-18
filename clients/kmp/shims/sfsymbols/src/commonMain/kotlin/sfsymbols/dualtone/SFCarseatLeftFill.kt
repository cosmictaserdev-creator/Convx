package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCarseatLeftFill (dualtone)
 * Viewport: 28.0948 x 26.2988
 */
public val SfSymbols.Dualtone.SFCarseatLeftFill: ImageVector
    get() {
        if (_sFCarseatLeftFill != null) {
            return _sFCarseatLeftFill!!
        }
        _sFCarseatLeftFill = sfIcon(
            name = "Dualtone.SFCarseatLeftFill",
            viewportWidth = 28.0948f,
            viewportHeight = 26.2988f
        ) {
            addSfPath("M0.194886 21.0742C1.24957 25.0684 2.45074 26.2891 4.95074 26.2891L19.355 26.2891C21.1519 26.2891 22.3726 25.2832 22.8218 23.457L27.5484 4.23828C28.2711 1.31836 26.7867 0 25.0679 0C22.7925 0 20.3414 2.34375 21.6304 6.32812L21.9429 5.01953C18.2613 6.94336 16.5035 11.6895 17.1578 17.2852L17.5582 16.748C14.941 16.2012 11.6011 15.8887 8.33942 15.8887C1.84528 15.8887-0.771911 17.4512 0.194886 21.0742Z", fillAlpha = 0.85f)
        }
        return _sFCarseatLeftFill!!
    }

private var _sFCarseatLeftFill: ImageVector? = null
