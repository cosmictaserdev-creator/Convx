package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDown (monochrome)
 * Viewport: 19.2676 x 23.6816
 */
public val SfSymbols.Monochrome.SFArrowDown: ImageVector
    get() {
        if (_sFArrowDown != null) {
            return _sFArrowDown!!
        }
        _sFArrowDown = sfIcon(
            name = "Monochrome.SFArrowDown",
            viewportWidth = 19.2676f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M9.45312 23.6816C9.7168 23.6816 9.96094 23.5742 10.166 23.3594L18.6133 14.9121C18.8184 14.6973 18.9062 14.4824 18.9062 14.2285C18.9062 13.6914 18.5156 13.291 17.9883 13.291C17.7344 13.291 17.4805 13.3789 17.3145 13.5449L13.8477 16.9727L9.44336 21.7383L5.05859 16.9727L1.5918 13.5449C1.42578 13.3789 1.17188 13.291 0.917969 13.291C0.390625 13.291 0 13.6914 0 14.2285C0 14.4824 0.0878906 14.6973 0.292969 14.9121L8.74023 23.3594C8.94531 23.5742 9.18945 23.6816 9.45312 23.6816ZM9.45312 22.5C9.95117 22.5 10.293 22.1484 10.293 21.6504L10.4004 18.1152L10.4004 0.9375C10.4004 0.390625 10.0098 0 9.45312 0C8.89648 0 8.50586 0.390625 8.50586 0.9375L8.50586 18.1152L8.61328 21.6602C8.61328 22.1484 8.95508 22.5 9.45312 22.5Z", fillAlpha = 0.85f)
        }
        return _sFArrowDown!!
    }

private var _sFArrowDown: ImageVector? = null
