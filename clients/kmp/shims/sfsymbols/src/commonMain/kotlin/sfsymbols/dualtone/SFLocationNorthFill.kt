package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationNorthFill (dualtone)
 * Viewport: 19.8926 x 26.4941
 */
public val SfSymbols.Dualtone.SFLocationNorthFill: ImageVector
    get() {
        if (_sFLocationNorthFill != null) {
            return _sFLocationNorthFill!!
        }
        _sFLocationNorthFill = sfIcon(
            name = "Dualtone.SFLocationNorthFill",
            viewportWidth = 19.8926f,
            viewportHeight = 26.4941f
        ) {
            addSfPath("M1.21094 25.8301C1.73828 25.8301 2.09961 25.6348 2.62695 25.1172L9.60938 18.1934C9.66797 18.1348 9.7168 18.0957 9.76562 18.0957C9.82422 18.0957 9.87305 18.1348 9.93164 18.1934L16.9043 25.1172C17.4414 25.6348 17.8027 25.8301 18.3301 25.8301C19.0723 25.8301 19.5312 25.2539 19.5312 24.502C19.5312 24.1113 19.375 23.6328 19.2188 23.1934L11.2793 1.41602C10.9277 0.458984 10.4102 0 9.76562 0C9.12109 0 8.61328 0.458984 8.26172 1.41602L0.322266 23.1934C0.15625 23.6328 0 24.1113 0 24.502C0 25.2539 0.46875 25.8301 1.21094 25.8301Z", fillAlpha = 0.85f)
        }
        return _sFLocationNorthFill!!
    }

private var _sFLocationNorthFill: ImageVector? = null
