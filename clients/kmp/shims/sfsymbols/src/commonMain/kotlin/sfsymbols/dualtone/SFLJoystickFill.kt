package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLJoystickFill (dualtone)
 * Viewport: 29.0527 x 24.1504
 */
public val SfSymbols.Dualtone.SFLJoystickFill: ImageVector
    get() {
        if (_sFLJoystickFill != null) {
            return _sFLJoystickFill!!
        }
        _sFLJoystickFill = sfIcon(
            name = "Dualtone.SFLJoystickFill",
            viewportWidth = 29.0527f,
            viewportHeight = 24.1504f
        ) {
            addSfPath("M14.3457 20.2539C16.0908 20.2539 17.7066 20.0894 19.1699 19.778L19.1699 20.4297C19.1699 22.7734 17.373 24.1504 14.3457 24.1504C11.3086 24.1504 9.51172 22.7734 9.51172 20.4297L9.51172 19.7757C10.9775 20.0887 12.5966 20.2539 14.3457 20.2539Z", fillAlpha = 0.2125f)
            addSfPath("M14.3457 18.6914C22.334 18.6914 27.1289 15.0977 27.1289 10.6348C27.1289 6.18164 22.334 2.58789 14.3262 2.58789C6.35742 2.58789 1.5625 6.18164 1.5625 10.6348C1.5625 15.0977 6.35742 18.6914 14.3457 18.6914Z", fillAlpha = 0.2125f)
            addSfPath("M12.5488 14.6484C12.0508 14.6484 11.7285 14.2871 11.7285 13.75L11.7285 7.09961C11.7285 6.5625 12.0605 6.21094 12.5488 6.21094C13.0469 6.21094 13.3789 6.55273 13.3789 7.09961L13.3789 13.2031L16.5918 13.2031C17.0312 13.2031 17.3242 13.4863 17.3242 13.916C17.3242 14.3457 17.0312 14.6484 16.5918 14.6484Z", fillAlpha = 0.85f)
        }
        return _sFLJoystickFill!!
    }

private var _sFLJoystickFill: ImageVector? = null
