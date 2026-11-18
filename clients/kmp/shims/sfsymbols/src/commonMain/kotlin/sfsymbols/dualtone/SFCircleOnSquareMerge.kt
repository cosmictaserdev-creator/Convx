package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleOnSquareMerge (dualtone)
 * Viewport: 27.9883 x 27.002
 */
public val SfSymbols.Dualtone.SFCircleOnSquareMerge: ImageVector
    get() {
        if (_sFCircleOnSquareMerge != null) {
            return _sFCircleOnSquareMerge!!
        }
        _sFCircleOnSquareMerge = sfIcon(
            name = "Dualtone.SFCircleOnSquareMerge",
            viewportWidth = 27.9883f,
            viewportHeight = 27.002f
        ) {
            addSfPath("M16.9336 27.002C22.832 27.002 27.627 22.2266 27.627 16.3086C27.627 11.5723 24.5215 7.53906 20.2637 6.15234L20.2637 4.33594C20.2637 1.85547 18.9746 0.576172 16.4551 0.576172L3.79883 0.576172C1.26953 0.576172 0 1.8457 0 4.33594L0 17.0703C0 19.5703 1.26953 20.8398 3.79883 20.8398L7.24609 20.8398C8.94531 24.4824 12.6465 27.002 16.9336 27.002ZM16.9336 25.2344C13.0469 25.2344 9.78516 22.8027 8.57422 19.4141C8.50586 19.1992 8.38867 19.1113 8.16406 19.1113L3.83789 19.1113C2.4707 19.1113 1.72852 18.3887 1.72852 16.9824L1.72852 4.43359C1.72852 3.02734 2.4707 2.30469 3.83789 2.30469L16.4258 2.30469C17.7637 2.30469 18.5254 3.02734 18.5254 4.43359L18.5254 7.23633C18.5254 7.45117 18.6426 7.59766 18.8672 7.63672C22.8711 8.47656 25.8496 12.0117 25.8496 16.3086C25.8496 21.2891 21.8945 25.2344 16.9336 25.2344Z", fillAlpha = 0.85f)
        }
        return _sFCircleOnSquareMerge!!
    }

private var _sFCircleOnSquareMerge: ImageVector? = null
