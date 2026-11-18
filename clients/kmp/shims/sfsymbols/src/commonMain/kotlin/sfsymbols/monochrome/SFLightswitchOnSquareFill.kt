package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOnSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLightswitchOnSquareFill: ImageVector
    get() {
        if (_sFLightswitchOnSquareFill != null) {
            return _sFLightswitchOnSquareFill!!
        }
        _sFLightswitchOnSquareFill = sfIcon(
            name = "Monochrome.SFLightswitchOnSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.04688 4.82422C7.51953 4.82422 7.14844 5.19531 7.14844 5.72266L7.14844 17.2461C7.14844 17.7734 7.51953 18.1543 8.04688 18.1543L14.9023 18.1543C15.4297 18.1543 15.8105 17.7734 15.8105 17.2461L15.8105 5.72266C15.8105 5.19531 15.4297 4.82422 14.9023 4.82422ZM14.4531 6.60156L14.4531 9.89258C14.4531 10.1465 14.2871 10.3125 14.043 10.3125L8.90625 10.3125C8.67188 10.3125 8.50586 10.1465 8.50586 9.89258L8.50586 6.60156C8.50586 6.34766 8.67188 6.18164 8.90625 6.18164L14.043 6.18164C14.2871 6.18164 14.4531 6.34766 14.4531 6.60156Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOnSquareFill!!
    }

private var _sFLightswitchOnSquareFill: ImageVector? = null
