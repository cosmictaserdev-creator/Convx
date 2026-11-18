package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilAndScribble (dualtone)
 * Viewport: 34.9244 x 25.7333
 */
public val SfSymbols.Dualtone.SFPencilAndScribble: ImageVector
    get() {
        if (_sFPencilAndScribble != null) {
            return _sFPencilAndScribble!!
        }
        _sFPencilAndScribble = sfIcon(
            name = "Dualtone.SFPencilAndScribble",
            viewportWidth = 34.9244f,
            viewportHeight = 25.7333f
        ) {
            addSfPath("M0.260046 22.559C1.36356 26.1528 6.81278 26.7485 12.6819 24.0727C13.6292 23.643 12.9944 22.4223 12.0862 22.8618C7.10575 25.1762 2.34989 24.8442 1.55887 22.1684C-0.433314 15.6157 13.5315 14.7563 11.9593 9.44381C11.1878 6.83639 5.02567 7.28561 0.631139 10.2446C-0.159876 10.7622 0.592077 11.8755 1.36356 11.3481C4.65458 9.17038 10.221 8.311 10.68 9.82467C11.012 10.9477 10.094 11.729 6.57841 13.7212C3.1995 15.6255-1.10714 18.0376 0.260046 22.559Z", fillAlpha = 0.425f)
            addSfPath("M17.7503 21.6606L31.2171 8.21334L29.2151 6.21139L15.7483 19.6489L14.5276 22.3442C14.4104 22.6176 14.7034 22.9204 14.9768 22.813ZM32.2522 7.19772L33.4436 6.02584C34.0491 5.42038 34.0784 4.79538 33.5315 4.2485L33.1897 3.89694C32.6526 3.36959 32.0081 3.42819 31.4222 4.00436L30.2307 5.17624Z", fillAlpha = 0.85f)
        }
        return _sFPencilAndScribble!!
    }

private var _sFPencilAndScribble: ImageVector? = null
