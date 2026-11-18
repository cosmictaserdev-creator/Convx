package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOnSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFLightswitchOnSquareFill: ImageVector
    get() {
        if (_sFLightswitchOnSquareFill != null) {
            return _sFLightswitchOnSquareFill!!
        }
        _sFLightswitchOnSquareFill = sfIcon(
            name = "Dualtone.SFLightswitchOnSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.04688 18.1543C7.51953 18.1543 7.14844 17.7734 7.14844 17.2461L7.14844 5.72266C7.14844 5.19531 7.51953 4.82422 8.04688 4.82422L14.9023 4.82422C15.4297 4.82422 15.8105 5.19531 15.8105 5.72266L15.8105 17.2461C15.8105 17.7734 15.4297 18.1543 14.9023 18.1543ZM8.90625 10.3125L14.043 10.3125C14.2871 10.3125 14.4531 10.1465 14.4531 9.89258L14.4531 6.60156C14.4531 6.34766 14.2871 6.18164 14.043 6.18164L8.90625 6.18164C8.67188 6.18164 8.50586 6.34766 8.50586 6.60156L8.50586 9.89258C8.50586 10.1465 8.67188 10.3125 8.90625 10.3125Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOnSquareFill!!
    }

private var _sFLightswitchOnSquareFill: ImageVector? = null
