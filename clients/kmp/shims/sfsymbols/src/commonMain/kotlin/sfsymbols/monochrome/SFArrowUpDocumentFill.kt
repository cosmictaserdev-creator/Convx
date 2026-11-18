package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpDocumentFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFArrowUpDocumentFill: ImageVector
    get() {
        if (_sFArrowUpDocumentFill != null) {
            return _sFArrowUpDocumentFill!!
        }
        _sFArrowUpDocumentFill = sfIcon(
            name = "Monochrome.SFArrowUpDocumentFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M9.66797 9.15039C9.66797 10.6738 10.3906 11.4062 11.9141 11.4062L20.9668 11.4062L20.9668 23.125C20.9668 25.6348 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.2793 0.00976562 3.75977 0.00976562L9.66797 0.00976562ZM9.89258 13.877L6.13281 17.4023C5.94727 17.5684 5.82031 17.7539 5.82031 18.0371C5.82031 18.4961 6.15234 18.8184 6.62109 18.8184C6.83594 18.8184 7.06055 18.7207 7.2168 18.5645L8.29102 17.4609L9.7499 15.9681L9.6582 18.2324L9.6582 23.3203C9.6582 23.7793 10.0488 24.1504 10.5176 24.1504C10.9766 24.1504 11.377 23.7793 11.377 23.3203L11.377 18.2324L11.2853 15.9681L12.7441 17.4609L13.8184 18.5645C13.9746 18.7207 14.1895 18.8184 14.4141 18.8184C14.8633 18.8184 15.2148 18.4961 15.2148 18.0371C15.2148 17.7539 15.0879 17.5684 14.9023 17.4023L11.1426 13.877C10.9082 13.6719 10.752 13.5938 10.5176 13.5938C10.2832 13.5938 10.127 13.6719 9.89258 13.877ZM12.6465 1.09375L19.8828 8.42773C20.4004 8.94531 20.7324 9.38477 20.8105 9.84375L11.9727 9.84375C11.4746 9.84375 11.2305 9.59961 11.2305 9.10156L11.2305 0.15625C11.6797 0.234375 12.1484 0.576172 12.6465 1.09375Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpDocumentFill!!
    }

private var _sFArrowUpDocumentFill: ImageVector? = null
