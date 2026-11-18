package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHexagonTophalfFilled (monochrome)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Monochrome.SFHexagonTophalfFilled: ImageVector
    get() {
        if (_sFHexagonTophalfFilled != null) {
            return _sFHexagonTophalfFilled!!
        }
        _sFHexagonTophalfFilled = sfIcon(
            name = "Monochrome.SFHexagonTophalfFilled",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M1.34766 21.9067L11.0938 27.5513C11.9531 28.0493 12.9297 28.0493 13.7793 27.5513L23.5352 21.9067C24.375 21.4185 24.873 20.5688 24.873 19.5825L24.873 8.32275C24.873 7.34619 24.375 6.48682 23.5352 5.99854L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535L1.34766 5.99854C0.498047 6.48682 0 7.34619 0 8.32275L0 19.5825C0 20.5688 0.498047 21.4185 1.34766 21.9067ZM1.72852 13.9575L23.1445 13.9575L23.1445 19.3188C23.1445 19.8267 22.8906 20.2759 22.4414 20.5298L13.1445 25.9204C12.7051 26.1743 12.1777 26.1743 11.7285 25.9204L2.43164 20.5298C1.99219 20.2759 1.72852 19.8267 1.72852 19.3188Z", fillAlpha = 0.85f)
        }
        return _sFHexagonTophalfFilled!!
    }

private var _sFHexagonTophalfFilled: ImageVector? = null
