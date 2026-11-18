package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRJoystickFill (monochrome)
 * Viewport: 29.0527 x 24.1504
 */
public val SfSymbols.Monochrome.SFRJoystickFill: ImageVector
    get() {
        if (_sFRJoystickFill != null) {
            return _sFRJoystickFill!!
        }
        _sFRJoystickFill = sfIcon(
            name = "Monochrome.SFRJoystickFill",
            viewportWidth = 29.0527f,
            viewportHeight = 24.1504f
        ) {
            addSfPath("M14.3457 20.2539C16.0908 20.2539 17.7066 20.0894 19.1699 19.778L19.1699 20.4297C19.1699 22.7734 17.373 24.1504 14.3457 24.1504C11.3086 24.1504 9.51172 22.7734 9.51172 20.4297L9.51172 19.7757C10.9775 20.0887 12.5966 20.2539 14.3457 20.2539Z", fillAlpha = 0.85f)
            addSfPath("M27.1289 10.6348C27.1289 15.0977 22.334 18.6914 14.3457 18.6914C6.35742 18.6914 1.5625 15.0977 1.5625 10.6348C1.5625 6.18164 6.35742 2.58789 14.3262 2.58789C22.334 2.58789 27.1289 6.18164 27.1289 10.6348ZM12.2656 6.61133C11.8457 6.61133 11.582 6.9043 11.582 7.38281L11.582 13.7402C11.582 14.1992 11.8555 14.4922 12.2656 14.4922C12.6953 14.4922 12.9688 14.1992 12.9688 13.7402L12.9688 11.6016L14.4336 11.6016L15.9375 14.0723C16.123 14.3848 16.2988 14.5117 16.5918 14.5117C16.9434 14.5117 17.2363 14.2578 17.2363 13.9062C17.2363 13.7109 17.1777 13.5645 17.0801 13.4082L15.8203 11.377C16.7773 11.0254 17.3828 10.166 17.3828 9.12109C17.3828 7.60742 16.2891 6.61133 14.6289 6.61133ZM16.0059 9.14062C16.0059 9.99023 15.4395 10.498 14.4824 10.498L12.9688 10.498L12.9688 7.75391L14.502 7.75391C15.4297 7.75391 16.0059 8.29102 16.0059 9.14062Z", fillAlpha = 0.85f)
        }
        return _sFRJoystickFill!!
    }

private var _sFRJoystickFill: ImageVector? = null
