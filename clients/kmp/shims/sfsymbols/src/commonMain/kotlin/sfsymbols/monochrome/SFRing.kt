package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRing (monochrome)
 * Viewport: 28.291 x 27.9395
 */
public val SfSymbols.Monochrome.SFRing: ImageVector
    get() {
        if (_sFRing != null) {
            return _sFRing!!
        }
        _sFRing = sfIcon(
            name = "Monochrome.SFRing",
            viewportWidth = 28.291f,
            viewportHeight = 27.9395f
        ) {
            addSfPath("M13.9648 27.9297C21.6406 27.9297 27.9297 21.6504 27.9297 13.9648C27.9297 6.2793 21.6406 0 13.9648 0C6.28906 0 0 6.2793 0 13.9648C0 21.6504 6.28906 27.9297 13.9648 27.9297ZM13.9648 23.3398C8.7793 23.3398 4.58008 19.1504 4.58008 13.9648C4.58008 8.7793 8.7793 4.58008 13.9648 4.58008C19.1504 4.58008 23.3496 8.7793 23.3496 13.9648C23.3496 19.1504 19.1504 23.3398 13.9648 23.3398Z", fillAlpha = 0.85f)
        }
        return _sFRing!!
    }

private var _sFRing: ImageVector? = null
