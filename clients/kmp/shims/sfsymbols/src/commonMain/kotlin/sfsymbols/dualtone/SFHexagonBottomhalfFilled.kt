package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHexagonBottomhalfFilled (dualtone)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Dualtone.SFHexagonBottomhalfFilled: ImageVector
    get() {
        if (_sFHexagonBottomhalfFilled != null) {
            return _sFHexagonBottomhalfFilled!!
        }
        _sFHexagonBottomhalfFilled = sfIcon(
            name = "Dualtone.SFHexagonBottomhalfFilled",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M1.34766 6.0083C0.498047 6.49658 0 7.35596 0 8.33252L0 19.6021C0 20.5786 0.498047 21.4282 1.34766 21.9165L11.0938 27.5513C11.9531 28.0493 12.9297 28.0493 13.7793 27.5513L23.5352 21.9165C24.375 21.4282 24.873 20.5786 24.873 19.6021L24.873 8.33252C24.873 7.35596 24.375 6.49658 23.5352 6.0083L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535ZM1.72852 13.9673L1.72852 8.59619C1.72852 8.09814 1.99219 7.64893 2.43164 7.39502L11.7285 2.00439C12.1777 1.75049 12.7051 1.75049 13.1445 2.00439L22.4414 7.39502C22.8906 7.64893 23.1445 8.09814 23.1445 8.59619L23.1445 13.9673Z", fillAlpha = 0.85f)
        }
        return _sFHexagonBottomhalfFilled!!
    }

private var _sFHexagonBottomhalfFilled: ImageVector? = null
