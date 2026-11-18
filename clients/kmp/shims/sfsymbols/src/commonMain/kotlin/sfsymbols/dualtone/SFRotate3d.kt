package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRotate3d (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFRotate3d: ImageVector
    get() {
        if (_sFRotate3d != null) {
            return _sFRotate3d!!
        }
        _sFRotate3d = sfIcon(
            name = "Dualtone.SFRotate3d",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M8.37891 12.7246C8.37891 20.0684 10.2148 25.3516 12.7051 25.3516L12.7051 23.623C11.4551 23.623 10.1074 19.7852 10.1074 12.7246C10.1074 5.6543 11.4551 1.82617 12.7051 1.82617L12.7051 0.0878906C10.2148 0.0878906 8.37891 5.37109 8.37891 12.7246ZM12.7246 17.0312C20.0781 17.0312 25.3613 15.2051 25.3613 12.7246L23.623 12.7246C23.623 13.9648 19.7949 15.3027 12.7246 15.3027C5.66406 15.3027 1.82617 13.9648 1.82617 12.7246L0.0976562 12.7246C0.0976562 15.2051 5.38086 17.0312 12.7246 17.0312ZM12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
        }
        return _sFRotate3d!!
    }

private var _sFRotate3d: ImageVector? = null
