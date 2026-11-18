package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHexagonLefthalfFilled (dualtone)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Dualtone.SFHexagonLefthalfFilled: ImageVector
    get() {
        if (_sFHexagonLefthalfFilled != null) {
            return _sFHexagonLefthalfFilled!!
        }
        _sFHexagonLefthalfFilled = sfIcon(
            name = "Dualtone.SFHexagonLefthalfFilled",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M1.34766 21.9067L11.0938 27.5513C11.9531 28.0493 12.9297 28.0493 13.7793 27.5513L23.5352 21.9067C24.375 21.4185 24.873 20.5688 24.873 19.5825L24.873 8.32275C24.873 7.34619 24.375 6.48682 23.5352 5.99854L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535L1.34766 5.99854C0.498047 6.48682 0 7.34619 0 8.32275L0 19.5825C0 20.5688 0.498047 21.4185 1.34766 21.9067ZM12.4414 26.1157L12.4414 1.79932C12.6758 1.79932 12.9199 1.86768 13.1348 1.99463L22.4414 7.37549C22.8906 7.63916 23.1445 8.07861 23.1445 8.58643L23.1445 19.3188C23.1445 19.8267 22.8906 20.2759 22.4414 20.5298L13.1445 25.9204C12.9297 26.0474 12.6855 26.1157 12.4414 26.1157Z", fillAlpha = 0.85f)
        }
        return _sFHexagonLefthalfFilled!!
    }

private var _sFHexagonLefthalfFilled: ImageVector? = null
