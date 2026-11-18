package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTablecellsFill (monochrome)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Monochrome.SFTablecellsFill: ImageVector
    get() {
        if (_sFTablecellsFill != null) {
            return _sFTablecellsFill!!
        }
        _sFTablecellsFill = sfIcon(
            name = "Monochrome.SFTablecellsFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 16.4648L0 14.7363L13.9258 14.7363L13.9258 8.25195L0 8.25195L0 6.52344L13.9258 6.52344L13.9258 0.0195312L15.6543 0.0195312L15.6543 6.52344L29.5898 6.52344L29.5898 8.25195L15.6543 8.25195L15.6543 14.7363L29.5898 14.7363L29.5898 16.4648L15.6543 16.4648L15.6543 22.9785L13.9258 22.9785L13.9258 16.4648ZM3.79883 22.9785L25.7812 22.9785C28.3105 22.9785 29.5898 21.6992 29.5898 19.209L29.5898 3.7793C29.5898 1.29883 28.3105 0.0195312 25.7812 0.0195312L3.79883 0.0195312C1.2793 0.0195312 0 1.2793 0 3.7793L0 19.209C0 21.709 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.85f)
        }
        return _sFTablecellsFill!!
    }

private var _sFTablecellsFill: ImageVector? = null
