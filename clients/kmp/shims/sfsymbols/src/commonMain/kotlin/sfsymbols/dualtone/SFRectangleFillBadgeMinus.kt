package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleFillBadgeMinus (dualtone)
 * Viewport: 45.6934 x 30.9082
 */
public val SfSymbols.Dualtone.SFRectangleFillBadgeMinus: ImageVector
    get() {
        if (_sFRectangleFillBadgeMinus != null) {
            return _sFRectangleFillBadgeMinus!!
        }
        _sFRectangleFillBadgeMinus = sfIcon(
            name = "Dualtone.SFRectangleFillBadgeMinus",
            viewportWidth = 45.6934f,
            viewportHeight = 30.9082f
        ) {
            addSfPath("M37.4609 7.74414L37.4609 23.1738C37.4609 25.6543 36.1816 26.9336 33.6523 26.9336L14.6825 26.9336C15.3573 25.7837 15.7422 24.4503 15.7422 23.0371C15.7422 18.7109 12.1973 15.166 7.87109 15.166L7.87109 7.74414C7.87109 5.24414 9.15039 3.97461 11.6699 3.97461L33.6523 3.97461C36.1816 3.97461 37.4609 5.25391 37.4609 7.74414Z", fillAlpha = 0.425f)
            addSfPath("M14.1895 23.0371C14.1895 26.4941 11.2988 29.3555 7.87109 29.3555C4.41406 29.3555 1.55273 26.5137 1.55273 23.0371C1.55273 19.5801 4.41406 16.7188 7.87109 16.7188C11.3379 16.7188 14.1895 19.5703 14.1895 23.0371ZM4.58984 22.3242C4.19922 22.3242 3.87695 22.666 3.87695 23.0371C3.87695 23.418 4.19922 23.75 4.58984 23.75L11.1523 23.75C11.5332 23.75 11.8555 23.418 11.8555 23.0371C11.8555 22.666 11.5332 22.3242 11.1523 22.3242Z", fillAlpha = 0.85f)
        }
        return _sFRectangleFillBadgeMinus!!
    }

private var _sFRectangleFillBadgeMinus: ImageVector? = null
