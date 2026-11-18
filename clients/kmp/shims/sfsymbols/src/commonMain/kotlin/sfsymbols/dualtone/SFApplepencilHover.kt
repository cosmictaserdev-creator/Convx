package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFApplepencilHover (dualtone)
 * Viewport: 24.6777 x 23.6035
 */
public val SfSymbols.Dualtone.SFApplepencilHover: ImageVector
    get() {
        if (_sFApplepencilHover != null) {
            return _sFApplepencilHover!!
        }
        _sFApplepencilHover = sfIcon(
            name = "Dualtone.SFApplepencilHover",
            viewportWidth = 24.6777f,
            viewportHeight = 23.6035f
        ) {
            addSfPath("M3.37891 23.6035C5.23438 23.6035 6.73828 23.0078 6.73828 22.2754C6.73828 21.5625 5.23438 20.9668 3.37891 20.9668C1.50391 20.9668 0 21.5625 0 22.2754C0 23.0078 1.50391 23.6035 3.37891 23.6035Z", fillAlpha = 0.425f)
            addSfPath("M4.98047 18.9062C4.80469 19.2285 5.15625 19.5215 5.42969 19.375L7.25586 18.3789L6.00586 17.1191ZM6.5332 16.1621L8.23242 17.8711L8.93555 17.4805L23.8965 2.58789C24.4531 2.03125 24.4531 1.13281 23.8965 0.585938C23.3398 0.0292969 22.4414 0.0292969 21.8945 0.576172L6.92383 15.4785Z", fillAlpha = 0.85f)
        }
        return _sFApplepencilHover!!
    }

private var _sFApplepencilHover: ImageVector? = null
