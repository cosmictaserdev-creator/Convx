package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTurnLeftDown (dualtone)
 * Viewport: 19.707 x 23.6816
 */
public val SfSymbols.Dualtone.SFArrowTurnLeftDown: ImageVector
    get() {
        if (_sFArrowTurnLeftDown != null) {
            return _sFArrowTurnLeftDown!!
        }
        _sFArrowTurnLeftDown = sfIcon(
            name = "Dualtone.SFArrowTurnLeftDown",
            viewportWidth = 19.707f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M9.44336 21.709L6.04492 17.998L1.5918 13.5449C1.42578 13.3789 1.17188 13.291 0.917969 13.291C0.390625 13.291 0 13.6914 0 14.2285C0 14.4824 0.0878906 14.6973 0.292969 14.9121L8.74023 23.3594C8.94531 23.5742 9.18945 23.6816 9.45312 23.6816C9.7168 23.6816 9.96094 23.5742 10.166 23.3594L18.6133 14.9121C18.8184 14.6973 18.9062 14.4824 18.9062 14.2285C18.9062 13.6914 18.5156 13.291 17.9883 13.291C17.7344 13.291 17.4805 13.3789 17.3145 13.5449L12.8613 17.998ZM16.8457 0C11.123 0 8.50586 2.35352 8.50586 8.07617L8.50586 17.4512L8.59375 21.709C8.60352 22.1777 8.98438 22.5586 9.44336 22.5586C9.91211 22.5586 10.293 22.1777 10.3027 21.709L10.4004 17.4512L10.3906 8.16406C10.3809 3.55469 12.3633 1.86523 16.9434 1.86523C17.5586 1.875 18.0566 1.89453 18.3984 1.9043C18.9453 1.9043 19.3457 1.54297 19.3457 0.986328C19.3457 0.439453 18.9648 0.175781 18.5352 0.078125C18.1934 0.00976562 17.5684 0 16.8457 0Z", fillAlpha = 0.85f)
        }
        return _sFArrowTurnLeftDown!!
    }

private var _sFArrowTurnLeftDown: ImageVector? = null
