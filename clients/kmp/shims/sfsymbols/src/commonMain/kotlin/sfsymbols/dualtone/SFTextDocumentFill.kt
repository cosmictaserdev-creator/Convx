package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextDocumentFill (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFTextDocumentFill: ImageVector
    get() {
        if (_sFTextDocumentFill != null) {
            return _sFTextDocumentFill!!
        }
        _sFTextDocumentFill = sfIcon(
            name = "Dualtone.SFTextDocumentFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M9.66797 9.15039C9.66797 10.6738 10.3906 11.4062 11.9141 11.4062L20.9668 11.4062L20.9668 23.125C20.9668 25.6348 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.2793 0.00976562 3.75977 0.00976562L9.66797 0.00976562ZM5.48828 20.1562C5.04883 20.1562 4.73633 20.4688 4.73633 20.8691C4.73633 21.2793 5.04883 21.5918 5.48828 21.5918L15.5273 21.5918C15.9473 21.5918 16.2695 21.2793 16.2695 20.8691C16.2695 20.4688 15.9473 20.1562 15.5273 20.1562ZM5.48828 15.4492C5.04883 15.4492 4.73633 15.7617 4.73633 16.1621C4.73633 16.5723 5.04883 16.8848 5.48828 16.8848L15.5273 16.8848C15.9473 16.8848 16.2695 16.5723 16.2695 16.1621C16.2695 15.7617 15.9473 15.4492 15.5273 15.4492ZM12.6465 1.09375L19.8828 8.42773C20.4004 8.94531 20.7324 9.38477 20.8105 9.84375L11.9727 9.84375C11.4746 9.84375 11.2305 9.59961 11.2305 9.10156L11.2305 0.15625C11.6797 0.234375 12.1484 0.576172 12.6465 1.09375Z", fillAlpha = 0.85f)
        }
        return _sFTextDocumentFill!!
    }

private var _sFTextDocumentFill: ImageVector? = null
