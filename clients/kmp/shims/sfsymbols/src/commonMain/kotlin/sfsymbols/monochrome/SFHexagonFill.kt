package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHexagonFill (monochrome)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Monochrome.SFHexagonFill: ImageVector
    get() {
        if (_sFHexagonFill != null) {
            return _sFHexagonFill!!
        }
        _sFHexagonFill = sfIcon(
            name = "Monochrome.SFHexagonFill",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M1.34766 21.9067L11.0938 27.5513C11.9531 28.0493 12.9297 28.0493 13.7793 27.5513L23.5352 21.9067C24.375 21.4185 24.873 20.5688 24.873 19.5825L24.873 8.32275C24.873 7.34619 24.375 6.48682 23.5352 5.99854L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535L1.34766 5.99854C0.498047 6.48682 0 7.34619 0 8.32275L0 19.5825C0 20.5688 0.498047 21.4185 1.34766 21.9067Z", fillAlpha = 0.85f)
        }
        return _sFHexagonFill!!
    }

private var _sFHexagonFill: ImageVector? = null
