package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplaneArrival (dualtone)
 * Viewport: 30.0557 x 28.2405
 */
public val SfSymbols.Dualtone.SFAirplaneArrival: ImageVector
    get() {
        if (_sFAirplaneArrival != null) {
            return _sFAirplaneArrival!!
        }
        _sFAirplaneArrival = sfIcon(
            name = "Dualtone.SFAirplaneArrival",
            viewportWidth = 30.0557f,
            viewportHeight = 28.2405f
        ) {
            addSfPath("M1.30228 25.2286L28.3726 25.2286C28.8511 25.2286 29.2417 24.838 29.2417 24.3693C29.2417 23.8907 28.8511 23.5001 28.3726 23.5001L1.30228 23.5001C0.823764 23.5001 0.433139 23.8907 0.433139 24.3693C0.433139 24.838 0.823764 25.2286 1.30228 25.2286Z", fillAlpha = 0.425f)
            addSfPath("M3.09915 12.4064L24.5347 18.2071C26.9859 18.8712 29.3296 18.2267 29.6617 16.4103C29.9253 14.7306 28.1382 13.2169 25.8531 12.7579L18.2261 11.254C17.7183 11.1466 17.4058 10.961 17.064 10.4532L10.3452 0.7755C10.1402 0.492297 9.8765 0.30675 9.55423 0.21886L8.71439 0.0137816C8.41165-0.0643434 8.16751 0.199328 8.2554 0.560657L10.687 10.6876L11.3706 9.89659L4.92533 8.63683L6.7222 9.74034L1.7515 3.94933C1.52689 3.67589 1.27298 3.588 0.833529 3.67589L0.413607 3.76378C0.0913417 3.83214-0.045377 4.06652 0.0132167 4.35948L1.37064 10.9025C1.53665 11.6739 2.13236 12.1427 3.09915 12.4064Z", fillAlpha = 0.85f)
        }
        return _sFAirplaneArrival!!
    }

private var _sFAirplaneArrival: ImageVector? = null
