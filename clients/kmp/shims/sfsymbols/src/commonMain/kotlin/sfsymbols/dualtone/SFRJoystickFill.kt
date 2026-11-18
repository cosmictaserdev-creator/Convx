package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRJoystickFill (dualtone)
 * Viewport: 29.0527 x 24.1504
 */
public val SfSymbols.Dualtone.SFRJoystickFill: ImageVector
    get() {
        if (_sFRJoystickFill != null) {
            return _sFRJoystickFill!!
        }
        _sFRJoystickFill = sfIcon(
            name = "Dualtone.SFRJoystickFill",
            viewportWidth = 29.0527f,
            viewportHeight = 24.1504f
        ) {
            addSfPath("M14.3457 20.2539C16.0908 20.2539 17.7066 20.0894 19.1699 19.778L19.1699 20.4297C19.1699 22.7734 17.373 24.1504 14.3457 24.1504C11.3086 24.1504 9.51172 22.7734 9.51172 20.4297L9.51172 19.7757C10.9775 20.0887 12.5966 20.2539 14.3457 20.2539Z", fillAlpha = 0.2125f)
            addSfPath("M14.3457 18.6914C22.334 18.6914 27.1289 15.0977 27.1289 10.6348C27.1289 6.18164 22.334 2.58789 14.3262 2.58789C6.35742 2.58789 1.5625 6.18164 1.5625 10.6348C1.5625 15.0977 6.35742 18.6914 14.3457 18.6914Z", fillAlpha = 0.2125f)
            addSfPath("M12.2656 14.4922C11.8555 14.4922 11.582 14.1992 11.582 13.7402L11.582 7.38281C11.582 6.9043 11.8457 6.61133 12.2656 6.61133L14.6289 6.61133C16.2891 6.61133 17.3828 7.60742 17.3828 9.12109C17.3828 10.166 16.7773 11.0254 15.8203 11.377L17.0801 13.4082C17.1777 13.5645 17.2363 13.7109 17.2363 13.9062C17.2363 14.2578 16.9434 14.5117 16.5918 14.5117C16.2988 14.5117 16.123 14.3848 15.9375 14.0723L14.4336 11.6016L12.9688 11.6016L12.9688 13.7402C12.9688 14.1992 12.6953 14.4922 12.2656 14.4922ZM12.9688 10.498L14.4824 10.498C15.4395 10.498 16.0059 9.99023 16.0059 9.14062C16.0059 8.29102 15.4297 7.75391 14.502 7.75391L12.9688 7.75391Z", fillAlpha = 0.85f)
        }
        return _sFRJoystickFill!!
    }

private var _sFRJoystickFill: ImageVector? = null
