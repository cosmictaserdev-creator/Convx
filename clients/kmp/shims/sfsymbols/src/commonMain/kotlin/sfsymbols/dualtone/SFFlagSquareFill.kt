package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFFlagSquareFill: ImageVector
    get() {
        if (_sFFlagSquareFill != null) {
            return _sFFlagSquareFill!!
        }
        _sFFlagSquareFill = sfIcon(
            name = "Dualtone.SFFlagSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.54297 18.3789C6.24023 18.3789 5.99609 18.125 5.99609 17.8418L5.99609 6.94336C5.99609 6.44531 6.25 6.06445 6.75781 5.83984C7.20703 5.63477 7.63672 5.51758 8.61328 5.51758C10.957 5.51758 12.4121 6.67969 14.6289 6.67969C15.7129 6.67969 16.2891 6.39648 16.6504 6.39648C17.1289 6.39648 17.3242 6.64062 17.3242 7.00195L17.3242 13.3887C17.3242 13.916 17.0801 14.2578 16.5625 14.502C16.0742 14.7266 15.6445 14.8242 14.6973 14.8242C12.4219 14.8242 10.9863 13.6816 8.68164 13.6816C7.85156 13.6816 7.32422 13.8477 7.07031 13.9453L7.07031 17.8418C7.07031 18.1348 6.85547 18.3789 6.54297 18.3789Z", fillAlpha = 0.85f)
        }
        return _sFFlagSquareFill!!
    }

private var _sFFlagSquareFill: ImageVector? = null
