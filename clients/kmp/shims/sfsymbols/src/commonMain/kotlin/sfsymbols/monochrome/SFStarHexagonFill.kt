package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarHexagonFill (monochrome)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Monochrome.SFStarHexagonFill: ImageVector
    get() {
        if (_sFStarHexagonFill != null) {
            return _sFStarHexagonFill!!
        }
        _sFStarHexagonFill = sfIcon(
            name = "Monochrome.SFStarHexagonFill",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M13.7793 0.373535L23.5352 5.99854C24.375 6.48682 24.873 7.34619 24.873 8.32275L24.873 19.5825C24.873 20.5688 24.375 21.4185 23.5352 21.9067L13.7793 27.5513C12.9297 28.0493 11.9531 28.0493 11.0938 27.5513L1.34766 21.9067C0.498047 21.4185 0 20.5688 0 19.5825L0 8.32275C0 7.34619 0.498047 6.48682 1.34766 5.99854L11.0938 0.373535C11.9531-0.124512 12.9297-0.124512 13.7793 0.373535ZM11.7578 6.86768L10.332 11.3306L5.6543 11.3013C4.80469 11.2915 4.59961 12.1899 5.21484 12.6489L9.02344 15.3638L7.54883 19.8169C7.28516 20.6079 7.99805 21.1255 8.66211 20.6177L12.4414 17.8638L16.2207 20.6177C16.8848 21.1255 17.5977 20.6079 17.334 19.8169L15.8496 15.3638L19.668 12.6392C20.2539 12.2095 20.0781 11.2915 19.2285 11.3013L14.541 11.3306L13.1152 6.86768C12.8809 6.10596 12.002 6.10596 11.7578 6.86768Z", fillAlpha = 0.85f)
        }
        return _sFStarHexagonFill!!
    }

private var _sFStarHexagonFill: ImageVector? = null
