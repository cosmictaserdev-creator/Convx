package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaystationLogo (dualtone)
 * Viewport: 33.7521 x 25.8496
 */
public val SfSymbols.Dualtone.SFPlaystationLogo: ImageVector
    get() {
        if (_sFPlaystationLogo != null) {
            return _sFPlaystationLogo!!
        }
        _sFPlaystationLogo = sfIcon(
            name = "Dualtone.SFPlaystationLogo",
            viewportWidth = 33.7521f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M12.5206 0C14.835 0.498047 18.1163 1.49414 19.9522 2.10938C24.5518 3.65234 26.1729 5.61523 26.1729 10.0586C26.1729 13.2715 24.6788 14.9707 22.462 14.9707C21.7198 14.9707 20.919 14.7852 20.0499 14.3652L20.0499 6.33789C20.0499 5.41992 19.9522 4.59961 19.0538 4.32617C18.3116 4.08203 17.9405 4.72656 17.9405 5.61523L17.9405 25.8203L12.5206 24.1113ZM18.9561 22.3145L18.9561 25.4102L30.0401 21.4551C31.6905 20.8789 32.7257 20.3027 33.1065 19.7852C33.7218 18.9941 33.4483 17.7441 31.046 16.9238C28.5753 16.0645 25.6749 15.791 23.3604 15.9766C21.9346 16.084 20.2061 16.5527 18.9561 16.9727L18.9561 20.3027L24.7764 18.1934C25.7628 17.8418 27.1397 17.793 27.9991 18.0762C28.7999 18.3496 28.7218 18.7988 27.6866 19.1895ZM2.003 21.748C4.44441 22.6172 7.56941 23.3203 11.4268 22.4902L11.4268 19.6875L8.85847 20.6348C7.79402 21.0254 6.08503 21.084 5.48933 20.8984C4.63972 20.6055 4.8155 20.0391 5.94832 19.6387L11.4268 17.6758L11.4268 14.5703L3.89753 17.2949C2.89168 17.666 1.87605 17.9492 0.713941 18.7402C-0.55559 19.6094-0.135668 20.9863 2.003 21.748Z", fillAlpha = 0.85f)
        }
        return _sFPlaystationLogo!!
    }

private var _sFPlaystationLogo: ImageVector? = null
