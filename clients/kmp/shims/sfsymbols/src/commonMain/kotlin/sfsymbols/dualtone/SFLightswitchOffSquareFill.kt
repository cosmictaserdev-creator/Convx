package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOffSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFLightswitchOffSquareFill: ImageVector
    get() {
        if (_sFLightswitchOffSquareFill != null) {
            return _sFLightswitchOffSquareFill!!
        }
        _sFLightswitchOffSquareFill = sfIcon(
            name = "Dualtone.SFLightswitchOffSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.04688 4.82422L14.9023 4.82422C15.4297 4.82422 15.8105 5.19531 15.8105 5.72266L15.8105 17.2461C15.8105 17.7734 15.4297 18.1543 14.9023 18.1543L8.04688 18.1543C7.51953 18.1543 7.14844 17.7734 7.14844 17.2461L7.14844 5.72266C7.14844 5.19531 7.51953 4.82422 8.04688 4.82422ZM8.90625 12.666C8.67188 12.666 8.50586 12.832 8.50586 13.0859L8.50586 16.377C8.50586 16.6309 8.67188 16.7969 8.90625 16.7969L14.043 16.7969C14.2871 16.7969 14.4531 16.6309 14.4531 16.377L14.4531 13.0859C14.4531 12.832 14.2871 12.666 14.043 12.666Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOffSquareFill!!
    }

private var _sFLightswitchOffSquareFill: ImageVector? = null
