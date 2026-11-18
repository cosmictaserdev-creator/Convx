package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDocumentFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFDocumentFill: ImageVector
    get() {
        if (_sFDocumentFill != null) {
            return _sFDocumentFill!!
        }
        _sFDocumentFill = sfIcon(
            name = "Monochrome.SFDocumentFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6348 20.9668 23.125L20.9668 11.4062L11.9141 11.4062C10.3906 11.4062 9.66797 10.6738 9.66797 9.15039L9.66797 0.00976562L3.75977 0.00976562C1.2793 0.00976562 0 1.28906 0 3.80859L0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238ZM11.9727 9.84375L20.8105 9.84375C20.7324 9.38477 20.4004 8.94531 19.8828 8.42773L12.6465 1.09375C12.1484 0.576172 11.6797 0.234375 11.2305 0.15625L11.2305 9.10156C11.2305 9.59961 11.4746 9.84375 11.9727 9.84375Z", fillAlpha = 0.85f)
        }
        return _sFDocumentFill!!
    }

private var _sFDocumentFill: ImageVector? = null
