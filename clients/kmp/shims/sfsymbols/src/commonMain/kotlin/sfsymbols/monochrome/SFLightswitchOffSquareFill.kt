package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOffSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLightswitchOffSquareFill: ImageVector
    get() {
        if (_sFLightswitchOffSquareFill != null) {
            return _sFLightswitchOffSquareFill!!
        }
        _sFLightswitchOffSquareFill = sfIcon(
            name = "Monochrome.SFLightswitchOffSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.04688 4.82422C7.51953 4.82422 7.14844 5.19531 7.14844 5.72266L7.14844 17.2461C7.14844 17.7734 7.51953 18.1543 8.04688 18.1543L14.9023 18.1543C15.4297 18.1543 15.8105 17.7734 15.8105 17.2461L15.8105 5.72266C15.8105 5.19531 15.4297 4.82422 14.9023 4.82422ZM14.4531 13.0859L14.4531 16.377C14.4531 16.6309 14.2871 16.7969 14.043 16.7969L8.90625 16.7969C8.67188 16.7969 8.50586 16.6309 8.50586 16.377L8.50586 13.0859C8.50586 12.832 8.67188 12.666 8.90625 12.666L14.043 12.666C14.2871 12.666 14.4531 12.832 14.4531 13.0859Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOffSquareFill!!
    }

private var _sFLightswitchOffSquareFill: ImageVector? = null
