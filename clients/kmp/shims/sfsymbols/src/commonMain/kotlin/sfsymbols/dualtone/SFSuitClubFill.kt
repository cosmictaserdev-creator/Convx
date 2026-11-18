package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSuitClubFill (dualtone)
 * Viewport: 23.8477 x 23.5059
 */
public val SfSymbols.Dualtone.SFSuitClubFill: ImageVector
    get() {
        if (_sFSuitClubFill != null) {
            return _sFSuitClubFill!!
        }
        _sFSuitClubFill = sfIcon(
            name = "Dualtone.SFSuitClubFill",
            viewportWidth = 23.8477f,
            viewportHeight = 23.5059f
        ) {
            addSfPath("M0 13.9844C0 17.002 2.29492 19.2676 5.3125 19.2676C7.02148 19.2676 8.71094 18.6621 9.45312 17.2168L9.58008 17.2168C9.52148 18.916 7.76367 20.3125 6.9043 21.1816C6.03516 22.0801 6.5625 23.3496 7.70508 23.3496L15.7812 23.3496C16.9238 23.3496 17.4512 22.0801 16.582 21.1816C15.7227 20.3125 13.9648 18.916 13.9062 17.2168L14.0332 17.2168C14.7754 18.6621 16.4648 19.2676 18.1738 19.2676C21.1914 19.2676 23.4863 17.002 23.4863 13.9844C23.4863 11.0059 21.2012 8.50586 18.2324 8.50586C17.1094 8.50586 15.9277 8.87695 15.0098 9.61914C16.6016 8.31055 17.0996 6.71875 17.0996 5.35156C17.0996 2.38281 14.6973 0 11.7383 0C8.78906 0 6.38672 2.38281 6.38672 5.35156C6.38672 6.71875 6.88477 8.31055 8.47656 9.61914C7.55859 8.87695 6.37695 8.50586 5.25391 8.50586C2.28516 8.50586 0 11.0059 0 13.9844Z", fillAlpha = 0.85f)
        }
        return _sFSuitClubFill!!
    }

private var _sFSuitClubFill: ImageVector? = null
