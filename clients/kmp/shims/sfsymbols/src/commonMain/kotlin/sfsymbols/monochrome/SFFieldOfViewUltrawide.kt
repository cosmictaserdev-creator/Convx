package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFieldOfViewUltrawide (monochrome)
 * Viewport: 37.4608 x 21.8848
 */
public val SfSymbols.Monochrome.SFFieldOfViewUltrawide: ImageVector
    get() {
        if (_sFFieldOfViewUltrawide != null) {
            return _sFFieldOfViewUltrawide!!
        }
        _sFFieldOfViewUltrawide = sfIcon(
            name = "Monochrome.SFFieldOfViewUltrawide",
            viewportWidth = 37.4608f,
            viewportHeight = 21.8848f
        ) {
            addSfPath("M1.12296 12.5293L15.9472 20.5469C17.0605 21.1426 17.7343 21.4648 18.5448 21.4648C19.3651 21.4648 20.039 21.1426 21.1523 20.5469L35.9667 12.5293C37.1191 11.9141 37.4609 10.5566 36.6698 9.41406C32.5976 3.68164 25.8495 0 18.5448 0C11.2499 0 4.50187 3.68164 0.429602 9.41406C-0.361414 10.5566-0.0196167 11.9141 1.12296 12.5293ZM2.19718 11.1621C1.79679 10.9375 1.71866 10.5762 2.03116 10.1465C5.82023 4.93164 11.8261 1.72852 18.5448 1.72852C25.2734 1.72852 31.2987 4.94141 35.078 10.166C35.3808 10.5859 35.3026 10.9375 34.9023 11.1621L20.1659 19.1211C19.4726 19.4922 19.0722 19.6582 18.5448 19.6582C18.0273 19.6582 17.6269 19.4922 16.9335 19.1211Z", fillAlpha = 0.85f)
        }
        return _sFFieldOfViewUltrawide!!
    }

private var _sFFieldOfViewUltrawide: ImageVector? = null
