package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrayFill (dualtone)
 * Viewport: 29.9512 x 21.2012
 */
public val SfSymbols.Dualtone.SFTrayFill: ImageVector
    get() {
        if (_sFTrayFill != null) {
            return _sFTrayFill!!
        }
        _sFTrayFill = sfIcon(
            name = "Dualtone.SFTrayFill",
            viewportWidth = 29.9512f,
            viewportHeight = 21.2012f
        ) {
            addSfPath("M3.79883 21.2012L25.7812 21.2012C28.3105 21.2012 29.5898 19.9219 29.5898 17.4316L29.5898 10.5176C29.5898 9.49219 29.4238 9.01367 28.9355 8.35938L24.3457 2.2168C23.0664 0.488281 22.3438 0.0292969 20.2441 0.0292969L9.3457 0.0292969C7.24609 0.0292969 6.52344 0.488281 5.24414 2.2168L0.644531 8.35938C0.166016 9.01367 0 9.49219 0 10.5176L0 17.4316C0 19.9316 1.2793 21.2012 3.79883 21.2012ZM14.7949 14.0918C12.8711 14.0918 11.5918 12.4805 11.5918 10.8203L11.5918 10.7617C11.5918 10.2539 11.2891 9.79492 10.6738 9.79492L2.31445 9.79492C1.95312 9.79492 1.875 9.49219 2.05078 9.23828L6.93359 2.64648C7.51953 1.8457 8.24219 1.55273 9.19922 1.55273L20.3906 1.55273C21.3477 1.55273 22.0703 1.8457 22.6562 2.64648L27.5293 9.23828C27.7051 9.49219 27.627 9.79492 27.2656 9.79492L18.916 9.79492C18.3008 9.79492 17.998 10.2539 17.998 10.7617L17.998 10.8203C17.998 12.4805 16.7188 14.0918 14.7949 14.0918Z", fillAlpha = 0.85f)
        }
        return _sFTrayFill!!
    }

private var _sFTrayFill: ImageVector? = null
