package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRotate3dFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFRotate3dFill: ImageVector
    get() {
        if (_sFRotate3dFill != null) {
            return _sFRotate3dFill!!
        }
        _sFRotate3dFill = sfIcon(
            name = "Monochrome.SFRotate3dFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M8.13477 15.4883C8.07617 14.5996 8.04688 13.6816 8.04688 12.7246C8.04688 7.08984 8.94531 2.56836 10.332 0.244141C4.49219 1.36719 0 6.57227 0 12.7246C0 14.0137 3.1543 15.0879 8.13477 15.4883ZM9.85352 15.5762C10.7617 15.625 11.7188 15.6543 12.7148 15.6543C20.2637 15.6543 25.4395 14.375 25.4395 12.7246C25.4395 5.76172 19.668 0 12.7051 0C11.0547 0 9.77539 5.17578 9.77539 12.7246C9.77539 13.7207 9.80469 14.668 9.85352 15.5762ZM8.24219 17.1875C4.69727 16.8555 1.89453 16.0938 0.244141 15.1074C1.20117 20.166 5.26367 24.2285 10.3223 25.1953C9.33594 23.5449 8.57422 20.7324 8.24219 17.1875ZM9.94141 17.2949C10.3418 22.2754 11.4062 25.4297 12.7051 25.4395C18.8672 25.4395 24.0723 20.9375 25.1953 15.0977C22.8711 16.4844 18.3496 17.3828 12.7148 17.3828C11.748 17.3828 10.8301 17.3535 9.94141 17.2949Z", fillAlpha = 0.85f)
        }
        return _sFRotate3dFill!!
    }

private var _sFRotate3dFill: ImageVector? = null
