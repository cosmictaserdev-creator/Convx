package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockDocumentFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFLockDocumentFill: ImageVector
    get() {
        if (_sFLockDocumentFill != null) {
            return _sFLockDocumentFill!!
        }
        _sFLockDocumentFill = sfIcon(
            name = "Monochrome.SFLockDocumentFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M9.66797 9.15039C9.66797 10.6738 10.3906 11.4062 11.9141 11.4062L20.9668 11.4062L20.9668 23.125C20.9668 25.6348 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.2793 0.00976562 3.75977 0.00976562L9.66797 0.00976562ZM7.61719 16.1719L7.61719 17.4707C6.97266 17.5098 6.64062 17.8906 6.64062 18.6719L6.64062 22.9297C6.64062 23.75 7.02148 24.1309 7.77344 24.1309L13.2227 24.1309C13.9844 24.1309 14.3555 23.75 14.3555 22.9297L14.3555 18.6719C14.3555 17.8906 14.0234 17.5098 13.3789 17.4707L13.3789 16.1719C13.3789 14.2676 12.207 12.9785 10.498 12.9785C8.78906 12.9785 7.61719 14.2676 7.61719 16.1719ZM12.334 16.084L12.334 17.4609L8.65234 17.4609L8.65234 16.084C8.65234 14.8145 9.38477 13.9844 10.498 13.9844C11.6016 13.9844 12.334 14.8145 12.334 16.084ZM12.6465 1.09375L19.8828 8.42773C20.4004 8.94531 20.7324 9.38477 20.8105 9.84375L11.9727 9.84375C11.4746 9.84375 11.2305 9.59961 11.2305 9.10156L11.2305 0.15625C11.6797 0.234375 12.1484 0.576172 12.6465 1.09375Z", fillAlpha = 0.85f)
        }
        return _sFLockDocumentFill!!
    }

private var _sFLockDocumentFill: ImageVector? = null
