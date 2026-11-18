package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCarseatLeftUpAndDownFill (dualtone)
 * Viewport: 28.0948 x 34.3262
 */
public val SfSymbols.Dualtone.SFCarseatLeftUpAndDownFill: ImageVector
    get() {
        if (_sFCarseatLeftUpAndDownFill != null) {
            return _sFCarseatLeftUpAndDownFill!!
        }
        _sFCarseatLeftUpAndDownFill = sfIcon(
            name = "Dualtone.SFCarseatLeftUpAndDownFill",
            viewportWidth = 28.0948f,
            viewportHeight = 34.3262f
        ) {
            addSfPath("M0.194886 21.0742C1.24957 25.0684 2.45074 26.2891 4.95074 26.2891L19.355 26.2891C21.1519 26.2891 22.3726 25.2832 22.8218 23.457L27.5484 4.23828C28.2711 1.31836 26.7867 0 25.0679 0C22.7925 0 20.3414 2.34375 21.6304 6.32812L21.9429 5.01953C18.2613 6.94336 16.5035 11.6895 17.1578 17.2852L17.5582 16.748C14.941 16.2012 11.6011 15.8887 8.33942 15.8887C1.84528 15.8887-0.771911 17.4512 0.194886 21.0742Z", fillAlpha = 0.425f)
            addSfPath("M6.48395 28.2129C5.85895 28.2129 5.61481 28.7305 5.93707 29.2578L8.66168 33.7402C8.99371 34.2676 9.59918 34.248 9.91168 33.7402L12.6656 29.248C12.9976 28.7207 12.7242 28.2129 12.1089 28.2129Z", fillAlpha = 0.85f)
            addSfPath("M6.48395 13.9746L12.1089 13.9746C12.7242 13.9746 12.9976 13.4668 12.6656 12.9395L9.91168 8.44727C9.59918 7.93945 8.98395 7.91992 8.66168 8.44727L5.93707 12.9297C5.60504 13.457 5.85895 13.9746 6.48395 13.9746Z", fillAlpha = 0.85f)
        }
        return _sFCarseatLeftUpAndDownFill!!
    }

private var _sFCarseatLeftUpAndDownFill: ImageVector? = null
