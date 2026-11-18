package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShippingbox (dualtone)
 * Viewport: 25.6348 x 27.3316
 */
public val SfSymbols.Dualtone.SFShippingbox: ImageVector
    get() {
        if (_sFShippingbox != null) {
            return _sFShippingbox!!
        }
        _sFShippingbox = sfIcon(
            name = "Dualtone.SFShippingbox",
            viewportWidth = 25.6348f,
            viewportHeight = 27.3316f
        ) {
            addSfPath("M1.63086 21.4246L11.6406 27.0496C12.3242 27.4207 12.9492 27.4304 13.6328 27.0496L23.6426 21.4246C24.7363 20.8093 25.2734 20.1746 25.2734 18.5632L25.2734 8.30934C25.2734 7.17653 24.834 6.46363 23.8965 5.93629L14.668 0.721447C13.3105-0.0500369 11.9629-0.0500369 10.5957 0.721447L1.37695 5.93629C0.439453 6.46363 0 7.17653 0 8.30934L0 18.5632C0 20.1746 0.537109 20.8093 1.63086 21.4246ZM2.63672 20.0281C1.875 19.6082 1.66016 19.198 1.66016 18.4949L1.66016 8.68043L11.7871 14.3933L11.7871 25.1648ZM22.6367 20.0281L13.4863 25.1648L13.4863 14.3933L23.6133 8.68043L23.6133 18.4949C23.6133 19.198 23.3984 19.6082 22.6367 20.0281ZM12.6367 12.8797L2.53906 7.22535L6.77734 4.82301L16.8945 10.5066ZM18.623 9.53004L8.47656 3.85621L11.3672 2.23512C12.2266 1.74684 13.0469 1.73707 13.9062 2.23512L22.7344 7.22535Z", fillAlpha = 0.85f)
        }
        return _sFShippingbox!!
    }

private var _sFShippingbox: ImageVector? = null
