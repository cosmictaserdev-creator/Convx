package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphoneCase (monochrome)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Monochrome.SFIphoneCase: ImageVector
    get() {
        if (_sFIphoneCase != null) {
            return _sFIphoneCase!!
        }
        _sFIphoneCase = sfIcon(
            name = "Monochrome.SFIphoneCase",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M3.33008 26.2207L12.3242 26.2207C14.3652 26.2207 15.6641 24.9707 15.6641 22.998L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266L0 22.998C0 24.9707 1.28906 26.2207 3.33008 26.2207ZM3.33984 7.30469C2.41211 7.30469 1.85547 6.77734 1.85547 5.89844L1.85547 3.26172C1.85547 2.39258 2.41211 1.85547 3.33984 1.85547L5.81055 1.85547C6.74805 1.85547 7.30469 2.39258 7.30469 3.26172L7.30469 5.89844C7.30469 6.77734 6.74805 7.30469 5.81055 7.30469Z", fillAlpha = 0.85f)
        }
        return _sFIphoneCase!!
    }

private var _sFIphoneCase: ImageVector? = null
