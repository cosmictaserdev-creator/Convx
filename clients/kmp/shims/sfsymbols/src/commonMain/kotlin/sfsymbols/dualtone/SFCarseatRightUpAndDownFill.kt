package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCarseatRightUpAndDownFill (dualtone)
 * Viewport: 28.0991 x 34.3262
 */
public val SfSymbols.Dualtone.SFCarseatRightUpAndDownFill: ImageVector
    get() {
        if (_sFCarseatRightUpAndDownFill != null) {
            return _sFCarseatRightUpAndDownFill!!
        }
        _sFCarseatRightUpAndDownFill = sfIcon(
            name = "Dualtone.SFCarseatRightUpAndDownFill",
            viewportWidth = 28.0991f,
            viewportHeight = 34.3262f
        ) {
            addSfPath("M27.5408 21.0742C28.5173 17.4512 25.8904 15.8887 19.406 15.8887C16.1345 15.8887 12.7947 16.2012 10.1775 16.748L10.5779 17.2852C11.2419 11.6895 9.47435 6.94336 5.79271 5.01953L6.10521 6.32812C7.39427 2.34375 4.9431 0 2.66771 0C0.94896 0-0.535415 1.31836 0.187241 4.23828L4.9138 23.457C5.36302 25.2832 6.59349 26.2891 8.39037 26.2891L22.7849 26.2891C25.2849 26.2891 26.4861 25.0684 27.5408 21.0742Z", fillAlpha = 0.425f)
            addSfPath("M21.2419 28.2129L15.6267 28.2129C15.0115 28.2129 14.738 28.7207 15.0603 29.248L17.8142 33.7402C18.1267 34.248 18.7419 34.2676 19.0642 33.7402L21.7986 29.2578C22.1111 28.7305 21.8767 28.2129 21.2419 28.2129Z", fillAlpha = 0.85f)
            addSfPath("M21.2419 13.9746C21.8767 13.9746 22.1208 13.457 21.7986 12.9297L19.0642 8.44727C18.7517 7.91992 18.1267 7.93945 17.8142 8.44727L15.0603 12.9395C14.738 13.4668 15.0115 13.9746 15.6267 13.9746Z", fillAlpha = 0.85f)
        }
        return _sFCarseatRightUpAndDownFill!!
    }

private var _sFCarseatRightUpAndDownFill: ImageVector? = null
