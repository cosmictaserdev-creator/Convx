package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLJoystickFill (monochrome)
 * Viewport: 29.0527 x 24.1504
 */
public val SfSymbols.Monochrome.SFLJoystickFill: ImageVector
    get() {
        if (_sFLJoystickFill != null) {
            return _sFLJoystickFill!!
        }
        _sFLJoystickFill = sfIcon(
            name = "Monochrome.SFLJoystickFill",
            viewportWidth = 29.0527f,
            viewportHeight = 24.1504f
        ) {
            addSfPath("M14.3457 20.2539C16.0908 20.2539 17.7066 20.0894 19.1699 19.778L19.1699 20.4297C19.1699 22.7734 17.373 24.1504 14.3457 24.1504C11.3086 24.1504 9.51172 22.7734 9.51172 20.4297L9.51172 19.7757C10.9775 20.0887 12.5966 20.2539 14.3457 20.2539Z", fillAlpha = 0.85f)
            addSfPath("M27.1289 10.6348C27.1289 15.0977 22.334 18.6914 14.3457 18.6914C6.35742 18.6914 1.5625 15.0977 1.5625 10.6348C1.5625 6.18164 6.35742 2.58789 14.3262 2.58789C22.334 2.58789 27.1289 6.18164 27.1289 10.6348ZM11.7285 7.09961L11.7285 13.75C11.7285 14.2871 12.0508 14.6484 12.5488 14.6484L16.5918 14.6484C17.0312 14.6484 17.3242 14.3457 17.3242 13.916C17.3242 13.4863 17.0312 13.2031 16.5918 13.2031L13.3789 13.2031L13.3789 7.09961C13.3789 6.55273 13.0469 6.21094 12.5488 6.21094C12.0605 6.21094 11.7285 6.5625 11.7285 7.09961Z", fillAlpha = 0.85f)
        }
        return _sFLJoystickFill!!
    }

private var _sFLJoystickFill: ImageVector? = null
