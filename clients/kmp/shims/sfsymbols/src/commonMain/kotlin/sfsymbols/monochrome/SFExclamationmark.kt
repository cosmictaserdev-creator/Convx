package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmark (monochrome)
 * Viewport: 3.50586 x 23.9941
 */
public val SfSymbols.Monochrome.SFExclamationmark: ImageVector
    get() {
        if (_sFExclamationmark != null) {
            return _sFExclamationmark!!
        }
        _sFExclamationmark = sfIcon(
            name = "Monochrome.SFExclamationmark",
            viewportWidth = 3.50586f,
            viewportHeight = 23.9941f
        ) {
            addSfPath("M1.58203 17.1387C2.14844 17.1387 2.53906 16.7676 2.54883 16.1816L2.63672 1.14258C2.63672 1.14258 2.63672 1.14258 2.63672 1.14258C2.63672 0.527344 2.14844 0.117188 1.57227 0.117188C0.996094 0.117188 0.507812 0.527344 0.507812 1.14258C0.507812 1.14258 0.507812 1.14258 0.507812 1.14258L0.615234 16.1816C0.625 16.7676 1.01562 17.1387 1.58203 17.1387ZM1.57227 23.9941C2.44141 23.9941 3.14453 23.2812 3.14453 22.4219C3.14453 21.543 2.44141 20.8496 1.57227 20.8496C0.712891 20.8496 0 21.543 0 22.4219C0 23.2812 0.712891 23.9941 1.57227 23.9941Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmark!!
    }

private var _sFExclamationmark: ImageVector? = null
