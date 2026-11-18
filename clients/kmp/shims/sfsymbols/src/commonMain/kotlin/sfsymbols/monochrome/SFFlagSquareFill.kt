package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFFlagSquareFill: ImageVector
    get() {
        if (_sFFlagSquareFill != null) {
            return _sFFlagSquareFill!!
        }
        _sFFlagSquareFill = sfIcon(
            name = "Monochrome.SFFlagSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM6.75781 5.83984C6.25 6.06445 5.99609 6.44531 5.99609 6.94336L5.99609 17.8418C5.99609 18.125 6.24023 18.3789 6.54297 18.3789C6.85547 18.3789 7.07031 18.1348 7.07031 17.8418L7.07031 13.9453C7.32422 13.8477 7.85156 13.6816 8.68164 13.6816C10.9863 13.6816 12.4219 14.8242 14.6973 14.8242C15.6445 14.8242 16.0742 14.7266 16.5625 14.502C17.0801 14.2578 17.3242 13.916 17.3242 13.3887L17.3242 7.00195C17.3242 6.64062 17.1289 6.39648 16.6504 6.39648C16.2891 6.39648 15.7129 6.67969 14.6289 6.67969C12.4121 6.67969 10.957 5.51758 8.61328 5.51758C7.63672 5.51758 7.20703 5.63477 6.75781 5.83984Z", fillAlpha = 0.85f)
        }
        return _sFFlagSquareFill!!
    }

private var _sFFlagSquareFill: ImageVector? = null
