package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarHexagonFill (dualtone)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Dualtone.SFStarHexagonFill: ImageVector
    get() {
        if (_sFStarHexagonFill != null) {
            return _sFStarHexagonFill!!
        }
        _sFStarHexagonFill = sfIcon(
            name = "Dualtone.SFStarHexagonFill",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M1.34766 21.9067L11.0938 27.5513C11.9531 28.0493 12.9297 28.0493 13.7793 27.5513L23.5352 21.9067C24.375 21.4185 24.873 20.5688 24.873 19.5825L24.873 8.32275C24.873 7.34619 24.375 6.48682 23.5352 5.99854L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535L1.34766 5.99854C0.498047 6.48682 0 7.34619 0 8.32275L0 19.5825C0 20.5688 0.498047 21.4185 1.34766 21.9067Z", fillAlpha = 0.2125f)
            addSfPath("M8.66211 20.6177C7.99805 21.1255 7.28516 20.6079 7.54883 19.8169L9.02344 15.3638L5.21484 12.6489C4.59961 12.1899 4.80469 11.2915 5.6543 11.3013L10.332 11.3306L11.7578 6.86768C12.002 6.10596 12.8809 6.10596 13.1152 6.86768L14.541 11.3306L19.2285 11.3013C20.0781 11.2915 20.2539 12.2095 19.668 12.6392L15.8496 15.3638L17.334 19.8169C17.5977 20.6079 16.8848 21.1255 16.2207 20.6177L12.4414 17.8638Z", fillAlpha = 0.85f)
        }
        return _sFStarHexagonFill!!
    }

private var _sFStarHexagonFill: ImageVector? = null
