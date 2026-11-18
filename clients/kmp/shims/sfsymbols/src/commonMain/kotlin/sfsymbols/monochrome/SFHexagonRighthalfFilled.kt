package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHexagonRighthalfFilled (monochrome)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Monochrome.SFHexagonRighthalfFilled: ImageVector
    get() {
        if (_sFHexagonRighthalfFilled != null) {
            return _sFHexagonRighthalfFilled!!
        }
        _sFHexagonRighthalfFilled = sfIcon(
            name = "Monochrome.SFHexagonRighthalfFilled",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M23.5352 21.9067C24.375 21.4185 24.873 20.5688 24.873 19.5825L24.873 8.32275C24.873 7.34619 24.375 6.48682 23.5352 5.99854L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535L1.34766 5.99854C0.498047 6.48682 0 7.34619 0 8.32275L0 19.5825C0 20.5688 0.498047 21.4185 1.34766 21.9067L11.0938 27.5513C11.9531 28.0493 12.9297 28.0493 13.7793 27.5513ZM12.4414 26.1157C12.1875 26.1157 11.9434 26.0474 11.7285 25.9204L2.43164 20.5298C1.99219 20.2759 1.72852 19.8267 1.72852 19.3188L1.72852 8.58643C1.72852 8.07861 1.99219 7.63916 2.43164 7.37549L11.7383 1.99463C11.9531 1.86768 12.1973 1.79932 12.4414 1.79932Z", fillAlpha = 0.85f)
        }
        return _sFHexagonRighthalfFilled!!
    }

private var _sFHexagonRighthalfFilled: ImageVector? = null
