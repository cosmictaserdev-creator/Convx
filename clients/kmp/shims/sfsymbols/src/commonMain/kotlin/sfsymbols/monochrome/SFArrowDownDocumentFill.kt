package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownDocumentFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFArrowDownDocumentFill: ImageVector
    get() {
        if (_sFArrowDownDocumentFill != null) {
            return _sFArrowDownDocumentFill!!
        }
        _sFArrowDownDocumentFill = sfIcon(
            name = "Monochrome.SFArrowDownDocumentFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M9.66797 9.15039C9.66797 10.6738 10.3906 11.4062 11.9141 11.4062L20.9668 11.4062L20.9668 23.125C20.9668 25.6348 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.2793 0.00976562 3.75977 0.00976562L9.66797 0.00976562ZM9.66797 14.4238L9.66797 19.5117L9.7526 21.7757L8.30078 20.2832L7.2168 19.1797C7.06055 19.0234 6.83594 18.9258 6.62109 18.9258C6.15234 18.9258 5.82031 19.248 5.82031 19.707C5.82031 19.9902 5.95703 20.1758 6.13281 20.3418L9.89258 23.8672C10.127 24.0723 10.293 24.1504 10.5176 24.1504C10.7617 24.1504 10.918 24.0723 11.1426 23.8672L14.9023 20.3418C15.0879 20.1758 15.2148 19.9902 15.2148 19.707C15.2148 19.248 14.8633 18.9258 14.4238 18.9258C14.1992 18.9258 13.9844 19.0234 13.8184 19.1797L12.7441 20.2832L11.2853 21.776L11.377 19.5117L11.377 14.4238C11.377 13.9648 10.9863 13.5938 10.5176 13.5938C10.0586 13.5938 9.66797 13.9648 9.66797 14.4238ZM12.6465 1.09375L19.8828 8.42773C20.4004 8.94531 20.7324 9.38477 20.8105 9.84375L11.9727 9.84375C11.4746 9.84375 11.2305 9.59961 11.2305 9.10156L11.2305 0.15625C11.6797 0.234375 12.1484 0.576172 12.6465 1.09375Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownDocumentFill!!
    }

private var _sFArrowDownDocumentFill: ImageVector? = null
