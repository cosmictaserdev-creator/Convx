package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOnSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFLightswitchOnSquare: ImageVector
    get() {
        if (_sFLightswitchOnSquare != null) {
            return _sFLightswitchOnSquare!!
        }
        _sFLightswitchOnSquare = sfIcon(
            name = "Dualtone.SFLightswitchOnSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M8.125 17.9883L14.8242 17.9883C15.332 17.9883 15.7031 17.6172 15.7031 17.1094L15.7031 5.85938C15.7031 5.3418 15.332 4.99023 14.8242 4.99023L8.125 4.99023C7.61719 4.99023 7.25586 5.3418 7.25586 5.85938L7.25586 17.1094C7.25586 17.6172 7.61719 17.9883 8.125 17.9883ZM8.96484 10.3418C8.74023 10.3418 8.57422 10.1758 8.57422 9.93164L8.57422 6.71875C8.57422 6.46484 8.74023 6.30859 8.96484 6.30859L13.9844 6.30859C14.2285 6.30859 14.3848 6.46484 14.3848 6.71875L14.3848 9.93164C14.3848 10.1758 14.2285 10.3418 13.9844 10.3418Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOnSquare!!
    }

private var _sFLightswitchOnSquare: ImageVector? = null
